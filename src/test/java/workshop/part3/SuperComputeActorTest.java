package workshop.part3;

import akka.actor.Actor;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.junit.Test;
import work.RiskyWorkException;
import work.Work;
import workshop.AkkaTest;
import workshop.helpers.AkkaTestHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static work.Work.*;

public class SuperComputeActorTest extends AkkaTest {

    @Test
    public void shouldComputeRiskyWorkWhenWorksHasNoFailures() {
        TestActorRef<Actor> superComputeActor = createSuperComputeActor();

        superComputeActor.tell(new RiskyAddition(1, 3), probe.ref());
        RiskyAdditionResult result = probe.expectMsgClass(RiskyAdditionResult.class);
        assertThat(result, is(new RiskyAdditionResult(4)));
    }

    @Test
    public void shouldComputeRiskyWorkInParallelWhenWorkHasNoFailures() {
        TestActorRef<Actor> superComputeActor = createSuperComputeActor();

        List<RiskyWork> workList = Arrays.asList(new RiskyAddition(1, 3, 150), new RiskyAddition(2, 3, 150), new RiskyAddition(4, 2, 150));
        List<RiskyAdditionResult> workListResults = Arrays.asList(new RiskyAdditionResult(4), new RiskyAdditionResult(5), new RiskyAdditionResult(6));

        workList.forEach(work -> superComputeActor.tell(work, probe.ref()));

        List<RiskyAdditionResult> results = getGivenNumberOfResultsWithin(workListResults.size(), 200);
        results.forEach(result -> assertThat(workListResults, hasItem(result)));
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

        TestActorRef<Actor> superComputeActor = createSuperComputeActor();

        List<RiskyWork> workList = Arrays.asList(new RiskyAddition(1, 3, 150), new WorkWithFailure(), new RiskyAddition(4, 2, 150));
        List<RiskyAdditionResult> workListResults = Arrays.asList(new RiskyAdditionResult(4), new RiskyAdditionResult(6));

        workList.forEach(work -> superComputeActor.tell(work, probe.ref()));

        List<RiskyAdditionResult> results = getGivenNumberOfResultsWithin(workListResults.size(), 200);
        results.forEach(result -> assertThat(workListResults, hasItem(result)));
    }

    private TestActorRef<Actor> createSuperComputeActor() {
        return TestActorRef.create(system, Props.create(SuperComputeActor.class));
    }

    private  List<RiskyAdditionResult> getGivenNumberOfResultsWithin(final int numberOfResults, final long duration) {
        return AkkaTestHelpers.expectParallel(duration, () -> IntStream.rangeClosed(1, numberOfResults).mapToObj(work -> probe.expectMsgClass(RiskyAdditionResult.class)).collect(Collectors.toList()));
    }

}
