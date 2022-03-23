package com.engie.csai.pc.pbftSimulatorold.replica;

import com.engie.csai.pc.pbftSimulatorold.PBFTsimulator;

public class ByztReplica extends Replica{
	
	public static final String BTZTPROCESSTAG = "BtztProcess";
	
	public static final String BTZTRECEIVETAG = "BtztReceive";
	
	public static final String BTZTSENDTAG = "BtztSend";
	
	public ByztReplica(int id, int[] netDlys, int[]netDlysToClis, PBFTsimulator pbfTsimulator) {
		super(id, netDlys, netDlysToClis, pbfTsimulator);
		receiveTag = BTZTRECEIVETAG;
		sendTag = BTZTSENDTAG;
	}
	
	
}