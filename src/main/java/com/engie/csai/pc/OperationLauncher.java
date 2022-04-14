package com.engie.csai.pc;

import com.engie.csai.pc.model.*;
import com.engie.csai.pc.model.json.ClientRequestJson;
import com.engie.csai.pc.model.json.ClientRequestsJson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OperationLauncher extends Thread
{

    private final String catId;
    private final int numberOfPeer;
    private final int nbRequests;
    private final Committee committee;

    public OperationLauncher(String catId, int numberOfPeer, int nbRequests, Committee committee)
    {
        this.catId = catId;
        this.numberOfPeer = numberOfPeer;
        this.nbRequests = nbRequests;
        this.committee = committee;
    }

    @Override
    public void run()
    {
        try
        {
            launchOperationForOneCategoryWithoutRabbitMQ();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void launchOperationForOneCategoryWithoutRabbitMQ() throws Exception{
        RecvRabbitMQ.standbyForReceiveMessages(catId, "Queue" + catId, committee);
        SendRabbitMQ.send(catId, "Queue" + catId);
    }

}
