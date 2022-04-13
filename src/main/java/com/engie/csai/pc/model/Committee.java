/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: Committee
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Committee.java
 *********************************************************************/

package com.engie.csai.pc.model;

//## auto_generated

import com.engie.csai.pc.pbftSimulator.subscriber.MessageSubscriber;
import com.engie.csai.pc.pbftSimulator.PBFTsimulator;
import lombok.Getter;
import org.apache.commons.codec.binary.StringUtils;

import java.util.*;

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

    protected int pql; // ## attribute PQL

    protected Category CategoryOfCommittee; // ## link CategoryOfCommittee

    protected Consensus ConsensusOfCommittee; // ## link ConsensusOfCommittee

    protected Network NetworkOfCommittee; // ## link NetworkOfCommittee

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

    public Committee(int id)
    {
        this.id = id;
    }

    public Committee()
    {
        // TODO Auto-generated constructor stub
    }

    public int reduceActualFreeSeats()
    {
        return this.freeSeats = this.freeSeats - 1;
    }

    public int increaseActualFreeSeats()
    {
        return this.freeSeats = this.freeSeats + 1;
    }

    public int getFreeSeats()
    {
        return freeSeats;
    }

    /**
     * This method assigns each peer to related committee, according to the selected
     * category based of which the peer has been created.
     *
     * @param peer
     */

    /**
     * This method creates a new peer, based on the selected category. For this, a
     * PoW with related difficulty must be solved.
     *
     * @param peerAddress
     */
    // ## operation createNewPeer(int)
    public void createNewPeer(String peerAddress)
    {
        // #[ operation createNewPeer(int)
        // #]
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

    /**
     * This method creates a new committee.
     *
     * @param ComCap
     * @param PQL
     */
    // ## operation newCom(int,int)
    public void newCom(int ComCap, int PQL)
    {
        // #[ operation newCom(int,int)
        // #]
    }

    /**
     * This method broadcasts a distributed data transmission in the committee by
     * notifying the members.
     *
     * @param clientRequestMessage
     */
    //	public void notofyPDTtoCommitteeMembers(Map<String, List<String>> __pdt) {
    //		Iterator<Peer> committeeMembers = this.getPeerOfCommittee();
    //		ArrayList<Peer> committeeMembersList = Lists.newArrayList(committeeMembers);
    //
    //		int s = committeeMembersList.size();
    //		int i = 0;
    //		while (s != 0) {
    //			committeeMembersList.get(i).pdt = new ArrayList<Map<String, List<String>>>();
    //			committeeMembersList.get(i).pdt.add(__pdt);
    //			i++;
    //			s--;
    //		}
    //		System.out.println("PDT has been broadcast to committee's members.");
    //		System.out.println("Consensus is going to start...");
    //	}
    public void notofyPDTtoCommitteeMembers(ClientRequestMessage clientRequestMessage)
    {
        List<Peer> committeeMembers = this.getPeerOfCommittee();
        List<Peer> committeeMembersList = new ArrayList<>();
        committeeMembers.iterator().forEachRemaining(committeeMembersList::add);

        int s = committeeMembersList.size();
        int i = 0;
        while (s != 0)
        {
            // committeeMembersList.get(i).pdt = new ArrayList<Map<String, List<String>>>();
            committeeMembersList.get(i).clientRequestMessage = clientRequestMessage;
            i++;
            s--;
        }
        System.out.println("\n PDT has been broadcast to committee's members.");
        //System.out.println("\n Consensus is going to start...");
    }

    /**
     * This method sets up the algorithm of the consensus, determining that which
     * consensus mechanism is used in each committee.
     *
     * @param consAlg
     */
    // ## operation setConsensus(RhpString)
    public void setConsensus(final String consAlg)
    {
        // #[ operation setConsensus(RhpString)
        // #]
    }

    /**
     * This method registers the result of each round of consensus for every
     * distributed data transmission.
     *
     * @param pdt
     */
    // ## operation setConsensusResult(ArrayList<String>)
    public void setConsensusResult(Map<String, String> pdt)
    {
        // #[ operation setConsensusResult(ArrayList<String>)
        // #]
    }

    // ## auto_generated
    public int getComCap()
    {
        return comCap;
    }

    // ## auto_generated
    public void setComCap(int p_ComCap)
    {
        comCap = p_ComCap;
    }

    // ## auto_generated
    public int getPQL()
    {
        return pql;
    }

    // ## auto_generated
    public void setPQL(int p_PQL)
    {
        pql = p_PQL;
    }

    // ## auto_generated
    public int getCommitteeID()
    {
        return id;
    }

    // ## auto_generated
    public void setCommitteeID(int p_committeeID)
    {
        id = p_committeeID;
    }

    // ## auto_generated
    public Category getCategoryOfCommittee()
    {
        return CategoryOfCommittee;
    }

    // ## auto_generated
    public void __setCategoryOfCommittee(Category p_Category)
    {
        CategoryOfCommittee = p_Category;
    }

    // ## auto_generated
    public void _setCategoryOfCommittee(Category p_Category)
    {
        if (CategoryOfCommittee != null)
        {
            CategoryOfCommittee.__setCommitteeOfCategory(null);
        }
        __setCategoryOfCommittee(p_Category);
    }

    // ## auto_generated
    public void setCategoryOfCommittee(Category p_Category)
    {
        if (p_Category != null)
        {
            p_Category._setCommitteeOfCategory(this);
        }
        _setCategoryOfCommittee(p_Category);
    }

    // ## auto_generated
    public void _clearCategoryOfCommittee()
    {
        CategoryOfCommittee = null;
    }

    // ## auto_generated
    public Consensus getConsensusOfCommittee()
    {
        return ConsensusOfCommittee;
    }

    // ## auto_generated
    public void __setConsensusOfCommittee(Consensus p_Consensus)
    {
        ConsensusOfCommittee = p_Consensus;
    }

    // ## auto_generated
    public void _setConsensusOfCommittee(Consensus p_Consensus)
    {
        if (ConsensusOfCommittee != null)
        {
            ConsensusOfCommittee.__setCommitteeOfConsensus(null);
        }
        __setConsensusOfCommittee(p_Consensus);
    }

    // ## auto_generated
    //	public Consensus newConsensusOfCommittee() {
    //		ConsensusOfCommittee = new Consensus(id);
    //		ConsensusOfCommittee._setCommitteeOfConsensus(this);
    //		return ConsensusOfCommittee;
    //	}

    // ## auto_generated
    public void deleteConsensusOfCommittee()
    {
        ConsensusOfCommittee.__setCommitteeOfConsensus(null);
        ConsensusOfCommittee = null;
    }

    // ## auto_generated
    public Network getNetworkOfCommittee()
    {
        return NetworkOfCommittee;
    }

    // ## auto_generated
    public void __setNetworkOfCommittee(Network p_Network)
    {
        NetworkOfCommittee = p_Network;
    }

    // ## auto_generated
    public void _clearNetworkOfCommittee()
    {
        NetworkOfCommittee = null;
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

    //	public int getFreeSeats() {
    //		return comCap - PeerOfCommittee.size();
    //	}

    // ## auto_generated
//    public Peer newPeerOfCommittee()
//    {
//        Peer newPeer = new Peer();
//        newPeer._setCommitteeOfPeer(this);
//        PeerOfCommittee.add(newPeer);
//        return newPeer;
//    }

    // ## auto_generated
    public void _removePeerOfCommittee(Peer p_Peer)
    {
        PeerOfCommittee.remove(p_Peer);
    }

    // ## auto_generated
    public void deletePeerOfCommittee(Peer p_Peer)
    {
        p_Peer._setCommitteeOfPeer(null);
        PeerOfCommittee.remove(p_Peer);
        p_Peer = null;
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
        if(peer.getActualQuota() == 0){

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

    public void subscribe(PBFTsimulator simulator){
        simulator.subscribe(this);
    }

    public void onMsgReceived(String msg){
        checkMsg(msg);
        executeProtocol();
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
        System.out.println("["+id+"] leader peer is: " + leaderPeer.getAddress() + " (" + leaderPeer.getActualQuota() + "/"+leaderPeer.quotaInitial + ")");
        leaderPeer.updateActualQuota(1);
        System.out.println("["+id+"] updated quotat is " + leaderPeer.getActualQuota() + "/"+leaderPeer.quotaInitial + ")");
        var newPeer = switchPeer(leaderPeer);
        if(newPeer != null)
        {
            System.out.println("[" + id + "] New Committee Peer is " + newPeer.getAddress() + " (" + newPeer.getActualQuota() + "/" + newPeer.quotaInitial + ")");
        }
        else{
            System.out.println("[" + id + "] Seat is free!");
        }
    }

    // ## auto_generated
    //	protected void initRelations() {
    //		ConsensusOfCommittee = newConsensusOfCommittee();
    //	}

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Committee.java
 *********************************************************************/
