package com.engie.csai.pc.consensus.replica;

import com.engie.csai.pc.consensus.PBFTsimulator;
import com.engie.csai.pc.consensus.message.Message;

public class OfflineReplica extends Replica{
	
	public OfflineReplica(int id, int[] netDlys, int[] netDlysToClis, PBFTsimulator pbfTsimulator) {
		super(id, netDlys, netDlysToClis, pbfTsimulator);
	}
	
	public void msgProcess(Message msg) {
		msg.print("Disconnect");
		return;
	}
	
	
}
