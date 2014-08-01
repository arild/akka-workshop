package workshop.part1;


import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.stream.EventFilter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static work.Work.RiskyAddition;
import static work.Work.RiskyAdditionResult;
import static workshop.part1.ComputeActor.*;

public class ComputeActorTest {

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
    public void shouldLogNumCompletedTasksEveryConfiguredIntervalOnSpecififFormat() {
        TestActorRef<ComputeActor> computeActor = createComputeActor();

        akka.testkit.EventFilter.info("Num completed tasks", "", "", "", 2);
    }

    private TestActorRef<ComputeActor> createComputeActor() {
        return TestActorRef.create(system, Props.create(ComputeActor.class));
    }

//  it should "log num completed tasks every configured interval on format 'Num completed tasks: <num_completed>'" in {
//    suppressStackTraceNoise {
//      TestActorRef(Props(classOf[ComputeActor], 100 millis))
//
//      // Throws timeout exception after 3 seconds if filter does not match
//      EventFilter.info(start = "Num completed tasks", occurrences = 2).intercept( {
//
//      })
//    }
//  }
}
