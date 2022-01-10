package com.engie.csai.pc.pbftSimulator.replica;

import com.engie.csai.pc.pbftSimulator.PBFTsimulator;
import com.engie.csai.pc.pbftSimulator.message.Message;

public class OfflineReplica extends Replica{
	
	public OfflineReplica(int id, int[] netDlys, int[] netDlysToClis) {
		super(id, netDlys, netDlysToClis);
	}
	
	public void msgProcess(PBFTsimulator pbfTsimulator, Message msg) {
		msg.print("Disconnect");
		return;
	}
	
	
}
