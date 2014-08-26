package examples;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class VolatileGreetingActor extends AbstractActor {

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(String.class, s -> context().stop(self())).build();
    }
}
