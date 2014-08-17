package workshop.helpers;

import akka.testkit.TestProbe;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.fail;

public class AkkaTestHelper {

    public static <R> R expectParallel(final long limit, final Supplier<R> block) {
        long t0 = System.currentTimeMillis();
        R result = block.get();
        long t1 = System.currentTimeMillis();
        if (t1 - t0 > limit) {
            fail("Did not compute work in parallel!");
        }
        return result;
    }

    public static <T> List<T> getGivenNumberOfResultsWithin(final TestProbe probe, final int numberOfResults, final long duration, final Class<T> expectedResultType) {
        FiniteDuration maxDurationPerResult = FiniteDuration.apply(duration + 100, MILLISECONDS);
        return expectParallel(duration, () -> rangeClosed(1, numberOfResults).mapToObj(i -> probe.expectMsgClass(maxDurationPerResult, expectedResultType)).collect(toList()));
    }
}