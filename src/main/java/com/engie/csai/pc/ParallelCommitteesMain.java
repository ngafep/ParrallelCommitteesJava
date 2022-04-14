package com.engie.csai.pc;

import com.engie.csai.pc.model.Committee;
import com.engie.csai.pc.model.Consensus;
import com.engie.csai.pc.model.KeyGenerator;
import com.engie.csai.pc.model.Network;
import com.engie.csai.pc.model.Peer;
import com.engie.csai.pc.model.PeerSetting;
import com.engie.csai.pc.model.PoW;
import com.engie.csai.pc.model.json.CategoriesConfigJson;
import com.engie.csai.pc.model.json.ClientRequestJson;
import com.engie.csai.pc.model.json.ClientRequestsJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
* *
* *
* *
* *
    Important note:
    In order to apply the effects of each round of consensus in related committee
    we do not need to consider everything that is done in PBFT algorithm, such as
    the view change, leader change etc. but it is enough to select a peer randomly
    in related committee in order to reduce its quota one unit after processing of each client's request.
    Then, if new quota is zero, peer should leave the committee and wait in the queue
    in order for joining the committee again. The selection process from the peers queue is randomly.
* *
* *
* *
* *
 */
//----------------------------------------------------------------------------
// ParallelCommitteesMain.java
//----------------------------------------------------------------------------

/*
 * ***********************************************************************
 * ***********************************************************************
 * When a peer's quota is finished, it leaves the committee and its seat gets free,
 * (provided that there is at least one peer waiting for entering the committee.
 * the reason of this condition is to keep the number of committee members in its maximum possible amount
 * in order to better supporting the saved logs and better tolerating against possibility of nodes failure.)
 * The 'isProcessor' property of that node after leaving the committee becomes false,
 * but the is still permitted to make it true again,
 * and wait in the queue again to re-enter the committee.
 * ***********************************************************************
 * ***********************************************************************
 *************************************************************************/

public class ParallelCommitteesMain {

    private static final Logger LOGGER = Logger.getLogger(ParallelCommitteesMain.class.getName());
    public static final String PEERS_IN_COMMITTEE_INPUT = "Enter number of peers in committee %s: (Maximum possible value is: %s)";
    public static final String RSA = "RSA";

    public static void main(String[] args)
        throws Exception {
        LOGGER.info("""
                        
            **** ************* ****
            **** Administrator ****
            **** ************* ****
            """);
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        LOGGER.info("Please provide a config file path");
        String configFile = sc.next();
        final CategoriesConfigJson config = readNetworkConfigFromJsonFile(configFile);

        LOGGER.info("The network has been configured as follows:\n");
        int numberOfCat = config.getNetworkConfigs()
            .size();
        LOGGER.info(() -> "Number of committees: " + numberOfCat);
        // Applying configuration to the network
        String[] catId = new String[numberOfCat];

        // Creating network
        Network network = new Network();
        network.setNoCats(numberOfCat);

        // creating committees
        Committee[] com = createCommittees(config, network);

        LOGGER.info("""
                        
            **** ************* ****
            **** User ****
            **** ************* ****
            """);

        /*
        All following actions should be done by an instance of the User class.
         */
        int[] targetPeerCreation = new int[numberOfCat];
        int[] numberOfPeers = new int[numberOfCat];
        KeyPairGenerator[][] keyPairGenSign = new KeyPairGenerator[0][];
        KeyPairGenerator[][] keyPeer = new KeyPairGenerator[0][];
        StringBuffer[][] publicKeyStrBPeer = new StringBuffer[0][];
        StringBuffer[][] privateKeyStrBPeer = new StringBuffer[0][];
        String[][] publicKeyStrPeer = new String[0][];
        String[][] addressPeer = new String[0][];
        String[][] privateKeyStrPeer = new String[0][];
        PeerSetting[][] peerSetting = null;
        Peer[][] peer = null;
        KeyPair[][] kPairSign = null;
        PrivateKey[][] prvKeySign = null;
        PublicKey[][] pubKeySign = null;
        float[] lockedTokens = new float[numberOfCat];
        float lockedTokensForAllCats = 2;

        // Initializing number of peers for each committee
        for (int committeeIndex = 0; committeeIndex < numberOfCat; committeeIndex++) {
            LOGGER.info(String.format(PEERS_IN_COMMITTEE_INPUT, committeeIndex, config.getNetworkConfigs()
                .get(committeeIndex)
                .getCapacity()));
            numberOfPeers[committeeIndex] = sc.nextInt();

            keyPeer = new KeyPairGenerator[numberOfCat][numberOfPeers[committeeIndex]];
            publicKeyStrBPeer = new StringBuffer[numberOfCat][numberOfPeers[committeeIndex]];
            publicKeyStrPeer = new String[numberOfCat][numberOfPeers[committeeIndex]];
            addressPeer = new String[numberOfCat][numberOfPeers[committeeIndex]];
            privateKeyStrBPeer = new StringBuffer[numberOfCat][numberOfPeers[committeeIndex]];
            privateKeyStrPeer = new String[numberOfCat][numberOfPeers[committeeIndex]];
            peerSetting = new PeerSetting[numberOfCat][numberOfPeers[committeeIndex]];
            peer = new Peer[numberOfCat][numberOfPeers[committeeIndex]];
            keyPairGenSign = new KeyPairGenerator[numberOfCat][numberOfPeers[committeeIndex]];
            kPairSign = new KeyPair[numberOfCat][numberOfPeers[committeeIndex]];
            prvKeySign = new PrivateKey[numberOfCat][numberOfPeers[committeeIndex]];
            pubKeySign = new PublicKey[numberOfCat][numberOfPeers[committeeIndex]];
            lockedTokens[committeeIndex] = lockedTokensForAllCats;
        }

        /*
         * Creating peers in each committee:
         */
        createPeer(network, com, targetPeerCreation, keyPeer, publicKeyStrBPeer, publicKeyStrPeer, addressPeer, privateKeyStrBPeer, privateKeyStrPeer, peerSetting, peer, keyPairGenSign, kPairSign, prvKeySign, pubKeySign, numberOfPeers, lockedTokens);

        /*
         ************************************************************************
         *************************************************************************
         * ***********************************************************************
         * Create several JSON file, each of them includes multiple Client-Requests (P2P Data Transfer).
         * Each JSON file is dedicated to a committee (category).
         * All the JSON files are read in parallel by Threads.
         * Threads after reading each file parse the Client-Requests'elements
         * and create Client-Requests (of Client-Request class type).
         * Threads then inserts created Client-Requests in related queue of the broker (RabbitMQ).
         * ***********************************************************************
         * ***********************************************************************
         *************************************************************************/
        LOGGER.info("Enter number of Client-Request in each JSON file (for each category): ");
        int numberOfRequestsInJSON = sc.nextInt();

        String[][] data = new String[numberOfCat][numberOfRequestsInJSON];
        int min = 1;
        int max = 10;
        int maxMinDistance = max - min;

        for (int categoryIndex = 0; categoryIndex < numberOfCat; categoryIndex++) {
            for (int dataIndex = 0; dataIndex < numberOfRequestsInJSON; dataIndex++) {

                StringBuilder dataCreator = new StringBuilder();
                String dataCreatorTemp;

                for (int ch = 0; ch < max; ch++) {
                    /*
                    Data includes only a sequence of '0'.
                    Number of Zeros is based on maximum authorized size of data in each category.
                    */
                    dataCreatorTemp = "0";
                    dataCreator.append(dataCreatorTemp);
                }

                data[categoryIndex][dataIndex] = dataCreator.toString();
                final var committeeInfo = "Committee" + categoryIndex + " Data " + dataIndex + ": " + data[categoryIndex][dataIndex] + "\n";
                LOGGER.info(committeeInfo);
                /*
                 ************************************************************************
                 *************************************************************************
                 * ***********************************************************************
                 * = "A Client-Request has two fields to transfer: 'data' and 'token'.
                 * If some tokens are being transferred, the Client-Request should necessarily have a receiver address,
                 * where 'data' can either include some comments or to be empty.
                 * In any case, the 'fee' is always calculated based on the size of the data.
                 * If the 'data' is empty, the 'fee' has the minimum value.
                 * If Client-Request is sent only in order to transfer some data (any kind of information),
                 * the fields 'token' and 'receiver' are empty."
                 * ***********************************************************************
                 * ***********************************************************************
                 *************************************************************************/
            }
            min = max + 1;
            max = min + maxMinDistance;
        }

        /*
         ************************************************************************
         *************************************************************************
         * ***********************************************************************
         *
         * This section is used when a Client-Request is sent apart from JSON file,
         * by clicking on a button dedicated for this purpose in the GUI.
         *
         * ***********************************************************************
         * ***********************************************************************
         *************************************************************************/
        /*
         * Create JSON files (Begin)
         */
        createJSONfilesClientRequests(catId, numberOfRequestsInJSON, data);

        /*
         * Reading all JSON files in parallel by Threads
         * then parsing JSON files to create Client-Requests of Client-Request Class type
         * and then sending Client-Requests through RabbitMQ (Each queue serves a committee.)
         */
        if (Objects.equals(Consensus.getConsAlg(), "pbft")) {

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(15);
            final long start = System.currentTimeMillis();
            LOGGER.info(() -> "PBFT starts ... " + start);
            for (int categoryIndex = 0; categoryIndex < numberOfCat; categoryIndex++) {
                scheduler.scheduleAtFixedRate(new OperationLauncher(catId[categoryIndex], com[categoryIndex]), 1, 1, TimeUnit.SECONDS);
            }
        }
    }

    private static void createJSONfilesClientRequests(
        String[] catId,
        int numberOfRequestsInJSON,
        String[][] data
    )
        throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();

        int fileIndex = 0;
        for (String cat : catId) {
            ClientRequestsJson json = new ClientRequestsJson();
            ClientRequestJson clientRequestJsonElements;
            final var random = new Random();
            for (int requestIndex = 0; requestIndex < numberOfRequestsInJSON; requestIndex++) {
                clientRequestJsonElements = new ClientRequestJson().data(data[fileIndex][requestIndex])
                    .fee((float) data[fileIndex][requestIndex].length())
                    .senderSignature("senderSignature_" + random.nextInt(100, 103))
                    .receiverAddress("receiverAddress")
                    .tokenToSend(requestIndex);
                json.addRequestsItem(clientRequestJsonElements);
            }
            mapper.writeValue(Paths.get("clientRequest_" + cat + ".json")
                .toFile(), json);
            fileIndex++;
        }
    }

    private static void createPeer(
        Network network,
        Committee[] com,
        int[] targetPeerCreation,
        KeyPairGenerator[][] keyPeer,
        StringBuffer[][] publicKeyStrBPeer,
        String[][] publicKeyStrPeer,
        String[][] addressPeer,
        StringBuffer[][] privateKeyStrBPeer,
        String[][] privateKeyStrPeer,
        PeerSetting[][] peerSetting,
        Peer[][] peer,
        KeyPairGenerator[][] keyPairGenSign,
        KeyPair[][] kPairSign,
        PrivateKey[][] prvKeySign,
        PublicKey[][] pubKeySign,
        int[] numberOfPeers,
        float[] lockedTokens
    )
        throws NoSuchAlgorithmException {

        int numberOfCat = network.getNoCats();
        for (int committeeIndex = 0; committeeIndex < numberOfCat; committeeIndex++) {
            for (int peerIndex = 0; peerIndex < numberOfPeers[committeeIndex]; peerIndex++) {
                keyPairGenSign[committeeIndex][peerIndex] = KeyPairGenerator.getInstance(RSA);
                keyPairGenSign[committeeIndex][peerIndex].initialize(2048, new SecureRandom());

                /*
                 * Generating key pair
                 */
                keyPeer[committeeIndex][peerIndex] = KeyGenerator.keyGenerator();
                publicKeyStrBPeer[committeeIndex][peerIndex] = KeyGenerator.publicKeyString(keyPeer[committeeIndex][peerIndex]);
                publicKeyStrPeer[committeeIndex][peerIndex] = publicKeyStrBPeer[committeeIndex][peerIndex].toString();
                addressPeer[committeeIndex][peerIndex] = publicKeyStrPeer[committeeIndex][peerIndex] + "Category" + committeeIndex;
                privateKeyStrBPeer[committeeIndex][peerIndex] = KeyGenerator.privateKeyString(keyPeer[committeeIndex][peerIndex]);
                privateKeyStrPeer[committeeIndex][peerIndex] = privateKeyStrBPeer[committeeIndex][peerIndex].toString();

                keyPeer[committeeIndex][peerIndex].initialize(1024);
                kPairSign[committeeIndex][peerIndex] = keyPeer[committeeIndex][peerIndex].generateKeyPair();
                prvKeySign[committeeIndex][peerIndex] = kPairSign[committeeIndex][peerIndex].getPrivate();
                pubKeySign[committeeIndex][peerIndex] = kPairSign[committeeIndex][peerIndex].getPublic();

                /*
                 * Initialize category
                 */
                final var catId = "Cat" + committeeIndex;
                var category = network.getCategories()
                    .get(catId);

                /*
                 * Getting related committee for selected category
                 */
                var committee = category.getCommitteeOfCategory();

                /*
                 * Setting peer
                 */
                peerSetting[committeeIndex][peerIndex] = category.getPeerSettingOfCategory();

                /*
                 * Creating peer
                 */
                LOGGER.info("Committee " + committeeIndex + " : Peer " + peerIndex + " is going to be created.");

                targetPeerCreation[committeeIndex] = 1;
                /*
                String address, String catId, float tokensInitial, float tokensCurrent, float tokensLocked,
                int quotaInitial, int quotaCurrent, String privateKey, String publicKey, Committee committeeOfPeer,
                boolean isProcessor, ClientRequestMessage clientRequestMessage, int target*/
                peer[committeeIndex][peerIndex] = new Peer(addressPeer[committeeIndex][peerIndex], catId, peerSetting[committeeIndex][peerIndex].getNoInitTokens(), peerSetting[committeeIndex][peerIndex].getNoInitTokens(), lockedTokens[committeeIndex], peerSetting[committeeIndex][peerIndex].getInitQuota(), peerSetting[committeeIndex][peerIndex].getInitQuota(), privateKeyStrPeer[committeeIndex][peerIndex], publicKeyStrPeer[committeeIndex][peerIndex], committee, true, targetPeerCreation[committeeIndex]);
                LOGGER.info("Committee " + committeeIndex + " Peer " + peerIndex + " has been created.");

                /*
                 * Check PoW answer
                 */
                boolean checkPoW;
                checkPoW = PoW.powCheck(catId, publicKeyStrPeer[committeeIndex][peerIndex], targetPeerCreation[committeeIndex], peer[committeeIndex][peerIndex].getPowAnswer());
                if (checkPoW) {
                    LOGGER.info("Committee " + committeeIndex + " : PoW has been solved successfully by Peer " + peerIndex);
                    LOGGER.info("Committee " + committeeIndex + " : Peer " + peerIndex + " PoW answer is: " + peer[committeeIndex][peerIndex].getPowAnswer());
                    LOGGER.info("Committee " + committeeIndex + " : Peer " + peerIndex + " address is: " + peer[committeeIndex][peerIndex].getAddress());
                    if (peer[committeeIndex][peerIndex].isProcessor()) {
                        if (committee.getFreeSeats() != 0) {
                            committee._addPeerOfCommittee(peer[committeeIndex][peerIndex]);
                            com[committeeIndex].reduceActualFreeSeats();
                        } else {
                            committee.insertPeerToQueue(peer[committeeIndex][peerIndex]);
                            if (committee.getQueueSize() > committee.getPql()) {
                                peer[committeeIndex][peerIndex].resetQuotaNotification("Cat0");
                            }
                        }
                    }
                } else {
                    LOGGER.info("Committee " + committeeIndex + " : PoW has not been solved by Peer " + peerIndex);
                }
            }
        }
    }

    private static Committee[] createCommittees(
        CategoriesConfigJson config,
        Network network
    ) {
        final var networkConfigs = config.getNetworkConfigs();
        Committee[] com = new Committee[networkConfigs.size()];

        int begin = 1;
        int end = 10;
        int beginTemp;
        int endBeginDistance = end - begin;

        for (int committeeIndex = 0; committeeIndex < networkConfigs.size(); committeeIndex++) {
            final var catName = "Cat" + committeeIndex;
            network.addCategory(catName, begin, end);
            beginTemp = begin;
            begin = end + 1;
            end = beginTemp + endBeginDistance;
            final var categoryConfigJson = networkConfigs.get(committeeIndex);
            com[committeeIndex] = new Committee(committeeIndex, categoryConfigJson.getCapacity(), categoryConfigJson.getPql(), categoryConfigJson.getCapacity());
            network.addCommittee(com[committeeIndex], catName);
            network.settingPeersForEachCategory(categoryConfigJson.getNumberOfQuota(), categoryConfigJson.getNumberOfInitialTokens(), catName);
            network.setConsAlgoForEachCategory("pbft", catName);
        }
        return com;
    }

    private static CategoriesConfigJson readNetworkConfigFromJsonFile(String configFile)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new FileReader(configFile), CategoriesConfigJson.class);
    }
}

