package examples;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

import static akka.actor.SupervisorStrategy.restart;
import static java.util.concurrent.TimeUnit.MINUTES;

public class LifecycleActorSupervisor extends AbstractActor {

    static class CreateLifeCycleActor {
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create(1, MINUTES),
                    t -> restart(), false);

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(CreateLifeCycleActor.class, a -> {
                    ActorRef actorRef = context().actorOf(Props.create(LifecycleActor.class), "lifecycleActor");
                    sender().tell(actorRef, self());
                })
                .match(Object.class, o -> {
                })
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");

        ActorRef supervisor = system.actorOf(Props.create(LifecycleActorSupervisor.class), "lifecycleSupervisor");
        Inbox inbox = Inbox.create(system);
        inbox.send(supervisor, new CreateLifeCycleActor());

        ActorRef child = (ActorRef) inbox.receive(Duration.create(1, TimeUnit.MINUTES));
        child.tell(new RuntimeException("Ay, caramba!"), ActorRef.noSender());
        child.tell(PoisonPill.getInstance(), ActorRef.noSender());

        // There are better ways to ensure message are received before termination
        Thread.sleep(100);
        system.shutdown();
    }

}
