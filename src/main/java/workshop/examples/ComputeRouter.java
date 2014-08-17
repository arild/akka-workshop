package workshop.examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.*;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ComputeRouter extends AbstractActor {

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
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(String.class, s -> {
                    if("shutdown".equals(s)) {
                        sender().tell("shutting down", self());
                    }
                    router.route(s, self());
                })
                .match(Integer.class, i -> System.out.println("got result " + i + " from " + sender()))
                .match(Terminated.class, x -> {
                    context().system().shutdown();
                    context().system().awaitTermination();})
                .build();
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ActorExamples");
        ActorRef router = system.actorOf(Props.create(ComputeRouter.class), "r00ter");
        Inbox inbox = Inbox.create(system);
        inbox.send(router, "Yo");
        inbox.send(router, "Yo this string");
        inbox.send(router, "Yo is a little bit");
        inbox.send(router, "Yo longer");
        inbox.send(router, "shutdown");
        shutdown(system, router, inbox);
    }

    private static void shutdown(ActorSystem system, ActorRef router, Inbox inbox) {
        inbox.watch(router);
        inbox.receive(Duration.create(1, TimeUnit.SECONDS));
        system.shutdown();
        system.awaitTermination();
    }

}
