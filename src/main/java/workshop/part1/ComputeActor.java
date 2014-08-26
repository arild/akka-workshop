package workshop.part1;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

import static workshop.work.Work.RiskyWork;
import static workshop.work.Work.RiskyWorkResult;

public class ComputeActor extends AbstractActor {
    private ActorRef numCompletedTaskActor;
    private FiniteDuration logCompletedTasksInterval;

    public ComputeActor(ActorRef numCompletedTaskActor, FiniteDuration logCompletedTasksInterval) {
        this.numCompletedTaskActor = numCompletedTaskActor;
        this.logCompletedTasksInterval = logCompletedTasksInterval;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        //TODO
        return ReceiveBuilder.matchAny(x -> {}).build();
    }

    public static class GetNumCompletedTasks {}
    public static class NumCompletedTasks {
        public final int numCompletedTasks;

        public NumCompletedTasks(int numCompletedTasks) {
            this.numCompletedTasks = numCompletedTasks;
        }
    }
    public static class Division {
        public final int dividend;
        public final int divisor;

        public Division(int dividend, int divisor) {
            this.dividend = dividend;
            this.divisor = divisor;
        }
    }
}