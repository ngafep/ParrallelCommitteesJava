package com.engie.csai.pc.pbftSimulatorold2.message;

public class TimeOutMsg extends Message
{

    public int viewNumber; // v -> viewNumber

    public int seqNumber; // n -> seqNumber

    // Message structure
    //<TIMEOUT, v, n>:
    // v: view number;
    // n: sequence number;
    public TimeOutMsg(int viewNumber, int seqNumber, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = TIMEOUT;
        this.len = TIMMSGLEN;
        this.viewNumber = viewNumber;
        this.seqNumber = seqNumber;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof TimeOutMsg)
        {
            TimeOutMsg msg = (TimeOutMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + seqNumber;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View Number:" + viewNumber + ";Sequence Number:" + seqNumber;
    }
}
