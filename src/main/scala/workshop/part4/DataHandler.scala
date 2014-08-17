package workshop.part4

import scala.language.postfixOps
import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class TrafficEvent()

class DataHandler() extends Actor {


  override def receive = {
    case trafficEvent: TrafficEvent => {
      println("Traffic event")
    }
  }
}

object DataHandler extends App {
  val system = ActorSystem("MySystem")
  val dataHandler = system.actorOf(Props(classOf[DataHandler]))
  implicit val timeout = Timeout(5 seconds)

  for (i <- 1 to 100) {
    dataHandler ! TrafficEvent()
  }

  system.shutdown()
//  private val actorRef: ActorRef = Await.result(futureActorRef, Duration.Inf).asInstanceOf[ActorRef]
}