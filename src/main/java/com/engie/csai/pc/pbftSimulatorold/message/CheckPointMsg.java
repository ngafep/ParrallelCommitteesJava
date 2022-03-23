package com.engie.csai.pc.pbftSimulatorold.message;

import java.util.Map;

public class CheckPointMsg extends Message{
	
	public int viewNumber; // v -> viewNumber
	
	public int seqNumber; // n -> seqNumber
	
	public Map<Integer, LastReply> lastReply; // s -> lastReply
	
	public int nodeId; // i -> nodeId
	
	//消息结构 // Message structure
	//<CHECKPOINT, v, n, s, i>:v表示视图编号;n表示序列号;s表示lastReply集合;i表示节点id
	// v: view number
	// n: sequence number (>>>Two different messages can never have the same sequence number
	// i.e. Non-faulty replicas agree on total order for requests within a view.)
	// s: last-Reply collection
	// i: node id
	/**
	 * 3 main phases in PBFT:
	 *
	 * ● Pre-prepare: Acknowledge a unique sequence number for the request
	 * ● Prepare: The replicas (nodes/peers) agree on this sequence number
	 * ● Commit: Ensuring requests are ordered consistently across views
	 *
	 * REQUEST (from client/user) → PRE-PREPARE → PREPARE → COMMIT → REPLY (to client/user)
	 *
	 */

	public CheckPointMsg(int viewNumber, int seqNumber, Map<Integer, LastReply> lastReply, int nodeId, int sndId, int rcvId, long rcvtime) {
		/**
		 * About Checkpoint:
		 *
		 * Recall the new primary needs to recompute which requests need to be committed again.
		 * - Redoing all the requests is expensive
		 * - Using checkpoints to speed up the process
		 * - After every 100 sequence number, all replicas save its current state into a checkpoint
		 * - Replicas should agree on the checkpoints as well.
		 */
		super(sndId, rcvId, rcvtime);
		long appendLen = lastReply == null ? 0L : lastReply.size() * LASTREPLEN;
		this.msgType = CHECKPOINT;
		this.len = CKPMSGBASELEN + appendLen;
		this.viewNumber = viewNumber;
		this.seqNumber = seqNumber;
		this.lastReply = lastReply;
		this.nodeId = nodeId;
	}
	
	public Message copy(int rcvId, long rcvtime) {
		//s是浅复制，不过没有关系，不会修改s的值
		// s is a copy, but it does not matter, the value of s will not be modified ??
		return new CheckPointMsg(viewNumber, seqNumber, lastReply, nodeId, sndId, rcvId, rcvtime);
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof CheckPointMsg) {
        	CheckPointMsg msg = (CheckPointMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }
        
    public int hashCode() {
        String str = "" + viewNumber + seqNumber + nodeId;
        return str.hashCode();
    }
    
    public String toString() {
    	// return super.toString() + "视图编号: "+v+";序列号: "+n;
		return super.toString() + "View number: "+ viewNumber +"; Sequence number: "+ seqNumber;
    }
}
