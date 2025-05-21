package com.tripmakin.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @RabbitListener(queues = "tripmakin-queue")
    public void receiveMessage(String message) {
        System.out.println("Odebrano wiadomość: " + message);
    }
}
