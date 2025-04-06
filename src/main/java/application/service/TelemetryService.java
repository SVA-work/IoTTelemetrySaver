package application.service;

import application.dto.RequestReadTelemetry;
import application.dto.ResponseReadTelemetry;
import application.entity.Telemetry;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

  public ResponseReadTelemetry readTelemetry(RequestReadTelemetry request) {
    BoundStatement boundStatement = selectStatement.bind(request.getDeviceId());
    ResultSet result = session.execute(boundStatement);

    List<Telemetry> audits = result.all().stream()
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
        .collect(Collectors.toList());

    return new ResponseReadTelemetry(audits);
  }
}