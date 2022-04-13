/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: Network
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Network.java
 *********************************************************************/

package com.engie.csai.pc.model;

//## auto_generated

import java.util.HashMap;
import java.util.Map;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Network.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Network 
public class Network
{

    protected int noCats; // ## attribute noCats

    protected Map<String, Category> categories = new HashMap<String, Category>(); // ## link CategoryOfNetwork

    // Constructors

    // ## auto_generated
    public Network()
    {
    }

    /**
     * This method adds a category to the list of categories.
     *
     * @param begin
     * @param end
     */
    // ## operation addCat(int,int)
    public void addCategory(String catId, int begin, int end)
    {
        Category category = new Category(catId, begin, end);
        category.setNetworkOfCategory(this);
        categories.put(catId, category);
    }

    // ## auto_generated
    public int getNoCats()
    {
        return noCats;
    }

    // ## auto_generated
    public void setNoCats(int p_noCats)
    {
        noCats = p_noCats;
    }

    // ## auto_generated
    public Map<String, Category> getCategories()
    {
        return categories;
    }

    // ## auto_generated
    public void _addCategoryOfNetwork(Category p_Category)
    {
        // categories.add(p_Category);
        categories.put(null, null);
    }

    // ## auto_generated
    public void _removeCategoryOfNetwork(Category p_Category)
    {
        categories.remove(p_Category);
    }

    // ## auto_generated
    public void deleteCategoryOfNetwork(Category p_Category)
    {
        p_Category._setNetworkOfCategory(null);
        categories.remove(p_Category);
        p_Category = null;
    }

    public void addCommittee(Committee committee, int capCom, int pql, String catId, int freeSeats)
    {
        Category category = categories.get(catId);
        category.__setCommitteeOfCategory(committee);
        committee.__setCategoryOfCommittee(category);

    }

    public void settingPeersForEachCategory(int quotaInit, float noInitTokens, String catId)
    {
        PeerSetting peerSetting = new PeerSetting(quotaInit, noInitTokens);
        Category category = categories.get(catId);
        category.__setPeerSettingOfCategory(peerSetting);
        peerSetting.__setCategoryOfPeerSetting(category);
    }

    public void setConsAlgoForEachCategory(String algoConsensus, String catId)
    {
        Consensus consensus = new Consensus(algoConsensus);
        Category category = categories.get(catId);
        category.__setConsensusOfCategory(consensus);
        consensus.__setCategoryOfConsensus(category);
    }

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Network.java
 *********************************************************************/
