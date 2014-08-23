package workshop.part4

import akka.actor.ActorRef

class Stats(timerActor: ActorRef) {
  def updateStats(event: TrafficEvent): Unit = {
    Thread.sleep(10)
    timerActor ! Invoked
  }
}
