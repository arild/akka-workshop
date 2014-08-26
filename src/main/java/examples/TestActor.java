package examples;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

class TestActor extends AbstractActor {
    @Override
    public void preStart() throws Exception {
        System.out.println("Created TestActor");
    }
    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(String.class, System.out::println).build();
    }
}