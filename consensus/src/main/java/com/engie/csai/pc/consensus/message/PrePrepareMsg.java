package com.engie.csai.pc.consensus.message;

public class PrePrepareMsg extends Message {
	
	public int viewNumber; // v -> viewNumber
	
	public int seqNumber; // n -> seqNumber
	
	public Message reqMsg; // m -> reqMsg
	
	public int nodeId; // i -> nodeId
	
	//消息结构
	//<RREPREPARE, v, n, m, i>:v表示视图编号;n表示序列号;m表示request消息;i表示节点id
	// v: view number;
	// n: sequence number;
	// m: request message;
	// i: node id
	public PrePrepareMsg(int viewNumber, int seqNumber, Message reqMsg, int nodeId, int sndId, int rcvId, long rcvtime) {
		super(sndId, rcvId, rcvtime);
		this.msgType = PREPREPARE;
		this.len = PPRMSGLEN;
		this.viewNumber = viewNumber;
		this.seqNumber = seqNumber;
		this.reqMsg = reqMsg;
		this.nodeId = nodeId;
	}
	
	public Message copy(int rcvId, long rcvtime) {
		//m是浅复制，不过没有关系，不会修改它的值
		// m is a copy, but it doesn’t matter, its value will not be modified ??
		return new PrePrepareMsg(viewNumber, seqNumber, reqMsg, nodeId, sndId, rcvId, rcvtime);
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof PrePrepareMsg) {
        	PrePrepareMsg msg = (PrePrepareMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber && nodeId == msg.nodeId && ((reqMsg == null && msg.reqMsg == null) || (reqMsg != null && reqMsg.equals(msg.reqMsg))));
        }
        return super.equals(obj);
    }
        
    public int hashCode() {
        String str = "" + viewNumber + seqNumber + nodeId;
        return str.hashCode();
    }
    
    public String toString() {
    	return super.toString() + "视图编号:"+ viewNumber +";序列号:"+ seqNumber;
    }
    
    public String mString() {
    	if(reqMsg == null) {
    		return "";
    	}
    	return reqMsg.toString();
    }
}
