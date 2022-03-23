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

import java.util.*;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Committee.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Committee 
public class Committee
{

    protected static int id;

    protected int comCap; // ## attribute ComCap

    protected int pql; // ## attribute PQL

    protected int peerQsize;

    protected Category CategoryOfCommittee; // ## link CategoryOfCommittee

    protected Consensus ConsensusOfCommittee; // ## link ConsensusOfCommittee

    protected Network NetworkOfCommittee; // ## link NetworkOfCommittee

    protected LinkedList<Peer> PeerOfCommittee = new LinkedList<Peer>(); // ## link PeerOfCommittee

    protected static int freeSeats;

    public Committee(int id, int comCap, int pql, int freeSeats)
    {
        Committee.id = id;
        this.comCap = comCap;
        this.pql = pql;
        Committee.freeSeats = freeSeats;
    }

    public Committee(int id)
    {
        Committee.id = id;
    }

    public Committee()
    {
        // TODO Auto-generated constructor stub
    }

    public int reduceActualFreeSeats()
    {
        return Committee.freeSeats = Committee.freeSeats - 1;
    }

    public int increaseActualFreeSeats()
    {
        return Committee.freeSeats = Committee.freeSeats + 1;
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
    public int insertPeerToQueue(Peer peer)
    {
        ArrayList<Peer> peerQ = new ArrayList<Peer>();
        peerQ.add(peer);
        peerQsize--;
        return peerQsize;
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
        Iterator<Peer> committeeMembers = this.getPeerOfCommittee();
        List<Peer> committeeMembersList = new ArrayList<>();
        committeeMembers.forEachRemaining(committeeMembersList::add);

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
    public static int getCommitteeID()
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
    public ListIterator<Peer> getPeerOfCommittee()
    {
        ListIterator<Peer> iter = PeerOfCommittee.listIterator();
        return iter;
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
    public Peer newPeerOfCommittee()
    {
        Peer newPeer = new Peer();
        newPeer._setCommitteeOfPeer(this);
        PeerOfCommittee.add(newPeer);
        return newPeer;
    }

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

    // ## auto_generated
    //	protected void initRelations() {
    //		ConsensusOfCommittee = newConsensusOfCommittee();
    //	}

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Committee.java
 *********************************************************************/
