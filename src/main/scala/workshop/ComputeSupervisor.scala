package workshop

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Props, OneForOneStrategy, ActorRef, Actor}
import akka.event.Logging
import scala.concurrent.duration._
import workshop.work.HeavyWorkException


case class StartComputeActor(actorName: String)

object ComputeSupervisor {
  def props(computeActorFactory: ComputeActorFactory): Props = Props(new ComputeSupervisor(computeActorFactory))
}

class ComputeSupervisor(computeActorFactory: ComputeActorFactory) extends Actor {
  val log = Logging(context.system, this)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _: ArithmeticException => {
        log.error("Resuming compute actor due to arithmetic exception")
        Resume
      }
      case e: HeavyWorkException => {
        log.error("Restarting compute actor due to heavy work exception. Reason: {}", e.getMessage)
        Restart
      }
      case e: Exception => {
        log.error("Stopping compute actor due to exception. Reason: {}", e.getMessage)
        Stop
      }
    }

  def receive = {
    case startComputeActor : StartComputeActor => {
      val computeActor: ActorRef = computeActorFactory.create(context, startComputeActor.actorName)

      sender ! computeActor

      log.info("Started compute actor with name {}", startComputeActor.actorName)
    }
  }
}