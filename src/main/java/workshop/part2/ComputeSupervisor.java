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

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        //TODO
        return ReceiveBuilder.matchAny(x -> {}).build();
    }

    static class StartComputeActor{
        final String actorName;
        StartComputeActor(String actorName){
            this.actorName = actorName;
        }
    }
}