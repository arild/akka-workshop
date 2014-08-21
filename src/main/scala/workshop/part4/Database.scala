package workshop.part4

import akka.actor.ActorRef

class Database(timerActor: ActorRef) {
  def store(events: List[TrafficEvent]): Unit = {
    events.foreach(_ => {
      Thread.sleep(5)
      timerActor ! Invoked
    })
  }
}
