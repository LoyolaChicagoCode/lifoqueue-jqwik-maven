package edu.luc.cs271.arrayqueue;

import java.util.List;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.state.*;


class TestLifoQueueJqwik {

  class OfferAction implements Action.Independent<Queue<String>> {
    @Override
    public Arbitrary<Transformer<Queue<String>>> transformer() {
      final var offerElements = Arbitraries.strings().alpha().ofLength(5);
      return offerElements.map(element -> Transformer.mutate(
        String.format("offer(%s)", element),
        queue -> {
          final var sizeBefore = queue.size();
          final var accepted = queue.offer(element);
          assertTrue(
            // !accepted
            false
            || !queue.isEmpty() 
              && queue.size() == sizeBefore + 1
              && element.equals(queue.peek())
          );
        //   assertFalse(queue.isEmpty());
        //   assertEquals(sizeBefore + 1, queue.size());
        }
      ));
    }
  }

  private Action<Queue<String>> poll() {
    return Action.<Queue<String>>when(queue -> !queue.isEmpty())
      .describeAs("poll")
      .justMutate(queue -> {
        final var peekBefore = queue.peek();
        final var sizeBefore = queue.size();
        final var polled = queue.poll();
        assertTrue(
          // polled == null
          false
          || polled.equals(peekBefore)
            && queue.size() == sizeBefore - 1
        );
      });
  }

  @Property
  void checkLifoQueue(@ForAll("lifoQueueActions") final ActionChain<Queue<String>> chain) {
    chain.run();
  }

  @Provide
  Arbitrary<ActionChain<Queue<String>>> lifoQueueActions() {
    return ActionChain
      .<Queue<String>>startWith(() -> Collections.asLifoQueue(new LinkedList<>()))
      .withAction(new OfferAction())
      .withAction(poll());
  }
}
