package com.engie.csai.pc.pbftSimulatorold;

import com.engie.csai.pc.pbftSimulatorold.message.Message;
import com.engie.csai.pc.pbftSimulatorold.message.CliTimeOutMsg;
import com.engie.csai.pc.pbftSimulatorold.message.ReplyMsg;
import com.engie.csai.pc.pbftSimulatorold.message.RequestMsg;

import java.util.HashMap;
import java.util.Map;

public class Client {
	/**
	 * User peer/node in P-C
	 */
	
	public static final int PROCESSING = 0;		//没有收到f+1个reply // Did not receive f+1 (identical) reply
	/**
	 * About f+1 replies:
	 *
	 * The client accepts the result after receiving f+1 identical/same replies.
	 * For example, if there is 1 faulty node, 2 identical replies (and not in general 2 replies) must be received by client.
	 */
	
	public static final int STABLE = 1;			//已经收到了f+1个reply // f+1 replies have been received.
	
	public int clientId; // id -> clientId								//客户端编号 // Client (User) ID

	public int viewNumber; // v -> viewNumber								//视图编号 // View number (View index)
	
	public Map<Long, Integer> reqStats;			//request请求状态 // request status
	
	public Map<Long, Message> reqMsgs;			//request消息（删除已经达到stable状态的request消息）
												// request message (delete the request message that has reached the stable state) ??
	
	public Map<Long, Map<Integer, Message>> repMsgs;	//reply消息（删除已经达到stable状态的reply消息）
														// reply message (delete the reply message that has reached the stable state) ??
	
	public long accTime;						//累积确认时间 // Accumulative confirmation time ??

	public int netDlys[];						//与各节点的基础网络时延 // Basic network delay with each node
	
	public String receiveTag = "CliReceive";
	
	public String sendTag = "CliSend";

	private PBFTsimulator pbfTsimulator;
	
	public Client(int clientId, int[] netDlys, PBFTsimulator pbfTsimulator) {
		this.clientId = clientId;
		this.netDlys = netDlys;
		reqStats = new HashMap<>();
		reqMsgs = new HashMap<>();
		repMsgs = new HashMap<>();
		this.pbfTsimulator = pbfTsimulator;
	}
	
	public void msgProcess(Message msg) {
		msg.print(receiveTag);
		switch(msg.msgType) {
		case Message.REPLY:
			receiveReply(msg);
			break;
		case Message.CLITIMEOUT:
			receiveCliTimeOut(msg);
			break;
		default:
			System.out.println("【Error】消息类型错误！"); // Wrong message type.
		}
		
	}
	
	public void sendRequest(long time) {
		//避免时间重复 // Avoid duplication of time
		while(reqStats.containsKey(time)) {
			time++;
		}
		int priId = viewNumber % pbfTsimulator.getNumberOfNodes(); //priId: Primary Id (Leader Id).
		Message requestMsg = new RequestMsg("Message", time, clientId, clientId, priId, time + netDlys[priId]);
		PBFTsimulator.sendMsg(requestMsg, sendTag);
		reqStats.put(time, PROCESSING);
		reqMsgs.put(time, requestMsg);
		repMsgs.put(time, new HashMap<>());
		//发送一条Timeout消息，以便将来检查是否发生超时
		// Send a Timeout message to check if a timeout has occurred.
		setTimer(time, time);
	}
	
	public void receiveReply(Message msg) {
		ReplyMsg repMsg = (ReplyMsg)msg;
		long t = repMsg.clientReqTimeStamp;
		//如果这条消息对应的request消息不存在或者已经是stable状态，那就忽略这条消息
		// If the request message corresponding to this message does not exist or is already in a stable state,
		// then ignore this message
		/**
		 *  He means by 'stable' request, probably, a request that is already processed.
		 */
		if(!reqStats.containsKey(t) || reqStats.get(t) == STABLE) {
			return;
		}
		//否则就将这条reply消息包含到缓存中
		// Otherwise, the reply message will be included in the cache ??
		saveReplyMsg(repMsg);
		//判断是否满足f+1条件，如果满足就设定主节点编号，累加确认时间并清理缓存
		// Determine whether the f+1 condition is met, if it is met,
		// set the master node number, accumulate the confirmation time and clear the cache
		if(isStable(repMsg)) {
			viewNumber = repMsg.viewNumber;
			accTime += repMsg.rcvtime - t;
			reqStats.put(t, STABLE);
			reqMsgs.remove(t);
			repMsgs.remove(t);
			/*System.out.println("【Stable】客户端"+id+"在"+t
					+"时间请求的消息已经得到了f+1条reply，进入稳态，共耗时"+(repMsg.rcvtime - t)+"毫秒,此时占用带宽为"+Simulator.inFlyMsgLen+"B");*/

			System.out.println("【Stable】Client"+ clientId +" at time "+t +"."
					+"The request message has received f+1 identical reply and its state became stable." +
					"Total time: "+(repMsg.rcvtime - t)+" millisecond." +
					"The occupied bandwidth: "+ PBFTsimulator.inFlyMsgLen+" Bytes.");
		}
	}
	
	public void receiveCliTimeOut(Message msg) {
		CliTimeOutMsg cliTimeOutMsg = (CliTimeOutMsg)msg;
		long t = cliTimeOutMsg.reqTimeSTP;
		//如果这条消息对应的request消息不存在或者已经是stable状态，那就忽略这条消息
		// If the request corresponding to this message does not exist
		// or is already in a stable state, then ignore the message.
		if(!reqStats.containsKey(t) || reqStats.get(t) == STABLE) {
			return;
		}
		//否则给所有的节点广播request消息
		// Otherwise, broadcast request to all nodes.
		for(int i = 0; i < pbfTsimulator.getNumberOfNodes(); i++) {
			Message requestMsg = new RequestMsg("Message", t, clientId, clientId, i, cliTimeOutMsg.rcvtime + netDlys[i]);
			PBFTsimulator.sendMsg(requestMsg, sendTag);
		}
		//发送一条Timeout消息，以便将来检查是否发生超时
		// Send a Timeout message to check if a timeout has occurred in the future ??
		setTimer(t, cliTimeOutMsg.rcvtime);
	}
	
	/**
     * 去重缓存reply消息 // Deduplicate reply message
     * @param msg reply消息 // reply message
     */
	public void saveReplyMsg(ReplyMsg msg) {
		Map<Integer, Message> rMap = repMsgs.get(msg.clientReqTimeStamp);
		for(Integer i : rMap.keySet()) {
			if(i == msg.nodeId && ((ReplyMsg)rMap.get(i)).viewNumber >= msg.viewNumber) {
				return;
			}
		}
		repMsgs.get(msg.clientReqTimeStamp).put(msg.nodeId, msg);
	}
	
	/**
	 * 判断请求消息是否已经达到稳定状态（即收到了f+1条reply消息）
	 * // Determine whether the request message has reached a stable state (that is, f+1 reply messages have been received)
	 * @param msg 请求消息 // Request message
	 * @return	是否达到稳态的判断结果 // Whether to reach a stable state
	 */
	public boolean isStable(ReplyMsg msg) {
		Map<Integer, Message> rMap = repMsgs.get(msg.clientReqTimeStamp);
		int cnt = 0;
		for(Integer i : rMap.keySet()) {
			if(((ReplyMsg)rMap.get(i)).viewNumber == msg.viewNumber && ((ReplyMsg)rMap.get(i)).reply == msg.reply) {
				cnt++;
			}
		}
		if(cnt > Utils.getMaxTorelentNumber(pbfTsimulator.getNumberOfNodes())) return true;
		return false;
	}
	
	/**
     * 根据数组下标获取客户端Id // Get the client Id according to the array index
     * @param index 表示客户端在数组中的下标 // Represents the client's index in the array
     * @return 返回客户端id // Return client id
     */
	public static int getCliId(int index) {
		return index * (-1) - 1;
	}
	
	/**
     * 根据客户端Id获取数组下标 // Get the array index according to the client Id
     * @param id 表示客户端id // Represents the client id
     * @return 返回数组下标 // Return array subscript
     */
	public static int getCliArrayIndex(int id) {
		return (id + 1) * (-1);
	}
	
	public int stableMsgNum() {
		int cnt = 0;
		if(reqStats == null) return cnt;
		for(long t : reqStats.keySet()) 
			if(reqStats.get(t) == STABLE) 
				cnt++;
		return cnt;
	}
	
	public void setTimer(long t, long time) {
		Message timeoutMsg = new CliTimeOutMsg(t, clientId, clientId, time + PBFTsimulator.CLITIMEOUT);
		PBFTsimulator.sendMsg(timeoutMsg, "ClientTimeOut");
	}
}
