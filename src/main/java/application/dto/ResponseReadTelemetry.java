package application.dto;

import application.entity.Telemetry;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseReadTelemetry {

  private List<Telemetry> allTelemetries;
}
