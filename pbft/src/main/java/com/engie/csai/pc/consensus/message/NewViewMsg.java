package com.engie.csai.pc.consensus.message;

import java.util.Set;

public class NewViewMsg extends Message {
	
	public int viewNumber; // v -> viewNumber
	
	public Set<Message> viewChangeMsg; // V -> viewChangeMsg
	
	public Set<Message> O;
	
	public Set<Message> N;
	
	public int nodeId; // i -> nodeId
	
	//消息结构
	//<NEWVIEW, v, V, O, N, i>:v表示视图编号;V表示viewChange消息集合;
	//O表示prePrepare消息集合;N表示prePrepare消息集合;i表示节点id
	// v: view number;
	// V: viewChange message collection;
	// O: prePrepare message collection; ?? (Operation message ??)
	// N: prePrepare message collection; ?? (probably it's 'New-view' message collection.)
	// i: node id
	public NewViewMsg(int viewNumber, Set<Message> viewChangeMsg, Set<Message> O, Set<Message> N,
					  int nodeId, int sndId, int rcvId, long rcvtime) {
		super(sndId, rcvId, rcvtime);
		this.msgType = NEWVIEW;
		this.len = NEVMSGBASELEN + accumulateLen(viewChangeMsg) + accumulateLen(O) + accumulateLen(N);
		this.viewNumber = viewNumber;
		this.viewChangeMsg = viewChangeMsg;
		this.O = O;
		this.N = N;
		this.nodeId = nodeId;
	}
	
	public Message copy(int rcvId, long rcvtime) {
		//V O N是浅复制，不过没有关系，不会修改它们的值
		// It is a copy, but it does not matter, and their values will not be modified
		return new NewViewMsg(viewNumber, viewChangeMsg, O, N, nodeId, sndId, rcvId, rcvtime);
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof NewViewMsg) {
        	NewViewMsg msg = (NewViewMsg) obj;
            return (viewNumber == msg.viewNumber && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }
        
    public int hashCode() {
        String str = "" + viewNumber + nodeId;
        return str.hashCode();
    }
    
    public String toString() {
    	return super.toString() + "视图编号:"+ viewNumber;
    }
}
