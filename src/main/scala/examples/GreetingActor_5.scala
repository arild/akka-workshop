package examples

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout

case class CreateGreetingActor5()

class GreetingActor_5 extends Actor {
    def receive = {
      case message : String => println("Hello " + message)
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

class GreetingActor_5Supervisor extends Actor {

    override val supervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
        case _ => Restart
      }

      def receive = {
        case CreateGreetingActor5 => {
          val actorRef: ActorRef = context.actorOf(Props[GreetingActor_5])
          sender ! actorRef
        }
        case _ => {}
      }
  }


object GreetingActor_5 extends App {
    val system = ActorSystem("MySystem")
    val supervisor = system.actorOf(Props[GreetingActor_5Supervisor])
    implicit val timeout = Timeout(5 seconds)
    private val futureActorRef = supervisor ? CreateGreetingActor5
    private val actorRef: ActorRef = Await.result(futureActorRef, Duration.Inf).asInstanceOf[ActorRef]
    actorRef ! new RuntimeException
    actorRef ! PoisonPill
  }
