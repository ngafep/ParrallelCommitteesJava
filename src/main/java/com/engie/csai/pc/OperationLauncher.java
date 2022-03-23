package com.engie.csai.pc;

import com.engie.csai.pc.model.ClientRequestMessage;
import com.engie.csai.pc.model.JsonReaderInParallel;
import com.engie.csai.pc.model.RecvRabbitMQ;
import com.engie.csai.pc.model.SendRabbitMQ;
import com.engie.csai.pc.model.json.ClientRequestJson;
import com.engie.csai.pc.model.json.ClientRequestsJson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OperationLauncher extends Thread
{

    private final String catId;
    private final int numberOfPeer;
    private final int nbRequests;

    public OperationLauncher(String catId, int numberOfPeer, int nbRequests)
    {
        this.catId = catId;
        this.numberOfPeer = numberOfPeer;
        this.nbRequests = nbRequests;
    }


    @Override
    public void run()
    {
        try
        {
            launchOperationForOneCategoryWithoutRabbitMQ();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void launchOperationForOneCategoryWithoutRabbitMQ() throws Exception{
        RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId);

        SendRabbitMQ.send(catId, "Queue" + catId);


    }

    private void launchOperationForOneCategory() throws Exception
    {
        ClientRequestsJson jsonFile = new JsonReaderInParallel().parseJsonFile(catId);
        if(jsonFile == null){
            System.out.println("not file found for : " + catId);
            return;
        }

        ClientRequestMessage[] clientRequestExtractedFromJson = new ClientRequestMessage[jsonFile.getRequests().size()]; // -> Client-Request[*category index*][*Client-Request index in that category*]
        int requestIndex = 0;
        List<ClientRequestJson> requestJsonList = jsonFile.getRequests();
        Set<String> clientsInJson = requestJsonList.stream().map(req -> req.getSenderSignature()).collect(Collectors.toSet());
        RecvRabbitMQ.addNumberOfClientsForCategory(catId, clientsInJson.size());
        RecvRabbitMQ.addNumberOfPeersForCategory(catId, numberOfPeer);
        RecvRabbitMQ.addNumberOfRequestsForCategory(catId, nbRequests);
        RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId);
        for (ClientRequestJson requestJson : requestJsonList)
        {
            assert false;
            clientRequestExtractedFromJson[requestIndex] =
                    new ClientRequestMessage(requestJson.getSenderSignature(), requestJson.getReceiverAddress(), requestJson.getFee(),
                            requestJson.getData(), requestJson.getTokenToSend(), false,
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
            /**
             * Inserting Client-Requests in related queue of RibbitMQ
             */
           // SendRabbitMQ.send(clientRequestExtractedFromJson[requestIndex].toString(), "Queue" + catId);
            SendRabbitMQ.send(catId, "Queue" + catId);
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
    }

    /*private static void receiveRequestLaunchPBFT(String catId) throws Exception
    {
        RecvRabbitMQ.recv("Queue" + catId);

        *//*PBFTsimulator pbfTsimulator = new PBFTsimulator();
        pbfTsimulator.launch();*//*
    }*/
}
