package com.infnet.infnetPB.listener;

import com.infnet.infnetPB.event.MesaCadastradaEvent;
import com.infnet.infnetPB.event.RestauranteCadastradoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RestauranteCadastradoListener {

    private static final Logger logger = LoggerFactory.getLogger(MesaCadastradaListener.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "restauranteCadastradoQueue")
    public void handleRestauranteCadastradoEvent(RestauranteCadastradoEvent event) {

        logger.info("Recebido evento RestauranteCadastrado: {}", event);

        boolean success = processEventWithRetry(event);

        if (success) {
            logger.info("Evento RestauranteCadastrado processado com sucesso.");
        } else {
            logger.error("Falha ao processar evento RestauranteCadastrado após {} tentativas.", MAX_RETRIES);
        }
    }
    private boolean processEventWithRetry(RestauranteCadastradoEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                messagingTemplate.convertAndSend("/topic/restauranteCadastrado", event);
                return true;
            } catch (Exception e) {
                logger.error("Erro ao processar evento (tentativa {}): {}", attempt, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return false;
    }
}