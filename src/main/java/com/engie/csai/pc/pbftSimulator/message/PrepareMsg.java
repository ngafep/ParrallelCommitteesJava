package com.engie.csai.pc.pbftSimulator.message;

public class PrepareMsg extends Message
{

    public int viewNumber; // v -> viewNumber

    public int seqNumber; // n -> seqNumber

    public String DigestMsg; // d -> DigestMsg

    public int nodeId; // i -> nodeId

    // Message structure
    //<PREPARE, v, n, d, i>:
    // v: view number;
    // n: sequence number; -> It should be considered as the index of each Client-Request.
    // d: digest of the request message;
    // i: node id
    public PrepareMsg(int viewNumber, int seqNumber, String DigestMsg, int nodeId, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = PREPARE;
        this.len = PREMSGLEN;
        this.viewNumber = viewNumber;
        this.seqNumber = seqNumber;
        this.DigestMsg = DigestMsg;
        this.nodeId = nodeId;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        return new PrepareMsg(viewNumber, seqNumber, new String(DigestMsg), nodeId, sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof PrepareMsg)
        {
            PrepareMsg msg = (PrepareMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber && DigestMsg.equals(msg.DigestMsg) && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + seqNumber + DigestMsg + nodeId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View Number:" + viewNumber + ";Sequence Number:" + seqNumber;
    }
}
