package workshop.part3;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import workshop.work.Work;

import static akka.actor.SupervisorStrategy.stop;
import static java.util.concurrent.TimeUnit.MINUTES;

public class SuperComputeActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        //TODO
        return ReceiveBuilder.matchAny(x -> {}).build();
    }
}