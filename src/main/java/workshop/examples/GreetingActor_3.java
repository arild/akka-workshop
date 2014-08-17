package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class GreetingActor_3 extends AbstractActor {

    static class DoGreeting{}

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(DoGreeting.class, m -> {
                    System.out.println("Hello");
                    scheduleNextGreeting();
                }).build();
    }

    private void scheduleNextGreeting() {
        ActorSystem system = context().system();
        system.scheduler().scheduleOnce(
                Duration.create(2, TimeUnit.SECONDS), self(), new DoGreeting(), system.dispatcher(), self());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExamples");
        ActorRef me = system.actorOf(Props.create(GreetingActor_3.class), "greeter");
        Inbox inbox = Inbox.create(system);
        inbox.send(me, new DoGreeting());
    }

}