package workshop.part2;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import workshop.work.RiskyWorkException;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;
import static java.util.concurrent.TimeUnit.MINUTES;

public class ComputeSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    ComputeActorFactory computeActorFactory;

    public ComputeSupervisor(ComputeActorFactory computeActorFactory) {
        this.computeActorFactory = computeActorFactory;
    }

    private SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create(1, MINUTES),
                    t -> {
                        if (t instanceof ArithmeticException) {
                            log.error("Resuming compute actor due to arithmetic exception");
                            return resume();
                        } else if (t instanceof RiskyWorkException) {
                            log.error("Restarting compute actor due to risky work exception. Reason: {}", t.getMessage());
                            return restart();
                        } else if (t instanceof IllegalArgumentException) {
                            log.error("Stopping due to illegal argument exception");
                            return stop();
                        } else {
                            return escalate();
                        }
                    }, false);

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.
                match(CreateComputeActor.class, s -> {
                    ActorRef computeActor = computeActorFactory.create(context(), s.actorName);
                    sender().tell(computeActor, self());
                    log.info("Started compute actor with name {}", s.actorName);
                }).build();
    }

    static class CreateComputeActor {
        final String actorName;
        CreateComputeActor(String actorName){
            this.actorName = actorName;
        }
    }
}