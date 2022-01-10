package com.engie.csai.pc.pbftSimulator.message;

public class PrePrepareMsg extends Message
{

    public int viewNumber; // v -> viewNumber

    public int seqNumber; // n -> seqNumber

    public Message reqMsg; // m -> reqMsg

    public int nodeId; // i -> nodeId

    // Message structure
    //<RREPREPARE, v, n, m, i>:
    // v: view number;
    // n: sequence number;
    // m: request message;
    // i: node id
    public PrePrepareMsg(int viewNumber, int seqNumber, Message reqMsg, int nodeId, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = PREPREPARE;
        this.len = PPRMSGLEN;
        this.viewNumber = viewNumber;
        this.seqNumber = seqNumber;
        this.reqMsg = reqMsg;
        this.nodeId = nodeId;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        // m is a copy, but it doesn’t matter, its value will not be modified ??
        return new PrePrepareMsg(viewNumber, seqNumber, reqMsg, nodeId, sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof PrePrepareMsg)
        {
            PrePrepareMsg msg = (PrePrepareMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber && nodeId == msg.nodeId && ((reqMsg == null && msg.reqMsg == null) || (reqMsg != null && reqMsg.equals(msg.reqMsg))));
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + seqNumber + nodeId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View Number:" + viewNumber + ";Sequence Number:" + seqNumber;
    }

    public String mString()
    {
        if (reqMsg == null)
        {
            return "";
        }
        return reqMsg.toString();
    }
}
