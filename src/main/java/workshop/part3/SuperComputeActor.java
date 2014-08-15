package workshop.part3;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import work.Work;

import static akka.actor.SupervisorStrategy.stop;
import static java.util.concurrent.TimeUnit.MINUTES;

public class SuperComputeActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create(1, MINUTES),
                    t -> {
                        log.info("Stopping problem actor!");
                        return stop();
                    });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(Work.RiskyWork.class, r -> {
                    ActorRef worker = context().actorOf(Props.create(Worker.class));
                    worker.tell(r, sender());
                })
                .build();
    }
}