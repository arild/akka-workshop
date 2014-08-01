package workshop.part1;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ComputeActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.
                match(String.class, s -> {
                    sender().tell(s.length(), self());
                }).
                match(Division.class, d -> {
                    int result = d.dividend / d.divisor;
                    sender().tell(result, self());
                }).build();
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