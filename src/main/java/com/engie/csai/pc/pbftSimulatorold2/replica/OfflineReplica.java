package com.engie.csai.pc.pbftSimulatorold2.replica;

import com.engie.csai.pc.pbftSimulatorold2.PBFTsimulator;
import com.engie.csai.pc.pbftSimulatorold2.message.Message;

public class OfflineReplica extends Replica{
	
	public OfflineReplica(int id, int[] netDlys, int[] netDlysToClis) {
		super(id, netDlys, netDlysToClis);
	}
	
	public void msgProcess(PBFTsimulator pbfTsimulator, Message msg) {
		msg.print("Disconnect");
		return;
	}
	
	
}
