/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: PeerSetting
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/PeerSetting.java
 *********************************************************************/

package com.engie.csai.pc.core.model;

import java.util.ArrayList;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/PeerSetting.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class PeerSetting 
public class PeerSetting
{

    protected int initQuota; // ## attribute initQuota

    protected float noInitTokens; // ## attribute noInitTokens

    protected Category CategoryOfPeerSetting; // ## link CategoryOfPeerSetting

    private String catId;

    // private String catId;

    // Constructors

    //    //## auto_generated
    //    public  PeerSetting() {
    //        initRelations();
    //    }
    public PeerSetting(int initQuota, float noInitTokens)
    {
        this.initQuota = initQuota;
        this.noInitTokens = noInitTokens;
        // this.setCatId(catId);
    }

    public PeerSetting(String catId)
    {
        this.catId = catId;
    }

    /**
     * This method returns the setting of a peer.
     *
     * @param selectedCat
     */
    // ## operation getPeerSetting(int)
    public ArrayList<String> getPeerSetting(String selectedCat)
    {
        return null;
        // #[ operation getPeerSetting(int)
        // #]
    }

    /**
     * @param quota
     * @param noInittokens
     * @param cat
     */
    // ## operation settingPeer(int,int,int)
    public void settingPeer(int quota, int noInittokens, int cat)
    {
        // #[ operation settingPeer(int,int,int)
        // #]
    }

    // ## auto_generated
    public int getInitQuota()
    {
        return initQuota;
    }

    // ## auto_generated
    public void setInitQuota(int p_initQuota)
    {
        initQuota = p_initQuota;
    }

    // ## auto_generated
    public float getNoInitTokens()
    {
        return noInitTokens;
    }

    // ## auto_generated
    public void setNoInitTokens(int p_noInitTokens)
    {
        noInitTokens = p_noInitTokens;
    }

    // ## auto_generated
    public Category getCategoryOfPeerSetting()
    {
        return CategoryOfPeerSetting;
    }

    // ## auto_generated
    public void __setCategoryOfPeerSetting(Category p_Category)
    {
        CategoryOfPeerSetting = p_Category;
    }

    // ## auto_generated
    public void _setCategoryOfPeerSetting(Category p_Category)
    {
        if (CategoryOfPeerSetting != null)
        {
            CategoryOfPeerSetting.__setPeerSettingOfCategory(null);
        }
        __setCategoryOfPeerSetting(p_Category);
    }

    // ## auto_generated
    public Category newCategoryOfPeerSetting()
    {
        CategoryOfPeerSetting = new Category(catId, initQuota, initQuota);
        CategoryOfPeerSetting._setPeerSettingOfCategory(this);
        return CategoryOfPeerSetting;
    }

    // ## auto_generated
    public void deleteCategoryOfPeerSetting()
    {
        CategoryOfPeerSetting.__setPeerSettingOfCategory(null);
        CategoryOfPeerSetting = null;
    }

    // ## auto_generated
    protected void initRelations()
    {
        CategoryOfPeerSetting = newCategoryOfPeerSetting();
    }

    public String getCatId()
    {
        return catId;
    }

    public void setCatId(String catId)
    {
        this.catId = catId;
    }

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/model/PeerSetting.java
 *********************************************************************/
