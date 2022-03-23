package com.engie.csai.pc.pbftSimulator.replica;

import com.engie.csai.pc.pbftSimulator.PBFTsimulator;
import com.engie.csai.pc.pbftSimulator.message.Message;

public class OfflineReplica extends Replica{
	
	public OfflineReplica(int id, int[] netDlys, int[] netDlysToClis, PBFTsimulator pbfTsimulator) {
		super(id, netDlys, netDlysToClis, pbfTsimulator);
	}
	
	public void msgProcess(Message msg) {
		msg.print("Disconnect");
		return;
	}
	
	
}
