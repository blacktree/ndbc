package io.trane.ndbc.pool;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Supplier;

import io.trane.future.Future;
import io.trane.future.Promise;

public class Pool<T> {

  private static final Future<Object> POOL_EXHAUSTED = Future.exception(new RuntimeException("Pool exhausted"));

  public static <T> Pool<T> apply(Supplier<Future<T>> supplier, Function<T, Future<Void>> release,
      Function<T, Future<Boolean>> isValid, int maxSize, int maxWaiters, Duration validationInterval) {
    return new Pool<>(supplier, release, isValid, maxSize, maxWaiters, validationInterval);
  }

  private final Supplier<Future<T>> supplier;
  private final Function<T, Future<Void>> release;
  private final Function<T, Future<Boolean>> isValid;
  private final Semaphore sizeSemaphore;
  private final Semaphore waitersSemaphore;
  private final Queue<T> items;
  private final Queue<Waiter<T, ?>> waiters;

  private Pool(Supplier<Future<T>> supplier, Function<T, Future<Void>> release, Function<T, Future<Boolean>> isValid,
      int maxSize, int maxWaiters, Duration validationInterval) {
    this.supplier = supplier;
    this.release = release;
    this.isValid = isValid;
    this.sizeSemaphore = semaphore(maxSize);
    this.waitersSemaphore = semaphore(maxWaiters);
    // TODO is this the best data structure?
    this.items = new ConcurrentLinkedQueue<>();
    this.waiters = new ConcurrentLinkedQueue<>();
    if (validationInterval.toMillis() != Long.MAX_VALUE)
      scheduleValidation(validationInterval, new ScheduledThreadPoolExecutor(1));
  }

  public <R> Future<R> apply(Function<T, Future<R>> f) {
    final T item = items.poll();
    if (item != null)
      return f.apply(item).ensure(() -> release(item));
    else if (sizeSemaphore.tryAcquire())
      return supplier.get().flatMap(i -> f.apply(i).ensure(() -> release(i)));
    else if (waitersSemaphore.tryAcquire()) {
      Waiter<T, R> p = new Waiter<>(f);
      waiters.offer(p);
      return p;
    } else
      return POOL_EXHAUSTED.unsafeCast();
  }

  private final void release(T item) {
    Waiter<T, ?> waiter = waiters.poll();
    if (waiter != null) {
      waitersSemaphore.release();
      waiter.apply(item).ensure(() -> release(item));
    } else
      items.offer(item);
  };

  private final Future<Void> validateN(int n) {
    if (n >= 0) {
      final T item = items.poll();
      if (item == null) {
        return Future.VOID;
      } else
        // TODO logging
        return isValid.apply(item).rescue(e -> Future.FALSE).flatMap(valid -> {
          if (!valid)
            return release.apply(item).rescue(e -> Future.VOID).ensure(() -> sizeSemaphore.release());
          else {
            items.offer(item);
            return Future.VOID;
          }
        }).flatMap(v -> validateN(n - 1));
    } else
      return Future.VOID;
  }

  private Future<Void> scheduleValidation(Duration validationInterval, ScheduledExecutorService scheduler) {
    return Future.VOID.delayed(validationInterval, scheduler).flatMap(v1 -> {
      long start = System.currentTimeMillis();
      return validateN(items.size()).flatMap(v2 -> {
        long next = validationInterval.toMillis() - System.currentTimeMillis() - start;
        if (next <= 0) {
          // TODO logging
          return scheduleValidation(validationInterval, scheduler);
        } else
          return scheduleValidation(Duration.ofMillis(next), scheduler);
      });
    });
  }

  private static class Waiter<T, R> extends Promise<R> {

    private final Function<T, Future<R>> f;

    public Waiter(Function<T, Future<R>> f) {
      super();
      this.f = f;
    }

    public Waiter<T, R> apply(T value) {
      become(f.apply(value));
      return this;
    }
  }

  private Semaphore semaphore(int permits) {
    if (permits == Integer.MAX_VALUE)
      return new Semaphore(permits) {
        private static final long serialVersionUID = 1L;

        @Override
        public void release() {
        }

        @Override
        public boolean tryAcquire() {
          return true;
        }
      };
    else
      return new Semaphore(permits);
  }
}
