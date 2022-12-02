package com.engie.csai.pc.main;

import com.engie.csai.pc.consensus.launcher.PBFTLauncher;
import com.engie.csai.pc.core.exception.ParallelCommitteeException;
import com.engie.csai.pc.core.listener.ICommitteeListener;
import com.engie.csai.pc.core.model.PoW;
import com.engie.csai.pc.core.model.json.CategoriesConfigJson;
import com.engie.csai.pc.core.model.json.ClientRequestJson;
import com.engie.csai.pc.core.model.json.ClientRequestsJson;
import com.engie.csai.pc.core.models.Committee;
import com.engie.csai.pc.core.models.Network;
import com.engie.csai.pc.core.models.ProcessorNode;
import com.engie.csai.pc.core.models.ProcessorQueue;
import com.engie.csai.pc.usecase.CommitteeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class ParallelCommitteesNewMain {
    private static final Logger LOGGER =
        Logger.getLogger(ParallelCommitteesNewMain.class.getName());

    public static final String PEERS_IN_COMMITTEE_INPUT =
        "Enter number of peers in committee " + "%s:" + " (Maximum possible value is: %s)";

    private static Network configureCommittees(CategoriesConfigJson config) {
        final var networkConfigs = config.getNetworkConfigs();
        var network = Network.builder()
            .build();
        //TODO Put datasizemax in json file
        var committees = networkConfigs.stream()
            .map(networkConfig -> {var committee = Committee.builder()
                .capacity(networkConfig.getCapacity())
                .numberOfMessages(networkConfig.getNumberOfRequests())
                .consensus(Committee.Consensus.PBFT)
                .dataSizeMax(networkConfig.getMaxDataSize())
                .numberOfClients(networkConfig.getNumberOfClients())
                .initialQuota(networkConfig.getNumberOfQuota())
                .category(networkConfig.getName())
                .processorNodes(new ArrayList<>())
                .clients(new ArrayList<>())
                .listeners(new ArrayList<>())
                .network(network)
                .processorQueue(ProcessorQueue.builder()
                    .capacity(networkConfig.getPql())
                    .processorNodes(new ArrayList<>())
                    .build()
                )
                .build();
                populateProcessorNodes(committee, networkConfig.getNumberOfReplicas());
            return committee;})
            .toList();
        network.setCommittees(committees);
        return Network.builder()
            .committees(committees)
            .build();
    }

    private static CategoriesConfigJson readNetworkConfigFromJsonFile(String configFile)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new FileReader(configFile), CategoriesConfigJson.class);
    }

    public static void main(String[] args)
        throws Exception {
        final CategoriesConfigJson config = getCategoriesConfigJson();
        // creating committees
        var network = configureCommittees(config);
        LOGGER.info(() -> "Number of committees: " + network.getCommittees()
            .size());
        generateClientJsonFiles(network);
        run(network);
    }

    private static void run(Network network) {
        int maxRequests = network.getCommittees().stream().mapToInt(Committee::getNumberOfMessages).max().orElseThrow();
        String modeMessage = """
            Please select the mode by entering 1 or 2 :
            1- Slow motion mode (Tree only will be displayed)
            2- Normal mode (Progress bar only will be displayed)
            """;
        LOGGER.info(modeMessage);
        Scanner sc = new Scanner(System.in);
        boolean slowMotion = sc.nextInt()==1;
        final var totalNumberOfClients = network.getCommittees()
            .stream()
            .mapToInt(
                Committee::getNumberOfClients)
            .sum();
        System.out.println("Total number of clients in the system : " + totalNumberOfClients);
        System.out.println("Total number of processed requests by the system : " + network.getCommittees().size()*maxRequests);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(15);
        final long start = System.currentTimeMillis();
        ICommitteeListener listener = new CommitteeListener();

        network.getCommittees()
            .forEach(commitee -> {
                listener.subscribe(commitee);
                var pb = new ProgressBarBuilder().setStyle(
                    ProgressBarStyle.ASCII).setInitialMax(maxRequests).setTaskName(
                    commitee.getCategory()).setUpdateIntervalMillis(10).build();
                commitee.setProgressBar(pb);
                commitee.setStart(start);
                commitee.setDisplayTree(slowMotion);
                if (Objects.equals(commitee.getConsensus(), Committee.Consensus.PBFT)) {
                    final var pbftLauncher = new PBFTLauncher(
                        commitee.getCategory(),
                        commitee,
                        CommitteeServiceImpl.getInstance());
                    pbftLauncher.register();
                    scheduler.scheduleAtFixedRate(
                        pbftLauncher, 1, 1, TimeUnit.SECONDS);
                }
            });
    }

    private static void generateClientJsonFiles(Network network) {
        network.getCommittees()
            .forEach(committee -> {
                ClientRequestsJson json = new ClientRequestsJson();
                ClientRequestJson clientRequestJsonElements;
                final var random = new Random();
                var maxClientId = 100+committee.getNumberOfClients();

                for (int requestIndex = 0; requestIndex < committee.getNumberOfMessages(); requestIndex++) {
                    String data = getData(committee.getDataSizeMax());

                    clientRequestJsonElements = new ClientRequestJson().data(data)
                        .fee((float) data.length())
                        .senderSignature("senderSignature_" + random.nextInt(100, maxClientId))
                        .receiverAddress("receiverAddress")
                        .tokenToSend(requestIndex);
                    json.addRequestsItem(clientRequestJsonElements);
                }
                try {
                    new ObjectMapper().writeValue(Paths.get("clientRequest_" + committee.getCategory() + ".json")
                        .toFile(), json);
                } catch (IOException e) {
                    throw new ParallelCommitteeException(e);
                }
            });
    }

    private static String getData(int dataSizeMax) {

        /*
                    Data includes only a sequence of '0'.
                    Number of Zeros is based on maximum authorized size of data in each category.
                    */
        return "0".repeat(Math.max(0, dataSizeMax));
    }

    private static void populateProcessorNodes(Committee committee, int processorNodesSize) {
        String category = committee.getCategory();
        for (int processorNodeIndex = 0; processorNodeIndex < processorNodesSize; processorNodeIndex++) {
            UUID uid = UUID.randomUUID();
            PoW.checkAnswer(category, uid);
            var processorNode = ProcessorNode.builder()
                .id(uid)
                .currentQuota(committee.getInitialQuota())
                .replications(new ArrayList<>())
                .committee(committee)
                .build();
            if (committee.getCapacity() <= committee.getProcessorNodes()
                .size()) {
                committee.getProcessorQueue()
                    .getProcessorNodes()
                    .add(processorNode);
            }
            else {
                committee.getProcessorNodes()
                    .add(processorNode);
            }
        }
    }


    private static CategoriesConfigJson getCategoriesConfigJson()
        throws IOException {
        LOGGER.info("""
                        
            **** ************* ****
            **** Administrator ****
            **** ************* ****
            """);
        Scanner sc = new Scanner(System.in);
        LOGGER.info("Please provide a config file path");
        String configFile = sc.next();
        return readNetworkConfigFromJsonFile(configFile);
    }
}
