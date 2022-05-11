package com.engie.csai.pc.consensus.message;

public class TimeOutMsg extends Message {
	
	public int viewNumber; // v -> viewNumber
	
	public int seqNumber; // n -> seqNumber
	
	//消息结构
	//<TIMEOUT, v, n>:v表示视图编号;n表示序号;
	// v: view number;
	// n: sequence number;
	public TimeOutMsg(int viewNumber, int seqNumber, int sndId, int rcvId, long rcvtime) {
		super(sndId, rcvId, rcvtime);
		this.msgType = TIMEOUT;
		this.len = TIMMSGLEN;
		this.viewNumber = viewNumber;
		this.seqNumber = seqNumber;
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof TimeOutMsg) {
        	TimeOutMsg msg = (TimeOutMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber);
        }
        return super.equals(obj);
    }
        
    public int hashCode() {
        String str = "" + viewNumber + seqNumber;
        return str.hashCode();
    }
    
    public String toString() {
    	return super.toString() + "视图编号:"+ viewNumber +";序号:"+ seqNumber;
    }
}
