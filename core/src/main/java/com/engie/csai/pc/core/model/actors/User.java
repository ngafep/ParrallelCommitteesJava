/*********************************************************************
	Rhapsody	: 9.0.1
	Login		: KX5710
	Component	: DefaultComponent
	Configuration 	: DefaultConfig
	Model Element	: User
//!	Generated Date	: Wed, 27, Oct 2021 
	File Path	: DefaultComponent/DefaultConfig/com/engie/csai/pc/actors/User.java
*********************************************************************/

package com.engie.csai.pc.core.model.actors;


//----------------------------------------------------------------------------
// com/engie/csai/pc/actors/User.java                                                                  
//----------------------------------------------------------------------------

//## package com::engie::csai::pc::actors 

import com.engie.csai.pc.core.model.Peer;

/**
 * [[ * @author $Author]] [[ * @version $Version]] [[ * @see $See]] [[ * @since
 * $Since]]
 */
//## actor User 
public class User {

	protected Peer PeerOfUser; // ## link PeerOfUser

	// Constructors

	// ## auto_generated
	public User() {
	}

	// ## auto_generated
	public Peer getPeerOfUser() {
		return PeerOfUser;
	}

	// ## auto_generated
	public void __setPeerOfUser(Peer p_Peer) {
		PeerOfUser = p_Peer;
	}

	// ## auto_generated
	public void _setPeerOfUser(Peer p_Peer) {
		if (PeerOfUser != null) {
			PeerOfUser.__setUserOfPeer(null);
		}
		__setPeerOfUser(p_Peer);
	}

	// ## auto_generated
	public Peer newPeerOfUser() {
		PeerOfUser = new Peer();
		PeerOfUser._setUserOfPeer(this);
		return PeerOfUser;
	}

//	public PDT makeP2PdataTransfer(ArrayList<String> sender, ArrayList<String> receivers,
//			ArrayList<String> fee, ArrayList<String> data, List<String> validStatus) {
//		PeerOfUser = new Peer();
//		PDT _pdt;// = new HashMap<String, String>();
//		_pdt = PeerOfUser.makePDT(sender, receivers, fee, data, validStatus);
//		return _pdt;
//	}

}
/*********************************************************************
 * File Path : DefaultComponent/DefaultConfig/com/engie/csai/pc/actors/User.java
 *********************************************************************/
