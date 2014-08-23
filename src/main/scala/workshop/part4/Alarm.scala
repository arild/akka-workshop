package workshop.part4

import akka.actor.ActorRef

class Alarm(timerActor: ActorRef) {
  def triggerAlarm(event: TrafficEvent, description: String): Unit = {
    Thread.sleep(10)
    timerActor ! Invoked
  }
}
