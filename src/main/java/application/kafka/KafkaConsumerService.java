package application.kafka;

import application.dto.KafkaMessage;
import application.entity.Telemetry;
import application.service.TelemetryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final TelemetryService telemetryService;

    @KafkaListener(topics = "${topic-to-consume-message}")
    public void consumeMessage(String message, Acknowledgment acknowledgment) {
        try {
            KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
            LOGGER.info("Received audit message: {}", kafkaMessage);

            if (kafkaMessage.getCommand() == Command.WRITE_TELEMETRY) {
                Telemetry telemetry = objectMapper.readValue(kafkaMessage.getMessage(), Telemetry.class);
                telemetryService.insertTelemetry(telemetry);
            } else {
                LOGGER.error("Not valid command in request");
            }


            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error processing Kafka message: {}", message, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while processing audit message", e);
        }
    }
}