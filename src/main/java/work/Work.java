package work;

public class Work {
    public static abstract class RiskyWork {
        public abstract RiskyWorkResult perform() throws RiskyWorkException;
    }

    public static abstract class RiskyWorkResult {}

    public static class RiskyAddition extends RiskyWork {

        private final int a;
        private final int b;
        private final int delay;

        public RiskyAddition(int a, int b) {
            this(a, b, 0);
        }

        public RiskyAddition(int a, int b, int delay) {
            this.a = a;
            this.b = b;
            this.delay = delay;
        }

        @Override
        public RiskyWorkResult perform() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to sleep thread");
            }
            return new RiskyAdditionResult(a + b);
        }
    }
    public static class RiskyAdditionResult extends RiskyWorkResult {

        private int result;

        public RiskyAdditionResult(int result) {
            this.result = result;
        }

        public int getResult() {
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RiskyAdditionResult that = (RiskyAdditionResult) o;

            if (result != that.result) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return result;
        }

		@Override
		public String toString() {
			return Integer.toString(result);
		}
	}
}
