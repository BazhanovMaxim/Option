# Option

`Option` - обёртка над стандартным в Java Optional с дополнительными методами.

## Функции области действия

В библиотеку Option добавлены 2 функции, единственной целью которых является выполнение блока кода 
в контексте объекта. Когда вы вызываете такую функцию для объекта с помощью лямбда-выражения, она 
формирует временную область видимости, в которой вы можете получить доступ к объекту без его имени.
Все функции выполняют одно и то же действие: выполняют блок кода для объекта.

### Apply

```java
public static void main(String[] args) {
    Option.of("   Hello world   ")
            .apply(System.out::println)
            .map(String::trim)
            .apply(System.out::println);
}
```
Результат выполнения:
```
Hello world
HELLO WORLD
```

### And

```java
public static void main(String[] args) {
    Option.of("   Hello world   ")
            .apply(System.out::println)
            .map(String::trim)
            .and(() -> System.out.println("Результат нового значения:"))
            .apply(System.out::println);
}
```
Результат выполнения:
```
Hello world
Результат нового значения:
HELLO WORLD
```

## Эффективная обработка ошибок
 
Работая с Java кодом мы нередко сталкиваемся с методами, которые в сигнатуре выбрасывают исключение, поэтому
нам необходимо их вручную обработать через try/catch, что делает код не очень читаемым и грубым.
В Option добавлены методы, которые позволяют обработать такие ситуации.

```java
public String getProperty(String path) throws Exception {
    return ProjectProperties.getInstance().findPropertyByPath(path);
}
```

### Базовый вариант
```java
final var dbConnectionUrl = "dbConnection.url";
final String url;
try {
    url = getProperty(dbConnectionUrl);
} catch (Exception e) {
    throw new RuntimeException("Не удалось найти свойство для подключения к базе данных: " + dbConnectionUrl);
}
DbConnection.connect(url);
```

### Вариант обработки ошибки с Option:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .onFailure(() -> new RuntimeException("Не удалось найти свойство для подключения к базе данных: " + dbConnectionUrl));
```

### Вариант обработки ошибки со вложенной ошибки:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .onFailureRunError();
```

### Вариант обработки ошибки с добавлением вложенной ошибки в StackTrace:
```java
final var dbConnectionUrl = "dbConnection.url";
Option.of(dbConnectionUrl)
        .runCatching(this::getProperty)
        .onSuccess(DbConnection::connect)
        .ofFailure(() -> new RuntimeException("Не удалось найти свойство для подключения к базе данных: " + dbConnectionUrl), true);
```

## Работа с проверкой типов классов

### isInstance

```java
public interface ICar { }
```

```java
public class PassengerCar implements ICar { }
```

```java
var car = new PassengerCar();
var isInstance = Option.of(car).isInstance(ICar.class);
System.out.println(isInstance);
```
Результат выполнения:
```java
true
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
Option.of(new PassengerCar())
        .ifInstance(ICar.class)
        .apply(ICar::drive);
```

#### ifInstance(@NotNull Class<?> aClass, Runnable runnable)

```java
Option.of(new PassengerCar())
        .ifInstance(ICar.class, () -> System.out.println("..."));
```

#### ifInstance(@NotNull Class<?> aClass, Consumer<? super T> action)

```java
Option.of(new PassengerCar())
        .ifInstance(ICar.class, ICar::drive);
```