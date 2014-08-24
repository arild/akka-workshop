package examples

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

case class CreateLifeCycleActor()

class LifeCycleActor extends Actor {
  def receive = {
    case e: Exception => throw e
  }

  override def preStart() {
    println("preStart() - called by FIRST actor-instance during startup")
  }

  override def postStop() {
    println("postStop() - called by ANY actor-instance during shutdown")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("preRestart() - called on ANY running actor about to be restarted")
  }

  override def postRestart(reason: Throwable) {
    println("postRestart() - called on a NEW INSTANCE of this actor after restart")
  }
}

class LifeCycleActorSupervisor extends Actor {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _ => Restart
    }

    def receive = {
      case CreateLifeCycleActor => {
        val actorRef: ActorRef = context.actorOf(Props[LifeCycleActor])
        sender ! actorRef
      }
      case _ => {}
    }
}


object LifeCycleActor extends App {
  val system = ActorSystem("MySystem")
    
  val supervisor = system.actorOf(Props[LifeCycleActorSupervisor])
  implicit val timeout = Timeout(5 seconds)
  val childFuture = supervisor ? CreateLifeCycleActor
  val child = Await.result(childFuture, Duration.Inf).asInstanceOf[ActorRef]
  child ! new RuntimeException
  child ! PoisonPill

  // There are better ways to ensure message are received before termination
  Thread.sleep(100)
  system.shutdown()
}
