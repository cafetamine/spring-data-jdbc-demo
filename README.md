# Spring data jdbc demo

## Synopsis

This project was created as a workspace to study and practise `Spring Data JDBC` library.

## Core concepts

1. Database table mapping.
1. Class relationships.
1. Aggregates.
1. Domain-Driven Design.

## Knowledge sources

1. Articles
    - [Spring Data JDBC, References, and Aggregates](https://spring.io/blog/2018/09/24/spring-data-jdbc-references-and-aggregates)
1. Talks
    - [The New Kid on the Block: Spring Data JDBC](https://www.youtube.com/watch?v=AnIouYdwxo0)
1. Books
    - `Domain-Driven Design`: Tackling Complexity in the Heart of Software `by Eric Evans`

## Set up

### Maven dependency

In simple spring boot project I have provided `Spring Data JBDC` library by adding fallowing dependency to `pom.xml`.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
```

### Logging queries

Another initial step I have found important was to add logger level to `application.yaml`. This configuration enables logging of queries invoked on database by Spring Data CrudRepositories.

```yml
logging:
  level:
    org:
      springframework:
        jdbc:
          core: TRACE
```

Example logline:

2019-12-10 21:19:52.528 DEBUG 10224 --- [main] o.s.jdbc.core.JdbcTemplate : Executing prepared SQL statement `[INSERT INTO ACTORS (name, surname, birthdate, deathdate) VALUES (?, ?, ?, ?)]`

## Simple entity implementation

### Database

Since I am using `H2` database in my project I will add `schema.sql` file in root of resources folder and create table for very creative object - an Actor.

```sql
CREATE TABLE ACTORS (
    Id BIGINT IDENTITY PRIMARY KEY,
    Name VARCHAR(120) NOT NULL,
    Surname VARCHAR(120) NOT NULL,
    Birthdate DATE NOT NULL,
    Deathdate DATE
);
```

### Class and database table mapping

```java
@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class ActorEntity {

    @Id Long id;
    String name, surname;
    LocalDate birthdate, deathdate;

}
```

1. `@Id` annotation describes to jdbc which of class fields should be a primary key of our entity. There is no alternative for jpa `@GeneratedValue` so we have to delegate primary key generation either to database or our application.

### Jdbc repository

To access a database all we have to do is to add into our project a spring data `CrudRepository<T, ID>` where:

- T is a type of our entity
- ID is a type of id representing application's mapping for database table primary key.

```java
@Repository
public interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {
}
```

Fallowing repository gives us some basic crud methods we can use to modify our database:

- save
- saveAll
- findById
- existsById
- findAll
- findAllById
- count
- deleteById
- delete
- deleteAll
- deleteAll

At this point I could finally start testing application. Spring data jdbc ships with test annotation `@DataJdbcTest`. It is a rather powerful tool that will manage our database while test are being performed. It will automatically manage transactions in our application so we don't have to worry about state of our database.

```java
@DataJdbcTest
class JdbcActorRepositoryTest {

    @Autowired JdbcActorRepository repository;

    private ActorEntity expected;

    @BeforeEach
    void beforeEach() {
        expected = repository.save(new ActorEntity(
                null,
                "Joaquin",
                "Phoenix",
                LocalDate.of(1974, 10, 28), null)
        );
    }

    @Test
    void test_M_findById() {
        assertThat(repository.findById(expected.getId())).hasValue(expected);
    }

}
```

### Naming strategy

Spring Data Jdbc has its own implementation of how class fields, as well as a name of class are mapped into a database table. Fallowing entity:

```java
class SomeClass {
    String someAttribute;
}
```

Would look as fallows in database with default configuration:

```sql
CREATE TABLE some_class (
    some_attribute VARCHAR(10);
)
```

If there is a specific way you want to name your database tables there are two options to choose from, and can be mixed. We can achieve fallowing result:

```sql
CREATE TABLE SOME_CLASS (
    SomeAttribute VARCHAR(10);
)
```

1. Using annotations:

    ```java
    @Table("SOME_CLASS")
    class SomeClass {
        @Column("SomeAttribute") String someAttribute;
    }
    ```

1. Defining a NamingStrategy:

    ```java
    @Component
    public class JdbcNamingStrategyConfig implements NamingStrategy {

        @Override
        public String getTableName(final Class<?> type) {
            Assert.notNull(type, "Type must not be null.");
            return ParsingUtils.reconcatenateCamelCase(type.getSimpleName(), "_").toUpperCase();
        }

        @Override
        public String getColumnName(final RelationalPersistentProperty property) {
            Assert.notNull(property, "Property must not be null.");
            return property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
        }

    }
    ```

1. If used together `@annotations` will always have higher priority over `NamingStrategy`.


```java
@Repository
public interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Modifying
    @Query("UPDATE ACTORS SET Deathdate = :deathdate WHERE Id = :id")
    boolean updateDeathdate(Long id, LocalDate deathdate);

}
```

```java
@DataJdbcTest
class JdbcActorRepositoryTest {

    @Autowired JdbcActorRepository repository;

    private ActorEntity expected;

    @BeforeEach
    void beforeEach() {
        expected = repository.save(new ActorEntity(
                null,
                "Joaquin",
                "Phoenix",
                LocalDate.of(1974, 10, 28), null)
        );
    }

    @Test
    void test_M_findByFullname() {
        final Optional<ActorEntity> actual = repository.findByFullname("Joaquin Phoenix");

        assertThat(Optional.of(expected)).isEqualTo(actual);
    }

    @Test
    void test_M_updateDeathdate() {
        final LocalDate expectedDate = LocalDate.of(2019, 12, 9);
        final boolean isSuccess = repository.updateDeathdate(1L, expectedDate); // sooorryy

        assertThat(isSuccess).isTrue();
        assertThat(repository.findById(1L)).hasValueSatisfying(actor ->
                assertThat(actor.getDeathdate()).isEqualTo(expectedDate));
    }

}
```
