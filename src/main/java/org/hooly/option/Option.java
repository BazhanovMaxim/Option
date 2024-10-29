package org.hooly.option;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hooly.fuction.FunctionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 * The class is represented as a container that allows you to manipulate objects
 *
 * @param <T> object type
 */
@AllArgsConstructor
@RequiredArgsConstructor
public final class Option<T> {

    /**
     * Empty value
     */
    private static final Option<?> EMPTY = new Option<>();

    /**
     * The current object that actions are taking place on
     */
    private final T value;

    /**
     * The result of executing the method with error handling
     *
     * @see #runCatching(FunctionException)
     */
    private Boolean result;

    /**
     * The current error when executing the method with error handling
     *
     * @see #runCatching(FunctionException)
     */
    private Exception exception;

    private Option() {
        this(null);
    }

    /**
     * The method of creating a wrapper over a value
     *
     * @param value object value
     * @param <T>   type of value
     * @return object wrapper
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Option<T> of(T value) {
        return new Option<>(value);
    }

    /**
     * The method of creating a wrapper over a value
     *
     * @param <T> object type
     * @return object wrapper
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Option<T> of(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new Option<>(supplier.get());
    }

    /**
     * Method for creating a wrapper over an object
     *
     * @param <T> object type
     * @return object wrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> empty() {
        return (Option<T>) EMPTY;
    }

    public static <T> Option<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    private static <T> Option<T> ofNullable(T value, Boolean result, Exception exception) {
        return value == null ? empty() : new Option<>(value, result, exception);
    }

    /**
     * Returns the wrapper value
     *
     * @return object
     */
    @Contract(pure = true)
    public T get() {
        return value;
    }

    /**
     * Causes an additional action to the object
     *
     * @param action action on an object
     * @return object wrapper
     */
    public Option<T> apply(Consumer<? super T> action) {
        if (value != null && value != empty()) {
            action.accept(value);
        }
        return ofNullable(value);
    }

    /**
     * Causes an additional outside the object
     *
     * @param runnable additional action outside the object
     * @return object wrapper
     * @throws NullPointerException if runnable is null
     */
    public Option<T> and(Runnable runnable) {
        Objects.requireNonNull(runnable).run();
        return ofNullable(value);
    }

    /**
     * Checks whether an object is an instance of a class
     *
     * @param aClass Class
     * @return {@code true} - object instance of the class
     */
    public boolean isInstance(@NotNull Class<?> aClass) {
        return Objects.requireNonNull(aClass).isInstance(value);
    }

    /**
     * Checks whether the object is an instance of the class.
     * If it is, it returns the wrapper of this object, otherwise an empty wrapper
     *
     * @param aClass Class
     * @param <U>    class type
     * @return Option
     */
    public <U> Option<U> ifInstance(Class<U> aClass) {
        return isInstance(aClass)
                ? ofNullable(aClass.cast(value))
                : empty();
    }

    /**
     * Checks that the object is not an instance of the class.
     * If the object is not an instance of the class, it returns the wrapper of this object, otherwise an empty wrapper
     *
     * @param aClass класс
     * @param <U>    class type
     * @return Option
     */
    public <U> Option<T> ifNotInstance(Class<U> aClass) {
        return value == null || aClass.isInstance(value)
                ? empty()
                : ofNullable(value);
    }

    /**
     * Checks that the object is an instance of the class and performs an action in this case
     *
     * @param aClass Class
     * @param action the action to be performed if the object is an instance of the class
     */
    public void ifInstance(@NotNull Class<?> aClass, Consumer<? super T> action) {
        if (Objects.requireNonNull(aClass).isInstance(value)) {
            action.accept(value);
        }
    }

    /**
     * Checks that the object is an instance of the class and performs an action in this case
     *
     * @param aClass   Class
     * @param runnable the action to be performed if the object is an instance of the class
     */
    public void ifInstance(@NotNull Class<?> aClass, Runnable runnable) {
        if (Objects.requireNonNull(aClass).isInstance(value)) {
            runnable.run();
        }
    }

    /**
     * Checks that the current value exists
     *
     * @return {@code true} - the value exists(!= null)
     */
    @Contract(pure = true)
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Performs an action if the current value exists
     *
     * @param action action
     */
    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * Performs an action with the current object, if it exists, otherwise a new wrapper is created over the new value
     *
     * @param mapper    the function of converting an object with type T to type Result
     * @param orElseGet function for getting a new object with the Result type
     * @param <Result>  the type of the new object
     * @return new object value
     */
    public <Result> Result ifPresentOrElse(@NotNull Function<? super T, ? extends Result> mapper, @NotNull Supplier<Result> orElseGet) {
        return value != null ? mapper.apply(value) : orElseGet.get();
    }

    /**
     * Performs an action with the current object, if it exists, otherwise a new wrapper is created over the new value
     *
     * @param orElseGet function for getting a new object with the Result type
     * @param <Result>  the type of the new object
     * @return new object value
     */
    public <Result> Result ifPresentOrElse(@NotNull Supplier<Result> ifPresent, @NotNull Supplier<Result> orElseGet) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(orElseGet);
        return value != null ? ifPresent.get() : orElseGet.get();
    }

    /**
     * Performs a condition check on the value
     *
     * @param predicate verification condition
     * @return the current wrapper, if the check is passed, otherwise an empty wrapper
     */
    public Option<T> filter(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isPresent() ? predicate.test(value) ? this : empty() : this;
    }

    /**
     * Checks if the value is empty
     *
     * @return {@code true} - if the value is null
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Checks if the value is not empty
     *
     * @return {@code true} - if the value is not null
     */
    @Contract(pure = true)
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Performs an action if the object is null
     *
     * @param action action
     */
    public void ifEmpty(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * Performs an action on an object if the object is == null,
     * otherwise performs another action outside the scope of the object itself
     *
     * @param action      action on an object
     * @param otherAction other action outside the object
     */
    public void ifEmptyOrElse(Consumer<? super T> action, Runnable otherAction) {
        if (value != null) {
            action.accept(value);
            return;
        }
        otherAction.run();
    }

    /**
     * Converts an object from type T to type U
     *
     * @param mapper conversion function
     * @param <U>    the type of the new object
     * @return wrapper over a new object
     */
    public <U> Option<U> map(@NotNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? ofNullable(mapper.apply(value)) : empty();
    }

    /**
     * Converts an object from type T to type U
     *
     * @param mapper conversion function
     * @param <U>    the type of the new object
     * @return object
     */
    public <U> U mapTo(@NotNull Function<? super T, ? extends U> mapper) {
        return Objects.requireNonNull(mapper).apply(value);
    }

    /**
     * Performs a nested conversion of an object from type T to type U
     */
    @SuppressWarnings("unchecked")
    public <U extends Iterable<?>> Option<U> flatMap(@NotNull Function<? super T, ? extends Option<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        }
        var r = (Option<U>) mapper.apply(value);
        return Objects.requireNonNull(r);
    }

    /**
     * Throws an exception if the current object is == null
     *
     * @param exceptionSupplier the function of calling an exception
     * @param <ExcOut>          type of exception
     * @return calling an exception
     * @throws ExcOut type of exception
     */
    public <ExcOut extends Throwable> T orElseThrow(Supplier<? extends ExcOut> exceptionSupplier) throws ExcOut {
        return Optional.ofNullable(value).orElseThrow(exceptionSupplier);
    }

    /**
     * Calls the method while handling its internal error, if there is one
     *
     * @param function the function of calling the method
     * @param <U>      object type
     * @return the wrapper above the object
     * @see #exception
     * @see #value
     */
    @SuppressWarnings("unchecked")
    public <U> Option<U> runCatching(FunctionException<? super T, ? extends U> function) {
        Objects.requireNonNull(function);
        if (!isPresent()) {
            return empty();
        }
        try {
            return ofNullable(function.apply(value), true, null);
        } catch (Exception e) {
            return (Option<U>) ofNullable(value, false, e);
        }
    }

    /**
     * Calls the function for converting an object from type T to type U
     * if the wrapper does not have an exception thrown earlier
     *
     * @param mapper function
     * @param <U>    object type
     * @return the wrapper above the object
     * @see #runCatching(FunctionException)
     * @see #exception
     * @see #value
     */
    @SuppressWarnings("unchecked")
    public <U> Option<U> onSuccessTo(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() && result != null
                ? result
                ? ofNullable(mapper.apply(value), result, exception)
                : (Option<U>) ofNullable(value, false, exception)
                : empty();
    }

    /**
     * Causes an action on an object if the wrapper does not have a previously thrown exception
     *
     * @return the wrapper above the object
     * @see #runCatching(FunctionException)
     * @see #exception
     * @see #value
     */
    public Option<T> onSuccess(@NotNull Consumer<T> action) {
        Objects.requireNonNull(action);
        if (!isPresent() || result == null) {
            return empty();
        }
        if (result) {
            action.accept(value);
        }
        return ofNullable(value, result, exception);
    }

    /**
     * Throws an exception if the wrapper has a previously thrown exception.
     *
     * @param exceptionSupplier exception
     * @param <ExcOut>          type of exception
     * @return exception
     * @throws ExcOut type of exception
     */
    public <ExcOut extends Throwable> Option<T> onFailure(Supplier<? extends ExcOut> exceptionSupplier) throws ExcOut {
        return onFailure(exceptionSupplier, false);
    }

    /**
     * Executes the call of the caught exception
     *
     * @param <X> type of exception
     * @return exception
     * @throws X type of exception
     * @see #exception
     */
    @SuppressWarnings("unchecked")
    public <X extends Throwable> Option<T> onFailureRunError() throws X {
        if (!isPresent()) {
            return empty();
        }
        if (result == null) {
            return ofNullable(value);
        }
        if (result) {
            return ofNullable(value, true, exception);
        }
        if (exception.getCause() == null) {
            throw (X) exception;
        }
        throw (X) exception.getCause();
    }

    /**
     * Performs the action provided that `flag == true`
     *
     * @param booleanSupplier value provider
     * @param action          action
     * @return Option
     */
    @NotNull
    private Option<T> runIf(@NotNull BooleanSupplier booleanSupplier, @NotNull Consumer<T> action) {
        return runIf(booleanSupplier.getAsBoolean(), action);
    }

    /**
     * Performs the action provided that `flag == true`
     *
     * @param flag   a flag indicating whether to perform an action or not
     * @param action action
     * @return Option
     */
    @NotNull
    private Option<T> runIf(boolean flag, @NotNull Consumer<T> action) {
        if (value == null) {
            return empty();
        }
        if (flag) {
            action.accept(value);
        }
        return ofNullable(value);
    }

    /**
     * Error handling action
     *
     * @param addDefaultError do I need to add the original error to StackTrace
     * @param <X>             type of exception
     * @return exception
     * @throws X the type of the original error
     */
    public <X extends Throwable> Option<T> onFailure(Supplier<? extends X> exceptionSupplier, Boolean addDefaultError) throws X {
        Objects.requireNonNull(exceptionSupplier);
        if (!isPresent()) {
            return empty();
        }
        if (result == null) {
            return ofNullable(value);
        }
        if (result) {
            return ofNullable(value, true, exception);
        }
        throw of(exceptionSupplier)
                .runIf(addDefaultError, exception -> exception.initCause(exception))
                .get();
    }
}