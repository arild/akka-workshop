import akka.actor.Actor
import akka.event.Logging

case class Division(dividend: Int, divisor: Int)

class ComputeActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case division: Division => sender ! division.dividend / division.divisor
  }
}