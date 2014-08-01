package workshop.examples;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class LifecycleActor extends AbstractActor {

    @Override
    public void preStart() throws Exception {
        System.out.println("preStart() - called) by FIRST actor-instance during startup ");
    }

    @Override
    public void postStop() throws Exception {
        System.out.println("postStop() - called by ANY actor-instance during shutdown");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        System.out.println("preRestart() - called on ANY running actor about to be restarted");
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        System.out.println("postRestart() - called on a NEW INSTANCE of this actor after restart");
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(Exception.class, e -> {
            throw new RuntimeException(e);
        }).build();
    }


}
