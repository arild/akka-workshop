package workshop.part1;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

import static work.Work.RiskyWork;
import static work.Work.RiskyWorkResult;

public class ComputeActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private int numCompletedTasks = 0;
    private ActorRef numCompletedTaskActor;
    private FiniteDuration logCompletedTasksInterval;

    public ComputeActor(ActorRef numCompletedTaskActor, FiniteDuration logCompletedTasksInterval) {
        this.numCompletedTaskActor = numCompletedTaskActor;
        this.logCompletedTasksInterval = logCompletedTasksInterval;
    }

    @Override
    public void preStart() throws Exception {
        scheduleLogging();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.
                match(String.class, s -> {
                    numCompletedTasks += 1;
                    sender().tell(s.length(), self());
                }).
                match(Division.class, d -> {
                    int result = d.dividend / d.divisor;
                    numCompletedTasks += 1;
                    sender().tell(result, self());
                }).
                match(RiskyWork.class, work -> {
                    RiskyWorkResult result = work.perform();
                    numCompletedTasks += 1;
                    sender().tell(result, self());
                }).
                match(GetNumCompletedTasks.class, m -> {
                    sender().tell(new NumCompletedTasks(numCompletedTasks), self());
                }).
                match(SendNumCompletedTasks.class, m -> {
                    numCompletedTaskActor.tell(new NumCompletedTasks(numCompletedTasks), self());
                    scheduleLogging();
                }).build();
    }

    private void scheduleLogging() {
        context().system().scheduler().scheduleOnce(logCompletedTasksInterval, self(),
                new SendNumCompletedTasks(), context().system().dispatcher(), self());
    }

    public static class GetNumCompletedTasks {}
    public static class NumCompletedTasks {
        private int numCompletedTasks;

        public NumCompletedTasks(int numCompletedTasks) {
            this.numCompletedTasks = numCompletedTasks;
        }

        public int getNumCompletedTasks() {
            return numCompletedTasks;
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
    private class SendNumCompletedTasks {}
}