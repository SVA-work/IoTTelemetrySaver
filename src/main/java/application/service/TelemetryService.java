package application.service;

import application.dto.DeviceIdDto;
import application.dto.RequestReadTelemetry;
import application.entity.Telemetry;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Service
public class TelemetryService {

  private final CqlSession session;
  private final PreparedStatement insertStatement;
  private final PreparedStatement selectStatement;

  @Autowired
  public TelemetryService(CqlSession session) {
    this.session = session;
    this.insertStatement = session.prepare(
        "INSERT INTO my_keyspace.telemetry " +
            "(device_id, time, temperature, humidity, pressure, aqi, rssi, snr) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    );
    this.selectStatement = session.prepare(
        "SELECT device_id, time, temperature, humidity, pressure, aqi, rssi, snr " +
            "FROM my_keyspace.telemetry WHERE device_id = ?"
    );
  }

  public void insertTelemetry(Telemetry request) {
    BoundStatement boundStatement = insertStatement.bind(
        request.getDeviceId(),
        request.getTime(),
        request.getTemperature(),
        request.getHumidity(),
        request.getPressure(),
        request.getAqi(),
        request.getRssi(),
        request.getSnr()
    );
    session.execute(boundStatement);
  }

  public Collection<Telemetry> readTelemetry(RequestReadTelemetry request) {
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<DeviceIdDto> response = restTemplate.getForEntity(
        "http://localhost:8091/device/" + request.getLogin() + "/" + request.getDeviceName(), DeviceIdDto.class);

    BoundStatement boundStatement = selectStatement.bind(response.getBody().getId());
    ResultSet result = session.execute(boundStatement);

    return result.all().stream()
        .map(row -> new Telemetry(
            row.getString("device_id"),
            row.getInstant("time"),
            row.getString("temperature"),
            row.getString("humidity"),
            row.getString("pressure"),
            row.getString("aqi"),
            row.getString("rssi"),
            row.getString("snr")
        ))
        .toList();
  }
}