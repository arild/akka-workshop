package workshop.part1;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.BoxedUnit;
import work.Work;

import java.util.concurrent.TimeUnit;

import static work.Work.RiskyWork;
import static work.Work.RiskyWorkResult;

public class ComputeActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private int numCompletedTasks = 0;
    private Duration logCompletedTasksInterval;

    public ComputeActor(Duration logCompletedTasksInterval) {
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
                }).match(DoLogging.class, m -> {
                    log.info("Num completed tasks: " + numCompletedTasks);
                    scheduleLogging();
                }).build();
    }

    private void scheduleLogging() {
        context().system().scheduler().scheduleOnce(Duration.create(1, TimeUnit.SECONDS), self(),
                new DoLogging(), context().system().dispatcher(), self());
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
    private class DoLogging {}
}