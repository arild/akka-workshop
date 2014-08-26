package workshop.part1;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import workshop.AkkaTest;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static workshop.work.Work.RiskyAddition;
import static workshop.work.Work.RiskyAdditionResult;
import static workshop.part1.ComputeActor.*;

public class ComputeActorTest extends AkkaTest {

    @Test
    public void shouldComputeLengthOfAStringAndReturnTheResult() {
        createComputeActor().tell("abc", probe.ref());

        Integer result = probe.expectMsgClass(Integer.class);
        assertThat(result, is(3));
    }

    @Test
    public void computeDivisionAndReturnTheResult() {
        createComputeActor().tell(new Division(9, 3), probe.ref());

        Integer result = probe.expectMsgClass(Integer.class);
        assertEquals(3L, (long) result);
    }

    @Test
    public void shouldPerformRiskyWorkAndReturnTheResult() {
        createComputeActor().tell(new RiskyAddition(3, 2), probe.ref());

        RiskyAdditionResult result = probe.expectMsgClass(RiskyAdditionResult.class);
        assertEquals(result.getResult(), 5);
    }

    @Test
    public void shouldInitiallyHaveZeroCompletedTasks() {
        createComputeActor().tell(new GetNumCompletedTasks(), probe.ref());

        NumCompletedTasks result = probe.expectMsgClass(NumCompletedTasks.class);
        assertEquals(0, result.getNumCompletedTasks());

    }

    @Test
    public void shouldIncrementNumberOfCompletedTasksForEachTaskSent() {
        TestActorRef<ComputeActor> computeActor = createComputeActor();

        computeActor.tell("abc", probe.ref());
        probe.expectMsgClass(Integer.class); // Result from length of string

        computeActor.tell(new GetNumCompletedTasks(), probe.ref());
        assertEquals(1, probe.expectMsgClass(NumCompletedTasks.class).getNumCompletedTasks());

        computeActor.tell(new Division(1, 1), probe.ref());
        probe.expectMsgClass(Integer.class); // Result from division

        computeActor.tell(new GetNumCompletedTasks(), probe.ref());
        assertEquals(2, probe.expectMsgClass(NumCompletedTasks.class).getNumCompletedTasks());

        computeActor.tell(new RiskyAddition(3, 5), probe.ref());
        probe.expectMsgClass(RiskyAdditionResult.class); // Result from risky addition

        computeActor.tell(new GetNumCompletedTasks(), probe.ref());
        assertEquals(3, probe.expectMsgClass(NumCompletedTasks.class).getNumCompletedTasks());
    }

    @Rule public ExpectedException exception = ExpectedException.none();
    @Test
    public void shouldNotIncrementNumberOfCompletedTasksWhenDivisjonFailsWithArithmeticException() {
        TestActorRef<ComputeActor> computeActor = createComputeActor();

        exception.expect(ArithmeticException.class);
        computeActor.receive(new Division(1, 0));

        computeActor.tell(new GetNumCompletedTasks(), probe.ref());
        assertEquals(0, probe.expectMsgClass(NumCompletedTasks.class).getNumCompletedTasks());
    }

    @Test
    public void shouldSendNumCompletedTasksToCompletedTasksActorEveryConfiguredInterval() {
        TestProbe numCompletedTasksActor = TestProbe.apply(system);
        TestActorRef<ComputeActor> computeActor = createComputeActor(numCompletedTasksActor.ref(), Duration.create(100, MILLISECONDS));

        NumCompletedTasks numCompletedTasks = numCompletedTasksActor.expectMsgClass(Duration.create(200, MILLISECONDS), NumCompletedTasks.class);
        assertEquals(0, numCompletedTasks.getNumCompletedTasks());

        computeActor.tell(new Division(1, 1), probe.ref());
        probe.expectMsgClass(Integer.class); // Result from division

        numCompletedTasks = numCompletedTasksActor.expectMsgClass(Duration.create(200, MILLISECONDS), NumCompletedTasks.class);
        assertEquals(1, numCompletedTasks.getNumCompletedTasks());
    }

    private TestActorRef<ComputeActor> createComputeActor() {
        ActorRef numCompletedTasksActor = TestProbe.apply(system).ref();
        FiniteDuration logCompletedTasksInterval = Duration.create(1, TimeUnit.SECONDS);
        return createComputeActor(numCompletedTasksActor, logCompletedTasksInterval);
    }

    private TestActorRef<ComputeActor> createComputeActor(ActorRef numCompletedTasksActor, FiniteDuration logCompletedTasksInterval) {
        return TestActorRef.create(system, Props.create(ComputeActor.class, numCompletedTasksActor, logCompletedTasksInterval));
    }
}
