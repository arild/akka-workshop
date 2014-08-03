package workshop.part2;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;

public class ComputeActorTestFactory extends ComputeActorFactory {

    public ComputeActorTestFactory() {
        super(null);
    }

    @Override
    public ActorRef create(ActorContext context, String actorName) {
        return context.actorOf(Props.create((ComputeTestActor.class)), actorName);
    }
}
