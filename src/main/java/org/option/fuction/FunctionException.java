package org.option.fuction;

/**
 * Фунциональный интерфейс для вызова метода без обработки ошибки.
 *
 * @param <T> входной тип
 * @param <R> выходной тип
 */
@FunctionalInterface
public interface FunctionException<T, R> {
    R apply(T t) throws Exception;
}