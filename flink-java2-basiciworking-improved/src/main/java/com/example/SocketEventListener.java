package com.example;

import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SocketEventListener {

    private AsyncHttpClient client;
    private static final Logger logger = LoggerFactory.getLogger(SocketEventListener.class);

    public SocketEventListener(AsyncHttpClient client){
        logger.info("= = = = = = CONSTRUCTOR SOCKETEVENTLISTENER");
        this.client = client;
    }

    //@Override
    public void onClose() throws IOException {
        logger.info("= = = = = = LISTENER ASKED TO CLOSE = = = = =");
        client.close();
    }
}
