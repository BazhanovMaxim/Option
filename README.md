* [Option](#Option)
    * [Scope functions](#scope-functions)
        * [Apply](#apply)
        * [And](#and)
    * [Error Handling](#effective-error-handling)
    * [Instance](#working-with-class-type-checking)
* [Any](#any)
    * [map](#map)
    * [flatMap](#flatmap)
    * [filterOrElse](#filterorelse)
    * [exists](#exists)
    * [fold](#fold)
    * [forEach](#foreach)
    * [joinLeft](#joinleft)
    * [joinRight](#joinright)
    * [getRight](#getright)
    * [getLeft](#getleft)
    * [isRight](#isright)
    * [isLeft](#isleft)
    * [ifRight](#ifright)
    * [ifLeft](#ifleft)
    * [toOption](#tooption)
    * [toOptional](#tooptional)

# Option

`Option` - wrapper over the standard in Java Optional with additional methods.

## Scope functions

2 functions have been added to the Option library, the sole purpose of which is to execute a block of code
in the context of the object. When you call such a function on an object using a lambda expression, it
forms a temporary scope in which you can access the object without its name.
All functions perform the same action: execute a block of code for an object.

### Apply

```java
public static void main(String[] args) {
    Option.of("   Hello world   ")
            .apply(System.out::println)     // Hello world
            .map(String::toUpperCase)
            .apply(System.out::println);    // HELLO WORLD
}
```

### And

```java
public static void main(String[] args) {
    Option.of("   Hello world   ")
            .apply(System.out::println)                 // Hello world
            .map(String::toUpperCase)
            .and(() -> System.out.println("result:"))
            .apply(System.out::println);                // HELLO WORLD
}
```

## Effective error handling

When working with Java code, we often come across methods that throw an exception in the signature, so
we need to manually process them through try/catch, which makes the code not very readable and rude.
The Option has added methods that allow you to handle such situations.

```java
public String getProperty(String path) throws Exception {
    return ProjectProperties.getInstance().findPropertyByPath(path);
}
```

### The basic Java version
```java
final var dbConnectionUrl = "dbConnection.url";
final String url;
try {
    url = getProperty(dbConnectionUrl);
} catch (Exception e) {
    throw new RuntimeException("Couldn't find a property to connect to the database: " + dbConnectionUrl);
}
DbConnection.connect(url);
```

### Error handling with Option:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .onFailure(() -> new RuntimeException("Couldn't find a property to connect to the database: " + dbConnectionUrl));
```

### Error handling option with nested error:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .onFailureRunError();
```

### A variant of error handling with the addition of a nested error in StackTrace:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .ofFailure(() -> new RuntimeException("Не удалось найти свойство для подключения к базе данных: " + dbConnectionUrl), true);
```

## Working with class type checking

### isInstance

```java
public interface ICar { }
```

```java
public class PassengerCar implements ICar { }
```

```java
Option.of(new PassengerCar()).isInstance(ICar.class);   // true
```

### ifInstance

```java
public interface ICar {
    void drive();
}
```

```java
public class PassengerCar implements ICar {
    @Override
    public void drive() {
        System.out.println("Passenger car is driving");
    }
}
```

#### ifInstance(Class<?> aClass)

```java
Option.of(new PassengerCar()).ifInstance(ICar.class).apply(ICar::drive);
```

#### ifInstance(@NotNull Class<?> aClass, Runnable runnable)

```java
Option.of(new PassengerCar()).ifInstance(ICar.class, () -> System.out.println("..."));
```

#### ifInstance(@NotNull Class<?> aClass, Consumer<? super T> action)

```java
Option.of(new PassengerCar()).ifInstance(ICar.class, ICar::drive);
```

# Any

Any is a data type that is used to represent a value, which can be one of two types: Any Left or Right.
This data type is used in functional programming to handle errors or to represent two possible states.

Main Features of Any:
1. Two states:
    * **Left**: Usually used to represent an error or an exceptional condition.
    * **Right**: Usually used to represent a successful outcome or a normal state.
2. Immutability:
    * Any is usually an immutable type, which means that once created, its value cannot be changed.
      Methods for working with values: **map**, **flatMap**, **filterOrElse**, **exists**, **fold**, **forEach** and
      other methods allow you to work with Any values without having to explicitly check the state (Left or Right).

## map
In the context of the Any class, it is used to convert the value of Right using a given function.
This method accepts a function that takes the value Right and returns a new value.
If the Any object is Right, the map method applies the function to the Right value and
returns a new Any object with the converted Right value. If Any object is Left,
the map method simply returns the same Left object unchanged.

```java
Any<String, Integer> any = new Right<>(5);
Any<String, Integer> result = any.map(x -> x * 2);
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
// Right: 10
any = new Left<>("Something went wrong");
result = any.map(x -> x * 2);
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
// Left: Something went wrong
```

## flatMap
It is used to convert the Right value to another Any object.
This method takes a function that takes the value Right and returns a new object Any.
If the original Any object is Left, the flatMap method simply returns the same Left object.

```java
Any<String, Integer> Any = new Right<>(5);
Any<String, Integer> result = Any.flatMap(x -> {
    if (x > 10) {
        return new Right<>(x * 2);
    } else {
        return new Left<>("Value is too small");
    }
});
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
```

## filterOrElse
Checks the value of Right using a predicate. If the predicate returns `true`,
the current Any is returned. If the predicate returns `false`, Left is returned with the specified value

```java
Any<String, Integer> any = new Right<>(5);
Any<String, Integer> result = any.filterOrElse(x -> x > 10, "Value is too small");
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
```

## exists
In the context of the Any class, it is used to check
whether a value that satisfies a given predicate exists
on the right side of the Any object. This method returns `true`
if the value of Right satisfies the predicate, and `false` otherwise.
If Any object is Left, the method returns `false`,
since there is no Right value in Left to check.

```java
Any<String, Integer> any = new Right<>(5);
boolean result = any.exists(x -> x > 10););
System.out.println("Exists: " + result); // Exists: false
any = new Right<>(15);
result = any.exists(x -> x > 10);
System.out.println("Exists: " + result); // Exists: true
```

## fold
It is used to convert Any object to a value of another type
by applying two functions: one for the Left case and the other for the Right case.
This method allows you to "collapse" Any object into a single value, regardless of whether
it is Left or Right.

```java
Any<String, Integer> any = new Right<>(5);
String result = any.fold(
        left -> "Error: " + left,
        right -> "Success: " + right
);
System.out.println(result); // Success: 5
any = new Left<>("Something went wrong");
result = any.fold(
        left -> "Error: " + left,
        right -> "Success: " + right
);
System.out.println(result); // Выведет: Error: Something went wrong
```

## forEach
It is used to perform an action (side effect) on the value of Right or Left,
depending on the type of Any object.
This method takes two arguments: a function to process the Left value and a function to process the Right value.
Depending on whether the object is Any Left or Right, the corresponding function will be called.

```java
Any<String, Integer> any = new Right<>(5);
any.forEach(
        left -> System.out.println("Left: " + left),
        right -> System.out.println("Right: " + right)
);
// Right: 5
any = new Left<>("Something went wrong");
any.forEach(
        left -> System.out.println("Left: " + left),
        right -> System.out.println("Right: " + right)
);
// Left: Something went wrong
```

## joinLeft
it is used to combine two objects Any, where one of them is Left.
This method takes another Any object and combines it with the current Any object
if the current object is Left. If the current object is Right,
the joinLeft method simply returns the current Right object.

```java
Any<String, Integer> any1 = new Left<>("Error 1");
Any<String, Integer> any2 = new Left<>("Error 2");
Any<String, Integer> result = any1.joinLeft(any2);
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
// Left: Error 2
Any<String, Integer> any3 = new Right<>(5);
Any<String, Integer> result2 = any3.joinLeft(any2);
if (result2.isLeft()) {
    System.out.println("Left: " + result2.getLeft());
} else {
    System.out.println("Right: " + result2.getRight());
}
// Right: 5
```

## joinRight
it is used to combine two objects Any, where one of them is Right.
This method takes another Any object and combines it with the current Any object
if the current object is Right. If the current object is Left,
the joinRight method simply returns the current Left object.

```java
Any<String, Integer> any1 = new Right<>(5);
Any<String, Integer> any2 = new Right<>(10);
Any<String, Integer> result = any1.joinRight(any2);
if (result.isLeft()) {
    System.out.println("Left: " + result.getLeft());
} else {
    System.out.println("Right: " + result.getRight());
}
// Right: 10
Any<String, Integer> any3 = new Left<>("Error");
Any<String, Integer> result2 = any3.joinRight(any2);
if (result2.isLeft()) {
    System.out.println("Left: " + result2.getLeft());
} else {
    System.out.println("Right: " + result2.getRight());
}
```

## getRight
Return value of Right or null

## getLeft
Return value of Left or null

## isRight
Return `true` if Any is Right or `false`

## isLeft
Return `true` if Any is Left or `false`

## ifRight
Performs an action on the value if Any is Right

## ifLeft
Performs an action on the value if Any is Left

## toOption
Converts Any Right value to Option

## toOptional
Converts Any Right value to Optional