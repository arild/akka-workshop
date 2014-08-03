package workshop.part2;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import workshop.part1.ComputeActor;

import java.util.concurrent.TimeUnit;

public class ComputeActorFactory {

    static FiniteDuration logCompletedTasksInterval = Duration.create(1, TimeUnit.SECONDS);

    public static ActorRef create(ActorContext context, String actorName) {
        return context.actorOf(Props.create((ComputeActor.class), logCompletedTasksInterval), actorName);
    }
}
