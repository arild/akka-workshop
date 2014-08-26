package examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class DeathWatchActor extends AbstractActor {

    @Override
    public void preStart() throws Exception {
        ActorRef greetingActor = context().actorOf(Props.create(VolatileGreetingActor.class), "volatileActor");
        context().watch(greetingActor);
        greetingActor.tell("print this message, please!", self());
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(
                Terminated.class, s -> {
                    System.out.println("Looks like an actor has died");
                    context().stop(self());
                }
        ).build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");
        system.actorOf(Props.create(DeathWatchActor.class), "deathWatchActor");

        // There are better ways to ensure message are received before termination
        Thread.sleep(100);
        system.shutdown();
    }
}
