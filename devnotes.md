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

`@Id` annotation describes to jdbc which of class fields, should be a primary key of our entity. There is no alternative for jpa `@GeneratedValue` so we have to delegate primary key generation either to database or our application.

## Accessing database data and mapping into entities

### Jdbc repository

At this point the next challenge was to provide a bridge between database and application. It is really easy with usage od spring data `CrudRepository<T, ID>`, where:

- `T` is a type of our entity
- `ID` is a type of id representing application's mapping for database table primary key.

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

### Testing repository

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

## Mapping class name and fields into database

I wasn't expecting this test to work right off the bat, as I have known that the query generated by repository will not match actual `ACTORS` table. But since I have already enabled `trace` level of spring data I was able to see exact queries that have been created.

`NOTE:` I have temporarily change `birthdate` and `deathdate` to debug how properties in camel case will be mapped.

```java
@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class ActorEntity {

    @Id Long id;
    String name, surname;
    LocalDate birthDate, deathDate;

}
```

Such class implementation generated fallowing sql query (jdbcRepository.save(entity)):

```sql
INSERT INTO actor_entity (name, surname, birth_date, death_date) VALUES (?, ?, ?, ?)
```

## Mapping fields and classes into database

Spring Data Jdbc has its own implementation of how class fields, as well as a name of class are mapped into a database table.

There are two options to choose from to map our classes correctly:

- annotations `@Column` and `@Table`
- custom implementation of `NamingStrategy`

Both of which can be used simultaneously with `annotations` having `higher priority`.

### Annotations `@Column` and `@Table`

- Annotation `@Column` replaces custom way field is mapped into query column name with value provided.
- Annotation `@Table` replaces custom way class name is mapped in query as a table name.

```java
@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("ACTORS")
public class ActorEntity {

    @Id Long id;
    @Column("Name") String name;
    @Column("Surname") String surname;
    @Column("BirthDate") LocalDate birthdate;
    @Column("DeathDate") LocalDate deathdate;

}
```

Such Actor class implementation would result in fallowing query. (jdbcRepository.save(entity))

```sql
INSERT INTO ACTORS (Name, Surname, Birthdate, Deathdate) VALUES (?, ?, ?, ?)
```

### Naming Strategy

More generic way to achieve same result would be to override default `NamingStrategy` used by String Data JDBC.

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

    There were two methods that can be overriden:

    - `getTableName` - I have implemented it so class names like `SomethingFun` would be by default by mapped into table name in query like `SOMETING_FUN`.
    - `getColumnName` - I have implemented it so class fields like `somethingFun` would be by default mapped into column name in query like `SomethingFun`

1. Redefining Actor class yet again:

    ```java
    @Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
    @Table("ACTORS")
    class ActorEntity {

        @Id Long id;
        String name, surname;
        LocalDate birthdate, deathdate;

    }
    ```

    I was able to drop `@Column` annotation since Spring Data JDBC will now take care of it for me. Yet I have decided on leaving `@Table` annotation since Not every class will later have entity postfix (aggregates).

### NamingStrategy in tests

At this point I have run into an issue where there was no `StringContext` in tests, as a result There was no custom `NamingStrategy` provided.

I have worked it out by adding a `@Configuration` to JdbcActorRepositoryTest, but since I have to enable `@EnableAutoConfiguration` its a bad solution. I have to go back to it later on.

```java
@DataJdbcTest
class JdbcActorRepositoryTest {

    @Configuration @EnableAutoConfiguration
    static class JdbcTestConfiguration {

        @Bean NamingStrategy namingStrategy(){
            return new JdbcNamingStrategyConfig();
        }

    }

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

### Final results

Final query with usage of annotations and custom NamingStrategy (jdbcRepository.save(entity)):

```sql
INSERT INTO ACTORS (Name, Surname, Birthdate, Deathdate) VALUES (?, ?, ?, ?)
```

## Custom Sql queries

Spring Data JDBC allows us to implement native sql queries in simple and painless way.

### Selects

By adding native sql query in `@Query` annotation it is possible to access database. CrudRepository will do all the work under the hood for us:

```java
@Repository
interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Query("SELECT * FROM ACTORS WHERE CONCAT(Name, ' ', Surname) LIKE :fullname")
    Optional<ActorEntity> findByFullname(String fullname);

}
```

```java
@Test
void test_M_findByFullname() {
    assertThat(repository.findByFullname("Joaquin Phoenix")).hasValue(expected);
}
```

### Updates

With update of data it is also important to add `@Modifying` annotation.

The result of method annotated with `@Modifying` can be either:

- void
- boolean - a callback if update was successful
- int - representing amount of rows affected by update query.

```java
@Repository
public interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Modifying
    @Query("UPDATE ACTORS SET Deathdate = :deathdate WHERE Id = :id")
    boolean updateDeathdate(Long id, LocalDate deathdate);

}
```

```java
@Test
void test_M_updateDeathdate() {
    final LocalDate expectedDate = LocalDate.of(2019, 12, 9);
    final boolean isSuccess = repository.updateDeathdate(expected.getId(), expectedDate); // sooorryy

    assertThat(isSuccess).isTrue();
    assertThat(repository.findById(expected.getId())).hasValueSatisfying(actor ->
            assertThat(actor.getDeathdate()).isEqualTo(expectedDate)
    );
}
```

## Enum attributes

### With CrudRepository

Next I have decided to check how does enum support work for library. I have added fallowing field to `ActorEntity`.

```java
public enum Gender {
    Male, Female, Other
}


@Data @AllArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
@Table("ACTORS")
class ActorEntity {

    @Id Long id;
    String name, surname;
    LocalDate birthdate, deathdate;
    Gender gender;

}
```

After running all tests have passed meaning that both conversion of enum between application and database works fine in default `CrudRepository` methods.

### In custom query

Next up I tried to use enum in a custom query as below:

```java
@Repository
interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Query("SELECT * FROM ACTORS WHERE Gender = :gender")
    List<ActorEntity> findAllByGender(Gender gender);

}
```

And check result with test:

```java
@Test
void test_M_findAllByGender() {
   assertThat(repository.findAllByGender(Gender.Male)).isEqualTo(Collections.singletonList(expected));
}
```

I was surprised to find out that this test did not pass. I have tried to find how to declare a mapper recognized by database engine with no luck. I will go back to it in the future.

Replacing `enum` with its `String` value works fine.

```java
@Repository
interface JdbcActorRepository extends CrudRepository<ActorEntity, Long> {

    @Query("SELECT * FROM ACTORS WHERE Gender = :gender")
    List<ActorEntity> findAllByGender(String gender);

}
```

```java
@Test
void test_M_findAllByGender() {
   assertThat(repository.findAllByGender(Gender.Male.name())).isEqualTo(Collections.singletonList(expected));
}
```