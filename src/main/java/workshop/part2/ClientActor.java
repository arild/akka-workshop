package workshop.part2;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import workshop.work.Work;

import java.util.List;

public class ClientActor extends AbstractActor {
    private ActorRef computeSupervisor;
    private ActorRef resultActor;
    private List<Work.RiskyWork> riskyWork;

    public ClientActor(ActorRef computeSupervisor, ActorRef resultActor, List<Work.RiskyWork> riskyWork) {
        this.computeSupervisor = computeSupervisor;
        this.resultActor = resultActor;
        this.riskyWork = riskyWork;
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        //TODO
        return ReceiveBuilder.matchAny(x -> {}).build();
    }

}