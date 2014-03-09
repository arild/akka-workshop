package examples

import akka.actor._
import scala.concurrent.duration._

case class SayHello()

class GreetingActor(myInterval: FiniteDuration) extends Actor {

  override def preStart() = {
    scheduleNextGreeting()
  }

  def receive = {
    case SayHello => {
      println("Hello!")
      scheduleNextGreeting()
    }
  }

  def scheduleNextGreeting() {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(myInterval, self, SayHello)
  }

}

object GreetingActor extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props(new GreetingActor(1 second)))
}
