package bk.scala

import scala.io.StdIn

object Chatbot1 {
    def main(args: Array[String]):Unit = {
        val name = StdIn.readLine("What is your name?")
        println(s"Say 'bye' to quit. Mr(s) $name")
        var timeToBye = false
        while (!timeToBye) {
            timeToBye = StdIn.readLine(">") match {
                case "bye" => println("ok, bye")
                    true
                case "time" => println(s"time is ${java.time.LocalTime.now()}")
                    false
                case _ => println("Interesting")
                    false
            }
        }
    }
}
