package examples

import akka.actor.SupervisorStrategy._
import akka.actor.{OneForOneStrategy, _}

import scala.concurrent.duration._
import scala.language.postfixOps

class SupervisorActor extends Actor {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }

  def receive = {
    case p: Props => {
      val testActor: ActorRef = context.actorOf(p)
      testActor ! "print this string, please"
    }
  }
}

class TestActor extends Actor {
  override def preStart(){
    println("Created TestActor")
  }
  def receive = {
    case s: String => {println(s)}
  }
}


object SupervisorActor extends App {
  val system = ActorSystem("MySystem")

  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! Props[TestActor]

  // There are better ways to ensure message are received before termination
  Thread.sleep(100)
  system.shutdown()
}