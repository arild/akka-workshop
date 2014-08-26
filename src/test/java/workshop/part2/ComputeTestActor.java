package workshop.part2;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ComputeTestActor extends AbstractActor {
    Boolean restarted = false;

    @Override
    public void postRestart(Throwable reason) throws Exception {
        restarted = true;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.
                match(Exception.class, e -> {
                    throw e;
                }).
                match(IsRestarted.class, m -> sender().tell(restarted, self())).build();
    }

    static class IsRestarted {}
}
