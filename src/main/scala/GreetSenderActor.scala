import akka.actor.{ActorSystem, Actor, Props}

class GreetSenderActor extends Actor {
  override def preStart(): Unit = {
    //create the greeter actor
    val greeter = context.actorOf(Props[Greeter], "greeter")
    // tell it to perform the greeting
    greeter ! Messages.Greet
    //create the worker actor
    val worker = context.actorOf(Props[Worker], "woorker")
    //tell it to work
    worker ! Messages.Work
    worker ! Messages.Greet
    worker ! Messages.BecomeGreet
    worker ! Messages.Greet
  }
  def receive = {
    case Messages.Done => context.stop(self)
  }
}

class Greeter extends Actor {
  def receive = {
    case Messages.Greet =>
      println("Hello world!")
      sender ! Messages.Done
  }
}

class Worker extends Actor {
  def receive = {
    case Messages.Greet => println("I don't do greet")
    case Messages.Work =>
      println("Doing work!")
      sender ! Messages.Done
    case Messages.BecomeGreet =>
      context.become({
        case Messages.Greet =>
          println("I am a greeter now")
          sender ! Messages.Done}, false
      )
  }
}

object Messages {
  case object Greet
  case object Work
  case object Done
  case object BecomeGreet
}

object Driver {
  def main(args: Array[String]) {
    ActorSystem("Main").actorOf(Props[GreetSenderActor])
  }

}
