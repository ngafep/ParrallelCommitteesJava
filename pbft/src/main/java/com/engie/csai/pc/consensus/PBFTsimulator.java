package com.engie.csai.pc.consensus;

import com.engie.csai.pc.consensus.message.Message;
import com.engie.csai.pc.consensus.replica.ByztReplica;
import com.engie.csai.pc.consensus.replica.Replica;
import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;
import com.engie.csai.pc.core.listener.EndMetrics;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class PBFTsimulator implements ConsensusSimulator {

	private final Set<MessageSubscriber> messageSubscribers = new HashSet<>();
	private MessageSubscriber endSuscriber;
	/**
	 * About the network configuration in this simulator:
	 *
	 * As the committees work in parallel, some parameters such as bandwidth etc.
	 * should be increased based on number of committees? ...
	 * (Each committee is considered to work as a separate subnetwork...)
	 */

	public static final int NUMBER_OF_FAULTY_NODES = 0; // FN -> numberOfFaultyNodes						//恶意节点的数量 //Number of malicious nodes

	/**
     * As in PBFT, the replicas (nodes) will directly send 'Reply' to the client,
	 * it can cause a limitation for 'number of clients' regardless of 'total number of requests',
	 * that is, if there are 5000 total requests, it is different that they are sent from 3 clients or 3000 clients,
	 * due to limitations for connecting all the nodes (validators) with all the clients.
	 * If we intend to respect all the PBFT's rules, then each committee can have a limited number of clients in a given time.
	 *
	 * We tested this simulator with 5 replicas and 30 clients for 5000 requests. It did not face a congestion.
	 * (with 40 clients, congestion occurred).
	 * However, the throughput decreased to 454 tps
	 * (5 replicas/nodes/peers and 20 clients: throughput: 625 tps)
	 * (5 replicas/nodes/peers and 10 clients: throughput: 1000 tps)
	 * (5 replicas/nodes/peers and 7 clients: 1250 tps)
	 * (5 replicas/nodes/peers and 3/5 clients: throughput: 1666 tps). (With 12 replicas also we got the same results.)
	 *
	 * <<If the network functionality is the same as that of this simulator>>, we may probably consider a waiting queue
	 * for the clients, so that in a given time, the system support a limited number of clients e.g. 30 clients
	 * and every client after receiving a reply for a request should go to the bottom of the queue.
	 * In this case, the system serves a group of 30 clients in every given time.
	 *
	 * In general, the configuration of the system can be very different in cases of open and closed network.
	 * For example, if the network is closed and the validators (in PBFT terminology: nodes) are trusted,
	 * we can use a failure fault tolerant consensus such as Paxos and Raft, in which
	 * 1. The throughput can be increased between 150,000 to 200,000 operation per second.
	 * (Ref: http://kth.diva-portal.org/smash/get/diva2:1471222/FULLTEXT01.pdf)
	 * 2. As the client communicates only with the leader, the limitation for number of clients is decreased.
	 *
	 * To be sure about above assumptions, Raft (or Paxos) consensus should be tested as well.
	 * (For closed system and trusted validators.)
	 */

	public static final int INFLIGHT = 2000; 					//最多同时处理多少请求
                                                                //How many requests can be processed simultaneously

	/**
	 * apparently by increasing the number of requests,
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

	public static final int TIMEOUT = 5000;					//节点超时设定(毫秒) //Node timeout setting (milliseconds)
    /**
     * The primary is detected to be faulty by using timeout.
     * When the timeout expires, another replica (peer/node) will be chosen as
     * primary.
     */

	public static final int CLITIMEOUT = 800;				//客户端超时设定(毫秒) //Client timeout setting (milliseconds)

	public static final int BASEDLYBTWRP = 2;				//节点之间的基础网络时延 //Basic network delay between nodes (replicas)

	public static final int DLYRNGBTWRP = 1;				//节点间的网络时延扰动范围 // Network delay range range between nodes

	public static final int BASEDLYBTWRPANDCLI = 10;		//节点与客户端之间的基础网络时延 //Basic network delay between node and client

	public static final int DLYRNGBTWRPANDCLI = 15;			//节点与客户端之间的网络时延扰动范围 //Network delay disturbance range between node and client
	// >>> It means by "node", participating nodes in consensus.

	public static final int BANDWIDTH = 300000;			//节点间网络的额定带宽(bytes)(超过后时延呈指数级上升)
														//Rated bandwidth of the network between nodes (bytes)
														//(the delay increases exponentially after exceeding)

	public static final double FACTOR = 1.005;				//超出额定负载后的指数基数 // ????

	public static final int COLLAPSEDELAY = 10000;			//视为系统崩溃的网络时延 //Network delay considered as a system crash

	public static final boolean SHOWDETAILINFO = false;		//是否显示完整的消息交互过程 // ????

	//消息优先队列（按消息计划被处理的时间戳排序）
	// Message priority queue (sorted by the timestamp when the message is scheduled to be processed)
	/**
	 * >>> In Parallel-Committees, each peer has a queue (FIFO)
	 * to proceed the clients' requests based on their timestamp.
	 */

	private Queue<Message> msgQue = new PriorityQueue<>(Message.cmp);

	public long getInFlyMsgLen()
	{
		return inFlyMsgLen;
	}

	//正在网络中传播的消息的总大小 // The total size of the messages being propagated on the network
	private long inFlyMsgLen = 0;

	//初始化节点之间的基础网络时延以及节点与客户端之间的基础网络时延
	// Initialize the basic network delay between all nodes
	// And the basic network delay between the nodes and the clients.
	private int[][] netDlys;
	private int peerCount; // RN -> numberOfNodes  						//replicas节点的数量(rn) //Number of nodes
	private int requestCount;					//请求消息总数量 //Total number of request messages

	public int getRequestIndex()
	{
		return requestIndex;
	}

	public void increment()
	{
		this.requestIndex++;
	}

	private int requestIndex;

	public PBFTsimulator(){
		requestIndex = 0;
	}
	public static void main (String[] args){
		Thread t1 = createPBFTThread();
		t1.start();
	}

	private static Thread createPBFTThread()
	{
		return new Thread(){
			@Override
			public void run()
			{
				new PBFTsimulator().launch(3, 5, 5000, "committee_0");
			}
		};
	}

	public void launch(int clientCount, int peerCount, int requestCount, String caller) {
		this.peerCount = peerCount;
		this.requestCount = requestCount;
		netDlys = netDlyBtwRpInit(peerCount);
		int[][] netDlysToClis = netDlyBtwRpAndCliInit(peerCount, clientCount);
		int[][] netDlysToNodes = Utils.flipMatrix(netDlysToClis);

		//初始化包含FN个拜占庭意节点的RN个replicas // Initialize RN replicas (nodes) containing FN Byzantine nodes.
		boolean[] byzantines = byztDistriInit(peerCount, NUMBER_OF_FAULTY_NODES); // byzts -> byzantines
//		boolean[] byzantines = {true, false, false, false, false, false, true};
		Replica[] replicas = new Replica[peerCount]; // reps -> replicas
		for(int i = 0; i < peerCount; i++) {
			if(byzantines[i]) {
				/**
				 * Even in case of using ByztReplica rather than Replica, the simulator
				 * does not consider the Byzantine nodes as we tested it when 'number of byzantines'
				 * was equal to the 'number of nodes', and it runs but it should not ...
				 * Probably, it should be considered another 'if' condition ...
				 */
				replicas[i] = new ByztReplica(i, netDlys[i], netDlysToClis[i], this);
			}else {
				replicas[i] = new Replica(i, netDlys[i], netDlysToClis[i], this);
			}
		}

		//初始化CN个客户端 // Initialize CN clients
		Client[] clients = new Client[clientCount]; // clis -> clients
		for(int i = 0; i < clientCount; i++) {
			//客户端的编号设置为负数 // The client's number is set to a negative number (??)
			clients[i] = new Client(Client.getCliId(i), netDlysToNodes[i], this);
		}

		//初始随机发送INFLIGHT个请求消息 // Initially randomly send INFLIGHT request messages
		Random rand = new Random(555);
		int requestNums = Math.min(INFLIGHT, requestCount);
//		List<Integer> listInt = new ArrayList<>();
		for(int i = 0; i < Math.min(INFLIGHT, requestCount); i++) {
			clients[rand.nextInt(clientCount)].sendRequest(0);
		}
//		for (Integer x : ProgressBar.wrap(
//			listInt, "TaskName")) {
//			clients[rand.nextInt(clientCount)].sendRequest(0);
//			requestNums++;
//		}
//
//		ProgressBar.wrap(
//			IntStream.range(0, Math.min(INFLIGHT, requestCount)).sequential(), "Handling messages").forEach(i -> {
//			clients[rand.nextInt(clientCount)].sendRequest(0);
//			try {
//				Thread.sleep(1000);
//			} catch (
//				InterruptedException e) {
//				throw new RuntimeException(
//					e);
//			}
//		});

		long timestamp = 0;
		//消息处理 // Message processing
		/**
		 * A message includes 'operation (request)', 'sender (client)' and 'receiver (primary or leader)'.
		 */
//		int ttt = 0;
		while(!msgQue.isEmpty()) {
			Message msg = msgQue.poll();
			switch(msg.msgType) {
			case Message.REPLY:
			case Message.CLITIMEOUT:
				clients[Client.getCliArrayIndex(msg.rcvId)].msgProcess(msg);
				break;
			default:
				replicas[msg.rcvId].msgProcess(msg);
			}
			//如果还未达到稳定状态的request消息小于INFLIGHT，随机选择一个客户端发送请求消息
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
			if(requestNums - getStableRequestNum(clients) < INFLIGHT && requestNums < requestCount) {
				clients[rand.nextInt(clientCount)].sendRequest(msg.rcvtime);
				requestNums++;
			}
			inFlyMsgLen -= msg.len;
			timestamp += msg.rcvtime;
//			if(getNetDelay(inFlyMsgLen, 0) > COLLAPSEDELAY ) {
//				/*System.out.println("[Error]网络消息总负载"+inFlyMsgLen
//						+"B,网络传播时延超过"+COLLAPSEDELAY/1000
//						+"秒，系统已严重拥堵，不可用！");*/
//				/**
//				 * Here, the application should be paused rather than being stopped
//				 * in order to complete the process of current requests.
//				 * The rest of requests should wait in a queue till the process of current requests would be completed.
//				 * And then application will start again to process the rest of requests ...
//				 *
//				 */
//				System.out.println("[Error]Total network message load: "+inFlyMsgLen
//						+"Bytes,Network propagation delay exceeds "+COLLAPSEDELAY/1000
//						+"seconds and system is heavily congested and unavailable!");
//				break; // Break probably should be removed.
//			}
		}
		long totalTime = 0;
		long totalStableMsg = 0;
		for(int i = 0; i < clientCount; i++) {
			totalTime += clients[i].accTime;
			totalStableMsg += clients[i].stableMsgNum();
		}
		double tps = getStableRequestNum(clients)/(double)(timestamp/1000.0);
		final var endMessage = "[The end]The average message confirmation time: " + totalTime / totalStableMsg
				+ " millisecond. The message throughput: " + tps + "tps";
//		System.out.println(endMessage);

		notify(EndMetrics.builder().throughput(tps).totalStableMessage(totalStableMsg).totalTime(totalTime).timestamp(timestamp).categoryId(caller).build());
	}

	/**
	 * 随机初始化replicas节点之间的基础网络传输延迟 // Randomly initialize the basic network transmission delay between replicas nodes
	 * @param n 表示节点总数 // n: Indicates the total number of nodes
	 * @return	返回节点之间的基础网络传输延迟数组 // Returns the underlying network transmission delay array between nodes
	 */
	private static int[][] netDlyBtwRpInit(int n){
		int[][] ltcs = new int[n][n];
		Random rand = new Random(999);
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < n; ++j)
				if(i < j && ltcs[i][j] == 0) {
					ltcs[i][j] = BASEDLYBTWRP + rand.nextInt(DLYRNGBTWRP);
					ltcs[j][i] = ltcs[i][j];
				}
		return ltcs;
	}

	/**
     * 随机初始化客户端与各节点之间的基础网络传输延迟 // Randomly initialize the basic network transmission delay between the client and each node
     * @param n 表示节点数量 // n: Indicates the number of nodes
     * @param m 表示客户端数量 // m: Indicates the number of clients
     * @return 返回客户端与各节点之间的基础网络传输延迟 // Returns the basic network transmission delay between the client and each node
     */
	private static int[][] netDlyBtwRpAndCliInit(int n, int m){
		int[][] ltcs = new int[n][m];
		Random rand = new Random(666);
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				ltcs[i][j] = BASEDLYBTWRPANDCLI + rand.nextInt(DLYRNGBTWRPANDCLI);
		return ltcs;
	}

	/**
	 * 随机初始化replicas节点的拜占庭标签 // Randomly initialize the Byzantine label of the replicas node
	 * @param n	节点数量 // Number of nodes
	 * @param f	拜占庭节点数量 // Number of Byzantine nodes
	 * @return	返回拜占庭标签数组（true为拜占庭节点，false为诚实节点）// Returns an array of Byzantine tags
	 * (true is a Byzantine node, false is an honest node)
	 * Labeling as 'Byzantine' is done by two approaches:
	 * Randomly: by this method.
	 * Manually by: boolean[] byzts = {true, false, false, false, false, false, true};
	 */
	private static boolean[] byztDistriInit(int n, int f) {
		boolean[] byzt = new boolean[n];
		Random rand = new Random(111);
		while(f > 0) {
			int i = rand.nextInt(n);
			if(!byzt[i]) {
				byzt[i] = true;
				--f;
			}
		}
		return byzt;
	}

	public void sendMsg(Message msg, String tag) {
		msg.print(tag);
		msgQue.add(msg);
		inFlyMsgLen += msg.len;
		/**
		 * Maybe the solution for congestion should be implemented here in such a way that
		 * each time after adding one unit to inFlyMsgLen (on the fly message length) it should be
		 * checked if it does not exceed the permitted limited value.
		 */
	}

	public void sendMsgToOthers(Message msg, int id, String tag) {
		for(int i = 0; i < peerCount; i++) {
			if(i != id) {
				Message m = msg.copy(i, msg.rcvtime + netDlys[id][i]);
				sendMsg(m, tag);
			}
		}
	}

	public static int getNetDelay(long inFlyMsgLen, int basedelay) {
		if(inFlyMsgLen < BANDWIDTH) {
			return basedelay;
		}else {
			return (int)Math.pow(FACTOR, inFlyMsgLen - BANDWIDTH) + basedelay;
		}
	}

	public static int getStableRequestNum(Client[] clients) { // clis -> clients
		int num = 0;
		for(int i = 0; i < clients.length; i++) {
			num += clients[i].stableMsgNum();
		}
		return num;
	}

	public int getPeerCount()
	{
		return peerCount;
	}

	public int getRequestCount()
	{
		return requestCount;
	}

	public void subscribe(MessageSubscriber committee)
	{
		messageSubscribers.add(committee);
	}

	public void subscribeEndPbft(MessageSubscriber endSuscriber)
	{
		this.endSuscriber = endSuscriber;
	}
	public void notify(EndMetrics endMetrics)
	{
		endSuscriber.onMsgReceived(endMetrics);
	}

	public void notify(String msg){
		messageSubscribers.forEach(listener -> listener.onMsgReceived(msg));
	}
}
