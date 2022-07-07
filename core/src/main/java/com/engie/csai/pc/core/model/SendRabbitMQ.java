package com.engie.csai.pc.core.model;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class SendRabbitMQ
{
    public SendRabbitMQ()
    {

    }

    public static void send(String message, String queueName)
            throws IOException, TimeoutException
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel())
        {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
//            System.out.println(
//                    " Client request: " + message + "\n has been sent via queue " + queueName + "\n" + "\n");
//            System.out.println("           ");
        }
    }
}
