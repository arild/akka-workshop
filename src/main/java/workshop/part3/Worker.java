package workshop.part3;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import static work.Work.RiskyWork;

public class Worker extends AbstractActor {

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder
                .match(RiskyWork.class, r -> {
                    sender().tell(r.perform(), self());
                    context().stop(self());
                }).build();
    }

}
