package workshop;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import org.junit.After;
import org.junit.Before;

public abstract class AkkaTest {

    protected ActorSystem system;
    protected TestProbe probe;

    @Before
    public void setup() {
        system = ActorSystem.create();
        probe = TestProbe.apply(system);
    }

    @After
    public void teardown() {
        JavaTestKit.shutdownActorSystem(system);
    }

}
