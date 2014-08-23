package workshop.part4

import akka.actor._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

case class TrafficEvent(speed: Option[Float], weight: Option[Int], lane: Option[Int])
object FlushTrafficEvents

class DataHandlerActor(alarm: Alarm, stats: Stats, database: Database) extends Actor {

  val bufferedEvents = new ListBuffer[TrafficEvent]

  override def preStart() = {
    scheduleFlushTrafficEvents()
  }

  override def receive = {
    case trafficEvent: TrafficEvent => {
      generateAlarms(trafficEvent)
      updateStatistics(trafficEvent)
      storeTrafficEvent(trafficEvent)
    }
    case FlushTrafficEvents => {
      database.store(bufferedEvents.toList)
      bufferedEvents.clear()
      scheduleFlushTrafficEvents()
    }
  }
  def generateAlarms(event: TrafficEvent) = {
    if (event.speed.isDefined && event.speed.get < 0f) {
      alarm.triggerAlarm(event, "ghost driver")
    }
    else if (!hasRequiredFields(event)) {
      alarm.triggerAlarm(event, "missing fields");
    }
  }

  def updateStatistics(event: TrafficEvent) = {
    if (hasRequiredFields(event) && event.speed.get >= 0f && event.speed.get <= 90f) {
      stats.updateStats(event)
    }
  }

  def storeTrafficEvent(event: TrafficEvent) = {
    bufferedEvents += event
    if (bufferedEvents.size > 5) {
      database.store(bufferedEvents.toList)
      bufferedEvents.clear()
    }
  }

  def hasRequiredFields(event: TrafficEvent): Boolean = {
    event.speed.isDefined && event.weight.isDefined && event.lane.isDefined
  }

  def scheduleFlushTrafficEvents() = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(100 millis, self, FlushTrafficEvents)
  }
}


object DataHandlerActor extends App {
  val system = ActorSystem("MySystem")

  val timerActor = system.actorOf(Props(classOf[TimerActor]))
  val dataHandler = system.actorOf(Props(classOf[DataHandlerActor], new Alarm(timerActor), new Stats(timerActor), new Database(timerActor)))

  println("Data handler started")

  val rand = new Random(11)

  // +200
  val normalEvents = List()
  for (i <- 1 to 100) {
    dataHandler ! TrafficEvent(Some(rand.nextFloat() * 80f), Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
  }

  // +200
  val missingFieldsEvents = List()
  for (i <- 1 to 100) {
    dataHandler ! TrafficEvent(None, Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
  }

  // +200
  val ghostDriverEvents = List()
  for (i <- 1 to 100) {
    dataHandler ! TrafficEvent(Some((rand.nextFloat() * -80f) - 1f), Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
  }

  println("Traffic events sent to data handler")
}

object Invoked
class TimerActor() extends Actor {
  var numInvocations = 0
  val start = System.currentTimeMillis()
  
  override def receive = {
    case Invoked => {
      numInvocations += 1
      if (numInvocations == 600) {
        val totalTime  = (System.currentTimeMillis() - start) / 1000f
        println(f"*** Finished in in $totalTime%1.3f seconds ***")

        context.system.shutdown()
      }
    }
  }
}