package workshop.helpers;

import java.util.function.Supplier;

import static org.junit.Assert.fail;

public class AkkaTestHelpers {

    public static <R> R expectParallel(final long limit, final Supplier<R> block) {
        long t0 = System.currentTimeMillis();
        R result = block.get();
        long t1 = System.currentTimeMillis();
        if (t1 - t0 > limit) {
            fail("Did not compute work in parallel!");
        }
        return result;
    }
}