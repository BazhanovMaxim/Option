package org.hooly.any;

import org.hooly.option.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Left<L, R> implements Any<L, R> {

    private final L value;

    public Left(L value) {
        this.value = value;
    }

    private L getValue() {
        Objects.requireNonNull(value);
        return value;
    }

    /**
     * @return {@code true} if Any is Left
     */
    @Override
    public boolean isLeft() {
        return true;
    }

    /**
     * Performs an action on the value if Any is Left
     *
     * @param action action
     * @throws NullPointerException if action is null
     */
    @Override
    public void ifLeft(@NotNull Consumer<? super L> action) {
        Objects.requireNonNull(action);
        action.accept(getValue());
    }

    /**
     * it is used to combine two objects Any, where one of them is Left.
     * This method takes another Any object and combines it with the current Any object
     * if the current object is Left. If the current object is Right,
     * the joinLeft method simply returns the current Right object.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any1 = new Left<>("Error 1");
     *       Any<String, Integer> any2 = new Left<>("Error 2");
     *       Any<String, Integer> result = any1.joinLeft(any2);
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *       // Left: Error 2
     *
     *       Any<String, Integer> any3 = new Right<>(5);
     *       Any<String, Integer> result2 = any3.joinLeft(any2);
     *       if (result2.isLeft()) {
     *           System.out.println("Left: " + result2.getLeft());
     *       } else {
     *           System.out.println("Right: " + result2.getRight());
     *       }
     *       // Right: 5
     *  }
     * </pre>
     *
     * @param other Any object
     * @return Any
     */
    @Override
    public Any<L, R> joinLeft(@NotNull Any<L, R> other) {
        return other;
    }

    /**
     * it is used to combine two objects Any, where one of them is Right.
     * This method takes another Any object and combines it with the current Any object
     * if the current object is Right. If the current object is Left,
     * the joinRight method simply returns the current Left object.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any1 = new Right<>(5);
     *       Any<String, Integer> any2 = new Right<>(10);
     *       Any<String, Integer> result = any1.joinLeft(any2);
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *       // Right: 10
     *
     *       Any<String, Integer> any3 = new Left<>("Error");
     *       Any<String, Integer> result2 = any3.joinRight(any2);
     *       if (result2.isLeft()) {
     *           System.out.println("Left: " + result2.getLeft());
     *       } else {
     *           System.out.println("Right: " + result2.getRight());
     *       }
     *  }
     * </pre>
     *
     * @param other Any object
     * @return Any
     */
    @Override
    public Any<L, R> joinRight(@NotNull Any<L, R> other) {
        return this;
    }

    /**
     * @return value of Left or null
     */
    @Override
    public @Nullable L getLeft() {
        return value;
    }

    /**
     * Checks the value of Right using a predicate. If the predicate returns {@code true},
     * the current Any is returned. If the predicate returns {@code false}, Left is returned with the specified value
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any = new Right<>(5);
     *
     *       Any<String, Integer> result = any.filterOrElse(x -> x > 10, "Value is too small");
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *  }
     * </pre>
     *
     * @param predicate verification condition
     * @throws NullPointerException if predicate is null
     */
    @Override
    public Any<L, R> filterOrElse(@NotNull Predicate<R> predicate, L orElse) {
        return this;
    }

    /**
     * In the context of the Any class, it is used to convert the value of Right using a given function.
     * This method accepts a function that takes the value Right and returns a new value.
     * If the Any object is Right, the map method applies the function to the Right value and
     * returns a new Any object with the converted Right value. If Any object is Left,
     * the map method simply returns the same Left object unchanged.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any = new Right<>(5);
     *
     *       Any<String, Integer> result = any.map(x -> x * 2);
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *       // Right: 10
     *
     *       any = new Left<>("Something went wrong");
     *       result = any.map(x -> x * 2);
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *       // Left: Something went wrong
     *  }
     * </pre>
     *
     * @param mapper function to value convert
     * @return new object Any
     * @throws NullPointerException if mapper is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Any<L, T> map(@NotNull Function<R, T> mapper) {
        return (Any<L, T>) this;
    }

    /**
     * It is used to convert the Right value to another Any object.
     * This method takes a function that takes the value Right and returns a new object Any.
     * If the original Any object is Left, the flatMap method simply returns the same Left object.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> Any = new Right<>(5);
     *
     *       Any<String, Integer> result = Any.flatMap(x -> {
     *           if (x > 10) {
     *               return new Right<>(x * 2);
     *           } else {
     *               return new Left<>("Value is too small");
     *           }
     *       });
     *
     *       if (result.isLeft()) {
     *           System.out.println("Left: " + result.getLeft());
     *       } else {
     *           System.out.println("Right: " + result.getRight());
     *       }
     *  }
     * </pre>
     *
     * @param mapper the function of changing the value of Right
     * @param <T>    new object type of Right
     * @return Any
     * @throws NullPointerException if mapper is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Any<L, T> flatMap(@NotNull Function<R, Any<L, T>> mapper) {
        return (Any<L, T>) this;
    }

    /**
     * It is used to convert Any object to a value of another type
     * by applying two functions: one for the Left case and the other for the Right case.
     * This method allows you to "collapse" Any object into a single value, regardless of whether
     * it is Left or Right.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any = new Right<>(5);
     *
     *       String result = any.fold(
     *               left -> "Error: " + left,
     *               right -> "Success: " + right
     *       );
     *       System.out.println(result); // Success: 5
     *
     *       any = new Left<>("Something went wrong");
     *       result = any.fold(
     *               left -> "Error: " + left,
     *               right -> "Success: " + right
     *       );
     *       System.out.println(result); // Выведет: Error: Something went wrong
     *  }
     * </pre>
     *
     * @param leftMapper  the "collapse" function is the Left value
     * @param rightMapper the "collapse" function is the Right value
     * @param <Result>    the type of another object
     * @throws NullPointerException if leftMapper or rightMapper is null
     */
    @Override
    public <Result> Result fold(@NotNull Function<L, Result> leftMapper, @NotNull Function<R, Result> rightMapper) {
        Objects.requireNonNull(leftMapper);
        return leftMapper.apply(getValue());
    }

    /**
     * It is used to perform an action (side effect) on the value of Right or Left,
     * depending on the type of Any object.
     * This method takes two arguments: a function to process the Left value and a function to process the Right value.
     * Depending on whether the object is Any Left or Right, the corresponding function will be called.
     *
     * <pre>
     *  {@code
     *       Any<String, Integer> any = new Right<>(5);
     *
     *       any.forEach(
     *               left -> System.out.println("Left: " + left),
     *               right -> System.out.println("Right: " + right)
     *       );
     *       // Right: 5
     *
     *       any = new Left<>("Something went wrong");
     *       any.forEach(
     *               left -> System.out.println("Left: " + left),
     *               right -> System.out.println("Right: " + right)
     *       );
     *       // Left: Something went wrong
     *  }
     * </pre>
     *
     * @param leftConsumer  a function for processing the Left value
     * @param rightConsumer a function for processing the Right value
     * @throws NullPointerException if leftConsumer or rightConsumer is null
     */
    @Override
    public void forEach(@NotNull Consumer<L> leftConsumer, @NotNull Consumer<R> rightConsumer) {
        Objects.requireNonNull(leftConsumer);
        leftConsumer.accept(getValue());
    }

    /**
     * Converts Any result to an Option
     *
     * @return Option
     * @see Option
     */
    @Override
    public @NotNull Option<R> toOption() {
        return Option.empty();
    }

    /**
     * Converts Any result to an Optional
     *
     * @return Optional
     * @see java.util.Optional
     */
    @Override
    public @NotNull Optional<R> toOptional() {
        return Optional.empty();
    }
}
