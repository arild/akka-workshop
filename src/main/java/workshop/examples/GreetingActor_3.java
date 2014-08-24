package workshop.examples;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public class GreetingActor_3 extends AbstractActor {

    static class DoGreeting {}

    @Override
    public void preStart() throws Exception {
        scheduleNextGreeting();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(DoGreeting.class, m -> {
                    System.out.println("Hello!");
                    scheduleNextGreeting();
                }).build();
    }

    private void scheduleNextGreeting() {
        ActorSystem system = context().system();
        system.scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS), self(), new DoGreeting(), system.dispatcher(), self());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MySystem");
        system.actorOf(Props.create(GreetingActor_3.class), "greeter");
    }
}