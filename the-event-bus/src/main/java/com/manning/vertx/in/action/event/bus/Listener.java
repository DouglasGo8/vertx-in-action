package com.manning.vertx.in.action.event.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

public class Listener extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @Override
    public void start() throws Exception {

        final EventBus bus = super.vertx.eventBus();

        bus.<JsonObject>consumer("sensor.updates.Q", msg -> {
            //
            final JsonObject json = msg.body();
            final String id = json.getString("id");
            final String temp = new DecimalFormat("#.##").format(json.getDouble("temp"));
            //
            log.info("{} reports a temperature ~{}C", id, temp);
        });
    }
}
