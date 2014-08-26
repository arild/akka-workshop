package workshop.part3;

import akka.actor.Actor;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.junit.Test;
import workshop.AkkaTest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static workshop.work.Work.RiskyAddition;
import static workshop.work.Work.RiskyAdditionResult;
import static workshop.helpers.AkkaTestHelper.getGivenNumberOfResultsWithin;

public class SuperComputeRouterTest extends AkkaTest {

    @Test
    public void shouldCompute5RiskyAdditionsWhereNoneFailAndWhereWorkIsDistributedOn5Routers() {
        TestActorRef<Actor> computeRouter = TestActorRef.create(system, Props.create(SuperComputeRouter.class));

        List<RiskyAddition> workList = Stream.generate(() -> new RiskyAddition(1, 1, 150)).limit(5).collect(toList());

        workList.forEach(work -> computeRouter.tell(work, probe.ref()));

        List<RiskyAdditionResult> results = getGivenNumberOfResultsWithin(probe, workList.size(), 200, RiskyAdditionResult.class);
        results.forEach(result -> assertThat(result, is(new RiskyAdditionResult(2))));
    }
}
