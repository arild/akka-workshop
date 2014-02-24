package workshop

import akka.actor.SupervisorStrategy.{Restart, Resume}
import akka.actor.{OneForOneStrategy, ActorRef, Actor}
import akka.event.Logging
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global


case class StartComputeActor(actorName: String)
case class Tick()

class ComputeSupervisor(computeActorFactory: ComputeActorFactory, requestInterval: FiniteDuration = 1 second) extends Actor {

  val log = Logging(context.system, this)
  var computeActor: Option[ActorRef] = None

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

  override def preStart() = scheduleTick()

  def receive = {
    case startComputeActor : StartComputeActor => {
      computeActor = Some(computeActorFactory.create(context, startComputeActor.actorName))

      sender ! computeActor.get

      log.info("Started compute actor with name {}", startComputeActor.actorName)
    }
    case Tick => {
      computeActor match {
        case Some(computeActor) => {
          computeActor ! GetNumCompletedTasks
        }
      }
      scheduleTick()
    }
    case NumCompletedTasks(numCompleted) => log.info("Num completed tasks: {}", numCompleted)
  }

  def scheduleTick() = {
    context.system.scheduler.scheduleOnce(requestInterval, self, Tick)
  }
}
