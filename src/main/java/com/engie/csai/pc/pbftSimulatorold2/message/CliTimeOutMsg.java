package com.engie.csai.pc.pbftSimulatorold2.message;

public class CliTimeOutMsg extends Message
{

    // Message structure
    public long reqTimeSTP; // t -> reqTimeSTP

    //<CLITIMEOUT, t>:
    // t: request timestamp
    public CliTimeOutMsg(long reqTimeSTP, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = CLITIMEOUT;
        this.len = CLTMSGLEN;
        this.reqTimeSTP = reqTimeSTP;
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof CliTimeOutMsg)
        {
            CliTimeOutMsg msg = (CliTimeOutMsg) obj;
            return (reqTimeSTP == msg.reqTimeSTP);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + reqTimeSTP;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "Request timestamp:" + reqTimeSTP;
    }

}
