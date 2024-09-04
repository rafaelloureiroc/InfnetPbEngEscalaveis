package com.infnet.infnetPB.listener;

import com.infnet.infnetPB.event.MesaCadastradaEvent;
import com.infnet.infnetPB.event.MesaReservadaEvent;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class MesaReservadaListener {

    private final static Logger logger = Logger.getLogger(MesaReservadaListener.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "mesaReservadaQueue")
    public void handleMesaReservadaEvent(MesaReservadaEvent event) {

        logger.info("Recebido evento mesaReservada: "+ event);

        boolean success = processEventWithRetry(event);

        if (success) {
            logger.info("Evento mesaReservada processado com sucesso.");
        } else {
            logger.error("Falha ao processar evento mesaReservada após" + MAX_RETRIES + " tentativas.");
        }
    }

    private boolean processEventWithRetry(MesaReservadaEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                messagingTemplate.convertAndSend("/topic/mesaReservada", event);
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