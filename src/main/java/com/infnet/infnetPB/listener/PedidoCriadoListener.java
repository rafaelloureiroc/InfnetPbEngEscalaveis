package com.infnet.infnetPB.listener;

import com.infnet.infnetPB.event.MesaCadastradaEvent;
import com.infnet.infnetPB.event.PedidoCriadoEvent;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;

@Component
public class PedidoCriadoListener {

    private final static Logger logger = Logger.getLogger(PedidoCriadoListener.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "pedidoCriadoQueue")
    public void handlePedidoCriadoEvent(PedidoCriadoEvent event) {

        logger.info("Recebido evento PedidoCriado: " + event);

        boolean success = processEventWithRetry(event);

        if (success) {
            logger.info("Evento pedidoCriado processado com sucesso.");
        } else {
            logger.error("Falha ao processar evento pedidoCriado após" + MAX_RETRIES + " tentativas.");
        }

    }

    private boolean processEventWithRetry(PedidoCriadoEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                messagingTemplate.convertAndSend("/topic/pedidoCriado", event);
                return true;
            } catch (Exception e) {
                logger.error("Erro ao enviar evento (tentativa " + attempt + "): " + e.getMessage());
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

