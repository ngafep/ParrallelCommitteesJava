package com.engie.csai.pc.core.model;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Consensus.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Consensus 
public class Consensus
{

    protected static String consAlgo; // ## attribute ConsAlg

    protected Committee CommitteeOfConsensus; // ## link CommitteeOfConsensus

    protected Category CategoryOfConsensus; // ## link CategoryOfConsensus

    // Constructors

    // ## auto_generated
    public Consensus(String consAlgo)
    {
        Consensus.consAlgo = consAlgo;
    }

    // ## auto_generated
    public static String getConsAlg()
    {
        return consAlgo;
    }

    // ## auto_generated
    public void __setCommitteeOfConsensus(Committee p_Committee)
    {
        CommitteeOfConsensus = p_Committee;
    }

    // ## auto_generated
    public void _setCommitteeOfConsensus(Committee p_Committee)
    {
        if (CommitteeOfConsensus != null)
        {
            CommitteeOfConsensus.__setConsensusOfCommittee(null);
        }
        __setCommitteeOfConsensus(p_Committee);
    }

    // ## auto_generated
    public void _clearCommitteeOfConsensus()
    {
        CommitteeOfConsensus = null;
    }

    // ## auto_generated
    public Category getCategoryOfConsensus()
    {
        return CategoryOfConsensus;
    }

    // ## auto_generated
    public void setCategoryOfConsensus(Category p_Category)
    {
        if (p_Category != null)
        {
            p_Category._setConsensusOfCategory(this);
        }
        _setCategoryOfConsensus(p_Category);
    }

    // ## auto_generated
    public void __setCategoryOfConsensus(Category p_Category)
    {
        CategoryOfConsensus = p_Category;
    }

    // ## auto_generated
    public void _setCategoryOfConsensus(Category p_Category)
    {
        if (CategoryOfConsensus != null)
        {
            CategoryOfConsensus.__setConsensusOfCategory(null);
        }
        __setCategoryOfConsensus(p_Category);
    }

    // ## auto_generated
    public void _clearCategoryOfConsensus()
    {
        CategoryOfConsensus = null;
    }

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Consensus.java
 *********************************************************************/
