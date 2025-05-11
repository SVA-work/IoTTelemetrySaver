package application.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Telemetry {
    private String deviceId;
    private Instant time;
    private String temperature;
    private String humidity;
    private String pressure;
    private String aqi;
    private String rssi;
    private String snr;
}
