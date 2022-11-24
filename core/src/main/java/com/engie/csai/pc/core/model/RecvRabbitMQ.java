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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class RecvRabbitMQ {
    private static Map<String, Integer> numberOfClientsPerCategory = new ConcurrentHashMap<>();
    private static Map<String, Integer> numberOfPeersPerCategory = new ConcurrentHashMap<>();
    private static Map<String, Integer> numberOfRequestsPerCategory = new ConcurrentHashMap<>();

    public static void standbyForReceiveMessages(
        String category,
        String queueName,
        Committee committee,
        CommitteeService service,
        ConsensusSimulator consensus
    )
        throws IOException, TimeoutException {
        boolean autoAck = false;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);

        channel.basicConsume(queueName, autoAck, "myConsumerTag", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            )
                throws IOException {
                ClientRequestMessage[] clientRequestMessages = new ClientRequestMessage[0];
                try {
                    clientRequestMessages = launchOperationForOneCategory(new String(body,
                        StandardCharsets.UTF_8), committee.getProcessorNodes()
                        .size());
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                if(clientRequestMessages != null && clientRequestMessages.length>0) {
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
        });
    }

    private static ClientRequestMessage[] launchOperationForOneCategory(
        String catId,
        int peerCount
    )
        throws IOException, TimeoutException {
        ClientRequestsJson jsonFile = new JsonReaderInParallel().parseJsonFile(catId);
        if (jsonFile == null) {
//            System.out.println("not file found for : " + catId);
            return new ClientRequestMessage[0];
        }

        ClientRequestMessage[] clientRequestExtractedFromJson = new ClientRequestMessage[jsonFile.getRequests()
            .size()]; // -> Client-Request[*category index*][*Client-Request index in that category*]
        int requestIndex = 0;
        List<ClientRequestJson> requestJsonList = jsonFile.getRequests();
        Set<String> clientsInJson = requestJsonList.stream()
            .map(req -> req.getSenderSignature())
            .collect(Collectors.toSet());
        RecvRabbitMQ.addNumberOfClientsForCategory(catId, clientsInJson.size());
        RecvRabbitMQ.addNumberOfPeersForCategory(catId, peerCount);
        RecvRabbitMQ.addNumberOfRequestsForCategory(catId, jsonFile.getRequests()
            .size());
        for (ClientRequestJson requestJson : requestJsonList) {
            assert false;
            clientRequestExtractedFromJson[requestIndex] = new ClientRequestMessage(requestJson.getSenderSignature(), requestJson.getReceiverAddress(), requestJson.getFee(), requestJson.getData(), requestJson.getTokenToSend(), false, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
            /**
             * Inserting Client-Requests in related queue of RibbitMQ
             */
            // SendRabbitMQ.send(clientRequestExtractedFromJson[requestIndex].toString(), "Queue" + catId);
            requestIndex++;
            /**
             * Receiving Client-Requests by committees from related RabbitMQ queue
             */
            /**
             * Launching PBFT consensus
             */
            // launch(Client-Request, number of nodes) // number of clients and number of requests should be removed from PBFT simulator,
            // as it does not make sense when there are always new requests (Client-Requests) from the clients ...
            // If number of clients affects PBFT performance, we should consider a limitation for having a limited number of clients in a given time.
            // This has been explained more in PBFT similator ...
            //
            // Another point is that in the protocol code, we need to define how to have a determined number of users (clients) in a given time.
            // We then consider an entry parameter determining the user (client) who sends the Client-Request
            /*PBFTsimulator pbfTsimulator = new PBFTsimulator();
            pbfTsimulator.launch(
                        *//*ClientRequestMessage request,
                        int numberOfCommitteeMember,
                        int numberOfRequests,
                        int numberOfUsers*//*);*/
            // launch(arguments:
            // 1. ClientRequestMessage[] request, -> message in PBFT simulator,
            //              (in each request, senderSeagnature is the client who has sent that request.)
            //              (Converting ClientRequestMessage to Class Message (in PBFT) here, and sending them as Class Message?)
            // 2. int numberOfCommitteeMember, -> numberOfNodes in PBFT simulator,
            // 3. int numberOfRequests -> REQNUM (number of requests) in PBFT simulator,
            // 4. int numberOfUsers -> numberOfClients in PBFT simulator);
            //
            // >>> If we intend to send a single Client-Request in each call of launch(), (it seems to be impossible ...)
            // then numberOfRequests in PBFT simulator should be deleted.
            //

            //            receiveRequestLaunchPBFT(catId);
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
        ConsensusSimulator consensus){
        service.register(category, committee ,consensus);
    }

    // private final static String QUEUE_NAME = "hello";

    /*public static void recv(String queueName) throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [...] Waiting for messages. To exit press CTRL+C" + "\n" + "\n");

        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received '" + message + "'" + "\n" + "\n");
            *//**
     * message should be returned to be used in PBFT launcher.
     *//*
            PBFTsimulator pbfTsimulator = new PBFTsimulator();
            pbfTsimulator.launch(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag ->
        {
        });
    }*/
}
