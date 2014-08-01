package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class GreetingActor_2 extends AbstractActor {

    static class SayHello {
        String name;
        public SayHello(String n){
            name = n;
        }
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(SayHello.class, hello -> {
            System.out.println("Hello " + hello.name);
            sender().tell("A reply", self());
        }).build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExcamples");
        ActorRef me = system.actorOf(Props.create(GreetingActor_2.class), "greeter");
        Inbox inbox = Inbox.create(system);
        inbox.send(me, new SayHello("Johnny"));
        shutdown(system, me, inbox);
    }

    private static void shutdown(ActorSystem system, ActorRef me, Inbox inbox) {
        inbox.watch(me);
        inbox.receive(Duration.create(1, TimeUnit.SECONDS));
        system.shutdown();
        system.awaitTermination();
    }
}
