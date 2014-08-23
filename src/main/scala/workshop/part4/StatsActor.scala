package workshop.part4

import akka.actor.Actor

class StatsActor(stats: Stats) extends Actor {

  override def receive = {
    case event: TrafficEvent => {
      updateStatistics(event)
    }
  }

  def updateStatistics(event: TrafficEvent) = {
    if (event.hasRequiredFields && event.speed.get >= 0f && event.speed.get <= 90f) {
      stats.updateStats(event)
    }
  }
}
