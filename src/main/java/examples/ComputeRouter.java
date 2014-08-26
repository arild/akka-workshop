package examples;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ComputeRouter extends AbstractActor {

    ActorRef router =
            getContext().actorOf(new RoundRobinPool(10).props(Props.create(Worker.class)), "router-1");

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(String.class, s -> {
                    if("shutdown".equals(s)) {
                        sender().tell("shutting down", self());
                    }
                    router.tell(s, self());
                })
                .match(Integer.class, i -> System.out.println("Got result " + i + " from " + sender()))
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef router = system.actorOf(Props.create(ComputeRouter.class), "router");
        router.tell("Yo", ActorRef.noSender());
        router.tell("Yo this string", ActorRef.noSender());
        router.tell("Yo is a little bit", ActorRef.noSender());
        router.tell("Yo longer", ActorRef.noSender());

        // There are better ways to ensure message are received before termination
        Thread.sleep(100);
        system.shutdown();
    }
}
