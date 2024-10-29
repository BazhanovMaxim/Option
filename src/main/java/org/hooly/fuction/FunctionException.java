package org.hooly.fuction;

/**
 * A functional interface for calling a method without error handling
 *
 * @param <T> input type
 * @param <R> output type
 */
@FunctionalInterface
public interface FunctionException<T, R> {
    R apply(T t) throws Exception;
}