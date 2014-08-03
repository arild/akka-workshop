package workshop.part2;

import akka.actor.*;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;
import workshop.part1.ComputeActor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComputeSupervisorTest {

    ActorSystem system;
    TestProbe probe;

    @Before
    public void setup() {
        system = ActorSystem.create();
        probe = TestProbe.apply(system);
    }

    @After
    public void teardown() {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    public void shouldStartComputActorAndReturnItsReference() {
        ComputeActorFactory computeActorFactory = mock(ComputeActorFactory.class);
        ActorRef computeActor = mock(ActorRef.class);
        when(computeActorFactory.create(any(ActorContext.class), any(String.class))).thenReturn(computeActor);

        TestActorRef<ComputeSupervisor> computeSupervisor = TestActorRef.create(system, Props.create(ComputeSupervisor.class, computeActorFactory));
        computeSupervisor.tell(new ComputeSupervisor.StartComputeActor("computeActor-1"), probe.ref());

        ActorRef actorRef = probe.expectMsgClass(ActorRef.class);
        assertTrue("Should reference same mocked object", computeActor == actorRef);
    }

    public void shouldResumeComputeActorOnArithmeticException() {
//    it should "resume compute actor on arithmetic exception" in {
//        suppressStackTraceNoise {
//            val computeSupervisor = TestActorRef(Props(classOf[ComputeSupervisor], new ComputeTestActorFactory))
//            computeSupervisor ! StartComputeActor("computeActor-1")
//
//            val computeTestActor: ActorRef = expectMsgClass(classOf[ActorRef])
//            val exception: ArithmeticException = new ArithmeticException
//            computeTestActor ! exception
//
//            computeTestActor ! IsRestarted
//            expectMsg(false)
//        }
//    }

    }



    private TestActorRef<ComputeSupervisor> createComputeActor(ComputeActorFactory fact) {
        return TestActorRef.create(system, Props.create(ComputeSupervisor.class, fact));
    }



}