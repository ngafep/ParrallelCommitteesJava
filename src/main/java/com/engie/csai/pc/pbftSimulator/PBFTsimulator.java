package com.engie.csai.pc.pbftSimulator;

import com.engie.csai.pc.pbftSimulator.message.Message;
import com.engie.csai.pc.pbftSimulator.replica.ByztReplica;
import com.engie.csai.pc.pbftSimulator.replica.Replica;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Terminology in PBFT:
 * <p>
 * Node : Machine running all the components necessary for a working request message
 * Server : Synonym for node.
 * Replica : Synonym for node.
 * Primary : (Leader) Node in charge of making the final consensus decisions.
 * Client : Machine that sends requests to and receives replies from the network of nodes.
 * Checkpoint : Point in time when logs can get garbage collected.
 * Checkpoint period : How many client requests between each checkpoint.
 * Message : Including Client-Request, Pre-prepared, Prepared, Commit, Checkpoint, ViewChange and Reply messages.
 * Low water mark : The sequence number of the last stable checkpoint.
 * High water mark : Low water mark plus the desired maximum size of nodes' message logs.
 * View : The scope of PBFT when the current primary is in charge.
 * The view changes when the primary is deemed faulty.
 * n : The total number of nodes in the network.
 * f : The maximum number of faulty nodes.
 * v : The current view number.
 * p : The primary server number; p = v mod n.
 */

public class PBFTsimulator
{

    public static final int numberOfNodes = 7; // RN -> numberOfNodes  						 //Number of nodes

    public static final int numberOfFaultyNodes = 2; // FN -> numberOfFaultyNodes						//Number of malicious nodes

    public static final int numberOfClients = 3; // CN -> numberOfClients						 //Number of clients
    /**
     * In order to initialize 'numberOfClients' an approach could be counting
     * the number of identical senders in Parallel-committees
     * i.e. we do not consider repeated senders.
     */

    /**
     * As in PBFT, the replicas (nodes) will directly send 'Reply' to the client,
     * it can cause a limitation for 'number of clients' regardless of 'total number of requests',
     * that is, if there are 5000 total requests, it is different that they are sent from 3 clients or 3000 clients,
     * due to limitations for connecting all the nodes (validators) with all the clients.
     * If we intend to respect all the PBFT's rules, then each committee can have a limited number of clients in a given time.
     * <p>
     * We tested this simulator with 5 replicas and 30 clients. It did not face a congestion.
     * (with 40 clients, congestion occurred).
     * However, the throughput decreased to 454 tps
     * (5 replicas and 20 clients: throughput: 625 tps)
     * (5 replicas and 10 clients: throughput: 1000 tps)
     * (5 replicas and 7 clients: 1250 tps)
     * (5 replicas and 3/5 clients: throughput: 1666 tps).
     * <p>
     * <<If the network functionality is the same as that of this simulator>>, we may probably consider a waiting queue
     * for the clients, so that in a given time, the system support a limited number of clients e.g. 30 clients
     * and every client after receiving a reply for a request should go to the bottom of the queue.
     * In this case, the system serves a group of 30 clients in every given time.
     * <p>
     * In general, the configuration of the system can be very different in cases of open and closed network.
     * For example, if the network is closed and the validators (in PBFT terminology: nodes) are trusted,
     * we can use a failure fault tolerant consensus such as Paxos and Raft, in which
     * 1. The throughput can be increased between 150,000 to 200,000 operation per second.
     * (Ref: http://kth.diva-portal.org/smash/get/diva2:1471222/FULLTEXT01.pdf)
     * 2. As the client communicates only with the leader, the limitation for number of clients is decreased.
     * <p>
     * To be sure about above assumptions, Raft (or Paxos) consensus should be tested as well.
     * (For closed system and trusted validators.)
     */

    public static final int INFLIGHT = 2000;
    //How many requests can be processed simultaneously

    public static final int RequestNumber = 5; // REQNUM -> RequestNumbers
    // Total number of request messages
    /**
     * Apparently by increasing the number of requests,
     * throughput is increased as well.
     * The maximum number of requests we tested is 30,000 and the throughput reached to 2,307 tps in maximum.
     * The hyperledger paper also stated that it can process about 3,000 tps.
     * Paxos and Raft can reach to 150,000 - 200,000
     *
     * For the Parallel-Committees protocol, the number of request as a constant
     * cannot make sense, and it should be considered infinitely,
     * that is, as the system is running, new requests are arriving from the clients.
     */

    /**
     * REQNUM determines there are how many client requests.
     * It is set to 5000.
     * In general, it should be equal to the number of PDTs sent to each committee.
     * Maybe it is better not to be a constant ...
     */

    public static final int TimeOut = 500; // TIMEOUT -> TimeOut
    // Node timeout setting (milliseconds)
    /**
     * The primary is detected to be faulty by using timeout.
     * When the timeout expires, another replica (peer/node) will be chosen as
     * primary.
     */

    public static final int ClientTimeOut = 800; // CLITIMEOUT -> ClientTimeOut
    // Client timeout setting (milliseconds)

    public static final int DelayBaseNodes = 2; // BASEDLYBTWRP -> DelayBaseNodes
    // Basic network delay between nodes (replicas)

    public static final int DelayRangeNodes = 1; // DLYRNGBTWRP -> DelayRangeNodes
    // Network delay range between nodes

    public static final int DelayBaseNodeClient = 10; // BASEDLYBTWRPANDCLI -> DelayBaseNodeClient
    // Basic network delay between node and client

    public static final int DelayRangeNodeClient = 15;  // DLYRNGBTWRPANDCLI -> DelayRangeNodeClient
    // Network delay disturbance range between node and client
    // >>> It means by "node", participating nodes in consensus.

    public static final int BANDWIDTH = 300000;
    //Rated bandwidth of the network between nodes (bytes)
    //(the delay increases exponentially after exceeding)

    public static final double FACTOR = 1.005;

    public static final int COLLAPSEDELAY = 10000;            //Network delay considered as a system crash

    public static final boolean SHOWDETAILINFO = false;


    // Message priority queue (sorted by the timestamp when the message is scheduled to be processed)
    /**
     * >>> In Parallel-Committees, each committee has a queue (FIFO)
     * to process the client requests based on their timestamp.
     */

    public Queue<Message> msgQue = new PriorityQueue<>(Message.cmp);
     // The total size of the messages being propagated on the network
    public static long inFlyMsgLen = 0;


    // Initialize the basic network delay between all nodes
    // And the basic network delay between the nodes and the clients.
    public static int[][] netDlys = netDlyBtwRpInit(numberOfNodes);

    public static int[][] netDlysToClis = netDlyBtwRpAndCliInit(numberOfNodes, numberOfClients);

    public static int[][] netDlysToNodes = Utils.flipMatrix(netDlysToClis);

    public void launch(String messageClientRequest)
    {

        // Initialize RN replicas (nodes) containing FN Byzantine nodes.
        boolean[] byzantines = byztDistriInit(numberOfNodes, numberOfFaultyNodes); // byzts -> byzantines
        // boolean[] byzantines = {true, false, false, false, false, false, true};
        Replica[] replicas = new Replica[numberOfNodes]; // reps -> replicas
        for (int i = 0; i < numberOfNodes; i++)
        {
            if (byzantines[i])
            {
                /**
                 * Even in case of using ByztReplica rather than Replica, the simulator
                 * does not consider the Byzantine nodes as we tested it when 'number of byzantines'
                 * was equal to the 'number of nodes', and it runs, but it should not ...
                 * Probably, it should be considered another 'if' condition ...
                 */
                replicas[i] = new ByztReplica(i, netDlys[i], netDlysToClis[i]);
            } else
            {
                Replica replica = new Replica(i, netDlys[i], netDlysToClis[i]);
                replica.setTimer(this, replica.lastRepNum + 1, 0);
                replicas[i] = replica;
            }
        }


        // Initialize CN clients
        Client[] clients = new Client[numberOfClients]; // clis -> clients
        for (int i = 0; i < numberOfClients; i++)
        {

            // The client's number is set to a negative number (??)
            clients[i] = new Client(Client.getCliId(i), netDlysToNodes[i]);
        }


        // Initially randomly send INFLIGHT request messages
        Random rand = new Random(555);
        int requestNums = 0;
        for (int i = 0; i < Math.min(INFLIGHT, RequestNumber); i++)
        {
            clients[rand.nextInt(numberOfClients)].sendRequest(this, 0, messageClientRequest);
            requestNums++;
        }

        long timestamp = 0;
        // Message processing
        /**
         * A message includes 'operation (request)', 'sender (client)' and 'receiver (primary or leader)'.
         */
        //		int ttt = 0;
        while (!msgQue.isEmpty())
        {
            Message msg = msgQue.poll();
            switch (msg.msgType)
            {
                case Message.REPLY:
                case Message.CLITIMEOUT:
                    clients[Client.getCliArrayIndex(msg.rcvId)].msgProcess(this, msg);
                    break;
                default:
                    replicas[msg.rcvId].msgProcess(this, msg);
            }

            // If the request message that has not reached a stable state is smaller than INFLIGHT,
            // a client is randomly selected to send the request message

            /**
             * About 'client' that is mentioned in this code & the client in P-C:
             *
             * In P-C, a peer can be validator (in this code: 'node') or user (in this code: client),
             * a user is not member of a committee, but its address category must be matched to the
             * committee to which, it sends PDT (commonly called 'transaction' in other protocols).
             * This accordance is detectable using the size of the data in the user's PDT;
             * for example, if a user sends a PDT with data size of 'x',
             * the 'x' must be in the range of supported by the receiver committee;
             * otherwise, the PDT is rejected by the participating nodes (validators) in the consensus.
             */
            if (requestNums - getStableRequestNum(clients) < INFLIGHT && requestNums < RequestNumber)
            {
                clients[rand.nextInt(numberOfClients)].sendRequest(this, msg.rcvtime, messageClientRequest);
                requestNums++;
            }
            inFlyMsgLen -= msg.len;
            timestamp = msg.rcvtime;
            if (getNetDelay(inFlyMsgLen, 0) > COLLAPSEDELAY)
            {
                System.out.println("【Error】Total network message load: " + inFlyMsgLen
                        + "Bytes,Network propagation delay exceeds " + COLLAPSEDELAY / 1000
                        + "seconds and system is heavily congested and unavailable!");
                break;
            }
        }
        long totalTime = 0;
        long totalStableMsg = 0;
        for (int i = 0; i < numberOfClients; i++)
        {
            totalTime += clients[i].accTime;
            totalStableMsg += clients[i].stableMsgNum();
        }
        double tps = getStableRequestNum(clients) / (double) (timestamp / 1000);
        System.out.println("【The end】The average message confirmation time: " + totalTime / totalStableMsg
                + " millisecond. The message throughput: " + tps + " tps");
    }

    /**
     * // Randomly initialize the basic network transmission delay between replicas nodes
     *
     * @param n  // n: Indicates the total number of nodes
     * @return  // Returns the underlying network transmission delay array between nodes
     */
    public static int[][] netDlyBtwRpInit(int n)
    {
        int[][] ltcs = new int[n][n]; // ltcs: latency (probably)
        Random rand = new Random(999);
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                if (i < j && ltcs[i][j] == 0)
                {
                    ltcs[i][j] = DelayBaseNodes + rand.nextInt(DelayRangeNodes);
                    ltcs[j][i] = ltcs[i][j];
                }
        return ltcs;
    }

    /**
     *  // Randomly initialize the basic network transmission delay between the client and each node
     *
     * @param n  // n: Indicates the number of nodes
     * @param m  // m: Indicates the number of clients
     * @return  // Returns the basic network transmission delay between the client and each node
     */
    public static int[][] netDlyBtwRpAndCliInit(int n, int m)
    {
        int[][] ltcs = new int[n][m];
        Random rand = new Random(666);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                ltcs[i][j] = DelayBaseNodeClient + rand.nextInt(DelayRangeNodeClient);
        return ltcs;
    }

    /**
     *  // Randomly initialize the Byzantine label of the replicas node
     *
     * @param n  // Number of nodes
     * @param f  // Number of Byzantine nodes
     * @return // Returns an array of Byzantine tags
     * (true is a Byzantine node, false is an honest node)
     * Labeling as 'Byzantine' is done by two approaches:
     * Randomly: by this method.
     * Manually by: boolean[] byzts = {true, false, false, false, false, false, true};
     */
    public static boolean[] byztDistriInit(int n, int f)
    {
        boolean[] byzt = new boolean[n];
        Random rand = new Random(111);
        while (f > 0)
        {
            int i = rand.nextInt(n);
            if (!byzt[i])
            {
                byzt[i] = true;
                --f;
            }
        }
        return byzt;
    }

    public void sendMsg(Message msg, String tag)
    {
        msg.print(tag);
        msgQue.add(msg);
        inFlyMsgLen += msg.len;
    }

    public void sendMsgToOthers(Message msg, int id, String tag)
    {
        for (int i = 0; i < numberOfNodes; i++)
        {
            if (i != id)
            {
                Message m = msg.copy(i, msg.rcvtime + netDlys[id][i]);
                sendMsg(m, tag);
            }
        }
    }

    public void sendMsgToOthers(Set<Message> msgSet, int id, String tag)
    {
        if (msgSet == null)
        {
            return;
        }
        for (Message msg : msgSet)
        {
            sendMsgToOthers(msg, id, tag);
        }
    }

    public int getNetDelay(long inFlyMsgLen, int basedelay)
    {
        if (inFlyMsgLen < BANDWIDTH)
        {
            return basedelay;
        } else
        {
            return (int) Math.pow(FACTOR, inFlyMsgLen - BANDWIDTH) + basedelay;
        }
    }

    public int getStableRequestNum(Client[] clients)
    { // clis -> clients
        int num = 0;
        for (int i = 0; i < clients.length; i++)
        {
            num += clients[i].stableMsgNum();
        }
        return num;
    }
}