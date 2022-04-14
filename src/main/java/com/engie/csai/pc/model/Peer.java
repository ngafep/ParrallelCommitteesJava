/*********************************************************************
 Rhapsody	: 9.0.1
 Login		: KX5710
 Component	: DefaultComponent
 Configuration 	: DefaultConfig
 Model Element	: Peer
 //!	Generated Date	: Wed, 27, Oct 2021
 File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Peer.java
 *********************************************************************/

package com.engie.csai.pc.model;

import java.security.NoSuchAlgorithmException;

import com.engie.csai.pc.actors.Administrator;
import com.engie.csai.pc.actors.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//----------------------------------------------------------------------------
// com/engie/csai/pc/model/Peer.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::model 

//## class Peer
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Peer
{

    private String address; // ## attribute address

    private String catId;

    private float tokensLocked; // ## attribute lockedTokens

    private float tokensCurrent; // ## attribute noTokens

    private float tokensInitial; // ## attribute noTokens

    @Getter
    private int quotaCurrent; // ## attribute noQuota

    @Getter
    private int quotaInitial; // ## attribute noQuota

    private String privateKey; // ## attribute privateK

    private String publicKey; // ## attribute publicKey

    private Committee committeeOfPeer; // ## link CommitteeOfPeer

    private Administrator adminOfPeer; // ## link AdminOfPeer

    private User userOfPeer; // ## link UserOfPeer

    private boolean isProcessor;

    @Getter
    private String powAnswer;

    // Constructors

    // ## auto_generated
    public Peer(String address, String catId, float tokensInitial, float tokensCurrent, float tokensLocked,
                int quotaInitial, int quotaCurrent, String privateKey, String publicKey, Committee committeeOfPeer,
                boolean isProcessor,  int target)
            throws NoSuchAlgorithmException
    {
        super();

        this.powAnswer = PoW.powAnswer(catId, publicKey, target);
        this.address = address;
        this.tokensLocked = tokensLocked;
        this.quotaCurrent = quotaCurrent;
        this.quotaInitial = quotaInitial;
        this.tokensCurrent = tokensCurrent;
        this.tokensInitial = tokensInitial;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.committeeOfPeer = committeeOfPeer;
        this.isProcessor = isProcessor;
    }

    public int updateActualQuota(int usedQuota)
    {
        if(quotaCurrent - usedQuota>=0)
            quotaCurrent = quotaCurrent - usedQuota;
        return quotaCurrent;
    }


    /**
     * If number of peers in queue of the committee is than PQL, this method resets
     * value of the quota in related committee.
     *
     * @param reducedQuota
     */
    // ## operation resetQuota(int)
    public int resetQuota(int reducedQuota)
    {
        return quotaInitial - reducedQuota;
        // #[ operation resetQuota(int)
        // #]
    }

    public void resetQuota()
    {
        quotaCurrent = quotaInitial;
    }

    /**
     * If number of authorized peers in waiting queue of the committee is more than
     * PQL, using this method, a request for re-configuring the quota is sent to
     * administrator.
     *
     * // @param Cat
     */
    public void resetQuotaNotification(String catId)
    {
        Administrator admin = new Administrator();
        admin.resetQuotaNotification(catId);
    }

    public boolean isProcessor()
    {
        return isProcessor;
    }

    // ## auto_generated
    public String getAddress()
    {
        return address;
    }

    // ## auto_generated
    public Peer setAddress(String p_address)
    {
        address = p_address;
        return this;
    }

    // ## auto_generated
    public void __setCommitteeOfPeer(Committee p_Committee)
    {
        committeeOfPeer = p_Committee;
    }

    // ## auto_generated
    public void __setAdminOfPeer(Administrator p_Administrator)
    {
        adminOfPeer = p_Administrator;
    }

    // ## auto_generated
    public void _setAdminOfPeer(Administrator p_Administrator)
    {
        if (adminOfPeer != null)
        {
            adminOfPeer._removePeerOfAdmin(this);
        }
        __setAdminOfPeer(p_Administrator);
    }

    // ## auto_generated
    public void __setUserOfPeer(User p_User)
    {
        userOfPeer = p_User;
    }

    // ## auto_generated
    public void _setUserOfPeer(User p_User)
    {
        if (userOfPeer != null)
        {
            userOfPeer.__setPeerOfUser(null);
        }
        __setUserOfPeer(p_User);
    }

    @Override
    public String toString()
    {
        return "address: " + this.address + ", catId: " + this.catId + ", tokensInitial: " + this.tokensInitial
                + "tokensCurrent: " + this.tokensCurrent + ", tokensLocked: " + this.tokensLocked + ", quotaInitial: "
                + this.quotaInitial + "quotaCurrent: " + this.quotaCurrent + ", privateKey: " + this.privateKey
                + ", publicKey: " + this.publicKey + "committeeOfPeer: " + this.committeeOfPeer + ", isProcessor: "
                + this.isProcessor + ", powAnswer: " + this.powAnswer;
        /*
         * (String address, String catId, float tokensInitial, float tokensCurrent,
         * float tokensLocked, int quotaInitial, int quotaCurrent, String privateKey,
         * String publicKey, Committee committeeOfPeer, boolean isProcessor, String
         * powAnswer, PDT pdt, int target, boolean waiteInQ)
         */
    }

}
