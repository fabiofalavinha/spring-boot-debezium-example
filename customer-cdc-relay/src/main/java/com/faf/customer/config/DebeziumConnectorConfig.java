package com.faf.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class DebeziumConnectorConfig {

    @Value("${customer.datasource.host}")
    private String customerDbHost;

    @Value("${customer.datasource.database}")
    private String customerDbName;

    @Value("${customer.datasource.port}")
    private String customerDbPort;

    @Value("${customer.datasource.username}")
    private String customerDbUsername;

    @Value("${customer.datasource.password}")
    private String customerDbPassword;

    @Bean
    public io.debezium.config.Configuration customerConnector() throws IOException {
        return io.debezium.config.Configuration.create()
            .with("name", "customer-mysql-connector")
            .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", "./tmp/offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            .with("database.hostname", customerDbHost)
            .with("database.port", customerDbPort)
            .with("database.user", customerDbUsername)
            .with("database.password", customerDbPassword)
            .with("database.dbname", customerDbName)
            .with("database.include.list", customerDbName)
            .with("include.schema.changes", "false")
            .with("database.server.id", "10181")
            .with("database.allowPublicKeyRetrieval", "true")
            .with("database.server.name", "customer-mysql-db-server")
            .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
            .with("database.history.file.filename", "./tmp/dbhistory.dat")
            .build();
    }
}
