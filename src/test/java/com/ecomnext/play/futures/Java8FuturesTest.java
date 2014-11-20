package com.ecomnext.play.futures;

import org.jooq.lambda.Unchecked;
import org.junit.Test;
import play.libs.F;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class Java8FuturesTest {

    @Test
    public void should_return_the_value() {
        running(fakeApplication(), () -> {
            F.Promise<String> stringPromise = Java8Futures.asPromise(
                    CompletableFuture.completedFuture("Whats up!")
            );
            assertEquals("Whats up!", stringPromise.get(1000));
        });
    }

    @Test
    public void should_return_the_value_using_executor() {
        F.Promise<String> stringPromise = Java8Futures.asPromise(
                CompletableFuture.completedFuture("Whats up!"),
                ForkJoinPool.commonPool()
        );
        assertEquals("Whats up!", stringPromise.get(1000));
    }

    @Test(expected = RuntimeException.class)
    public void should_return_the_error() {
        CompletableFuture<String> cf = new CompletableFuture<>();
        cf.completeExceptionally(new RuntimeException("Oops!"));
        F.Promise<String> stringPromise = Java8Futures.asPromise(cf);
        stringPromise.get(1000);
    }

    @Test(expected = RuntimeException.class)
    public void should_return_the_error_using_executor() {
        CompletableFuture<String> cf = new CompletableFuture<>();
        cf.completeExceptionally(new RuntimeException("Oops!"));
        F.Promise<String> stringPromise = Java8Futures.asPromise(cf, ForkJoinPool.commonPool());
        stringPromise.get(1000);
    }

//    @Test
//    public void should_return_the_value_within_scala_future() throws Exception {
//        running(fakeApplication(), Unchecked.runnable(() -> {
//            F.Promise<String> stringFuture = Java8Futures.asScalaFuture(
//                    CompletableFuture.completedFuture("Whats up!")
//            );
//            assertEquals("Whats up!", Await.result(stringFuture, Duration.create(5, "seconds")));
//        }));
//    }

    @Test(expected = RuntimeException.class)
    public void should_return_the_error_within_scala_future() throws Exception {
        running(fakeApplication(), Unchecked.runnable( () -> {
            CompletableFuture<String> cf = new CompletableFuture<>();
            cf.completeExceptionally(new RuntimeException("Oops!"));
            Future<String> stringFuture = Java8Futures.asScalaFuture(cf);
            Await.result(stringFuture, Duration.create(5, "seconds"));
        }));
    }

}