package workshop.part2;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.testkit.TestActorRef;
import org.junit.Test;
import work.RiskyWorkException;
import workshop.AkkaTest;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComputeSupervisorTest extends AkkaTest {

    @Test
    public void shouldStartComputeActorAndReturnItsReference() {
        ComputeActorFactory computeActorFactory = mock(ComputeActorFactory.class);
        ActorRef computeActor = mock(ActorRef.class);
        when(computeActorFactory.create(any(ActorContext.class), any(String.class))).thenReturn(computeActor);

        TestActorRef<ComputeSupervisor> computeSupervisor = TestActorRef.create(system, Props.create(ComputeSupervisor.class, computeActorFactory));
        computeSupervisor.tell(new ComputeSupervisor.StartComputeActor("computeActor-1"), probe.ref());

        ActorRef actorRef = probe.expectMsgClass(ActorRef.class);
        assertTrue("Should reference same mocked object", computeActor == actorRef);
    }

    @Test
    public void shouldResumeComputeActorOnArithmeticException() {
        TestActorRef<ComputeSupervisor> computeSupervisor = TestActorRef.create(system, Props.create(ComputeSupervisor.class, new ComputeActorTestFactory()));
        computeSupervisor.tell(new ComputeSupervisor.StartComputeActor("computeActor-1"), probe.ref());
        ActorRef computeTestActor = probe.expectMsgClass(ActorRef.class);

        computeTestActor.tell(new ArithmeticException(), probe.ref());

        probe.send(computeTestActor, new ComputeTestActor.IsRestarted());
        assertFalse(probe.expectMsgClass(Boolean.class));
    }

    @Test
    public void shouldRestartComputeActorOnRiskyWorkException() {
        TestActorRef<ComputeSupervisor> computeSupervisor = TestActorRef.create(system, Props.create(ComputeSupervisor.class, new ComputeActorTestFactory()));
        computeSupervisor.tell(new ComputeSupervisor.StartComputeActor("computeActor-1"), probe.ref());
        ActorRef computeTestActor = probe.expectMsgClass(ActorRef.class);

        computeTestActor.tell(new RiskyWorkException("test exception"), probe.ref());

        probe.send(computeTestActor, new ComputeTestActor.IsRestarted());
        assertTrue(probe.expectMsgClass(Boolean.class));
    }

    @Test
    public void shouldStopComputeActorOnAnyExceptionOtherThanArithmeticAndRiskyWorkException() {
        TestActorRef<ComputeSupervisor> computeSupervisor = TestActorRef.create(system, Props.create(ComputeSupervisor.class, new ComputeActorTestFactory()));
        computeSupervisor.tell(new ComputeSupervisor.StartComputeActor("computeActor-1"), probe.ref());
        ActorRef computeTestActor = probe.expectMsgClass(ActorRef.class);

        probe.watch(computeTestActor);
        computeTestActor.tell(new NumberFormatException("test exception"), probe.ref());

        probe.expectMsgClass(Terminated.class);
    }
}