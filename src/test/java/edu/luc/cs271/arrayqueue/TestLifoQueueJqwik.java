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
          assertTrue(accepted);
          assertFalse(queue.isEmpty());
          assertEquals(sizeBefore + 1, queue.size());
          assertTrue(element.equals(queue.peek()));
        }
      ));
    }
  }

  private Action<Queue<String>> pollAction() {
    return Action.<Queue<String>>when(queue -> !queue.isEmpty())
      .describeAs("poll")
      .justMutate(queue -> {
        final var peekBefore = queue.peek();
        final var sizeBefore = queue.size();
        final var polled = queue.poll();
        assertEquals(peekBefore, polled);
        assertEquals(sizeBefore - 1, queue.size());
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
      .withAction(pollAction());
  }
}
