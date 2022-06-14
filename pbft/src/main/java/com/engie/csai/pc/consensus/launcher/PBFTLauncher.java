package com.engie.csai.pc.consensus.launcher;

import com.engie.csai.pc.consensus.PBFTsimulator;
import com.engie.csai.pc.core.consensus.launcher.ConsensusLauncher;
import com.engie.csai.pc.core.model.Committee;
import com.engie.csai.pc.core.model.RecvRabbitMQ;
import com.engie.csai.pc.core.model.SendRabbitMQ;
import com.engie.csai.pc.core.service.CommitteeService;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public record PBFTLauncher(String catId, Committee committee, CommitteeService committeeService) implements ConsensusLauncher {

    @Override
    public void run() {
        try {
            RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId, committee, committeeService, new PBFTsimulator());
            SendRabbitMQ.send(catId, "Queue" + catId);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
