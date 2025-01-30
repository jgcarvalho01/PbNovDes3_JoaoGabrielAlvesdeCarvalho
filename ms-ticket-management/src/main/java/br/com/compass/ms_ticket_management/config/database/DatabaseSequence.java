package br.com.compass.ms_ticket_management.config.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
public class DatabaseSequence {
    @Id
    private String id;
    private long sequenceValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSequenceValue() {
        return sequenceValue;
    }

    public void setSequenceValue(long sequenceValue) {
        this.sequenceValue = sequenceValue;
    }
}
