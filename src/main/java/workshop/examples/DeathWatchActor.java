package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class DeathWatchActor extends AbstractActor {

    @Override
    public void preStart() throws Exception {
        ActorRef actorRef = context().actorOf(Props.create(VolatileGreetingActor.class), "volatileActor");
        context().watch(actorRef);
        actorRef.tell("print this message, please!", self());
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

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExamples");
        ActorRef me = system.actorOf(Props.create(DeathWatchActor.class), "dwa");
        Inbox inbox = Inbox.create(system);
        inbox.send(me, "Yo");
        shutdown(system, me, inbox);
    }

    private static void shutdown(ActorSystem system, ActorRef me, Inbox inbox) {
        inbox.watch(me);
        inbox.receive(Duration.create(3, TimeUnit.SECONDS));
        system.shutdown();
        system.awaitTermination();
    }

}
