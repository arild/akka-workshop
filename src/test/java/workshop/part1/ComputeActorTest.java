package workshop.part1;


import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        createComputeActor().tell(new ComputeActor.Division(9, 3), probe.ref());

        Integer result = probe.expectMsgClass(Integer.class);
        assertEquals(3L, (long)result);
    }

  public void shouldPerformRiskyWorkAndReturnTheResult() {
    suppressStackTraceNoise {
      computeActor ! new RiskyAddition(3, 2)
      expectMsg(RiskyAdditionResult(5))
    }
  }



    private TestActorRef<ComputeActor> createComputeActor() {
        return TestActorRef.create(system, Props.create(ComputeActor.class));
    }

//  it should "initially have zero completed tasks" in new Actor {
//    suppressStackTraceNoise {
//      computeActor ! GetNumCompletedTasks
//      expectMsg(NumCompletedTasks(0))
//    }
//  }
//
//  it should "increment number of completed tasks for each task sent" in new Actor {
//    suppressStackTraceNoise {
//      computeActor ! "abc"
//      expectMsgClass(classOf[Int]) // Result from length of string
//
//      computeActor ! GetNumCompletedTasks
//      expectMsg(NumCompletedTasks(1))
//
//      computeActor ! Division(1, 1)
//      expectMsgClass(classOf[Int]) // Result from division
//
//      computeActor ! GetNumCompletedTasks
//      expectMsg(NumCompletedTasks(2))
//
//      computeActor ! new RiskyAddition(3, 5)
//      expectMsgClass(classOf[RiskyAdditionResult]) // Result from risky addition
//
//      computeActor ! GetNumCompletedTasks
//      expectMsg(NumCompletedTasks(3))
//    }
//  }
//
//  it should "not increment number of completed tasks when division fails with arithmetic exception" in new Actor {
//    suppressStackTraceNoise {
//      // Prevents stack trace from displaying when running tests
//      intercept[ArithmeticException] {
//        computeActor.receive(Division(1, 0))
//      }
//
//      computeActor ! GetNumCompletedTasks
//      expectMsg(NumCompletedTasks(0))
//    }
//  }
//
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
