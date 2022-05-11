/*********************************************************************
	Rhapsody	: 9.0.1
	Login		: KX5710
	Component	: DefaultComponent
	Configuration 	: DefaultConfig
	Model Element	: Administrator
//!	Generated Date	: Wed, 27, Oct 2021 
	File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/actors/Administrator.java
*********************************************************************/

package com.engie.csai.pc.core.model.actors;

import com.engie.csai.pc.core.model.Peer;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

//----------------------------------------------------------------------------
// com/engie/csai/pc/actors/Administrator.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::actors 

/**
 * [[ * @author $Author]] [[ * @version $Version]] [[ * @see $See]] [[ * @since
 * $Since]]
 */
//## actor Administrator 
public class Administrator {
	protected LinkedList<Peer> PeerOfAdmin = new LinkedList<Peer>(); // ## link PeerOfAdmin

	// Constructors

	// ## auto_generated
	public Administrator() {
	}

	// ## auto_generated
	public ListIterator<Peer> getPeerOfAdmin() {
		ListIterator<Peer> iter = PeerOfAdmin.listIterator();
		return iter;
	}

	// ## auto_generated
	public void _addPeerOfAdmin(Peer p_Peer) {
		PeerOfAdmin.add(p_Peer);
	}

	// ## auto_generated
	public Peer newPeerOfAdmin() {
		Peer newPeer = new Peer();
		newPeer._setAdminOfPeer(this);
		PeerOfAdmin.add(newPeer);
		return newPeer;
	}

	// ## auto_generated
	public void _removePeerOfAdmin(Peer p_Peer) {
		PeerOfAdmin.remove(p_Peer);
	}

	// ## auto_generated
	public void deletePeerOfAdmin(Peer p_Peer) {
		p_Peer._setAdminOfPeer(null);
		PeerOfAdmin.remove(p_Peer);
		p_Peer = null;
	}

	public void resetQuotaNotification(String catId) {
//		Administrator sees a message of request for
//		resetting up the quota in related category.
		String resetQuotaMessage = "Request for resetting up the quota in category:" + catId;
		System.out.println(resetQuotaMessage);
		Peer peer = newPeerOfAdmin();
		System.out.println("Enter category of address/account/node/peer:");
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		int newQuota = sc.nextInt();
		peer.resetQuota(newQuota);
		System.out.println("Quota was reset to a new value: " + newQuota);
	}

}
/*********************************************************************
 * File Path :
 * DefaultComponent/DefaultConfig/com/engie/csai/pc/actors/Administrator.java
 *********************************************************************/
