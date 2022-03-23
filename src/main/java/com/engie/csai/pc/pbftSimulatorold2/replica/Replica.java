package com.engie.csai.pc.pbftSimulatorold2.replica;

import com.engie.csai.pc.pbftSimulatorold2.Client;
import com.engie.csai.pc.pbftSimulatorold2.PBFTsimulator;
import com.engie.csai.pc.pbftSimulatorold2.Utils;
import com.engie.csai.pc.pbftSimulatorold2.message.*;

import java.util.*;

public class Replica {
	
	public static final int K = 10;
	// Period of sending checkpoint messages
	
	public static final int L = 30;
	// L = high bit level-low bit level (usually L>=K*2) ??
	
	public static final int PROCESSING = 0;
	// Did not receive f+1 (identical) reply
	
	public static final int STABLE = 1;
	// f+1 (identical) replies have been received
	
	public String receiveTag = "Receive";
	
	public String sendTag = "Send";
	
	public int nodeId; // id -> nodeId 										// The id of the current node
	
	public int viewNumber; // v -> viewNumber										// View number
	
	public int seqNumber; // n -> seqNumber										// Message processing sequence number
	
	public int lastRepNum;								// The latest reply message processing sequence number
	
	public int h;
	// Low bit level = stable state of checkpoint of n ??
	
	public int netDlys[];								// Network latency with other nodes
	
	public int netDlyToClis[];							// Network latency with the client
	
	public boolean isTimeOut;
	// Whether current request has timed out (if it has timed out, no more messages will be sent)
	
	// Message cache <type, <msg>>:type Message type;
	public Map<Integer, Set<Message>> msgCache;
	

	// The state collection of the latest reply <c, <c, t, r>>:
	// c: client number
	// t: request message timestamp
	// r: returned result
	public Map<Integer, LastReply> lastReplyMap;		

	// checkpoints collection <n, <c, <c, t, r>>>:
	// n: message processing sequence number
	public Map<Integer, Map<Integer, LastReply>> checkPoints;
	
	public Map<Message, Integer> reqStats;			 // request status
	
	public static Comparator<PrePrepareMsg> nCmp = new Comparator<PrePrepareMsg>(){
		@Override
		public int compare(PrePrepareMsg c1, PrePrepareMsg c2) {
			return (int) (c1.seqNumber - c2.seqNumber);
		}
	};
	
	public Replica(int nodeId, int[] netDlys, int[] netDlyToClis) {
		this.nodeId = nodeId;
		this.netDlys = netDlys;
		this.netDlyToClis = netDlyToClis;
		msgCache = new HashMap<>();
		lastReplyMap = new HashMap<>();
		checkPoints = new HashMap<>();
		reqStats = new HashMap<>();
		checkPoints.put(0, lastReplyMap);

		// Start Timer initially
//		setTimer(lastRepNum + 1, 0);
	}
	
	public void msgProcess(PBFTsimulator pbfTsimulator, Message msg) {
		msg.print(receiveTag);
		switch(msg.msgType) {
		case Message.REQUEST:
			receiveRequest(pbfTsimulator, msg);
			break;
		case Message.PREPREPARE:
			receivePreprepare(pbfTsimulator, msg);
			break;
		case Message.PREPARE:
			receivePrepare(msg);
			break;
		case Message.COMMIT:
			receiveCommit(msg);
			break;
		case Message.VIEWCHANGE:
			receiveViewChange(pbfTsimulator, msg);
			break;
		case Message.NEWVIEW:
			receiveNewView(pbfTsimulator, msg);
			break;
		case Message.TIMEOUT:
			receiveTimeOut(pbfTsimulator, msg);
			break;
		case Message.CHECKPOINT:
			receiveCheckPoint(msg);
			break;
		default:
			System.out.println("【Error】Wrong message type！");
			return;
		}

		// Collect all prePrepare messages that meet the conditions and perform follow-up processing
		Set<Message> prePrepareMsgSet = msgCache.get(Message.PREPREPARE); 
		Queue<PrePrepareMsg> executeQ = new PriorityQueue<>(nCmp);
		if(prePrepareMsgSet == null) return; 
		for(Message m : prePrepareMsgSet) {
			PrePrepareMsg mm = (PrePrepareMsg)m;
			if(mm.viewNumber >= viewNumber && mm.seqNumber >= lastRepNum + 1) {
				sendCommit(pbfTsimulator, m, msg.rcvtime);
				executeQ.add(mm);			
			}
		}
		while(!executeQ.isEmpty()) {
			execute(pbfTsimulator, executeQ.poll(), msg.rcvtime);
		}
		// Garbage disposal
		garbageCollect(pbfTsimulator);
	}
	
	public void sendCommit(PBFTsimulator pbfTsimulator, Message msg, long time) {
		PrePrepareMsg mm = (PrePrepareMsg)msg;
		String d = Utils.getMD5Digest(mm.mString());
		CommitMsg cm = new CommitMsg(mm.viewNumber, mm.seqNumber, d, nodeId, nodeId, nodeId, time);
		if(isInMsgCache(cm) || !prepared(mm, pbfTsimulator)) {
			return;
		}
		pbfTsimulator.sendMsgToOthers(cm, nodeId, sendTag);
		addMessageToCache(cm);
	}
	
	public void execute(PBFTsimulator pbfTsimulator, Message msg, long time) {
		PrePrepareMsg mm = (PrePrepareMsg)msg;
		RequestMsg rem = null;
		ReplyMsg rm = null;
		if(mm.reqMsg != null) {
			rem = (RequestMsg)(mm.reqMsg);
			rm = new ReplyMsg(mm.viewNumber, rem.clientReqTimeStamp, rem.clientId, nodeId, "result",
					nodeId, rem.clientId, time + netDlyToClis[Client.getCliArrayIndex(rem.clientId)]);
		}
		
		if((rem == null || !isInMsgCache(rm)) && mm.seqNumber == lastRepNum + 1 && commited(mm, pbfTsimulator)) {
			lastRepNum++;
			setTimer(pbfTsimulator, lastRepNum+1, time);
			if(rem != null) {
				pbfTsimulator.sendMsg(rm, sendTag);
				LastReply llp = lastReplyMap.get(rem.clientId);
				if(llp == null) {
					llp = new LastReply(rem.clientId, rem.clientReqTimeStamp, "result");
					lastReplyMap.put(rem.clientId, llp);
				}
				llp.t = rem.clientReqTimeStamp;
				reqStats.put(rem, STABLE);
				
			}

			// Send checkpoint messages periodically
			if(mm.seqNumber % K == 0) {
				Message checkptMsg = new CheckPointMsg(viewNumber, mm.seqNumber, lastReplyMap, nodeId, nodeId, nodeId, time);
//				System.out.println("send:"+checkptMsg.toString());
				addMessageToCache(checkptMsg);
				pbfTsimulator.sendMsgToOthers(checkptMsg, nodeId, sendTag);
			}
		}
	}
	
	public boolean prepared(PrePrepareMsg m, PBFTsimulator pbfTsimulator) {
		Set<Message> prepareMsgSet = msgCache.get(Message.PREPARE);
		if (prepareMsgSet == null) return false;
		int cnt = 0;
		String d = Utils.getMD5Digest(m.mString());
		for(Message msg : prepareMsgSet) {
			PrepareMsg pm = (PrepareMsg)msg;
			if(pm.viewNumber == m.viewNumber && pm.seqNumber == m.seqNumber && pm.DigestMsg.equals(d)) {
				cnt++;
			}
		}
		if(cnt >= 2 * Utils.getMaxTorelentNumber(pbfTsimulator.getNbPeers())) {
			return true;
		}
		return false;
	}
	
	public boolean commited(PrePrepareMsg m, PBFTsimulator pbfTsimulator) {
		Set<Message> commitMsgSet = msgCache.get(Message.COMMIT);
		if (commitMsgSet == null) return false;
		int cnt = 0;
		String d = Utils.getMD5Digest(m.mString());
		for(Message msg : commitMsgSet) {
			CommitMsg pm = (CommitMsg)msg;
			if(pm.viewNumber == m.viewNumber && pm.seqNumber == m.seqNumber && pm.DigestMsg.equals(d)) {
				cnt++;
			}
		}
		if(cnt > 2 * Utils.getMaxTorelentNumber(pbfTsimulator.getNbPeers())) {
			return true;
		}
		return false;
	}
	
	public boolean viewChanged(ViewChangeMsg m, PBFTsimulator pbfTsimulator) {
		Set<Message> viewChangeMsgSet = msgCache.get(Message.VIEWCHANGE);
		if (viewChangeMsgSet == null) return false;
		int cnt = 0;	
		for(Message msg : viewChangeMsgSet) {
			ViewChangeMsg vm = (ViewChangeMsg)msg;
			if(vm.viewNumber == m.viewNumber && vm.seqNumberOfStableState == m.seqNumberOfStableState) {
				cnt++;
			}
		}
		if(cnt > 2 * Utils.getMaxTorelentNumber(pbfTsimulator.getNbPeers())) {
			return true;
		}
		return false;
	}
	
	public void garbageCollect(PBFTsimulator pbfTsimulator) {
		Set<Message> checkptMsgSet = msgCache.get(Message.CHECKPOINT);
		if(checkptMsgSet == null) return;

		// Find the largest sequence number that satisfies the f+1 (identical replies) condition
		Map<Integer, Integer> snMap = new HashMap<>();
		int maxN = 0;
		for(Message msg : checkptMsgSet) {
			CheckPointMsg ckt = (CheckPointMsg)msg;
			if(!snMap.containsKey(ckt.seqNumber)) {
				snMap.put(ckt.seqNumber, 0);
			}
			int cnt = snMap.get(ckt.seqNumber)+1;
			snMap.put(ckt.seqNumber, cnt);
			if(cnt > Utils.getMaxTorelentNumber(pbfTsimulator.getNbPeers())) {
				checkPoints.put(ckt.seqNumber, ckt.lastReply);
				maxN = Math.max(maxN, ckt.seqNumber);
			}
		}

		// Delete all data less than n in msgCache and checkPoints, and update the h value to sn
		deleteCache(maxN);
		deleteCheckPts(maxN);
		h = maxN;

	}
	
	public void receiveRequest(PBFTsimulator pbfTsimulator, Message msg) {
		if(msg == null) return;
		RequestMsg reqMsg = (RequestMsg)msg;
		int clientId = reqMsg.clientId; // c -> clientId
		long clientReqTimeStamp = reqMsg.clientReqTimeStamp; // t -> clientReqTimeStamp

		// If this request has been replied, then reply again
		if(reqStats.containsKey(msg) && reqStats.get(msg) == STABLE) {
			long recTime = msg.rcvtime + netDlyToClis[Client.getCliArrayIndex(clientId)];
			Message replyMsg = new ReplyMsg(viewNumber, clientReqTimeStamp, clientId, nodeId, "result", nodeId, clientId, recTime);
			pbfTsimulator.sendMsg(replyMsg, sendTag);
			return;
		}
		if(!reqStats.containsKey(msg)) {
			// Put the message in the cache
			addMessageToCache(msg);
			reqStats.put(msg, PROCESSING);
		}

		// If it is primary node
		if(isPrimary(pbfTsimulator)) {

			// If the Prepare message has been sent, broadcast it again
			Set<Message> prePrepareSet = msgCache.get(Message.PREPREPARE);
			if(prePrepareSet != null) {
				for(Message m : prePrepareSet) {
					PrePrepareMsg ppMsg = (PrePrepareMsg)m;
					if(ppMsg.viewNumber == viewNumber && ppMsg.nodeId == nodeId && ppMsg.reqMsg.equals(msg)) {
						m.rcvtime = msg.rcvtime;
						pbfTsimulator.sendMsgToOthers(m, nodeId, sendTag);
						return;
					}
				}
			}

			// Otherwise, if the water level is not exceeded,
			// a new prePrepare message is generated and broadcast,
			// and timeout is started at the same time
			/**
			 * The sequence number in the pre-prepare message is
			 * between a low water mark and a high water mark, defined during the leader election.
			 * - Low water mark: The sequence number of the last stable checkpoint.
			 * - High water mark: Low water mark plus the desired maximum size of nodes' message logs.
			 * - Checkpoint	Point in time when logs can get garbage collected.
			 */
			if(inWater(seqNumber + 1)) {
				seqNumber++;
				Message prePrepareMsg = new PrePrepareMsg(viewNumber, seqNumber, reqMsg, nodeId, nodeId, nodeId, reqMsg.rcvtime);
				addMessageToCache(prePrepareMsg);
				pbfTsimulator.sendMsgToOthers(prePrepareMsg, nodeId, sendTag);
			}
		}
	}
	
	public void receivePreprepare(PBFTsimulator pbfTsimulator, Message msg) {
		if(isTimeOut) return;
		PrePrepareMsg prePrepareMsg = (PrePrepareMsg)msg;
		int msgv = prePrepareMsg.viewNumber;
		int msgn = prePrepareMsg.seqNumber;
		int i = prePrepareMsg.nodeId;

		// Check whether the view of the message matches the view of the node, and whether the sender of the message is the primary node,
		// Whether the view of the message is legal and whether the sequence number is within the bit level
		if(msgv < viewNumber || !inWater(msgn) || i != msgv % pbfTsimulator.getNbPeers() || !hasNewView(viewNumber)) {
			return;
		}

		// Put the prepare message and the request message it contains into the cache ??
		receiveRequest(pbfTsimulator, prePrepareMsg.reqMsg);
		addMessageToCache(msg);
		seqNumber = Math.max(seqNumber, prePrepareMsg.seqNumber);
		 // Generate Prepare message and broadcast
		String d = Utils.getMD5Digest(prePrepareMsg.mString());
		Message prepareMsg = new PrepareMsg(msgv, msgn, d, nodeId, nodeId, nodeId, msg.rcvtime);
		if(isInMsgCache(prepareMsg)) return;
		addMessageToCache(prepareMsg);
		pbfTsimulator.sendMsgToOthers(prepareMsg, nodeId, sendTag);
	}
	
	public void receivePrepare(Message msg) {
		if(isTimeOut) return;
		PrepareMsg prepareMsg = (PrepareMsg)msg;
		int msgv = prepareMsg.viewNumber;
		int msgn = prepareMsg.seqNumber;

		// Check whether there is this message in the cache,
		// whether the view of the message is legal,
		// and whether the sequence number is within the bit level
		if(isInMsgCache(msg) || msgv < viewNumber || !inWater(msgn) || !hasNewView(viewNumber)) {
			return;
		}

		// Put the prepare message into the cache
		addMessageToCache(msg);
	}
	
	public void receiveCommit(Message msg) {
		if(isTimeOut) return;
		CommitMsg commitMsg = (CommitMsg)msg;
		int msgv = commitMsg.viewNumber;
		int msgn = commitMsg.seqNumber;

		// Check whether the view of the message is legal
		// and whether the sequence number is within the bit level
		if(isInMsgCache(msg) || msgv < viewNumber || !inWater(msgn) || !hasNewView(viewNumber)) {
			return;
		}

		// Put the commit message into the cache
		addMessageToCache(msg);
	}
	
	public void receiveTimeOut(PBFTsimulator pbfTsimulator, Message msg) {
		TimeOutMsg timeoutMsg = (TimeOutMsg)msg; // tMsg -> timeoutMsg

		// If the message has entered a stable state, ignore the message
		if(timeoutMsg.seqNumber <= lastRepNum || timeoutMsg.viewNumber < viewNumber) return;

		// If there are no new requests, stop timeOut
		if(reqStats.size() >= pbfTsimulator.getRequestNumber()) return;
		isTimeOut = true;

		// Send viewChange message
		Map<Integer, LastReply> ss = checkPoints.get(h);
		Set<Message> C = computeC();
		Map<Integer, Set<Message>> P = computeP(pbfTsimulator);
		Message vm = new ViewChangeMsg(viewNumber + 1, h, ss, C, P, nodeId, nodeId, nodeId, msg.rcvtime);
		addMessageToCache(vm);
		pbfTsimulator.sendMsgToOthers(vm, nodeId, sendTag);
	}
	
	public void receiveCheckPoint(Message msg) {
		CheckPointMsg checkptMsg = (CheckPointMsg)msg;
		int msgv = checkptMsg.viewNumber;

		// Check whether there is this message in the cache and whether the view of the message is legal
		if(msgv < viewNumber) {
			return;
		}

		// Put the checkpoint message into the cache
		addMessageToCache(msg);
	}
	
	
	public void receiveViewChange(PBFTsimulator pbfTsimulator, Message msg) {
		ViewChangeMsg vcMsg = (ViewChangeMsg)msg;
		int msgv = vcMsg.viewNumber;
		int msgn = vcMsg.seqNumberOfStableState;

		// Check whether there is this message in the cache and whether the view of the message is legal
		if(msgv <= viewNumber || msgn < h) {
			return;
		}

		// Put the checkpoint message into the cache
		addMessageToCache(msg);

		// Have you received 2f+1 viewChange messages
		/**
		 * >>> When the new primary receives 2f+1 view-change
		 * messages, it will begin the view change
		 */
		if(viewChanged(vcMsg, pbfTsimulator)) {
			viewNumber = vcMsg.viewNumber;
			h = vcMsg.seqNumberOfStableState;
			lastRepNum = h;
			lastReplyMap = vcMsg.replySetOfStableState;
			seqNumber = lastRepNum;
			Map<Integer, Set<Message>> prePrepareMap = vcMsg.P;
			if(prePrepareMap != null) {
				for(Integer nn : prePrepareMap.keySet()) {
					seqNumber = Math.max(seqNumber, nn);
				}
			}
			isTimeOut = false;
			setTimer(pbfTsimulator, lastRepNum + 1, msg.rcvtime);
			if(isPrimary(pbfTsimulator)) {
				// Send NewView message
				Map<String, Set<Message>> VONMap = computeVON();
				Message nvMsg = new NewViewMsg(viewNumber, VONMap.get("V"), VONMap.get("O"), VONMap.get("N"), nodeId, nodeId, nodeId, msg.rcvtime);
				addMessageToCache(nvMsg);
				pbfTsimulator.sendMsgToOthers(nvMsg, nodeId, sendTag);

				Set<Message> reqSet = msgCache.get(Message.REQUEST);
				if(reqSet == null) reqSet = new HashSet<>();
				Set<Message> OSet = VONMap.get("O");
				reqSet.removeAll(OSet);
				for(Message m : reqSet) {
					RequestMsg reqMsg = (RequestMsg)m;
					reqMsg.rcvtime = msg.rcvtime;
					receiveRequest(pbfTsimulator, reqMsg);
				}
			}
		}
	}
	
	public void receiveNewView(PBFTsimulator pbfTsimulator, Message msg) {
		NewViewMsg nvMsg = (NewViewMsg)msg;
		int msgv = nvMsg.viewNumber;

		// Check whether there is this message in the cache and whether the view of the message is legal
		if(msgv < viewNumber) {
			return;
		}
		viewNumber = msgv;
		addMessageToCache(msg);
		

		// Process the prepare messages in the new view one by one
		Set<Message> O = nvMsg.O;
		for(Message m : O) {
			PrePrepareMsg ppMsg = (PrePrepareMsg)m;
			PrePrepareMsg newPPm = new PrePrepareMsg(viewNumber, ppMsg.seqNumber, ppMsg.reqMsg, ppMsg.nodeId, msg.sndId, msg.rcvId, msg.rcvtime);
			receivePreprepare(pbfTsimulator, newPPm);
		}
		Set<Message> N = nvMsg.N; 
		for(Message m : N) {
			PrePrepareMsg ppMsg = (PrePrepareMsg)m;
			PrePrepareMsg newPPm = new PrePrepareMsg(ppMsg.viewNumber, ppMsg.seqNumber, ppMsg.reqMsg, ppMsg.nodeId, msg.sndId, msg.rcvId, msg.rcvtime);
			receivePreprepare(pbfTsimulator, newPPm);
		}
	}
	
	public int getPriId(PBFTsimulator pbfTsimulator) {
		return viewNumber % pbfTsimulator.getNbPeers();
	}
	
	public boolean isPrimary(PBFTsimulator pbfTsimulator) {
		return getPriId(pbfTsimulator) == nodeId;
	}
	
	/**
	 *  // Store the message in the cache
	 * @param m
	 */
	private boolean isInMsgCache(Message m) {
		Set<Message> msgSet = msgCache.get(m.msgType);
		if(msgSet == null) {
			return false;
		}
		return msgSet.contains(m);
	}
	
	/**
	 *  // Store the message in the cache
	 * @param m
	 */
	private void addMessageToCache(Message m) {
		Set<Message> msgSet = msgCache.get(m.msgType);
		if(msgSet == null) {
			msgSet = new HashSet<>();
			msgCache.put(m.msgType, msgSet);
		}
		msgSet.add(m);
	}
	
	/**
	 *  // Delete all cached messages before sequence number n
	 * @param n
	 */
	private void deleteCache(int n) {
		Map<Integer, LastReply> lastReplyMap = checkPoints.get(n);
		if(lastReplyMap == null)  return;
		for(Integer type : msgCache.keySet()) {
			Set<Message> msgSet = msgCache.get(type);
			if(msgSet != null) {
				Iterator<Message> it = msgSet.iterator();
				while(it.hasNext()) {
					Message m = it.next();
					if(m instanceof RequestMsg) {
						RequestMsg mm = (RequestMsg)m;
						if(lastReplyMap.get(mm.clientId) != null && mm.clientReqTimeStamp <= lastReplyMap.get(mm.clientId).t) {
							it.remove();
						}
					}else if(m instanceof PrePrepareMsg) {
						PrePrepareMsg mm = (PrePrepareMsg)m;
						if(mm.seqNumber <= n) {
							it.remove();
						}
					}else if(m instanceof PrepareMsg) {
						PrepareMsg mm = (PrepareMsg)m;
						if(mm.seqNumber <= n) {
							it.remove();
						}
					}else if(m instanceof CommitMsg) {
						CommitMsg mm = (CommitMsg)m;
						if(mm.seqNumber <= n) {
							it.remove();
						}
					}else if(m instanceof CheckPointMsg) {
						CheckPointMsg mm = (CheckPointMsg)m;
						if(mm.seqNumber < n) {
							it.remove();
						}
					}else if(m instanceof ViewChangeMsg) {
						ViewChangeMsg mm = (ViewChangeMsg)m;
						if(mm.seqNumberOfStableState < n) {
							it.remove();
						}
					}
				}
			}
		}
	}
	
	private void deleteCheckPts(int n) {
		Iterator<Map.Entry<Integer, Map<Integer, LastReply>>> it = checkPoints.entrySet().iterator();
		while(it.hasNext()){  
			Map.Entry<Integer, Map<Integer, LastReply>> entry=it.next(); 
			int sn = entry.getKey(); 
			if(sn < n) {
				it.remove();
			}
		}
	}
	
	/**
	 *  // Determine whether a view number has the message based on NewView
	 * @return
	 */
	public boolean hasNewView(int v) {
		if(v == 0)
			return true;
		Set<Message> msgSet = msgCache.get(Message.NEWVIEW);
		if(msgSet != null) {
			for(Message m : msgSet) {
				NewViewMsg nMsg = (NewViewMsg)m;
				if(nMsg.viewNumber == v) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean inWater(int n) {
		return n == 0 || (n > h && n < h + L);
	}
	
	private Set<Message> computeC(){
		if(h == 0) return null;
		Set<Message> result = new HashSet<>();
		Set<Message> checkptSet = msgCache.get(Message.CHECKPOINT);
		for(Message msg : checkptSet) {
			CheckPointMsg ckpt = (CheckPointMsg)msg;
			if(ckpt.seqNumber == h) {
				result.add(msg);
			}
		}
		return result;
	}
	
	private Map<Integer, Set<Message>> computeP(PBFTsimulator pbfTsimulator){
		Map<Integer, Set<Message>> result = new HashMap<>();
		Set<Message> prePrepareSet = msgCache.get(Message.PREPREPARE);
		if(prePrepareSet == null) return null;
		for(Message msg : prePrepareSet) {
			PrePrepareMsg ppm = (PrePrepareMsg)msg;
			if(ppm.seqNumber > h && prepared(ppm, pbfTsimulator)) {
				Set<Message> set = result.get(ppm.seqNumber);
				if(set == null) {
					set = new HashSet<>();
					result.put(ppm.seqNumber, set);
				}
				set.add(msg);
			}
		}
		return result;
	}
	
	private Map<String, Set<Message>> computeVON(){
		int maxN = h;
		Set<Message> V = new HashSet<>();
		Set<Message> O = new HashSet<>();
		Set<Message> N = new HashSet<>();
		Set<Message> vcSet = msgCache.get(Message.VIEWCHANGE);
		for(Message msg : vcSet) {
			ViewChangeMsg ckpt = (ViewChangeMsg)msg;
			if(ckpt.viewNumber == viewNumber) {
				V.add(msg);
				Map<Integer, Set<Message>> ppMap = ckpt.P;
				if(ppMap == null) continue;
				for(Integer n : ppMap.keySet()) {
					Set<Message> ppSet = ppMap.get(n);
					if(ppSet == null) continue;
					for(Message m : ppSet) {
						PrePrepareMsg ppm = (PrePrepareMsg)m;
						Message ppMsg = new PrePrepareMsg(viewNumber, n, ppm.reqMsg, nodeId, nodeId, nodeId, 0);
						O.add(ppMsg);
						maxN = Math.max(maxN, n);
					}
				}
			}
		}
		for(int i = h; i < maxN; i++) {
			boolean flag = false;
			for(Message msg : O) {
				PrePrepareMsg ppm = (PrePrepareMsg)msg;
				if(ppm.seqNumber == i) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				Message ppMsg = new PrePrepareMsg(viewNumber, seqNumber, null, nodeId, nodeId, nodeId, 0);
				N.add(ppMsg);
			}
		}
		Map<String, Set<Message>> map = new HashMap<>();
		map.put("V", V);
		map.put("O", O);
		map.put("N", N);
		seqNumber = maxN;
		return map;
	}
	
	public void setTimer(PBFTsimulator pbfTsimulator, int n, long time) {
		Message timeOutMsg = new TimeOutMsg(viewNumber, n, nodeId, nodeId, time + PBFTsimulator.TimeOut);
		pbfTsimulator.sendMsg(timeOutMsg, sendTag);
	}

}
