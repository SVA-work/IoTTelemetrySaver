package application.dbconfiguration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScyllaConfig {

  @Bean
  public CqlSession cqlSession() {
    CqlSession session = CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .build();

    initializeSchema(session);

    return CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("my_keyspace")
        .build();
  }

  private void initializeSchema(CqlSession session) {
    session.execute(
        "CREATE KEYSPACE IF NOT EXISTS my_keyspace " +
            "WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 1}"
    );

    session.execute(
        "CREATE TABLE IF NOT EXISTS my_keyspace.telemetry (" +
            "    device_id TEXT," +
            "    time TIMESTAMP," +
            "    temperature TEXT," +
            "    humidity TEXT," +
            "    pressure TEXT," +
            "    aqi TEXT," +
            "    rssi TEXT," +
            "    snr TEXT," +
            "    PRIMARY KEY ((device_id), time)" +
            ") WITH CLUSTERING ORDER BY (time DESC)" +
            "   AND default_time_to_live = 31536000;"
    );
  }
}