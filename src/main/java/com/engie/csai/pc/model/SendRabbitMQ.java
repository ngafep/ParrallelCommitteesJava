package com.engie.csai.pc.model;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class SendRabbitMQ
{

    // private final static String QUEUE_NAME = "hello";

    public SendRabbitMQ()
    {

    }

    public static void send(String message, String queueName)
            throws NoSuchAlgorithmException, IOException, TimeoutException
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel())
        {
            channel.queueDeclare(queueName, false, false, false, null);
            // String message = "Hello World!";
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(
                    " The massage (client-request): " + message + "\n has been sent via queue " + queueName + "\n" + "\n");
            System.out.println("           ");
        }
    }
}
