package workshop;

import akka.testkit.TestProbe;
import scala.concurrent.duration.Duration;
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

    public static <T> List<T> getGivenNumberOfResultsWithin(final TestProbe probe, final int numberOfResults, final long maxDuration, final Class<T> expectedResultType) {
        return expectParallel(maxDuration, () -> rangeClosed(1, numberOfResults).mapToObj(i -> getResultWithin(probe, maxDuration, expectedResultType)).collect(toList()));
    }

    public static <T> T getResultWithin(final TestProbe probe, final long maxDuration, final Class<T> expectedResultType) {
        return probe.expectMsgClass(Duration.create(maxDuration, MILLISECONDS), expectedResultType);
    }

    public static <T> T getResult(final TestProbe probe, final Class<T> expectedResultType) {
        return getResultWithin(probe, 100, expectedResultType);
    }
}