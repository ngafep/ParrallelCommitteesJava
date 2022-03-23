package com.engie.csai.pc.pbftSimulator.message;

public class ReplyMsg extends Message {
	
	public int viewNumber; // v -> viewNumber
	
	public long clientReqTimeStamp; // t -> clientReqTimeStamp
	
	public int clientId; // c -> clientId
	
	public int nodeId; // i -> nodeId
	
	public String reply; // r -> reply
	
	//消息结构
	//<REPLY, v, t, c, i, r>:v表示视图编号;t表示客户端请求时间戳;c表示客户端id;i表示节点id;r表示处理返回结果
	// v: view number;
	// t: client request timestamp;
	// c: client id;
	// i: node id;
	// r: processing returned result (reply)
	public ReplyMsg(int viewNumber, long clientReqTimeStamp, int clientId, int nodeId, String reply, int sndId, int rcvId, long rcvtime) {
		super(sndId, rcvId, rcvtime);
		this.msgType = REPLY;
		this.len = REPMSGLEN;
		this.viewNumber = viewNumber;
		this.clientReqTimeStamp = clientReqTimeStamp;
		this.clientId = clientId;
		this.nodeId = nodeId;
		this.reply = reply;
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof ReplyMsg) {
        	ReplyMsg msg = (ReplyMsg) obj;
            return (viewNumber == msg.viewNumber && clientReqTimeStamp == msg.clientReqTimeStamp && clientId == msg.clientId && nodeId == msg.nodeId && reply.equals(msg.reply));
        }
        return super.equals(obj);
    }
        
    public int hashCode() {
        String str = "" + viewNumber + clientReqTimeStamp + clientId + nodeId + reply;
        return str.hashCode();
    }
    
    public String toString() {
    	return super.toString() + "视图编号:"+ viewNumber +";时间戳:"+ clientReqTimeStamp +";客户端编号:"+ clientId;
    }
}