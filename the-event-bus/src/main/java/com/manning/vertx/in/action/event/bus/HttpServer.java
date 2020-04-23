package com.manning.vertx.in.action.event.bus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.TimeoutStream;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class HttpServer extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.vertx.createHttpServer().requestHandler(this::handler)
                .listen(super.config().getInteger("port", 8080));
    }

    private void handler(HttpServerRequest request) {
        if ("/".equals(request.path())) {
            request.response().sendFile("./index.html");
        } else if ("/sse".equals(request.path())) {
            sse(request);
        } else {
            request.response().setStatusCode(404);
        }
    }

    private void sse(HttpServerRequest request) {
        final HttpServerResponse response = request.response();
        response.putHeader("Content-Type", "text/event-stream")
                .putHeader("Cache-Control", "no-cache")
                .setChunked(true);

        final MessageConsumer<JsonObject> consumer = super.vertx.eventBus().consumer("sensor.updates.Q");

        consumer.handler(msg -> {
            response.write("event: update\n");
            response.write("data: " + msg.body().encode() + "\n\n");
        });

        final TimeoutStream ticks = super.vertx.periodicStream(1000);
        final DeliveryOptions options = new DeliveryOptions().setSendTimeout(1000);
        //
        ticks.handler(id -> {
            super.vertx.eventBus().<JsonObject>request("sensor.average.Q", "", options, reply -> {
                if (reply.succeeded()) {
                    response.write("event: average\n");
                    response.write("data: " + reply.result().body().encode() + "\n\n");
                }
            });
        });

        response.endHandler(v -> {
            consumer.unregister();
            ticks.cancel();
        });


    }
}
