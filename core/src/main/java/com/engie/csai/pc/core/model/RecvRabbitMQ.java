package com.engie.csai.pc.core.model;

import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.model.json.ClientRequestJson;
import com.engie.csai.pc.core.model.json.ClientRequestsJson;
import com.engie.csai.pc.core.models.Committee;
import com.engie.csai.pc.core.service.CommitteeService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class RecvRabbitMQ {

    private RecvRabbitMQ() {
    }

    private static final Map<String, Integer> numberOfClientsPerCategory = new ConcurrentHashMap<>();
    private static final Map<String, Integer> numberOfPeersPerCategory = new ConcurrentHashMap<>();
    private static final Map<String, Integer> numberOfRequestsPerCategory = new ConcurrentHashMap<>();

    public static void standbyForReceiveMessages(
        String category,
        Committee committee,
        CommitteeService service
    ) {
        ClientRequestMessage[] clientRequestMessages = launchOperationForOneCategory(category, committee.getProcessorNodes()
            .size());
        if (clientRequestMessages.length > 0) {
            service.callConsensus(
                category,
                numberOfClientsPerCategory.get(
                    category),
                numberOfPeersPerCategory.get(
                    category),
                numberOfRequestsPerCategory.get(
                    category),
                clientRequestMessages[0].toString());
        }
    }

    private static ClientRequestMessage[] launchOperationForOneCategory(
        String catId,
        int peerCount
    ) {
        ClientRequestsJson jsonFile = new JsonReaderInParallel().parseJsonFile(catId);
        if (jsonFile == null) {
            return new ClientRequestMessage[0];
        }

        ClientRequestMessage[] clientRequestExtractedFromJson = new ClientRequestMessage[jsonFile.getRequests()
            .size()]; // -> Client-Request[*category index*][*Client-Request index in that category*]
        int requestIndex = 0;
        List<ClientRequestJson> requestJsonList = jsonFile.getRequests();
        Set<String> clientsInJson = requestJsonList.stream()
            .map(ClientRequestJson::getSenderSignature)
            .collect(Collectors.toSet());
        RecvRabbitMQ.addNumberOfClientsForCategory(catId, clientsInJson.size());
        RecvRabbitMQ.addNumberOfPeersForCategory(catId, peerCount);
        RecvRabbitMQ.addNumberOfRequestsForCategory(catId, jsonFile.getRequests()
            .size());
        for (ClientRequestJson requestJson : requestJsonList) {
            assert false;
            clientRequestExtractedFromJson[requestIndex] = new ClientRequestMessage(requestJson.getSenderSignature(), requestJson.getReceiverAddress(), requestJson.getFee(), requestJson.getData(), requestJson.getTokenToSend(), false, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
            requestIndex++;
        }
        return clientRequestExtractedFromJson;
    }

    public static void addNumberOfClientsForCategory(
        String catId,
        int nbClients
    ) {
        numberOfClientsPerCategory.put(catId, nbClients);
    }

    public static void addNumberOfPeersForCategory(
        String catId,
        int numberOfPeers
    ) {
        numberOfPeersPerCategory.put(catId, numberOfPeers);
    }

    public static void addNumberOfRequestsForCategory(
        String catId,
        int numberOfRequets
    ) {
        numberOfRequestsPerCategory.put(catId, numberOfRequets);
    }

    public static void register(
        String category,
        Committee committee,
        CommitteeService service,
        ConsensusSimulator consensus
    ) {
        service.register(category, committee, consensus);
    }
}
