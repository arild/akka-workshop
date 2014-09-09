package workshop.part3;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.junit.Test;
import workshop.AkkaTest;
import workshop.work.RiskyWorkException;
import workshop.work.Work;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static workshop.helpers.AkkaTestHelper.getGivenNumberOfResultsWithin;
import static workshop.helpers.AkkaTestHelper.getResult;
import static workshop.work.Work.*;

public class SuperComputeActorTest extends AkkaTest {

    @Test
    public void shouldComputeRiskyWorkWhenWorksHasNoFailures() {
        ActorRef superComputeActor = createSuperComputeActor();

        superComputeActor.tell(new RiskyAddition(1, 3), probe.ref());
        RiskyAdditionResult result = getResult(probe, RiskyAdditionResult.class);
        assertThat(result, is(new RiskyAdditionResult(4)));
    }

    @Test
    public void shouldComputeRiskyWorkInParallelWhenWorkHasNoFailures() {
		ActorRef superComputeActor = createSuperComputeActor();

        List<RiskyWork> workList = Arrays.asList(new RiskyAddition(1, 3, 150), new RiskyAddition(2, 3, 150), new RiskyAddition(4, 2, 150));
        List<RiskyAdditionResult> workListResults = new LinkedList<>(Arrays.asList(new RiskyAdditionResult(4), new RiskyAdditionResult(5), new RiskyAdditionResult(6)));

        workList.forEach(work -> superComputeActor.tell(work, probe.ref()));

        List<RiskyAdditionResult> results = getGivenNumberOfResultsWithin(probe, workListResults.size(), 200, RiskyAdditionResult.class);
        results.forEach(result -> assertResult(result, workListResults));

		assertTrue(workListResults.isEmpty());
    }

    @Test
    public void shouldComputeRiskyWorkInParallelWhenWorkHasFailures() {
        class WorkWithFailure extends RiskyWork {
            @Override
            public Work.RiskyWorkResult perform() throws RiskyWorkException {
                RiskyWorkException riskyWorkException = new RiskyWorkException("Risky work failed");
                riskyWorkException.setStackTrace(new StackTraceElement[]{});
                throw riskyWorkException;
            }
        }

        ActorRef superComputeActor = createSuperComputeActor();

        List<RiskyWork> workList = Arrays.asList(new RiskyAddition(1, 3, 150), new WorkWithFailure(), new RiskyAddition(4, 2, 150));
        List<RiskyAdditionResult> workListResults = new LinkedList<>(Arrays.asList(new RiskyAdditionResult(4), new RiskyAdditionResult(6)));

        workList.forEach(work -> superComputeActor.tell(work, probe.ref()));

        List<RiskyAdditionResult> results = getGivenNumberOfResultsWithin(probe, workListResults.size(), 200, RiskyAdditionResult.class);
		results.forEach(result -> assertResult(result, workListResults));
    }

    private ActorRef createSuperComputeActor() {
        return system.actorOf(Props.create(SuperComputeActor.class));
    }

	private void assertResult(final RiskyAdditionResult result, List<RiskyAdditionResult> expectedResults) {
		assertTrue("Unexpected result [" + result + "] expected one of " + expectedResults, expectedResults.remove(result));
	}
}
