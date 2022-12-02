package com.engie.csai.pc.core.model;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class SendRabbitMQ
{
    private SendRabbitMQ()
    {
        // nothing to be implemented here
    }

    public static void send(String message, String queueName)
            throws IOException, TimeoutException
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Properties rabbitMQConf = new Properties();
        try {
            rabbitMQConf.load(SendRabbitMQ.class.getClassLoader().getResourceAsStream("rabbitmq.conf"));
        } catch (IOException errorLoadRabbitMQConfig) {
            errorLoadRabbitMQConfig.getStackTrace();
        }
        factory = factory.load(rabbitMQConf);
        factory.setHandshakeTimeout(900000);
        factory.setConnectionTimeout(900000);
        factory.setChannelRpcTimeout(900000);
        factory.setWorkPoolTimeout(900000);
        factory.setShutdownTimeout(900000);
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
