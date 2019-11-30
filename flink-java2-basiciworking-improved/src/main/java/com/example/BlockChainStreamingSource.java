package com.example;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class BlockChainStreamingSource extends RichSourceFunction<String> {

    private volatile boolean isRunning;
    private String url;
    private String onOpenExec;
    private  SocketEventListener socketEventListener;

    public void setSocketEventListener(SocketEventListener socketEvent){
        this.socketEventListener = socketEvent;
    }

    private static final Logger logger = LoggerFactory.getLogger(BlockChainStreamingSource.class);

    public BlockChainStreamingSource(){
        logger.info("# # # # # CONSTRUCTOR A");
        isRunning = true;
    }

    public BlockChainStreamingSource(String url, String onOpenExec){
        this.url = url;
        this.onOpenExec = onOpenExec;
        isRunning = true;
        logger.info("# # # # # CONSTRUCTOR B");
    }

    @Override
    public void run(SourceFunction.SourceContext<String> ctx) throws Exception {

        logger.info("# # # # # RUN BEGIN");
        AsyncHttpClient c = asyncHttpClient();
        logger.info("# # # # # SET SOCKET EVENT LISTENER");
        setSocketEventListener(new SocketEventListener(c));
        logger.info("= = = = = " + isRunning);
        while(isRunning){
            logger.info("= = = = = = WHILE");
            c.prepareGet(url)
                    .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                            new WebSocketListener() {

                                @Override
                                public void onOpen(WebSocket websocket) {
                                    logger.info("= = = = =  OPEN()");
                                    //System.out.println("* * * * OPEN");
                                    //websocket.sendTextFrame("{\"op\":\"unconfirmed_sub\"}");
                                    websocket.sendTextFrame(onOpenExec);
                                }


                                @Override
                                public void onClose(WebSocket websocket, int code, String reason) {
                                    logger.info("= = = = = CLOSING()");
                                }

                                @Override
                                public void onTextFrame(String payload, boolean finalFragment, int rsv) {
                                    logger.info("= = = = = TEXTFRAME");
                                    if(isRunning) {
                                        ctx.collect(payload);
                                    }  else {
                                        logger.info("= = = = = = = TEXTFRAME - CLOSING");
                                    }
                                }

                                @Override
                                public void onError(Throwable t) {
                                    logger.info("= = = = = ERROR");
                                    StringWriter sw = new StringWriter();
                                    PrintWriter pw = new PrintWriter(sw);
                                    t.printStackTrace(pw);
                                    String sStackTrace = sw.toString(); // stack trace as a string
                                    logger.info(sStackTrace);
                                }

                            }).build()).get();
        }
        logger.info("# # # # # # # # RUN ENDS # # # # # # # # # ");
        isRunning = false;
    }

    @Override
    public void cancel() {
        logger.info("# # # # # CANCEL");
        if(socketEventListener != null) {
            logger.info("# # # # # CANCEL :: IF");
            try {
                socketEventListener.onClose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
    }
}
