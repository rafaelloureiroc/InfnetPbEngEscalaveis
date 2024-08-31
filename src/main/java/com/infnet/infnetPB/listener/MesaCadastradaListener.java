package com.infnet.infnetPB.listener;

import com.infnet.infnetPB.event.MesaCadastradaEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MesaCadastradaListener {

    private static final Logger logger = LoggerFactory.getLogger(MesaCadastradaListener.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "mesaCadastradaQueue")
    public void handleMesaCadastradaEvent(MesaCadastradaEvent event) {

        logger.info("Recebido evento MesaCadastrada: {}", event);

        boolean success = processEventWithRetry(event);

        if (success) {
            logger.info("Evento MesaCadastrada processado com sucesso.");
        } else {
            logger.error("Falha ao processar evento MesaCadastrada após {} tentativas.", MAX_RETRIES);
        }
    }

    private boolean processEventWithRetry(MesaCadastradaEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                messagingTemplate.convertAndSend("/topic/mesaCadastrada", event);
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