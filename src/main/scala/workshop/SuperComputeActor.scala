package workshop

import scala.language.postfixOps
import akka.actor.{OneForOneStrategy, ActorRef, Props, Actor}
import workshop.work.RiskyWork
import akka.actor.SupervisorStrategy.{Stop}
import akka.event.Logging
import scala.concurrent.duration._

class SuperComputeActor() extends Actor {

  val log = Logging(context.system, this)

  override val supervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
        case _ => {
          log.info("Stopping problem actor!")
          Stop
        }
      }

  def receive = {
    case riskyWork : RiskyWork => {
      val worker: ActorRef = context.actorOf(Props(new Worker))
      worker.tell(riskyWork, sender)
    }
  }
}

class Worker extends Actor {

  override def postRestart(reason: Throwable) {
    println("postRestart() - called on a NEW INSTANCE of this actor after restart")
  }

  def receive = {
    case riskyWork: RiskyWork => {
      sender ! riskyWork.perform()
      context.stop(self)
    }
  }

}
