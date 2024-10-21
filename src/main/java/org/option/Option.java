package org.option;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.option.fuction.FunctionException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 * Класс представлен в виде контейнера, который позволяет манипулировать над объектами.
 *
 * @param <T> тип объекта
 */
@AllArgsConstructor
@RequiredArgsConstructor
public final class Option<T> {

    /**
     * Пустое значение.
     */
    private static final Option<?> EMPTY = new Option<>();

    /**
     * Текущий объект, над которым происходят действия.
     */
    private final T value;

    /**
     * Результат выполнения метода с обработкой ошибки.
     *
     * @see #runCatching(FunctionException)
     */
    private Boolean result;

    /**
     * Текущая ошибка при выполнении метода с обработкой ошибки.
     *
     * @see #runCatching(FunctionException)
     */
    private Exception exception;

    private Option() {
        this(null);
    }

    /**
     * Метод создания обёртки над значением.
     *
     * @param value значение
     * @param <T>   тип значения
     * @return обёртка объекта
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Option<T> of(T value) {
        return new Option<>(value);
    }

    /**
     * Метод создания обёртки над значением.
     *
     * @param <T> тип объекта
     * @return обёртка объекта
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Option<T> of(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new Option<>(supplier.get());
    }

    /**
     * Метод создания обёртки над объектом.
     *
     * @param <T> тип объекта
     * @return обёртка объекта
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
     * Возвращает значение обёртки.
     *
     * @return объект
     */
    @Contract(pure = true)
    public T get() {
        return value;
    }

    /**
     * Вызывает дополнительное действие к объекту.
     *
     * @param action действие над объектом
     * @return обёртка объекта
     */
    public Option<T> apply(Consumer<? super T> action) {
        if (value != null && value != empty()) {
            action.accept(value);
        }
        return ofNullable(value);
    }

    /**
     * Вызывает дополнительное вне объекта.
     *
     * @param runnable дополнительное действие вне объекта
     * @return обёртка объекта
     */
    public Option<T> and(Runnable runnable) {
        Objects.requireNonNull(runnable).run();
        return ofNullable(value);
    }

    /**
     * Проверяет, является объект экземпляром класса.
     *
     * @param aClass класс
     * @return `true` - является
     */
    public boolean isInstance(@NotNull Class<?> aClass) {
        return Objects.requireNonNull(aClass).isInstance(value);
    }

    /**
     * Проверяет, является объект экземпляром класса. Если является - то возвращает обёртку этого объекта, иначе
     * пустую обёртку.
     *
     * @param aClass класс
     * @param <U>    тип класса
     * @return Option
     */
    public <U> Option<U> ifInstance(Class<U> aClass) {
        return isInstance(aClass)
                ? ofNullable(aClass.cast(value))
                : empty();
    }

    /**
     * Проверяет, что объект не является экземпляром класса. Если объект не экземпляр класса - то возвращает обёртку
     * этого объекта, иначе пустую обёртку.
     *
     * @param aClass класс
     * @param <U>    тип класса
     * @return Option
     */
    public <U> Option<T> ifNotInstance(Class<U> aClass) {
        return value == null || aClass.isInstance(value)
                ? empty()
                : ofNullable(value);
    }

    /**
     * Проверяет, что объект является экземпляром класса и выполняет действие в этом случае.
     *
     * @param aClass класс
     * @param action действие, которое необходимо выполнить, если объект является экземпляром класса
     */
    public void ifInstance(@NotNull Class<?> aClass, Consumer<? super T> action) {
        if (Objects.requireNonNull(aClass).isInstance(value)) {
            action.accept(value);
        }
    }

    /**
     * Проверяет, что объект является экземпляром класса и выполняет действие в этом случае.
     *
     * @param aClass   класс
     * @param runnable действие, которое необходимо выполнить, если объект является экземпляром класса
     */
    public void ifInstance(@NotNull Class<?> aClass, Runnable runnable) {
        if (Objects.requireNonNull(aClass).isInstance(value)) {
            runnable.run();
        }
    }

    /**
     * Проверяет, что текущее значение существует.
     *
     * @return `true` - значение существует(!= null)
     */
    @Contract(pure = true)
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Выполняет действие, если текущее значение существует.
     *
     * @param action действие
     */
    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * Выполняет действие с текущим объектом, если оно существует, иначе создаётся новая обёртка над новым значением.
     *
     * @param mapper    функция перевода объекта с типом T в тип Result
     * @param orElseGet функция получения новое объекта с типом Result
     * @param <Result>  тип нового объекта
     * @return новое значение объекта
     */
    public <Result> Result ifPresentOrElse(@NotNull Function<? super T, ? extends Result> mapper, @NotNull Supplier<Result> orElseGet) {
        return value != null ? mapper.apply(value) : orElseGet.get();
    }

    /**
     * Выполняет действие с текущим объектом, если оно существует, иначе создаётся новая обёртка над новым значением.
     *
     * @param orElseGet функция получения новое объекта с типом Result
     * @param <Result>  тип нового объекта
     * @return новое значение объекта
     */
    public <Result> Result ifPresentOrElse(@NotNull Supplier<Result> ifPresent, @NotNull Supplier<Result> orElseGet) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(orElseGet);
        return value != null ? ifPresent.get() : orElseGet.get();
    }

    /**
     * Выполняет проверку по условию над значением.
     *
     * @param predicate условие проверки
     * @return текущий обёртка, если проверка пройдена, иначе пустая обёртка
     */
    public Option<T> filter(@NotNull Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isPresent() ? predicate.test(value) ? this : empty() : this;
    }

    /**
     * Проверяет, является ли значение пустым.
     *
     * @return `true` - если значение null
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Проверяет, является ли значение не пустым.
     *
     * @return `true` - если значение не null
     */
    @Contract(pure = true)
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Выполняет действие, если объект null.
     *
     * @param action действие
     */
    public void ifEmpty(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * Выполняет действие над объектом, если объект == null, иначе выполняет другое действие вне рамок самого объекта.
     *
     * @param action      действие над объектом
     * @param otherAction другое действие вне объекта
     */
    public void ifEmptyOrElse(Consumer<? super T> action, Runnable otherAction) {
        if (value != null) {
            action.accept(value);
            return;
        }
        otherAction.run();
    }

    /**
     * Выполняет преобразование объекта из типа T в тип U.
     *
     * @param mapper функция преобразования
     * @param <U>    тип нового объекта
     * @return обёртка над новым объектом
     */
    public <U> Option<U> map(@NotNull Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? ofNullable(mapper.apply(value)) : empty();
    }

    /**
     * Выполняет преобразование объекта из типа T в тип U.
     *
     * @param mapper функция преобразования
     * @param <U>    тип нового объекта
     * @return объект
     */
    public <U> U mapTo(@NotNull Function<? super T, ? extends U> mapper) {
        return Objects.requireNonNull(mapper).apply(value);
    }

    /**
     * Выполняет вложенное преобразование объекта из типа T в тип U.
     */
    public <U extends Iterable<?>> Option<U> flatMap(@NotNull Function<? super T, ? extends Option<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        }
        @SuppressWarnings("unchecked")
        var r = (Option<U>) mapper.apply(value);
        return Objects.requireNonNull(r);
    }

    /**
     * Выполняет выброс исключения в случае, если текущий объект == null.
     *
     * @param exceptionSupplier функция вызова исключения
     * @param <X>               тип исключения
     * @return вызов исключения
     * @throws X тип исключения
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return Optional.ofNullable(value)
                .orElseThrow(exceptionSupplier);
    }

    /**
     * Вызывает метод при этом обрабатывает его внутреннюю ошибку в случае, если она есть.
     *
     * @param function функция вызова метода
     * @param <U>      тип объекта
     * @return обёртка над объектом.
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
     * Вызывает функцию преобразования объекта из типа T в тип U в случае,
     * если у обёртки отсутствует выброшенное ранее исключение.
     *
     * @param mapper функция
     * @param <U>    тип объекта
     * @return обёртка над объектом
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
     * Вызывает действие над объектом в случае, если у обёртки отсутствует выброшенное ранее исключение.
     *
     * @return обёртка над объектом
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
     * Выбрасывает исключение в случае, если у обёртки существует выброшенное ранее исключение.
     *
     * @param exceptionSupplier исключение
     * @param <X>               тип исключения
     * @return исключение
     * @throws X тип исключения
     */
    public <X extends Throwable> Option<T> onFailure(Supplier<? extends X> exceptionSupplier) throws X {
        return onFailure(exceptionSupplier, false);
    }

    /**
     * Выполняет вызов отловленного исключения
     *
     * @param <X> тип исключения
     * @return исключение
     * @throws X тип исключения
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
     * Выполняет действие при условии, что `flag == true`
     *
     * @param booleanSupplier провайдер значения
     * @param action          действие
     * @return Option
     */
    @NotNull
    private Option<T> runIf(@NotNull BooleanSupplier booleanSupplier, @NotNull Consumer<T> action) {
        return runIf(booleanSupplier.getAsBoolean(), action);
    }

    /**
     * Выполняет действие при условии, что `flag == true`
     *
     * @param flag   флаг обозначающий, выполнять действие или нет
     * @param action действие
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
     * Действие обработки ошибки.
     *
     * @param addDefaultError нужно ли добавить в StackTrace исходную ошибку
     * @param <X>             тип ошибки
     * @return ошибка
     * @throws X тип исходной ошибки
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