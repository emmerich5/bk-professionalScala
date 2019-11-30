package bigdata

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object BlockChainData {
    def main(args: Array[String]) : Unit = {
        val url = "wss://ws.blockchain.info/inv"
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val text = env.addSource(new BlockChainStreamingSource(url))
        def f[T](v: T) = v
        println(f(text))
        val result = text
            .map(_ => ("Count", 1))
            .keyBy(0)
            .timeWindow(Time.seconds(5))
            .sum(1)
        println(f(result))
        result.print()
        env.execute("Streaming BlockChain")
    }
}