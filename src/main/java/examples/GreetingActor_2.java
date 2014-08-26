package examples;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class GreetingActor_2 extends AbstractActor {

    static class SayHello {
        String name;

        public SayHello(String n){
            name = n;
        }
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return ReceiveBuilder.match(SayHello.class, hello -> {
            System.out.println("Hello " + hello.name);
            sender().tell(hello.name, self());
        }).build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("MySystem");

        ActorRef greetingActor = system.actorOf(Props.create(GreetingActor_2.class), "greeter");
        greetingActor.tell(new SayHello("Pope Benedict"), ActorRef.noSender());

        // There are better ways to ensure message are received before termination
        Thread.sleep(100);
        system.shutdown();
    }
}
