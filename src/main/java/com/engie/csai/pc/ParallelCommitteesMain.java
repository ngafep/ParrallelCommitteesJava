package com.engie.csai.pc;

import com.engie.csai.pc.actors.User;
import com.engie.csai.pc.model.*;
import com.engie.csai.pc.model.json.ClientRequestJson;
import com.engie.csai.pc.model.json.ClientRequestsJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

//----------------------------------------------------------------------------
// ParallelCommitteesMain.java
//----------------------------------------------------------------------------

//## ignore
public class ParallelCommitteesMain
{

    public static void main(String[] args) throws Exception
    {
        /**** ************* ****
         **** Administrator ****
         **** ************* ****/
        System.err.println("**** ************* ****");
        System.err.println("**** Administrator ****");
        System.err.println("**** ************* ****\n");

        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);

        int numberOfCat;
        int numberOfQuota;
        /*************************************************************************
         *************************************************************************
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
        float tokensInitForAllCats;
        int comCapacityForAllCats;
        int pqlForAllCats;

        //Reading configuration JSON file:
        final JSONObject config = readNetworkConfigFromJsonFile();
        //End reading config file.

        // Showing network configuration information
        System.err.println("The network has been configured as follows:\n");
        numberOfCat = config.getInt("numberOfCommittees");
        System.out.println("Number of committees: " + numberOfCat);
        numberOfQuota = config.getInt("numberOfQuotaForAllCommittees");
        System.out.println("Number of quota for all committees: " + numberOfQuota);
        tokensInitForAllCats = config.getFloat("numberOfInitialTokensForAllCommittees");
        System.out.println("Number of initial tokens for all committees: " + tokensInitForAllCats);
        comCapacityForAllCats = config.getInt("capacityForAllCommittees");
        System.out.println("Capacity for all committees: " + comCapacityForAllCats);
        pqlForAllCats = config.getInt("PQLForAllCommittees");
        System.out.println("PQL (Peer Queue Limit) for all committees: " + pqlForAllCats + "\n\n");

        // Applying configuration to the network
        String[] catId = new String[numberOfCat];
        int[] comId = new int[numberOfCat];
        int[] quotaInit = new int[numberOfCat];
        float[] tokensInit = new float[numberOfCat];
        int[] comCapacity = new int[numberOfCat];
        int[] pql = new int[numberOfCat];
        int[] freeSeats = new int[numberOfCat];
        applyNetworkConfig(numberOfCat, numberOfQuota, tokensInitForAllCats, comCapacityForAllCats, pqlForAllCats,
                catId, comId, quotaInit, tokensInit, comCapacity, pql, freeSeats);

        // Setting consensus algorithms
        String[] consensusId = new String[4];
        consensusId[0] = "pow";
        consensusId[1] = "raft";
        consensusId[2] = "pbft";

        // Creating network
        Network network = new Network();
        network.setNoCats(numberOfCat);

        // creating committees
        Committee[] com = createCommittees(numberOfCat, catId, comId, quotaInit, tokensInit, comCapacity,
                pql, freeSeats, consensusId[0], network);

        /**** **** ****
         **** User ****
         **** **** ****/
        /*
        All following actions should be done by an instance of the User class.
        This user is a client in PBFT simulator.
         */
        Category[] category = new Category[numberOfCat];
        Committee[] committee = new Committee[numberOfCat];
        int[] targetPeerCreation = new int[numberOfCat], numberOfPeers = new int[numberOfCat];
        KeyPairGenerator[][] keyPeer = new KeyPairGenerator[0][], keyPairGenSign = new KeyPairGenerator[0][];
        StringBuffer[][] publicKeyStrBPeer = new StringBuffer[0][], privateKeyStrBPeer = new StringBuffer[0][];
        String[][] publicKeyStrPeer = new String[0][], addressPeer = new String[0][], privateKeyStrPeer = new String[0][];
        PeerSetting[][] peerSetting = null;
        Peer[][] peer = null;
        boolean waitInQ = false;
        KeyPair[][] kPairSign = null;
        PrivateKey[][] prvKeySign = null;
        PublicKey[][] pubKeySign = null;
        float[] lockedTokens = new float[numberOfCat];
        int peerQsize = 0;
        float lockedTokensForAllCats = 0;
        lockedTokensForAllCats = 2;

        // Initializing number of peers for each committe
        for (int committeeIndex : comId)
        {
            System.out.println("Enter number of peers in committee " + committeeIndex + ": " +
                    "(Maximum possible value is: " + comCapacityForAllCats + ")");
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
        createPeer(numberOfCat, catId, network, com, category, committee, targetPeerCreation,
                keyPeer, publicKeyStrBPeer, publicKeyStrPeer, addressPeer, privateKeyStrBPeer,
                privateKeyStrPeer, peerSetting, peer, waitInQ, keyPairGenSign, kPairSign, prvKeySign,
                pubKeySign, numberOfPeers, lockedTokens);

        /*************************************************************************
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
        int numberOfRequestsInJSON = 0;
        System.out.println("Enter number of Client-Request in each JSON file (for each category): ");
        numberOfRequestsInJSON = sc.nextInt();

        User user = new User();

        ArrayList<String> senderSignature = new ArrayList<String>();
        String[] receiverAddresses = new String[numberOfRequestsInJSON];
        String[][] data = new String[numberOfCat][numberOfRequestsInJSON];
        int randomPeer = 0;
        String[] signatureStr = new String[numberOfRequestsInJSON];
        float[] feeToSpend = null;
        float[] tokenToSend = new float[numberOfRequestsInJSON];
        ClientRequestMessage[][] clientRequestMessage = null;

        int min = 1;
        int max = 10;
        int maxMinDistance = max - min;

        for (int categoryIndex = 0; categoryIndex < numberOfCat; categoryIndex++)
        {
            for (int dataIndex = 0; dataIndex < numberOfRequestsInJSON; dataIndex++)
            {

                StringBuilder dataCreator = new StringBuilder();
                String dataCreatorTemp;

                for (int ch = 0; ch < max; ch++)
                {
                    dataCreatorTemp = "0";
                    dataCreator.append(dataCreatorTemp);
                }

                data[categoryIndex][dataIndex] = dataCreator.toString();
                System.out.println("Committee" + categoryIndex + " Data " + dataIndex + ": " + data[categoryIndex][dataIndex] + "\n");
                /*************************************************************************
                 *************************************************************************
                 * ***********************************************************************
                 * = "A Client-Request has two fields to transfer: 'data' and 'token'.
                 * If some tokens are being transferred, the Client-Request should necessarily have a receiver address,
                 * where 'data' can either include some comments or to be empty.
                 * In any case, the 'fee' is always calculated based on the size of the data.
                 * If the 'data' is empty, the 'fee' has the minimum value.
                 * If Client-Request is sent only in order to transfer some data (any kind of information),
                 * the fields 'token' and 'receiver' are empty.";
                 * ***********************************************************************
                 * ***********************************************************************
                 *************************************************************************/
            }
            min = max + 1;
            max = min + maxMinDistance;
        }

        /*************************************************************************
         *************************************************************************
         * ***********************************************************************
         *
         * This section is used when a Client-Request is sent apart from JSON file,
         * by clicking on the button dedicated for this purpose in the GUI.
         *
         * ***********************************************************************
         * ***********************************************************************
         *************************************************************************/
        /*clientRequest_a_part_from_Json_file(numberOfCat, peer, kPairSign, numberOfPeers, numberOfRequestsInJSON,
                senderSignature, receiverAddresses, data, signatureStr, tokenToSend);*/

        /*
         * Create JSON files (Begin)
         */
        createJSONfilesClientRequests(catId, numberOfRequestsInJSON, data);

        /*
         * Reading all JSON files in parallel by Threads
         * then parsing JSON files to create Client-Requests of Client-Request Class type
         * and then sending Client-Requests through RabbitMQ (Each queue serves a committee.)
         */
        OperationLauncher operationLauncherCat0 = new OperationLauncher(catId[0]);
        operationLauncherCat0.start();
        //Thread.sleep(3000);
        OperationLauncher operationLauncherCat1 = new OperationLauncher(catId[1]);
        operationLauncherCat1.start();
        //Thread.sleep(3000);
        OperationLauncher operationLauncherCat2 = new OperationLauncher(catId[2]);
        operationLauncherCat2.start();

        operationLauncherCat0.join();
        operationLauncherCat1.join();
        operationLauncherCat2.join();

        operationLauncherCat0.interrupt();
        operationLauncherCat1.interrupt();
        operationLauncherCat2.interrupt();





        System.exit(0);


        /*
         * Check if consensus is PoW and then execute PoW in parallel by Threads ...
         *
         */
        //        if (Consensus.getConsAlg() == "pow") {
        //            System.out.println("\n Selected consensus is PoW." + "\n");
        //            for (int c = 0; c < numberOfCat; c++) {
        //                for (int pdtBulk = 0; pdtBulk < numberOfPDTinJSON; pdtBulk++) {
        //                    String winnerThreadIdStr = powByThreads(numberOfCat, catId[c], numberOfPeers[c]);
        //                    int winnerThreadIdInt = Integer.parseInt(winnerThreadIdStr);
        //                    /**
        //                     * Repeat PoW till winner peer in not in the waiting queue
        //                     *
        //                     * !!!! This approach must be changed, as sometimes infinitely a peer in the
        //                     * waiting queue becomes winner of PoW ... !!!!
        //                     */
        //                    // while (peer[c][Integer.parseInt(winnerThreadIdStr)].waiteInQ == true) {
        //                    //   winnerThreadIdStr = powByThreads(numberOfCat, catId[c], numberOfPeers[c]);
        //                    //}
        //                    /**
        //                     * Temporarily we select a random peer as the winner/leader peer ...
        //                     */
        //                    int winnerPeer = ThreadLocalRandom.current().nextInt(0, numberOfPeers[c]);
        //                    while (peer[c][winnerPeer].waiteInQ == true) {
        //                        winnerPeer = ThreadLocalRandom.current().nextInt(0, numberOfPeers[c]);
        //                    }
        //                    peer[c][winnerPeer].updateActualQuota(1);// Leader peer: Reducing quota.
        //                    if (peer[c][winnerPeer].getActualQuota() == 0) {
        //                        peerQsize = committee[c].insertPeerToQueue(peer[c][winnerPeer]);
        //                        peer[c][winnerPeer].waiteInQ = true;
        //                        if (peerQsize > committee[c].getPQL()) {
        //                            peer[c][winnerPeer].resetQuotaNotification(catId[c]);
        //                        }
        //                        committee[c].increaseActualFreeSeats();
        //                    }
        ////					/**
        ////					 * Leader peer processes sent PDT. At this step, we assume all PDTs are done
        ////					 * correctly ...
        ////					 */
        //                    // ERROR ...
        //                    if (pdt[c][pdtBulk] != null) {
        //                        Send.send(pdt[c][pdtBulk].toString(), "Queue" + catId[c].toString());
        //                        Recv.recv("Queue" + catId[c].toString());
        //
        //                        /**
        //                         * Leader peer processes sent PDT. At this step, we assume all PDTs are done
        //                         * correctly ...
        //                         */
        //                        // PoW runs infinitely...
        //                        pdt[c][pdtBulk].validityStatus = true; // We assume that all PDTs are valid
        //                        System.err.println("\n" + "PDT value after confirmation: " + pdt[c][pdtBulk].toString() + "\n");
        //
        //                    }
        //                }
        //            }
        //
        //        } else {
        //            System.out.println("Consensus has not been still implemented.");
        //        }

    } // end main()

    private static void clientRequest_a_part_from_Json_file(int numberOfCat, Peer[][] peer, KeyPair[][] kPairSign, int[] numberOfPeers, int numberOfRequestsInJSON, ArrayList<String> senderSignature, String[] receiverAddresses, String[][] data, String[] signatureStr, float[] tokenToSend) throws Exception
    {
        float[] feeToSpend;
        int randomPeer;
        ClientRequestMessage[][] clientRequestMessage;
        for (int c = 0; c < numberOfCat; c++)
            for (int dataToBeSign = 0; dataToBeSign < numberOfRequestsInJSON; dataToBeSign++)
            {

                /*
                 * Signing data by a random selected peer.
                 */
                randomPeer = ThreadLocalRandom.current().nextInt(0, numberOfPeers[c]);
                System.out.println("Selected peer in Committee " + c + " is Peer: " + randomPeer);
                signatureStr[dataToBeSign] = Sign.sign(data[c][dataToBeSign], kPairSign[c][randomPeer]);
                senderSignature.add(signatureStr[dataToBeSign]);

                /*
                 * Fee calculation
                 */
                feeToSpend = new float[numberOfRequestsInJSON];
                feeToSpend[dataToBeSign] = 0;
                if (data[c][dataToBeSign].length() > 0)
                {
                    feeToSpend[dataToBeSign] = data[c][dataToBeSign].length();
                    peer[c][randomPeer].updateActualTokens(feeToSpend[dataToBeSign]);// Sender peer: Reducing tokens as fee.
                    System.out.println("\n Peer " + peer[c][randomPeer].getAddress()
                            + "\n 's number of tokens after Client-Request is: " + peer[c][randomPeer].getNoTokens());

                } else
                {
                    feeToSpend[dataToBeSign] = 1;
                }

                /*
                 * Creating Client-Request for each data.
                 */
                clientRequestMessage = new ClientRequestMessage[numberOfCat][numberOfRequestsInJSON];

                if (feeToSpend[dataToBeSign] < peer[c][randomPeer].getNoTokens())
                {
                    // tokenToSend = 0;


                    clientRequestMessage[c][dataToBeSign] = new ClientRequestMessage(signatureStr[dataToBeSign], receiverAddresses[dataToBeSign], feeToSpend[dataToBeSign],
                            data[c][dataToBeSign], tokenToSend[dataToBeSign], false,
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
                    System.err.println("\n" + "Client-Request value before conformation: " +
                            clientRequestMessage[c][dataToBeSign].toString() + "\n");

                    /**
                     * Before finalizing creation of Client-Request, it must be checked if sender and receiver
                     * addresses are matched to the size of data transferred by Client-Request. (Not done yet ...)
                     */
                    System.out.println("\n Client-Request is going to be broadcast.");
                    peer[c][randomPeer].broadcastClientRequest(clientRequestMessage[c][dataToBeSign]);

                    /**
                     * RabbitMQ
                     *
                     * Sending Client-Requests from users to committees for processing. We can consider a
                     * separate queue for each committee. The committees and queue therefore work in
                     * parallel.
                     */

                } else
                {
                    System.err
                            .println("Required Fee has not been paid. Client-Request therefore cannot be created nor broadcast.");
                }
            }
    }

    private static void createJSONfilesClientRequests(String[] catId, int numberOfRequestsInJSON, String[][] data) throws IOException
    {
        ObjectMapper mapper;
        mapper = new ObjectMapper();

        int fileIndex = 0;
        for (String cat : catId)
        {
            ClientRequestsJson json = new ClientRequestsJson();
            ClientRequestJson clientRequestJsonElements;
            for (int requestIndex = 0; requestIndex < numberOfRequestsInJSON; requestIndex++)
            {
                clientRequestJsonElements = new ClientRequestJson().data(data[fileIndex][requestIndex]).fee((float) data[fileIndex][requestIndex].length())
                        .senderSignature("senderSignaature").receiverAddress("receiverAddress").tokenToSend(requestIndex);
                json.addRequestsItem(clientRequestJsonElements);
            }
            mapper.writeValue(Paths.get("JsonFiles/clientRequest_" + cat + ".json").toFile(), json);
            fileIndex++;
        }
    }

    private static void createPeer(int numberOfCat, String[] catId, Network network, Committee[] com, Category[] category, Committee[] committee, int[] targetPeerCreation, KeyPairGenerator[][] keyPeer, StringBuffer[][] publicKeyStrBPeer, String[][] publicKeyStrPeer, String[][] addressPeer, StringBuffer[][] privateKeyStrBPeer, String[][] privateKeyStrPeer, PeerSetting[][] peerSetting, Peer[][] peer, boolean waitInQ, KeyPairGenerator[][] keyPairGenSign, KeyPair[][] kPairSign, PrivateKey[][] prvKeySign, PublicKey[][] pubKeySign, int[] numberOfPeers, float[] lockedTokens) throws NoSuchAlgorithmException
    {
        int peerQsize;
        for (int committeeIndex = 0; committeeIndex < numberOfCat; committeeIndex++)
            for (int peerIndex = 0; peerIndex < numberOfPeers[committeeIndex]; peerIndex++)
            {
                keyPairGenSign[committeeIndex][peerIndex] = KeyPairGenerator.getInstance("RSA");
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
                category[committeeIndex] = network.getCategories().get(catId[0]);

                /*
                 * Getting related committee for selected category
                 */
                committee[committeeIndex] = category[committeeIndex].getCommitteeOfCategory();

                /*
                 * Setting peer
                 */
                peerSetting[committeeIndex][peerIndex] = category[committeeIndex].getPeerSettingOfCategory();

                /*
                 * Creating peer
                 */
                System.out.println("Committee " + committeeIndex + " : Peer " + peerIndex + " is going to be created.");

                targetPeerCreation[committeeIndex] = 1;

                peer[committeeIndex][peerIndex] = new Peer(addressPeer[committeeIndex][peerIndex], catId[committeeIndex], peerSetting[committeeIndex][peerIndex].getNoInitTokens(),
                        peerSetting[committeeIndex][peerIndex].getNoInitTokens(), lockedTokens[committeeIndex], peerSetting[committeeIndex][peerIndex].getInitQuota(),
                        peerSetting[committeeIndex][peerIndex].getInitQuota(), privateKeyStrPeer[committeeIndex][peerIndex], publicKeyStrPeer[committeeIndex][peerIndex], committee[committeeIndex],
                        true, null, null, targetPeerCreation[committeeIndex], waitInQ);
                System.out.println("Committee " + committeeIndex + " Peer " + peerIndex + " has been created.");

                /*
                 * Check PoW answer
                 */
                boolean checkPoW;
                checkPoW = PoW.powCheck(catId[committeeIndex], publicKeyStrPeer[committeeIndex][peerIndex], targetPeerCreation[committeeIndex], peer[committeeIndex][peerIndex].powAnswer);
                System.out.flush();
                if (checkPoW)
                {
                    System.out.println("Committee " + committeeIndex + " : PoW has been solved successfully by Peer " + peerIndex);
                    System.out.println("Committee " + committeeIndex + " : Peer " + peerIndex + " PoW answer is: " + peer[committeeIndex][peerIndex].powAnswer);
                    System.out.println("Committee " + committeeIndex + " : Peer " + peerIndex + " address is: " + peer[committeeIndex][peerIndex].getAddress());
                    System.out.println();
                    if (peer[committeeIndex][peerIndex].isProcessor())
                    {
                        if (committee[committeeIndex].getFreeSeats() != 0)
                        {
                            committee[committeeIndex]._addPeerOfCommittee(peer[committeeIndex][peerIndex]);
                            com[committeeIndex].reduceActualFreeSeats();
                        } else
                        {
                            peerQsize = committee[committeeIndex].insertPeerToQueue(peer[committeeIndex][peerIndex]);
                            peer[committeeIndex][peerIndex].waiteInQ = true;
                            if (peerQsize > committee[committeeIndex].getPQL())
                            {
                                peer[committeeIndex][peerIndex].resetQuotaNotification(catId[0]);
                            }
                        }
                    }
                } else
                {
                    System.out.println("Committee " + committeeIndex + " : PoW has not been solved by Peer " + peerIndex);
                }
            }
    }

    private static Committee[] createCommittees(int numberOfCat, String[] catId, int[] comId, int[] quotaInit, float[] tokensInit, int[] comCapacity, int[] pql, int[] freeSeats, String algoConsensus, Network network)
    {
        Committee[] com = new Committee[numberOfCat];

        int begin = 1;
        int end = 10;
        int beginTemp;
        int endBeginDistance = end - begin;
        for (int committeeIndex = 0; committeeIndex < numberOfCat; committeeIndex++)
        {
            /**
             * Creating categories
             */
            network.addCategory(catId[committeeIndex], begin, end);
            //			beginTemp = begin;
            //			begin = end + 1;
            //			end = beginTemp + (end - beginTemp);
            beginTemp = begin;
            begin = end + 1;
            end = beginTemp + endBeginDistance;

            /**
             * Creating committees
             */
            com[committeeIndex] = new Committee(comId[committeeIndex], comCapacity[committeeIndex], pql[committeeIndex], freeSeats[committeeIndex]);
            network.addCommittee(comId[committeeIndex], comCapacity[committeeIndex], pql[committeeIndex], catId[committeeIndex], freeSeats[committeeIndex]);

            /**
             * Setting peer for each category
             */
            network.settingPeersForEachCategory(quotaInit[committeeIndex], tokensInit[committeeIndex], catId[committeeIndex]);

            /**
             * Setting consensus
             */
            network.setConsAlgoForEachCategory(algoConsensus, catId[committeeIndex]);
        }
        return com;
    }

    private static void applyNetworkConfig(int numberOfCat, int numberOfQuota, float tokensInitForAllCats, int comCapacityForAllCats, int pqlForAllCats, String[] catId, int[] comId, int[] quotaInit, float[] tokensInit, int[] comCapacity, int[] pql, int[] freeSeats)
    {
        for (int c = 0; c < numberOfCat; c++)
        {
            catId[c] = "Cat" + c;
            comId[c] = c;
            quotaInit[c] = numberOfQuota;
            tokensInit[c] = tokensInitForAllCats;
            comCapacity[c] = comCapacityForAllCats;
            pql[c] = pqlForAllCats;
            freeSeats[c] = comCapacity[c];
        }
    }

    private static JSONObject readNetworkConfigFromJsonFile() throws IOException
    {
        String contentJSON;
        BufferedReader br = new BufferedReader(new FileReader("NetworkConfiguration.json"));
        try
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            contentJSON = sb.toString();
        } finally
        {
            br.close();
        }

        final JSONObject obj = new JSONObject(contentJSON);
        final JSONArray networkConfig = obj.getJSONArray("networkConfig");
        final JSONObject config = networkConfig.getJSONObject(0);
        return config;
    }

} // end class ParallelCommitteesMain

