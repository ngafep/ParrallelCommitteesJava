package com.engie.csai.pc.consensus.launcher;

import com.engie.csai.pc.consensus.PBFTsimulator;
import com.engie.csai.pc.core.consensus.launcher.ConsensusLauncher;
import com.engie.csai.pc.core.model.RecvRabbitMQ;
import com.engie.csai.pc.core.models.Committee;
import com.engie.csai.pc.core.service.CommitteeService;

public record PBFTLauncher(String catId, Committee committee,
                           CommitteeService committeeService) implements ConsensusLauncher {

    @Override
    public void run() {
        RecvRabbitMQ.standbyForReceiveMessages(catId, committee, committeeService);
    }

    @Override
    public void register() {
        RecvRabbitMQ.register(catId, committee, committeeService, new PBFTsimulator());
    }
}
