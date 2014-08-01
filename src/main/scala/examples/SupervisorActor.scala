package examples

import akka.actor._

import scala.language.postfixOps
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy

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
    println("created TestActor")
  }
  def receive = {
    case s: String => {println(s)}
  }
}


object SupervisorActor extends App {
  val system = ActorSystem("MySystem")
  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! Props[TestActor]
}