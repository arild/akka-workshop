package workshop.part3

import akka.actor.Actor
import workshop.work.RiskyWork

class Worker extends Actor {
  def receive = {
    case riskyWork: RiskyWork => {
      sender ! riskyWork.perform()
      context.stop(self)
    }
  }
}