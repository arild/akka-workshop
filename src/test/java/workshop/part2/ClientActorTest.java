package workshop.part2;

import akka.actor.*;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import org.junit.Test;
import workshop.work.RiskyWorkException;
import workshop.AkkaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static workshop.work.Work.*;
import static workshop.part2.ComputeSupervisor.StartComputeActor;

public class ClientActorTest extends AkkaTest {

    @Test
    public void shouldStartComputeActorAtStartup() {
        TestProbe resultProbe = TestProbe.apply(system);
        TestProbe computeSupervisorProbe = TestProbe.apply(system);

        List riskyWork = new ArrayList<RiskyWork>();
        createClientActor(computeSupervisorProbe.ref(), resultProbe.ref(), riskyWork);

        computeSupervisorProbe.expectMsgClass(StartComputeActor.class);
    }

    @Test
    public void shouldStopIfComputeActorTerminates() {
        TestActorRef<Actor> computeTestActor = TestActorRef.create(system, Props.create(ComputeTestActor.class));

        TestProbe computeSupervisorProbe = TestProbe.apply(system);
        TestActorRef<Actor> clientActor = createClientActor(computeSupervisorProbe.ref(), mock(ActorRef.class), new ArrayList<RiskyWork>());

        computeSupervisorProbe.expectMsgClass(StartComputeActor.class);
        computeSupervisorProbe.reply(computeTestActor);

        TestProbe probe = TestProbe.apply(system);
        probe.watch(clientActor);

        computeTestActor.tell(PoisonPill.getInstance(), ActorRef.noSender()); // Poison pill makes actor terminate
        probe.expectMsgClass(Terminated.class);

    }

    @Test
    public void shouldComputeAndSendRiskyWorkResultToResultActorWhenWorkThrowsNoExceptions() {
        List<RiskyAddition> work = Arrays.asList(new RiskyAddition(2, 3), new RiskyAddition(3, 3));

        TestActorRef<Actor> computeSupervisor = createComputeSupervisor();
        TestProbe resultProbe = TestProbe.apply(system);
        createClientActor(computeSupervisor, resultProbe.ref(), work);

        assertEquals(5, resultProbe.expectMsgClass(RiskyAdditionResult.class).getResult());
        assertEquals(6, resultProbe.expectMsgClass(RiskyAdditionResult.class).getResult());
    }

    @Test
    public void shouldComputeAndSendRemainingRiskyWorkToResultActorWhenWorkThrowsRiskyWorkException() {
        List<RiskyWork> work = Arrays.asList(new RiskyAddition(2, 3), new WorkWithFailure(), new RiskyAddition(3, 3));

        TestActorRef<Actor> computeSupervisor = createComputeSupervisor();
        TestProbe resultProbe = TestProbe.apply(system);
        createClientActor(computeSupervisor, resultProbe.ref(), work);

        assertEquals(5, resultProbe.expectMsgClass(RiskyAdditionResult.class).getResult());
        assertEquals(6, resultProbe.expectMsgClass(RiskyAdditionResult.class).getResult());
    }

    public class WorkWithFailure extends RiskyWork {
        @Override
        public RiskyWorkResult perform() throws RiskyWorkException {
            throw new RiskyWorkException("test exception");
        }
    }

    private TestActorRef<Actor> createComputeSupervisor() {
        ComputeActorFactory computeActorFactory = new ComputeActorFactory(TestProbe.apply(system).ref());
        return TestActorRef.create(system, Props.create(ComputeSupervisor.class, computeActorFactory));
    }

    private TestActorRef<Actor> createClientActor(ActorRef computeSupervisor, ActorRef resultActor, List riskyWork) {
        return TestActorRef.create(system, Props.create(ClientActor.class, computeSupervisor, resultActor, riskyWork));
    }
}