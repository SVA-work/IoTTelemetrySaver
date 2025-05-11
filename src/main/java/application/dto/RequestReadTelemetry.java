package application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestReadTelemetry {
    private String deviceName;
    private String login;
}