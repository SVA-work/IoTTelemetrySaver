package application.controller;

import application.dto.RequestReadTelemetry;
import application.entity.Telemetry;
import application.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@RestController
public class TelemetryController {
  private final TelemetryService telemetryService;

  @GetMapping("/telemetry/{deviceName}/{login}")
  public Collection<Telemetry> deviceTelemetry(@PathVariable String deviceName, @PathVariable String login) {
    RequestReadTelemetry request = new RequestReadTelemetry(deviceName, login);
    return telemetryService.readTelemetry(request);
  }
}
