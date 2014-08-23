package workshop.part4

import akka.actor.Actor

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object FlushTrafficEvents

class DatabaseActor(database: Database) extends Actor {

  val bufferedEvents = new ListBuffer[TrafficEvent]

  override def preStart() = {
    scheduleFlushTrafficEvents()
  }

  override def receive: Receive = {
    case event: TrafficEvent => {
      storeTrafficEvent(event)
    }
    case FlushTrafficEvents => {
      database.store(bufferedEvents.toList)
      bufferedEvents.clear()
      scheduleFlushTrafficEvents()
    }
  }

  def storeTrafficEvent(event: TrafficEvent) = {
    bufferedEvents += event
    if (bufferedEvents.size > 5) {
      database.store(bufferedEvents.toList)
      bufferedEvents.clear()
    }
  }

  def scheduleFlushTrafficEvents() = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(100 millis, self, FlushTrafficEvents)
  }
}
