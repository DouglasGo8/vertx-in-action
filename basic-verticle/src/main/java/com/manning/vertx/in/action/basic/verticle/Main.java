package com.manning.vertx.in.action.basic.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetSocket;

import static java.lang.System.out;

/**
 * Main Verticle
 */
public class Main extends AbstractVerticle {


    private static int numberOfConnections = 0;

    @Override
    public void start() {

        out.println("Start....");

        super.vertx.setPeriodic(5000, id -> out.println(Main.howMany()));
        //
        // brew install netcat
        // netcat localhost 3000
        //
        super.vertx.createNetServer().connectHandler(Main::handleNewClient).listen(3000);
        super.vertx.createHttpServer().requestHandler(r -> r.response().end(howMany())).listen(8082);
    }

    private static void handleNewClient(NetSocket socket) {
        out.println("Ready....");
        numberOfConnections++;
        socket.handler(buffer -> {
            socket.write(buffer);
            if (buffer.toString().endsWith("/quit\n")) {
                socket.close();
            }
        });
        socket.closeHandler(v -> numberOfConnections--);
    }

    private static String howMany() {
        return "We now have " + numberOfConnections + " connections";
    }

}
