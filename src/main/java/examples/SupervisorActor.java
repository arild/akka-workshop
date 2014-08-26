package examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;
import static java.util.concurrent.TimeUnit.MINUTES;

public class SupervisorActor extends AbstractActor {

    @Override
    public SupervisorStrategy supervisorStrategy() {
        boolean loggingEnabled = false;
        return new OneForOneStrategy(10, Duration.create(1, MINUTES),
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
                }, loggingEnabled);
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(Props.class, props -> {
            ActorRef testActor = context().actorOf(props);
            testActor.tell("print this string, please", self());
        }).build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");

        ActorRef supervisor = system.actorOf(Props.create(SupervisorActor.class), "supervisor");
        Props testActorProps = Props.create(TestActor.class);
        supervisor.tell(testActorProps, ActorRef.noSender());

        // There are better ways to ensure message are received before termination
        Thread.sleep(100);
        system.shutdown();
    }
}
