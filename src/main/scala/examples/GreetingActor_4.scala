package examples

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import scala.concurrent.duration._
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout

case class CreateGreetingActor4()

class GreetingActor_4 extends Actor {
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

}

class GreetingActor_4Supervisor extends Actor {

    override val supervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
        case _ => Restart
      }

      def receive = {
        case CreateGreetingActor4 => {
          val actorRef: ActorRef = context.actorOf(Props[GreetingActor_4])
          sender ! actorRef
        }
        case _ => {}
      }
  }


object GreetingActor_4 extends App {
    val system = ActorSystem("MySystem")
    val supervisor = system.actorOf(Props[GreetingActor_4Supervisor])
    implicit val timeout = Timeout(5 seconds)
    private val futureActorRef = supervisor ? CreateGreetingActor4
    private val actorRef: ActorRef = Await.result(futureActorRef, Duration.Inf).asInstanceOf[ActorRef]
    actorRef ! PoisonPill
  }
