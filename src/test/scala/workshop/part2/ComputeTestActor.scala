package workshop.helpers

import akka.actor.Actor
import akka.event.Logging


case class IsRestarted()

class ComputeTestActor extends Actor {
  val log = Logging(context.system, this)
  var restarted = false

  override def postRestart(reason: Throwable): Unit = {
    restarted = true
  }

  override def receive = {
    case e: Exception => {
      throw e
    }
    case IsRestarted => sender ! restarted
  }
}