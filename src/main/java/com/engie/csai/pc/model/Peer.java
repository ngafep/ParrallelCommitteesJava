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
import java.util.ArrayList;
import java.util.List;

import com.engie.csai.pc.actors.Administrator;
import com.engie.csai.pc.actors.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    protected String address; // ## attribute address

    protected String catId;

    protected float tokensLocked; // ## attribute lockedTokens

    protected float tokensCurrent; // ## attribute noTokens

    protected float tokensInitial; // ## attribute noTokens

    protected int quotaCurrent; // ## attribute noQuota

    protected int quotaInitial; // ## attribute noQuota

    protected String privateKey; // ## attribute privateK

    protected String publicKey; // ## attribute publicKey

    protected Committee committeeOfPeer; // ## link CommitteeOfPeer

    protected Administrator AdminOfPeer; // ## link AdminOfPeer

    protected User UserOfPeer; // ## link UserOfPeer

    protected boolean isProcessor;

    public String powAnswer;

    public ClientRequestMessage clientRequestMessage;// = new ArrayList<Map<String,String>>();

    public boolean waiteInQ;

    // Constructors

    // ## auto_generated
    public Peer(String address, String catId, float tokensInitial, float tokensCurrent, float tokensLocked,
                int quotaInitial, int quotaCurrent, String privateKey, String publicKey, Committee committeeOfPeer,
                boolean isProcessor, String powAnswer, ClientRequestMessage clientRequestMessage, int target, boolean waiteInQ)
            throws NoSuchAlgorithmException
    {
        super();

        //		BigInteger target; // The target is initialised based on selected category.
        //		target = new BigInteger(
        //				"1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        powAnswer = PoW.powAnswer(catId, publicKey, target);
        System.out.flush();

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
        this.powAnswer = powAnswer;
        this.clientRequestMessage = clientRequestMessage;
        this.waiteInQ = waiteInQ;
    }

    public float updateActualTokens(float spentTokens)
    {
        return tokensCurrent = tokensInitial - spentTokens;
    }

    public int updateActualQuota(int usedQuota)
    {
        if(quotaCurrent - usedQuota>=0)
            quotaCurrent = quotaCurrent - usedQuota;
        return quotaCurrent;
    }

    // ## operation insertTxInFiFoQ()
    public void insertTxInFiFoQ()
    {
        // #[ operation insertTxInFiFoQ()
        // #]
    }

    /**
     * This method creates and sets up a new distributed data transmission using
     * sender signature, receiver address, metadata, and required fees regarding the
     * size of data.
     *
     * // @param sender
     * @param receivers
     * @param fee
     * @param data
     */
    // ## operation makeDistributedDataTransmission(Peer,Peer,int,String)
    public ClientRequestMessage makePDT(String senderSignature, ArrayList<String> receivers, int fee, ArrayList<String> data,
                                        List<String> validStatus)
    {

        // Map<String, String> ddt = new HashMap<String, String>();

        // Add keys and values (Country, City)
        //		ddt.put("senderSignature", senderSignature);
        //		ddt.put("receiverAddress", receiver);
        //		ddt.put("fee", fee);
        //		ddt.put("data", data);

        //		PDT pdt = new PDT();
        //		pdt.put("senderSignature", senderSignature);
        //		pdt.put("receiverAddresses", receivers);
        //		pdt.put("fee", fee);
        //		pdt.put("data", data);
        //		pdt.put("validStatus", validStatus);

        return clientRequestMessage;
        // #[ operation makeDDT(Peer,Peer,int,String)
        // #]
    }

    //	public void broadcastPDT(Map<String, List<String>> __pdt) {
    //		Committee committe = getCommitteeOfPeer();
    //		committe.notofyPDTtoCommitteeMembers(__pdt);
    //	}

    public void broadcastClientRequest(ClientRequestMessage clientRequestMessage)
    {
        Committee committe = getCommitteeOfPeer();
        committe.notofyPDTtoCommitteeMembers(clientRequestMessage);
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
        // #[ operation resetQuotaNotification(int)
        // #]
    }

    public String getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }

    public boolean isProcessor()
    {
        return isProcessor;
    }

    public void setProcessor(boolean isProcessor)
    {
        this.isProcessor = isProcessor;
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
    public float getLockedTokens()
    {
        return tokensLocked;
    }

    // ## auto_generated
    public void setLockedTokens(int p_lockedTokens)
    {
        tokensLocked = p_lockedTokens;
    }

    // ## auto_generated
    public int getActualQuota()
    {
        return quotaCurrent;
    }

    // ## auto_generated
    public void setNoQuota(int p_noQuota)
    {
        quotaCurrent = p_noQuota;
    }

    // ## auto_generated
    public float getNoTokens()
    {
        return tokensCurrent;
    }

    // ## auto_generated
    public void setNoTokens(int p_noTokens)
    {
        tokensCurrent = p_noTokens;
    }

    // ## auto_generated
    public String getPublicKey()
    {
        return publicKey;
    }

    // ## auto_generated
    public void setPublicKey(String p_publicKey)
    {
        publicKey = p_publicKey;
    }

    // ## auto_generated
    public Committee getCommitteeOfPeer()
    {
        return committeeOfPeer;
    }

    // ## auto_generated
    public void __setCommitteeOfPeer(Committee p_Committee)
    {
        committeeOfPeer = p_Committee;
    }

    // ## auto_generated
    public void _setCommitteeOfPeer(Committee p_Committee)
    {
        if (committeeOfPeer != null)
        {
            committeeOfPeer._removePeerOfCommittee(this);
        }
        __setCommitteeOfPeer(p_Committee);
    }

    // ## auto_generated
    public void setCommitteeOfPeer(Committee p_Committee)
    {
        if (p_Committee != null)
        {
            p_Committee._addPeerOfCommittee(this);
        }
        _setCommitteeOfPeer(p_Committee);
    }

    // ## auto_generated
    public void _clearCommitteeOfPeer()
    {
        committeeOfPeer = null;
    }

    public Administrator getAdminOfPeer()
    {
        return AdminOfPeer;
    }

    // ## auto_generated
    public void __setAdminOfPeer(Administrator p_Administrator)
    {
        AdminOfPeer = p_Administrator;
    }

    // ## auto_generated
    public void _setAdminOfPeer(Administrator p_Administrator)
    {
        if (AdminOfPeer != null)
        {
            AdminOfPeer._removePeerOfAdmin(this);
        }
        __setAdminOfPeer(p_Administrator);
    }

    // ## auto_generated
    public void setAdminOfPeer(Administrator p_Administrator)
    {
        if (p_Administrator != null)
        {
            p_Administrator._addPeerOfAdmin(this);
        }
        _setAdminOfPeer(p_Administrator);
    }

    // ## auto_generated
    public void _clearAdminOfPeer()
    {
        AdminOfPeer = null;
    }

    public User getUserOfPeer()
    {
        return UserOfPeer;
    }

    // ## auto_generated
    public void __setUserOfPeer(User p_User)
    {
        UserOfPeer = p_User;
    }

    // ## auto_generated
    public void _setUserOfPeer(User p_User)
    {
        if (UserOfPeer != null)
        {
            UserOfPeer.__setPeerOfUser(null);
        }
        __setUserOfPeer(p_User);
    }

    // ## auto_generated
    public void setUserOfPeer(User p_User)
    {
        if (p_User != null)
        {
            p_User._setPeerOfUser(this);
        }
        _setUserOfPeer(p_User);
    }

    // ## auto_generated
    public void _clearUserOfPeer()
    {
        UserOfPeer = null;
    }

    @Override
    public String toString()
    {
        return "address: " + this.address + ", catId: " + this.catId + ", tokensInitial: " + this.tokensInitial
                + "tokensCurrent: " + this.tokensCurrent + ", tokensLocked: " + this.tokensLocked + ", quotaInitial: "
                + this.quotaInitial + "quotaCurrent: " + this.quotaCurrent + ", privateKey: " + this.privateKey
                + ", publicKey: " + this.publicKey + "committeeOfPeer: " + this.committeeOfPeer + ", isProcessor: "
                + this.isProcessor + ", powAnswer: " + this.powAnswer + "pdt: " + this.clientRequestMessage + ", waiteInQ: "
                + this.waiteInQ;
        /**
         * (String address, String catId, float tokensInitial, float tokensCurrent,
         * float tokensLocked, int quotaInitial, int quotaCurrent, String privateKey,
         * String publicKey, Committee committeeOfPeer, boolean isProcessor, String
         * powAnswer, PDT pdt, int target, boolean waiteInQ)
         */
    }

}
/*********************************************************************
 * File Path : DefaultComponent/DefaultConfig/com/engie/csai/pc/model/Peer.java
 *********************************************************************/
