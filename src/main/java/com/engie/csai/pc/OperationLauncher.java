package com.engie.csai.pc;

import com.engie.csai.pc.model.Committee;
import com.engie.csai.pc.model.RecvRabbitMQ;
import com.engie.csai.pc.model.SendRabbitMQ;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public record OperationLauncher(String catId, Committee committee) implements Runnable {

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
        RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId, committee);
        SendRabbitMQ.send(catId, "Queue" + catId);
    }
}
