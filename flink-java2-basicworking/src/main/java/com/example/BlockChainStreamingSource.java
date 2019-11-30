package com.example;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class BlockChainStreamingSource extends RichSourceFunction<String> {

    transient private volatile boolean isRunning;
    transient private String url;
    transient private volatile String onOpen;
    private static final Logger logger = LoggerFactory.getLogger(BlockChainStreamingSource.class);
    //public AsyncHttpClient c = asyncHttpClient();

    //private final AsyncHttpClient cc = asyncHttpClient();

    public BlockChainStreamingSource(){
        logger.info("# # # # # CONSTRUCTOR A");
        isRunning = true;
    }

    public BlockChainStreamingSource(String url, String onOpen){
        this.url = url;
        this.onOpen = onOpen;
        isRunning = true;
        logger.info("# # # # # CONSTRUCTOR B");
    }


    @Override
    public void run(SourceFunction.SourceContext<String> ctx) throws Exception {

        logger.info("# # # # # RUN BEGIN");
        AsyncHttpClient c = asyncHttpClient();

        while(true){
            System.out.println("* * * * WHILE");
            c.prepareGet("wss://ws.blockchain.info/inv")
                    .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                            new WebSocketListener() {

                                @Override
                                public void onOpen(WebSocket websocket) {
                                    logger.info("# # # # # OPEN");
                                    System.out.println("* * * * OPEN");
                                    websocket.sendTextFrame("{\"op\":\"unconfirmed_sub\"}");
                                }


                                @Override
                                public void onClose(WebSocket websocket, int code, String reason) {
                                    System.out.println("* * * CLOSING");
                                }

                                @Override
                                public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                                    ctx.collect(payload);
                                    System.out.println(payload);
                                }

                                @Override
                                public void onError(Throwable t) {
                                }

                            }).build()).get();
        }
        //isRunning = false;
        //logger.info("# # # # # RUN ENDS");
    }

    @Override
    public void cancel() {
        logger.info("# # # # # CANCEL");
        isRunning = false;
    }
}
