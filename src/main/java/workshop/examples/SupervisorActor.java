package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;
import static java.util.concurrent.TimeUnit.MINUTES;

public class SupervisorActor extends AbstractActor {

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create(1, MINUTES),
                    t -> {
                        if (t instanceof ArithmeticException) {
                            return resume();
                        } else if (t instanceof NullPointerException) {
                            return restart();
                        } else if (t instanceof IllegalArgumentException) {
                            return stop();
                        } else {
                            return escalate();
                        }
                    });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(Props.class, p -> {
            ActorRef testActor = context().actorOf(p);
            testActor.tell("print this string, please", self());
            context().stop(self());
        }).build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExcamples");
        ActorRef supervisor = system.actorOf(Props.create(SupervisorActor.class), "greeter");
        Inbox inbox = Inbox.create(system);
        Props p = Props.create(TestActor.class);
        inbox.send(supervisor, p);
        shutdown(system, supervisor, inbox);
    }

    private static void shutdown(ActorSystem system, ActorRef me, Inbox inbox) {
        inbox.watch(me);
        inbox.receive(Duration.create(1, TimeUnit.SECONDS));
        system.shutdown();
        system.awaitTermination();
    }
}
