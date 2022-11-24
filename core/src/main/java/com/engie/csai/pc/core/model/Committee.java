/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: Committee
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Committee.java
 *********************************************************************/

package com.engie.csai.pc.core.model;

//## auto_generated

import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;
import com.engie.csai.pc.core.listener.EndMetrics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Committee.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Committee

public class Committee implements MessageSubscriber
{

    private int timeSpent = 0;
    private int numberOfMessages = 0;

    private int id;

    protected int comCap; // ## attribute ComCap

    @Getter
    private int pql; // ## attribute PQL

    protected Category CategoryOfCommittee; // ## link CategoryOfCommittee

    protected Consensus ConsensusOfCommittee; // ## link ConsensusOfCommittee

    protected LinkedList<Peer> PeerOfCommittee = new LinkedList<Peer>(); // ## link PeerOfCommittee

    private int freeSeats;

    @Getter
    private final List<Peer> peerQ = new ArrayList<>();


    public Committee(int id, int comCap, int pql, int freeSeats)
    {
        this.id = id;
        this.comCap = comCap;
        this.pql = pql;
        this.freeSeats = freeSeats;
    }

    public Committee()
    {
        // Dummy constructor
    }

    public int reduceActualFreeSeats()
    {
        return this.freeSeats = this.freeSeats - 1;
    }

    public int getFreeSeats()
    {
        return freeSeats;
    }

    /**
     * If there is no free seat in the committee, this method insert a peer in
     * waiting queue of of the committee.
     *
     * @param peer
     */
    public void insertPeerToQueue(Peer peer)
    {
        peerQ.add(peer);
    }

    public int getQueueSize(){
        return peerQ.size();
    }

    // ## auto_generated
    public void __setCategoryOfCommittee(Category p_Category)
    {
        CategoryOfCommittee = p_Category;
    }

    // ## auto_generated
    public void __setConsensusOfCommittee(Consensus p_Consensus)
    {
        ConsensusOfCommittee = p_Consensus;
    }

    // ## auto_generated
    public List<Peer> getPeerOfCommittee()
    {
        return PeerOfCommittee;
    }

    // ## auto_generated
    public void _addPeerOfCommittee(Peer p_Peer)
    {
        PeerOfCommittee.add(p_Peer);
    }

    // ## auto_generated
    public void _removePeerOfCommittee(Peer p_Peer)
    {
        PeerOfCommittee.remove(p_Peer);
    }

    public Peer selectLeaderPeer()
    {

        Random r = new Random();
        List<Peer> peerOfCommittee = getPeerOfCommittee();
        if (peerOfCommittee.isEmpty())
        {
            return null;
        }
        return peerOfCommittee.get(r.nextInt(peerOfCommittee.size()));
    }

    public Peer switchPeer(Peer peer)
    {
        if(peer.getQuotaCurrent() == 0){

            // remove the peer from committee
            this._removePeerOfCommittee(peer);
            peer.resetQuota();
            // get a waiting peer in the queue and remove it from the queue
            if(getQueueSize()>0)
            {
                int waitingPeerIndex = new Random().nextInt(getQueueSize());
                var waitingPeer = peerQ.get(waitingPeerIndex);
                peerQ.remove(waitingPeer);

                // insert the initial committee peer in the queue
                this.insertPeerToQueue(peer);

                // insert the waiting peer in the committee
                this._addPeerOfCommittee(waitingPeer);
                return waitingPeer;
            }
            this.insertPeerToQueue(peer);
            return null;
        }
        return peer;
    }

    public void subscribe(ConsensusSimulator simulator){
        simulator.subscribe(this);
    }

    public void onMsgReceived(String msg){
        checkMsg(msg);
        executeProtocol();
    }

    @Override
    public void onMsgReceived(EndMetrics metrics) {

    }

    public int getTimeSpent()
    {
        return timeSpent;
    }

    public int getNumberOfMessages()
    {
        return numberOfMessages;
    }

    private void checkMsg(String msg)
    {
        System.out.println("["+id+"]------ checking : " + msg.substring(0,30));
        numberOfMessages++;
        String timeString = msg.substring(msg.indexOf("Total time: "),msg.indexOf("Total time: ")+30);
        String[] splitTimeString = timeString.split(" ");
        timeSpent += Integer.parseInt(splitTimeString[2]);
        System.out.println("["+id+"] ("+System.currentTimeMillis()+") Cumulative time spent is : " + timeSpent);
        System.out.println("["+id+"] ("+System.currentTimeMillis()+") mean time of the committee : " + ((float)timeSpent/numberOfMessages));
    }

    private void executeProtocol()
    {
        Peer leaderPeer = selectLeaderPeer();
        System.out.println("["+id+"] leader peer is: " + leaderPeer.getAddress() + " (" + leaderPeer.getQuotaCurrent() + "/"+leaderPeer.getQuotaInitial() + ")");
        leaderPeer.updateActualQuota(1);
        System.out.println("["+id+"] updated quotat is " + leaderPeer.getQuotaCurrent() + "/"+leaderPeer.getQuotaInitial() + ")");
        var newPeer = switchPeer(leaderPeer);
        if(newPeer != null)
        {
            System.out.println("[" + id + "] New Committee Peer is " + newPeer.getAddress() + " (" + newPeer.getQuotaCurrent() + "/" + newPeer.getQuotaInitial() + ")");
        }
        else{
            System.out.println("[" + id + "] Seat is free!");
        }
    }
}
