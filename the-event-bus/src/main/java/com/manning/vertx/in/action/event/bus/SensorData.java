package com.manning.vertx.in.action.event.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SensorData extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        final EventBus bus = super.vertx.eventBus();
        final HashMap<String, Double> map = new HashMap<>();

        // Message<JsonObject>
        bus.<JsonObject>consumer("sensor.updates.Q", handler -> {
            final JsonObject json = handler.body();
            map.put(json.getString("id"), json.getDouble("temp"));

        });

        // Message<JsonObject>
        bus.<JsonObject>consumer("sensor.average.Q", handler -> {
            final double avg = map.values().stream().collect(Collectors.averagingDouble(Double::doubleValue));
            final JsonObject json = new JsonObject().put("average", avg);
            handler.reply(json);
        });
    }
}
