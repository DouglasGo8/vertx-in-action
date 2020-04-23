package com.manning.vertx.in.action.event.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 *
 */
public class Main extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        //
        super.vertx.deployVerticle(Listener.class.getName());
        super.vertx.deployVerticle(SensorData.class.getName());
        super.vertx.deployVerticle(HttpServer.class.getName());
        super.vertx.deployVerticle(HeatSensor.class.getName(), new DeploymentOptions().setInstances(4));
    }
}
