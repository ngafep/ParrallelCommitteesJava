package com.engie.csai.pc.service;

import com.engie.csai.pc.model.ClientRequestMessage;

public class ClientRequestDeserializer
{
    static ClientRequestMessage deserialize(String serializedClientRequest){
        ClientRequestMessage message = new ClientRequestMessage();
        String[] split = serializedClientRequest.split(" >>> ");
        String fullSender = split[1];
        String[] split1 = fullSender.split(": ");
        message.senderSignature = split1[1];
        return message;
    }
}
