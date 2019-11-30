package bigdata

import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.ws.{WebSocket, WebSocketListener, WebSocketUpgradeHandler}

class BlockChainStreamingSource(url: String) extends RichSourceFunction[String] {
    var running = true
    lazy val c= asyncHttpClient()

    override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
        while (running) {
            c.prepareGet(url).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
                new WebSocketListener() {
                    override def onOpen(websocket: WebSocket): Unit = {
                        websocket.sendTextFrame("{\"op\":\"unconfirmed_sub\"}")
                        println("* * * * * OPENING * * * * * *")
                    }

                    override def onClose(websocket: WebSocket, code: Int, reason: String): Unit = {
                        println("* * * * * CLOSING * * * * * *")
                    }

                    override def onTextFrame(payload: String, finalFragment: Boolean, rsv: Int): Unit = {
                        ctx.collect(payload)
                        print("f")
                    }

                    override def onError(t: Throwable): Unit = {
                        println("* * * * * ERROR * * * * * *")
                    }
                }).build()).get()
        }
        running = false
    }

    override def cancel(): Unit = {
        running = false
    }
}