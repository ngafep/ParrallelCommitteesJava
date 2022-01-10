/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: Category
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Category.java
 *********************************************************************/

package com.engie.csai.pc.model;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Category.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Category 
public class Category
{

    protected final String id;

    protected final int begin; // ## attribute begin

    protected final int end; // ## attribute end

    protected Committee CommitteeOfCategory; // ## link CommitteeOfCategory

    protected Network NetworkOfCategory; // ## link NetworkOfCategory

    protected PeerSetting PeerSettingOfCategory; // ## link PeerSettingOfCategory

    protected Consensus ConsensusOfCategory; // ## link ConsensusOfCategory

    // Constructors

    /**
     * @param begin
     * @param end
     */
    // ## operation Category(int,int)
    public Category(String id, int begin, int end)
    {
        this.id = id;
        this.begin = begin;
        this.end = end;
    }

    /**
     * @param committee
     */
    // ## operation setCom(Committee)
    public void setCom(final Committee committee)
    {
        // #[ operation setCom(Committee)
        // #]
    }

    /**
     * This method sets up the peer setting.
     *
     * @param peerSetting
     */
    // ## operation setPeerSetting(Peer)
    public void setPeerSetting(final Peer peerSetting)
    {
        // #[ operation setPeerSetting(Peer)
        // #]
    }

    // ## auto_generated
    public final int getBegin()
    {
        return begin;
    }

    // ## auto_generated
    public final int getEnd()
    {
        return end;
    }

    // ## auto_generated
    public final int getNoCats()
    {
        return getNoCats();
    }

    // ## auto_generated
    public Committee getCommitteeOfCategory()
    {
        return CommitteeOfCategory;
    }

    // ## auto_generated
    public void __setCommitteeOfCategory(Committee p_Committee)
    {
        CommitteeOfCategory = p_Committee;
    }

    // ## auto_generated
    public void _setCommitteeOfCategory(Committee p_Committee)
    {
        if (CommitteeOfCategory != null)
        {
            CommitteeOfCategory.__setCategoryOfCommittee(null);
        }
        __setCommitteeOfCategory(p_Committee);
    }

    // ## auto_generated
    public Committee newCommitteeOfCategory()
    {
        CommitteeOfCategory = new Committee();
        CommitteeOfCategory._setCategoryOfCommittee(this);
        return CommitteeOfCategory;
    }

    // ## auto_generated
    public void deleteCommitteeOfCategory()
    {
        CommitteeOfCategory.__setCategoryOfCommittee(null);
        CommitteeOfCategory = null;
    }

    // ## auto_generated
    public Network getNetworkOfCategory()
    {
        return NetworkOfCategory;
    }

    // ## auto_generated
    public void __setNetworkOfCategory(Network p_Network)
    {
        NetworkOfCategory = p_Network;
    }

    // ## auto_generated
    public void _setNetworkOfCategory(Network p_Network)
    {
        if (NetworkOfCategory != null)
        {
            NetworkOfCategory._removeCategoryOfNetwork(this);
        }
        __setNetworkOfCategory(p_Network);
    }

    // ## auto_generated
    public void setNetworkOfCategory(Network p_Network)
    {
        if (p_Network != null)
        {
            p_Network._addCategoryOfNetwork(this);
        }
        _setNetworkOfCategory(p_Network);
    }

    // ## auto_generated
    public void _clearNetworkOfCategory()
    {
        NetworkOfCategory = null;
    }

    // ## auto_generated
    public PeerSetting getPeerSettingOfCategory()
    {
        return PeerSettingOfCategory;
    }

    // ## auto_generated
    public void __setPeerSettingOfCategory(PeerSetting p_PeerSetting)
    {
        PeerSettingOfCategory = p_PeerSetting;
    }

    // ## auto_generated
    public void _setPeerSettingOfCategory(PeerSetting p_PeerSetting)
    {
        if (PeerSettingOfCategory != null)
        {
            PeerSettingOfCategory.__setCategoryOfPeerSetting(null);
        }
        __setPeerSettingOfCategory(p_PeerSetting);
    }

    // ## auto_generated
    public void setPeerSettingOfCategory(PeerSetting p_PeerSetting)
    {
        if (p_PeerSetting != null)
        {
            p_PeerSetting._setCategoryOfPeerSetting(this);
        }
        _setPeerSettingOfCategory(p_PeerSetting);
    }

    // ## auto_generated
    public void _clearPeerSettingOfCategory()
    {
        PeerSettingOfCategory = null;
    }

    /**
     * @param consAlg
     */
    // ## operation setConsensus(String)
    public void setConsensus(final String consAlg)
    {
        // #[ operation setConsensus(String)
        // #]
    }

    // ## auto_generated
    public Consensus getConsensusOfCategory()
    {
        return ConsensusOfCategory;
    }

    // ## auto_generated
    public void __setConsensusOfCategory(Consensus p_Consensus)
    {
        ConsensusOfCategory = p_Consensus;
    }

    // ## auto_generated
    public void _setConsensusOfCategory(Consensus p_Consensus)
    {
        if (ConsensusOfCategory != null)
        {
            ConsensusOfCategory.__setCategoryOfConsensus(null);
        }
        __setConsensusOfCategory(p_Consensus);
    }

    // ## auto_generated
    public Consensus newConsensusOfCategory()
    {
        ConsensusOfCategory = new Consensus(id);
        ConsensusOfCategory._setCategoryOfConsensus(this);
        return ConsensusOfCategory;
    }

    // ## auto_generated
    public void deleteConsensusOfCategory()
    {
        ConsensusOfCategory.__setCategoryOfConsensus(null);
        ConsensusOfCategory = null;
    }

    // ## auto_generated
    protected void initRelations()
    {
        CommitteeOfCategory = newCommitteeOfCategory();
        ConsensusOfCategory = newConsensusOfCategory();
    }

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Category.java
 *********************************************************************/
