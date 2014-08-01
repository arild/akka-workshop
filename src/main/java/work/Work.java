package work;

public class Work {
    public static abstract class RiskyWork {
        public abstract RiskyWorkResult perform();
    }

    public static abstract class RiskyWorkResult {}

//    public static class class RiskyWorkException() extends Exception;

    public static class RiskyAddition extends RiskyWork {

        private final int a;
        private final int b;

        public RiskyAddition(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public RiskyWorkResult perform() {
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
    }
}
