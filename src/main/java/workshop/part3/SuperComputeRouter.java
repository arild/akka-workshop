package workshop.part3;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.*;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import workshop.examples.*;

import java.util.ArrayList;
import java.util.List;

import static akka.actor.SupervisorStrategy.resume;
import static work.Work.RiskyWork;

public class SuperComputeRouter extends AbstractActor {

    private SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"),
                    t -> resume(), false);

    Router router;
    {
        List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ActorRef r = context().actorOf(Props.create(Worker.class));
            context().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(RiskyWork.class, w -> router.route(w, sender()))
                .build();
    }

}
