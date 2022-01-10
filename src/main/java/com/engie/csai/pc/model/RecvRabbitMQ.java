package com.engie.csai.pc.model;

import java.nio.charset.StandardCharsets;

import com.engie.csai.pc.pbftSimulator.PBFTsimulator;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RecvRabbitMQ
{

    // private final static String QUEUE_NAME = "hello";

    public static void recv(String queueName) throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [...] Waiting for messages. To exit press CTRL+C" + "\n" + "\n");

        DeliverCallback deliverCallback = (consumerTag, delivery) ->
        {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received '" + message + "'" + "\n" + "\n");
            /**
             * message should be returned to be used in PBFT launcher.
             */
            PBFTsimulator pbfTsimulator = new PBFTsimulator();
            pbfTsimulator.launch(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag ->
        {
        });
    }
}
