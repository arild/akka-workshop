package workshop

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{OneForOneStrategy, ActorRef, Actor}
import akka.event.Logging
import scala.concurrent.duration._


case class StartComputeActor(actorName: String)

class ComputeSupervisor(computeActorFactory: ComputeActorFactory) extends Actor {

  val log = Logging(context.system, this)

  def this() = this(new ComputeActorFactory())

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _: ArithmeticException => {
        log.error("Resuming compute actor due to arithmetic exception")
        Resume
      }
      case e: Exception => {
        log.error("Restarting compute actor due to exception. Reason: {}", e.getMessage)
        Restart
      }
    }

  def receive = {
    case startComputeActor : StartComputeActor => {
      val computeActor: ActorRef = computeActorFactory.create(context, startComputeActor.actorName)
      sender ! computeActor
      log.info("Created compute-actor with name {}", startComputeActor.actorName)
    }
  }
}
