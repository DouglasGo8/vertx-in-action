package com.manning.vertx.in.action.event.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.util.Random;
import java.util.UUID;

public class HeatSensor extends AbstractVerticle {

    private final Random random = new Random();


    @Override
    public void start() throws Exception {
        this.scheduleNextUpdate();
    }

    private void scheduleNextUpdate() {
        super.vertx.setTimer(random.nextInt(5000) + 1000, this::update);
    }

    private void update(long tid) {

        final double temp = 21.0d + (this.delta() / 10);
        final String id = UUID.randomUUID().toString();
        final JsonObject payload = new JsonObject().put("id", id).put("temp", temp);
        //
        super.vertx.eventBus().publish("sensor.updates.Q", payload);
        //
        this.scheduleNextUpdate();
    }

    private double delta() {
        if (random.nextInt() > 0) {
            return random.nextGaussian();
        }
        return -random.nextGaussian();
    }


}
