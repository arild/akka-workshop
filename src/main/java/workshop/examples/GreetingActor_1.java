package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class GreetingActor_1 extends AbstractActor {

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(String.class, message -> {
            System.out.println("Hello " + message);
            context().stop(self());
        }).build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExamples");
        ActorRef me = system.actorOf(Props.create(GreetingActor_1.class), "greeter");
        Inbox inbox = Inbox.create(system);
        inbox.send(me, "Yo");
        shutdown(system, me, inbox);
    }

    private static void shutdown(ActorSystem system, ActorRef me, Inbox inbox) {
        inbox.watch(me);
        inbox.receive(Duration.create(1, TimeUnit.SECONDS));
        system.shutdown();
        system.awaitTermination();
    }
}
