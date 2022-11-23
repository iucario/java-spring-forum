package com.demo.app.notification;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@PrimaryKeyClass
public class NotificationPrimaryKey {
    @PrimaryKeyColumn(name = "receiver", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String receiver;
    @PrimaryKeyColumn(
            name = "id",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.TIMEUUID)
    private UUID id;

    public NotificationPrimaryKey() {
    }

    public NotificationPrimaryKey(String receiver, UUID id) {
        this.receiver = receiver;
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
