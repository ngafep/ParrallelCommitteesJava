package com.engie.csai.pc.consensus.launcher;

import com.engie.csai.pc.core.model.Committee;
import com.engie.csai.pc.core.model.RecvRabbitMQ;
import com.engie.csai.pc.core.model.SendRabbitMQ;
import com.engie.csai.pc.core.service.CommitteeService;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public record OperationLauncher(String catId, Committee committee, CommitteeService committeeService) implements Runnable {

    @Override
    public void run() {
        try {
            launchOperationForOneCategoryWithoutRabbitMQ();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchOperationForOneCategoryWithoutRabbitMQ()
        throws IOException, TimeoutException {
        RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId, committee, committeeService);
        SendRabbitMQ.send(catId, "Queue" + catId);
    }
}
