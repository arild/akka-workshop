package workshop.part4

import akka.actor.Actor

class AlarmActor(alarm: Alarm) extends Actor {

  override def receive = {
    case event: TrafficEvent => {
      generateAlarms(event)
    }
  }

  def generateAlarms(event: TrafficEvent) = {
    if (event.speed.isDefined && event.speed.get < 0f) {
      alarm.triggerAlarm(event, "ghost driver")
    }
    else if (!event.hasRequiredFields) {
      alarm.triggerAlarm(event, "missing fields");
    }
  }
}
