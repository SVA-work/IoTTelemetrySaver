package application.dto;

import application.kafka.Command;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaMessage {
  private Command command;
  private String message;
}
