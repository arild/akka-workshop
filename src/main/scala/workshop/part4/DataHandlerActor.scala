package workshop.part4

import akka.actor.SupervisorStrategy.Resume
import akka.actor._
import akka.routing.RoundRobinPool

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

case class TrafficEvent(speed: Option[Float], weight: Option[Int], lane: Option[Int]) {
  val hasRequiredFields = speed.isDefined && weight.isDefined && lane.isDefined
}

class DataHandlerActor(alarm: Alarm, stats: Stats, database: Database) extends Actor {

  // Should be wrapped in factories or similar for testability
  val alarmActor = context.system.actorOf(Props(classOf[AlarmActor], alarm))
  val statsRouter = context.actorOf(RoundRobinPool(50).props(Props(classOf[StatsActor], stats)))
  val databaseActor = context.system.actorOf(Props(classOf[DatabaseActor], database))

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _: Exception => Resume
    }

  override def receive = {
    case trafficEvent: TrafficEvent => {
      alarmActor ! trafficEvent
      statsRouter ! trafficEvent
      databaseActor ! trafficEvent
    }
  }
}


object DataHandlerActor extends App {
  val system = ActorSystem("MySystem")

  val timerActor = system.actorOf(Props(classOf[TimerActor]))

  val dataHandlerActor = system.actorOf(Props(classOf[DataHandlerActor], new Alarm(timerActor), new Stats(timerActor), new Database(timerActor)))

  println("Data handler started")

  val rand = new Random(11)

  // +200
  val normalEvents = List()
  for (i <- 1 to 100) {
    dataHandlerActor ! TrafficEvent(Some(rand.nextFloat() * 80f), Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
  }

  // +200
  val missingFieldsEvents = List()
  for (i <- 1 to 100) {
    dataHandlerActor ! TrafficEvent(None, Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
  }

  // +200
  val ghostDriverEvents = List()
  for (i <- 1 to 100) {
    dataHandlerActor ! TrafficEvent(Some((rand.nextFloat() * -80f) - 1f), Some(50 + rand.nextInt(150)), Some(rand.nextInt(10)))
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