package com.engie.csai.pc.pbftSimulator;

import com.engie.csai.pc.pbftSimulator.message.CliTimeOutMsg;
import com.engie.csai.pc.pbftSimulator.message.Message;
import com.engie.csai.pc.pbftSimulator.message.ReplyMsg;
import com.engie.csai.pc.pbftSimulator.message.RequestMsg;
import java.util.HashMap;
import java.util.Map;

public class Client
{
    /**
     * User peer/node in P-C
     */

    public static final int PROCESSING = 0;       // Did not receive f+1 (identical) reply
    /**
     * About f+1 replies:
     * <p>
     * The client accepts the result after receiving f+1 identical/same replies.
     * For example, if there is 1 faulty node, 2 identical replies (and not in general 2 replies) must be received by client.
     */

    public static final int STABLE = 1;            // f+1 replies have been received.

    public int clientId; // id -> clientId								// Client (User) ID

    public int viewNumber; // v -> viewNumber								// View number (View index)

    public Map<Long, Integer> reqStats;             // request status

    public Map<Long, Message> reqMsgs;
    // request message (delete the request message that has reached the stable state) ??

    public Map<Long, Map<Integer, Message>> repMsgs;
    // reply message (delete the reply message that has reached the stable state) ??

    public long accTime;                         // Accumulative confirmation time ??

    public int netDlys[];                         // Basic network delay with each node

    public String receiveTag = "CliReceive";

    public String sendTag = "CliSend";

    public Client(int clientId, int[] netDlys)
    {
        this.clientId = clientId;
        this.netDlys = netDlys;
        reqStats = new HashMap<>();
        reqMsgs = new HashMap<>();
        repMsgs = new HashMap<>();
    }

    public void msgProcess(PBFTsimulator pbfTsimulator, Message msg)
    {
        msg.print(receiveTag);
        switch (msg.msgType)
        {
            case Message.REPLY:
                receiveReply(msg);
                break;
            case Message.CLITIMEOUT:
                receiveCliTimeOut(pbfTsimulator, msg);
                break;
            default:
                System.out.println("【Error】Wrong message type！"); // Wrong message type.
        }

    }

    public void sendRequest(PBFTsimulator pbfTsimulator, long time
    ,String messageClientRequest/*I added here a new parameter as String message i.e. client-request*/)
    {
         // Avoid duplication of time
        while (reqStats.containsKey(time))
        {
            time++;
        }
        int priId = viewNumber % PBFTsimulator.numberOfNodes; //priId: Primary Id (Leader Id).
        Message requestMsg = new RequestMsg(/*"Message" It can be considered as data parameter*/messageClientRequest,
                time, clientId, clientId, priId, time + netDlys[priId]);
        System.out.println("Sent message is: " + messageClientRequest);
        pbfTsimulator.sendMsg(requestMsg, sendTag);
        reqStats.put(time, PROCESSING);
        reqMsgs.put(time, requestMsg);
        repMsgs.put(time, new HashMap<>());
        // Send a Timeout message to check if a timeout has occurred.
        setTimer(pbfTsimulator, time, time);
    }

    public void receiveReply(Message msg)
    {
        ReplyMsg repMsg = (ReplyMsg) msg;
        long t = repMsg.clientReqTimeStamp;
        // If the request message corresponding to this message does not exist or is already in a stable state,
        // then ignore this message
        /**
         *  He means by 'stable' request, probably, a request that is already processed.
         */
        if (!reqStats.containsKey(t) || reqStats.get(t) == STABLE)
        {
            return;
        }
        // Otherwise, the reply message will be included in the cache ??
        saveReplyMsg(repMsg);

        // Determine whether the f+1 condition is met, if it is met,
        // set the master node number, accumulate the confirmation time and clear the cache
        if (isStable(repMsg))
        {
            viewNumber = repMsg.viewNumber;
            accTime += repMsg.rcvtime - t;
            reqStats.put(t, STABLE);
            reqMsgs.remove(t);
            repMsgs.remove(t);

            System.out.println("【Stable】Client" + clientId + " at time " + t + "."
                    + "The request message has received f+1 identical reply and its state became stable." +
                    "Total time: " + (repMsg.rcvtime - t) + " millisecond." +
                    "The occupied bandwidth: " + PBFTsimulator.inFlyMsgLen + " Bytes.");
        }
    }

    public void receiveCliTimeOut(PBFTsimulator pbfTsimulator, Message msg)
    {
        CliTimeOutMsg cliTimeOutMsg = (CliTimeOutMsg) msg;
        long t = cliTimeOutMsg.reqTimeSTP;

        // If the request corresponding to this message does not exist
        // or is already in a stable state, then ignore the message.
        if (!reqStats.containsKey(t) || reqStats.get(t) == STABLE)
        {
            return;
        }

        // Otherwise, broadcast request to all nodes.
        for (int i = 0; i < PBFTsimulator.numberOfNodes; i++)
        {
            Message requestMsg = new RequestMsg("Message", t, clientId, clientId, i, cliTimeOutMsg.rcvtime + netDlys[i]);
            pbfTsimulator.sendMsg(requestMsg, sendTag);
        }

        // Send a Timeout message to check if a timeout has occurred in the future ??
        setTimer(pbfTsimulator, t, cliTimeOutMsg.rcvtime);
    }

    /**
     *  // Deduplicate reply message
     *
     * @param msg  // reply message
     */
    public void saveReplyMsg(ReplyMsg msg)
    {
        Map<Integer, Message> rMap = repMsgs.get(msg.clientReqTimeStamp);
        for (Integer i : rMap.keySet())
        {
            if (i == msg.nodeId && ((ReplyMsg) rMap.get(i)).viewNumber >= msg.viewNumber)
            {
                return;
            }
        }
        repMsgs.get(msg.clientReqTimeStamp).put(msg.nodeId, msg);
    }

    /**
     *
     * // Determine whether the request message has reached a stable state (that is, f+1 reply messages have been received)
     *
     * @param msg  // Request message
     * @return  // Whether to reach a stable state
     */
    public boolean isStable(ReplyMsg msg)
    {
        Map<Integer, Message> rMap = repMsgs.get(msg.clientReqTimeStamp);
        int cnt = 0;
        for (Integer i : rMap.keySet())
        {
            if (((ReplyMsg) rMap.get(i)).viewNumber == msg.viewNumber && ((ReplyMsg) rMap.get(i)).reply == msg.reply)
            {
                cnt++;
            }
        }
        if (cnt > Utils.getMaxTorelentNumber(PBFTsimulator.numberOfNodes)) return true;
        return false;
    }

    /**
     *  // Get the client Id according to the array index
     *
     * @param index  // Represents the client's index in the array
     * @return  // Return client id
     */
    public static int getCliId(int index)
    {
        return index * (-1) - 1;
    }

    /**
     *  // Get the array index according to the client Id
     *
     * @param id  // Represents the client id
     * @return  // Return array subscript
     */
    public static int getCliArrayIndex(int id)
    {
        return (id + 1) * (-1);
    }

    public int stableMsgNum()
    {
        int cnt = 0;
        if (reqStats == null) return cnt;
        for (long t : reqStats.keySet())
            if (reqStats.get(t) == STABLE)
                cnt++;
        return cnt;
    }

    public void setTimer(PBFTsimulator pbfTsimulator, long t, long time)
    {
        Message timeoutMsg = new CliTimeOutMsg(t, clientId, clientId, time + PBFTsimulator.ClientTimeOut);
        pbfTsimulator.sendMsg(timeoutMsg, "ClientTimeOut");
    }
}
