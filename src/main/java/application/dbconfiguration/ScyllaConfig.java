package application.dbconfiguration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScyllaConfig {

    private final String host;
    private final int port;

    public ScyllaConfig(@Value("${scylla.host}") String host, @Value("${scylla.port}") int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public CqlSession cqlSession() {
        CqlSession session = CqlSession.builder()
            .addContactPoint(InetSocketAddress.createUnresolved(host, port))
            .withLocalDatacenter("datacenter1")
            .build();

        initializeSchema(session);

        return CqlSession.builder()
            .addContactPoint(InetSocketAddress.createUnresolved(host, port))
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