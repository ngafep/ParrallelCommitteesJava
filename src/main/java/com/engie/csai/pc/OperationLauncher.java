package com.engie.csai.pc;

import com.engie.csai.pc.model.ClientRequestMessage;
import com.engie.csai.pc.model.JsonReaderInParallel;
import com.engie.csai.pc.model.RecvRabbitMQ;
import com.engie.csai.pc.model.SendRabbitMQ;
import com.engie.csai.pc.model.json.ClientRequestJson;
import com.engie.csai.pc.model.json.ClientRequestsJson;
import com.engie.csai.pc.pbftSimulator.PBFTsimulator;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class OperationLauncher extends Thread
{

    private final String catId;

    public OperationLauncher(String catId)
    {
        this.catId = catId;
    }


    @Override
    public void run()
    {
        try
        {
            launchOperationForOneCategory(catId);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void launchOperationForOneCategory(String catId) throws Exception
    {
        JsonReaderInParallel jsonReaderInParallel = new JsonReaderInParallel();
        ClientRequestsJson jsonFile = jsonReaderInParallel.parseJsonFile(catId);

        ClientRequestMessage[] clientRequestExtractedFromJson = new ClientRequestMessage[jsonFile.getRequests().size()]; // -> Client-Request[*category index*][*Client-Request index in that category*]
        int requestIndex = 0;
        List<ClientRequestJson> requestJsonList = jsonFile.getRequests();
        for (ClientRequestJson requestJson : requestJsonList)
        {
            assert false;
            clientRequestExtractedFromJson[requestIndex] =
                    new ClientRequestMessage(requestJson.getSenderSignature(), requestJson.getReceiverAddress(), requestJson.getFee(),
                            requestJson.getData(), requestJson.getTokenToSend(), false,
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
            requestIndex++;
            /**
             * Inserting Client-Requests in related queue of RibbitMQ
             */
            SendRabbitMQ.send(clientRequestExtractedFromJson.toString(), "Queue" + catId);
            /**
             * Receiving Client-Requests by committees from related RabbitMQ queue
             */
            //receiveRequestLaunchPBFT(catId);
            /**
             * Before starting the consensus, it should be considered to put the received Client-Requests in the Peers' Client-Request queue.
             * Each peer in the committee before starting to process the received Client-Request, checks for knowing if the number of existing Client-Requests
             * does exceed the authorized number, that is, there are apparently some limitations for the bandwidth etc. So, the peers (is PBFT: nodes/replicas)
             * everytime before starting a consensus, they must check the network limitation to be sure whether the limitations have not yet exceeded.
             * If so, they start to process the oldest received Client-Request (from the FiFo queue) ... WE EXTEND THIS SECTION ...
             * An error in current code is that all the fies are reading in parallel, but waiting for finish all the files and then creating Client-Requests ...
             * This approach is not correct (because in this case, reading the file in parallel is NOT util)
             * So, at the same time the files are reading in parallel, the Client-Requests also should be created and sent to the committees,
             * otherwise, why do we need to read the files in parallel ?!
             *
             *
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

            receiveRequestLaunchPBFT(catId);
        }
    }

    private static void receiveRequestLaunchPBFT(String catId) throws Exception
    {
        RecvRabbitMQ.recv("Queue" + catId);

        /*PBFTsimulator pbfTsimulator = new PBFTsimulator();
        pbfTsimulator.launch();*/
    }
}
