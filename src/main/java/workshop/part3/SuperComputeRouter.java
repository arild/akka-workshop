package workshop.part3;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import static akka.actor.SupervisorStrategy.resume;
import static java.util.concurrent.TimeUnit.MINUTES;
import static workshop.work.Work.RiskyWork;

public class SuperComputeRouter extends AbstractActor {

   private RoundRobinPool roundRobinPool = new RoundRobinPool(10)
           .withResizer(new DefaultResizer(5, 20));

    ActorRef router =
            getContext().actorOf(roundRobinPool.props(Props.create(Worker.class)), "router-1");

    private SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create(1, MINUTES),
                    t -> resume(), false);

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(RiskyWork.class, w -> router.tell(w, sender()))
                .build();
    }
}
