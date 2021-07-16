package com.faf.student.listener;

import com.faf.student.elasticsearch.service.StudentService;
import com.faf.student.utils.Operation;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class ChangeDataCaptureListener {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final EmbeddedEngine engine;
    private final StudentService studentService;

    private ChangeDataCaptureListener(Configuration studentConnector, StudentService studentService) {
        this.engine = EmbeddedEngine
                .create()
                .using(studentConnector)
                .notifying(this::handleEvent).build();

        this.studentService = studentService;
    }

    @PostConstruct
    private void start() {
        this.executor.execute(engine);
    }

    @PreDestroy
    private void stop() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }

    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();

        if (sourceRecordValue != null) {
            final Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));

            if (operation != null && operation != Operation.READ) {

                String record = AFTER;
                if (operation == Operation.DELETE) {
                    record = BEFORE;
                }

                final Struct struct = (Struct) sourceRecordValue.get(record);
                final Map<String, Object> message =
                    struct.schema().fields().stream()
                        .map(Field::name)
                        .filter(fieldName -> struct.get(fieldName) != null)
                        .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                        .collect(toMap(Pair::getKey, Pair::getValue));

                this.studentService.maintainReadModel(message, operation);
                log.info("Data Changed: {} with Operation: {}", message, operation.name());
            }
        }
    }
}
