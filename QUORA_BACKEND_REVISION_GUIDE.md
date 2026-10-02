# Quora Backend — Java & Spring Production Revision Handbook

> **Purpose:** This document is a revision handbook for everything we have learned while building the Quora-like backend.
>
> This is **not** a chronological tutorial. It is a **memory/reference document**.
>
> When revising in the future, focus on:
>
> - What problem does this concept solve?
> - Why do we need it?
> - How does it work?
> - What should I remember?
> - What mistake should I avoid?
> - Where is it used in our project?

---

# Table of Contents

1. [Java Fundamentals](#1-java-fundamentals)
2. [Spring and Spring Boot](#2-spring-and-spring-boot)
3. [IoC, DI and DIP](#3-ioc-di-and-dip)
4. [Production Application Architecture](#4-production-application-architecture)
5. [Lombok](#5-lombok)
6. [JPA, Hibernate and Spring Data JPA](#6-jpa-hibernate-and-spring-data-jpa)
7. [Entities and BaseEntity](#7-entities-and-baseentity)
8. [JPA Relationships](#8-jpa-relationships)
9. [Fetch Types and N+1](#9-fetch-types-and-n1)
10. [Transactions](#10-transactions)
11. [Dirty Checking](#11-dirty-checking)
12. [Optimistic Locking and `@Version`](#12-optimistic-locking-and-version)
13. [DTOs](#13-dtos)
14. [Validation](#14-validation)
15. [Exception Handling](#15-exception-handling)
16. [Flyway and Database Schema Management](#16-flyway-and-database-schema-management)
17. [Pagination](#17-pagination)
18. [Database Indexes](#18-database-indexes)
19. [Composite Indexes and Leftmost Prefix](#19-composite-indexes-and-leftmost-prefix)
20. [EXPLAIN](#20-explain)
21. [LIKE Search](#21-like-search)
22. [MySQL FULLTEXT Search](#22-mysql-fulltext-search)
23. [Specifications and Dynamic Filtering](#23-specifications-and-dynamic-filtering)
24. [Elasticsearch](#24-elasticsearch)
25. [Elasticsearch Internals](#25-elasticsearch-internals)
26. [Search Architecture](#26-search-architecture)
27. [Authentication vs Authorization](#27-authentication-vs-authorization)
28. [Spring Security](#28-spring-security)
29. [Password Hashing](#29-password-hashing)
30. [JWT](#30-jwt)
31. [Roles, Permissions and Authorities](#31-roles-permissions-and-authorities)
32. [Ownership Authorization](#32-ownership-authorization)
33. [Events](#33-events)
34. [Publisher and Subscriber](#34-publisher-and-subscriber)
35. [Synchronous vs Asynchronous Processing](#35-synchronous-vs-asynchronous-processing)
36. [Message Brokers and Kafka](#36-message-brokers-and-kafka)
37. [Dual-Write Problem](#37-dual-write-problem)
38. [Outbox Pattern](#38-outbox-pattern)
39. [Idempotency and Event Ordering](#39-idempotency-and-event-ordering)
40. [Current Project Architecture](#40-current-project-architecture)
41. [Critical Rules to Remember](#41-critical-rules-to-remember)
42. [Common Mistakes](#42-common-mistakes)
43. [Interview Revision](#43-interview-revision)

---

# 1. Java Fundamentals

## 1.1 Java Is Object-Oriented

The important object-oriented concepts are:

```text
Class
Object
Encapsulation
Inheritance
Polymorphism
Abstraction
Composition
```

### Class

A class is a blueprint.

```java
public class User {
    private String name;
}
```

### Object

An object is an instance of a class.

```java
User user = new User();
```

### Remember

```text
Class  = blueprint
Object = actual instance
```

---

# 1.2 Encapsulation

Encapsulation means keeping internal state controlled by the class.

Instead of:

```java
public String password;
```

prefer:

```java
private String password;
```

and expose controlled behavior/access.

### Why?

Because external code should not freely modify internal state.

---

# 1.3 Inheritance

Inheritance allows one class to reuse/extend another.

```java
public class Question extends BaseEntity {
}
```

Our project uses inheritance here:

```text
BaseEntity
    |
    +---- User
    +---- Question
    +---- Answer
    +---- Comment
    +---- Tag
```

The common fields live in `BaseEntity`.

---

# 1.4 Composition

Composition means building an object using other objects.

Example:

```text
Question
  |
  +---- User
  +---- Tags
  +---- Answers
```

In production systems, composition is often preferred over deep inheritance hierarchies because relationships between objects are usually more flexible than inheritance.

---

# 1.5 Interface

An interface defines a contract.

```java
public interface QuestionService {
    QuestionResponse createQuestion(...);
}
```

Implementation:

```java
@Service
public class QuestionServiceImpl implements QuestionService {
}
```

### Remember

```text
Interface = what
Implementation = how
```

---

# 1.6 `static` vs Instance

A `static` member belongs to the class.

An instance member belongs to an object.

```java
class Example {

    static int count;

    String name;
}
```

```text
static count
    ↓
shared by class

name
    ↓
different for each object
```

---

# 1.7 `final`

`final` prevents reassignment.

```java
private final UserRepository userRepository;
```

The reference cannot be assigned to another repository after construction.

This is why constructor injection works well with:

```java
@RequiredArgsConstructor
```

---

# 1.8 UUID

We use UUIDs for entity IDs.

```java
UUID
```

Why?

- Very large ID space
- Can be generated without depending on a central numeric sequence
- Useful in distributed systems
- Harder to guess than simple sequential IDs

Our BaseEntity:

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

### Remember

UUID is an identifier strategy, not automatically a security mechanism.

---

# 2. Spring and Spring Boot

# 2.1 What Is Spring?

Spring is a framework/ecosystem that provides infrastructure for building Java applications.

Important concepts:

```text
IoC
DI
Beans
AOP
Transactions
Web
Security
Data access
```

---

# 2.2 What Is Spring Boot?

Spring Boot is built on top of Spring and simplifies application setup.

Without Boot, we would configure much more infrastructure manually.

Spring Boot provides:

```text
Auto-configuration
Starter dependencies
Embedded server
Externalized configuration
Production-oriented defaults
```

### Remember

```text
Spring      = framework/ecosystem
Spring Boot = easier way to build Spring applications
```

---

# 2.3 Bean

A Spring bean is an object managed by the Spring container.

Example:

```java
@Service
public class QuestionService {
}
```

Spring creates and manages the object.

---

# 2.4 Component Scanning

Spring scans configured packages for components such as:

```java
@Component
@Service
@Repository
@Controller
@RestController
@Configuration
```

and registers them as beans.

---

# 3. IoC, DI and DIP

# 3.1 IoC — Inversion of Control

Normally:

```text
Your code creates objects
```

With Spring:

```text
Spring container creates/manages objects
```

So control over object creation is inverted.

```text
Without IoC:
Application -> creates dependencies

With IoC:
Spring -> creates dependencies
```

---

# 3.2 Dependency Injection

Instead of:

```java
QuestionService service = new QuestionService();
```

Spring injects dependencies.

```java
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
}
```

Spring provides `questionRepository`.

### Why?

Because the service should not be responsible for creating infrastructure dependencies.

---

# 3.3 Constructor Injection

Preferred:

```java
public QuestionService(QuestionRepository questionRepository) {
    this.questionRepository = questionRepository;
}
```

With Lombok:

```java
@RequiredArgsConstructor
```

### Why constructor injection?

- Dependencies are explicit
- Fields can be `final`
- Object cannot be created without required dependencies
- Easier testing
- Better immutability

### Remember

```text
Prefer constructor injection.
Avoid field injection.
```

---

# 3.4 DIP — Dependency Inversion Principle

High-level business logic should depend on abstractions rather than concrete low-level implementations.

Example:

```text
QuestionService
      |
      v
QuestionRepository
      |
      v
JPA implementation
```

The service works with the repository abstraction.

---

# 4. Production Application Architecture

Our project follows a feature-oriented structure:

```text
feature/
    user/
    question/
    answer/
    comment/
    tag/
    auth/
    authorization/

core/
    common/
    security/
    config/
    elasticsearch/
    outbox/
```

### Important rule

Feature-specific business logic belongs inside the feature.

Generic infrastructure belongs inside `core`.

For example:

```text
Question event payload
    -> feature.question.event

Outbox infrastructure
    -> core.outbox
```

### Why?

Core infrastructure should not depend on a specific feature.

```text
core
  X
  ↓
question

question
  ↓
core
```

The dependency should flow toward reusable infrastructure.

---

# 4.1 Controller → Service → Repository

Our basic application flow:

```text
HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
JPA/Hibernate
     ↓
MySQL
```

### Controller

Handles HTTP concerns.

```text
Request
Response
Path variables
Query parameters
HTTP status
```

### Service

Handles business logic.

### Repository

Handles persistence/data access.

### Remember

```text
Controller = HTTP
Service = Business logic
Repository = Data access
```

---

# 4.2 Don't Put Business Logic in Controllers

Bad:

```java
@PostMapping
public ResponseEntity<?> create(...) {

    // lots of business logic
    // database calls
    // authorization
    // tag resolution
}
```

Better:

```text
Controller
    ↓
Service
```

Controller should stay thin.

---

# 4.3 Entity vs DTO

Entity:

```text
Database representation
```

DTO:

```text
API representation
```

Never assume they must be identical.

---

# 5. Lombok

Common annotations we use:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
```

## `@Getter`

Generates getters.

## `@Setter`

Generates setters.

## `@Builder`

Allows:

```java
Question.builder()
    .title(...)
    .content(...)
    .build();
```

## `@NoArgsConstructor`

Generates no-argument constructor.

JPA entities need a no-argument constructor.

## `@AllArgsConstructor`

Generates constructor containing all fields.

Use carefully on entities because it can make it easy to construct invalid objects.

## `@RequiredArgsConstructor`

Generates constructor for required fields, especially `final` fields.

This is why it is commonly used for Spring dependency injection.

## `@Slf4j`

Creates:

```java
log.info(...)
log.warn(...)
log.error(...)
log.debug(...)
```

### Remember

Do not use `System.out.println` for production application logging.

---

# 6. JPA, Hibernate and Spring Data JPA

These three are related but different.

```text
JPA
 ↓
Specification/API

Hibernate
 ↓
JPA implementation

Spring Data JPA
 ↓
Repository abstraction/productivity layer
```

---

# 6.1 JPA

JPA is a Java specification for ORM.

It defines concepts such as:

```text
@Entity
@Id
@OneToMany
@ManyToOne
@ManyToMany
```

JPA itself is not the database engine.

---

# 6.2 Hibernate

Hibernate is an implementation of JPA.

It performs the actual ORM work.

```text
Java Entity
    ↓
Hibernate
    ↓
SQL
    ↓
Database
```

---

# 6.3 Spring Data JPA

Spring Data JPA provides repository abstractions.

Example:

```java
public interface UserRepository
        extends JpaRepository<User, UUID> {
}
```

It provides methods such as:

```java
save()
findById()
findAll()
delete()
existsById()
```

---

# 6.4 ORM

ORM = Object Relational Mapping.

It maps:

```text
Java Object
     ↕
Database Row
```

Example:

```java
@Entity
@Table(name = "questions")
public class Question {
}
```

maps the Java class to the database table.

---

# 6.5 Important Mental Model

```text
Java Object
    ↓
JPA mapping
    ↓
Hibernate
    ↓
SQL
    ↓
MySQL
```

---

# 7. Entities and BaseEntity

Our entities inherit common fields from:

```java
BaseEntity
```

Current model:

```java
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
```

---

# 7.1 `@MappedSuperclass`

It means:

```text
BaseEntity
   ↓
its fields are inherited by entities
```

But BaseEntity itself is not a separate database table.

For example:

```text
users
    id
    created_at
    updated_at
    version

questions
    id
    created_at
    updated_at
    version
```

---

# 7.2 `@Id`

Marks the primary key.

```java
@Id
private UUID id;
```

---

# 7.3 `@GeneratedValue`

Tells JPA how the identifier is generated.

```java
@GeneratedValue(strategy = GenerationType.UUID)
```

---

# 7.4 Auditing

We use:

```java
@CreatedDate
@LastModifiedDate
```

with:

```java
@EnableJpaAuditing
```

and:

```java
@EntityListeners(AuditingEntityListener.class)
```

This allows Spring Data JPA to populate audit timestamps.

### Remember

```text
createdAt = when entity was created
updatedAt = when entity was last changed
```

---

# 7.5 `@CreatedDate`

Usually populated when the entity is first persisted.

It is marked:

```java
updatable = false
```

because creation time should not change.

---

# 7.6 `@LastModifiedDate`

Updated when the entity is modified through JPA.

---

# 8. JPA Relationships

Main relationship types:

```text
@ManyToOne
@OneToMany
@ManyToMany
@OneToOne
```

---

# 8.1 Many-to-One

Many questions belong to one user:

```text
User
 |
 +---- Question
 +---- Question
 +---- Question
```

Question:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

Database:

```text
questions.user_id
        ↓
users.id
```

---

# 8.2 One-to-Many

One user can have many questions:

```java
@OneToMany(mappedBy = "user")
private List<Question> questions;
```

`mappedBy = "user"` refers to the Java field in `Question`.

It does **not** refer to the database column.

---

# 8.3 Many-to-Many

A question can have many tags.

A tag can belong to many questions.

```text
Question
   |
   +---- Tag
   +---- Tag

Tag
   |
   +---- Question
   +---- Question
```

Therefore we use a join table:

```text
question_tags
```

with:

```text
question_id
tag_id
```

---

# 8.4 Why No `CascadeType.ALL` for Tags?

Tags are shared.

Suppose:

```text
Question A -> Java
Question B -> Java
```

If deleting Question A automatically deletes Tag Java:

```text
Question A deleted
      ↓
Tag Java deleted
      ↓
Question B broken
```

Therefore we should not blindly cascade delete shared entities.

### Remember

```text
Cascade is about lifecycle propagation.
It is not a performance optimization.
```

---

# 8.5 Ownership in JPA Relationships

The owning side controls the relationship mapping.

For:

```java
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

the Question side owns the relationship.

The inverse side:

```java
@OneToMany(mappedBy = "user")
```

uses `mappedBy`.

### Remember

```text
mappedBy = "Java field name on owning side"
```

---

# 9. Fetch Types and N+1

# 9.1 LAZY

LAZY means:

```text
Don't load the relationship until it is needed.
```

Example:

```java
@ManyToOne(fetch = FetchType.LAZY)
private User user;
```

---

# 9.2 EAGER

EAGER means the relationship is expected to be loaded immediately.

This can accidentally load too much data.

### Production rule

Prefer LAZY by default for relationships and fetch what you actually need.

---

# 9.3 N+1 Query Problem

Suppose:

```text
1 query -> fetch 20 questions

Then:
20 additional queries -> fetch each user's data
```

Total:

```text
1 + 20 = 21 queries
```

That is N+1.

---

# 9.4 How We Solve N+1

Common solutions:

```text
@EntityGraph
JOIN FETCH
DTO projections
Batch fetching
```

We used:

```java
@EntityGraph(attributePaths = {"user", "tags"})
```

or JPQL `JOIN FETCH` depending on the use case.

### Remember

Do not solve N+1 by making everything EAGER.

Instead:

```text
Identify query
   ↓
Determine required relationships
   ↓
Fetch intentionally
```

---

# 10. Transactions

A transaction groups database operations into an atomic unit.

Example:

```java
@Transactional
public void createQuestion(...) {
    ...
}
```

The goal is:

```text
ALL SUCCESS
    OR
ALL ROLLBACK
```

---

# 10.1 ACID

Important transaction properties:

```text
Atomicity
Consistency
Isolation
Durability
```

### Atomicity

All operations succeed or the transaction rolls back.

### Consistency

Database rules remain valid.

### Isolation

Concurrent transactions should not improperly interfere with each other.

### Durability

Committed data survives failures according to the database's durability guarantees.

---

# 10.2 Where Should `@Transactional` Go?

Usually at the service layer because the service defines the business operation.

Example:

```java
@Transactional
public QuestionResponse updateQuestion(...) {
    ...
}
```

---

# 10.3 Critical Rule: `@Transactional` Does Not Mean "Save Automatically"

This is a very important distinction.

Inside a transaction, if an entity is **managed**, Hibernate can detect changes automatically through dirty checking.

Example:

```java
@Transactional
public void updateQuestion(UUID id) {

    Question question =
        questionRepository.findById(id)
            .orElseThrow();

    question.setTitle("New title");
}
```

You do **not necessarily need**:

```java
questionRepository.save(question);
```

because:

```text
find entity
   ↓
managed entity
   ↓
change field
   ↓
dirty checking
   ↓
Hibernate generates UPDATE
   ↓
transaction commit
```

### Remember

```text
@Transactional
+
managed entity
+
field changed
=
dirty checking can persist the change
```

But do not overgeneralize:

```text
@Transactional does NOT mean every object you modify is automatically saved.
```

The entity must be managed by the current persistence context.

---

# 11. Dirty Checking

Dirty checking is one of the most important Hibernate concepts.

Suppose:

```java
Question question = repository.findById(id).orElseThrow();

question.setTitle("New title");
```

Hibernate keeps track of the managed entity's state.

Conceptually:

```text
Initial state:
title = "Old"

        ↓

Application:
setTitle("New")

        ↓

Hibernate compares state

        ↓

Detected change

        ↓

UPDATE questions
SET title = 'New'
WHERE id = ...
```

---

# 11.1 Why Does Dirty Checking Exist?

Without dirty checking, developers would have to explicitly tell Hibernate about every field change.

Dirty checking lets Hibernate synchronize managed entity changes automatically at flush/commit.

---

# 11.2 When Does Dirty Checking Happen?

Typically at flush/transaction commit.

Conceptually:

```text
Transaction starts
      ↓
Entity loaded
      ↓
Entity becomes managed
      ↓
Fields changed
      ↓
Flush
      ↓
SQL UPDATE
      ↓
Commit
```

---

# 11.3 `save()` vs Dirty Checking

This is a critical revision point.

### New entity

Usually:

```java
Question question = new Question();
questionRepository.save(question);
```

is needed to persist a new entity.

### Existing managed entity

Usually:

```java
@Transactional
public void update(...) {

    Question question = repository.findById(id).orElseThrow();

    question.setTitle(...);
}
```

does not require another `save()` just to persist the changed field.

### Why?

Because the entity is already managed.

---

# 11.4 What Does `save()` Actually Mean?

Spring Data JPA's `save()` is not simply:

```text
always INSERT
```

For a new entity it generally leads to persist behavior.

For an existing entity, Spring Data may use merge semantics depending on entity state.

Therefore:

```text
save() != "required after every setter"
```

### Production memory rule

```text
New entity:
    save/persist

Managed existing entity inside transaction:
    modify entity
    dirty checking handles UPDATE
```

---

# 11.5 `saveAndFlush()`

We use `saveAndFlush()` in places where we need the persistence context flushed before continuing.

For example, before creating an event payload that must contain updated database-managed values such as:

```text
version
createdAt
updatedAt
```

we may use:

```java
questionRepository.saveAndFlush(question);
```

### Important

```text
flush != commit
```

Flush means Hibernate synchronizes pending changes with the database.

Commit completes the transaction.

---

# 12. Optimistic Locking and `@Version`

Our BaseEntity contains:

```java
@Version
private Long version;
```

This is used for optimistic locking.

---

# 12.1 The Problem

Imagine two requests read the same question:

```text
Request A -> version 5
Request B -> version 5
```

Then:

```text
A changes title
B changes content
```

If both blindly update:

```text
A writes version 6
B writes version 6
```

One update may overwrite the other.

---

# 12.2 Optimistic Locking

Hibernate uses the version in the update condition.

Conceptually:

```sql
UPDATE questions
SET title = ?,
    version = 6
WHERE id = ?
  AND version = 5;
```

If another transaction already changed version 5 to 6:

```text
WHERE version = 5
```

matches no row.

Hibernate detects the optimistic locking conflict.

---

# 12.3 Why "Optimistic"?

Because we assume conflicts are relatively uncommon and don't lock the database row for the entire business operation.

Instead:

```text
Read
 ↓
Work
 ↓
Check version at update
 ↓
Fail if somebody changed it
```

---

# 12.4 `@Version` Has Another Role in Our Architecture

We also use the version as an event ordering value for Elasticsearch.

Conceptually:

```text
MySQL Question version
        ↓
Outbox event version
        ↓
Elasticsearch external version
```

This helps prevent:

```text
New event
   ↓
Old event
   ↓
Old data overwrites new data
```

---

# 13. DTOs

DTO = Data Transfer Object.

We use DTOs between API and domain/entity layers.

Example:

```java
public class UserRegistrationRequest {
    private String name;
    private String username;
    private String email;
    private String password;
}
```

---

# 13.1 Why DTOs?

Without DTOs, exposing entities directly can cause:

- Password exposure
- Uncontrolled fields
- Tight coupling between DB and API
- Serialization problems
- Difficult API evolution

### Remember

```text
Entity = persistence model
DTO = API/data-transfer model
```

---

# 13.2 Request DTO vs Response DTO

Request:

```text
Client -> Application
```

Response:

```text
Application -> Client
```

They often should be different.

For example:

```text
RegistrationRequest
    password included

UserResponse
    password NOT included
```

---

# 13.3 Mapper

A mapper converts:

```text
Entity -> DTO
DTO -> Entity
```

Example:

```java
questionMapper.toResponse(question);
```

### Why?

Keep mapping concerns separate from business logic.

---

# 14. Validation

We use Jakarta Validation.

Examples:

```java
@NotBlank
@NotNull
@Email
@Size
```

and:

```java
@Valid
```

at the controller boundary.

---

# 14.1 Why Validation?

Validation prevents invalid input from entering business logic.

Example:

```java
@NotBlank
private String title;
```

means the title cannot be blank.

---

# 14.2 `@NotNull` vs `@NotBlank`

`@NotNull`:

```text
value must not be null
```

`@NotBlank`:

```text
must not be null
must contain non-whitespace characters
```

`@NotBlank` is appropriate for strings where empty/whitespace values are invalid.

---

# 14.3 Validation Is Not Business Authorization

Validation:

```text
Is the request structurally valid?
```

Authorization:

```text
Is this user allowed to perform this operation?
```

Do not confuse them.

---

# 15. Exception Handling

We use global exception handling:

```java
@RestControllerAdvice
```

Instead of writing:

```text
try/catch
```

in every controller.

The global handler converts exceptions into consistent HTTP responses.

---

# 15.1 401 vs 403

Very important:

### 401 Unauthorized

The request is not authenticated.

Meaning:

```text
Who are you?
```

was not established.

Examples:

```text
Missing JWT
Invalid JWT
Expired JWT
```

### 403 Forbidden

The user is authenticated but does not have permission.

Meaning:

```text
I know who you are,
but you cannot perform this action.
```

---

# 15.2 Common Exceptions

Our application uses concepts such as:

```text
ResourceNotFoundException
UnauthorizedException
ForbiddenException
```

---

# 16. Flyway and Database Schema Management

Flyway manages database schema migrations.

Example:

```text
V1__create_users.sql
V2__create_questions.sql
V3__create_answers.sql
...
```

---

# 16.1 Why Flyway?

Without schema migration management:

```text
Developer A
Developer B
Production
Testing
```

can have different schemas.

Flyway gives us versioned schema history.

---

# 16.2 Critical Rule

Once a Flyway migration has been applied:

```text
DO NOT EDIT IT
```

Create a new migration.

Example:

```text
V10 applied

Need change

→ create V11
```

---

# 16.3 Flyway vs Hibernate `ddl-auto`

Our project uses:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Meaning:

```text
Flyway -> creates/changes schema

Hibernate -> validates entity mappings against schema
```

### Remember

Do not let both Flyway and Hibernate freely manage production schema.

---

# 17. Pagination

For large tables, don't load everything.

Bad:

```text
SELECT all questions
```

Better:

```text
page = 0
size = 10
```

---

# 17.1 Page

A page represents one subset of results.

```text
Page 0 -> questions 1-10
Page 1 -> questions 11-20
```

---

# 17.2 Generic PageResponse

Our API uses a generic structure:

```text
content
page
size
totalElements
totalPages
first
last
```

This keeps pagination response consistent across features.

---

# 18. Database Indexes

An index is a data structure that helps the database find rows more efficiently.

Without an appropriate index:

```text
Database
   ↓
scan many rows
```

With an appropriate index:

```text
Index
   ↓
locate matching rows
   ↓
fetch rows
```

---

# 18.1 Why Do We Need Indexes?

Suppose:

```sql
SELECT *
FROM answers
WHERE question_id = ?;
```

If `question_id` is indexed, MySQL can efficiently locate matching answers.

---

# 18.2 Index Is Not Free

Indexes have costs:

```text
Extra storage
+
Insert overhead
+
Update overhead
+
Delete overhead
```

Therefore:

```text
Don't index everything.
```

Index based on actual query patterns.

---

# 18.3 Foreign Key Indexes

Foreign keys are commonly used in lookups and joins.

Our tables have indexes around relationships such as:

```text
answers.question_id
answers.user_id
comments.question_id
comments.answer_id
comments.user_id
questions.user_id
```

---

# 19. Composite Indexes and Leftmost Prefix

Example:

```sql
INDEX(question_id, tag_id)
```

This is a composite index.

---

# 19.1 Leftmost Prefix Rule

For:

```text
(question_id, tag_id)
```

the index is naturally useful for:

```text
question_id
```

and:

```text
question_id + tag_id
```

But not necessarily for:

```text
tag_id alone
```

because `question_id` is the leftmost column.

---

# 19.2 Example

Index:

```text
(A, B)
```

Good:

```sql
WHERE A = ?
```

Good:

```sql
WHERE A = ?
AND B = ?
```

Not the same as having an index starting with B:

```sql
WHERE B = ?
```

### Remember

```text
Composite index order matters.
```

---

# 20. EXPLAIN

`EXPLAIN` shows how MySQL plans to execute a query.

Example:

```sql
EXPLAIN
SELECT *
FROM answers
WHERE question_id = ?;
```

We used it to verify that MySQL was using the expected index.

---

# 20.1 What Should We Look At?

Important fields include:

```text
type
key
rows
Extra
```

For example:

```text
type = ref
key  = fk_answers_question
```

indicates that MySQL is using the relevant index in that query plan.

---

# 20.2 Why EXPLAIN?

Don't assume:

```text
"I created an index, therefore the query is fast."
```

Instead:

```text
Create index
   ↓
EXPLAIN
   ↓
Check query plan
   ↓
Measure/verify
```

---

# 21. LIKE Search

Our initial search pattern was similar to:

```sql
WHERE LOWER(title) LIKE '%spring%'
```

---

# 21.1 Why Is `%spring%` Expensive?

The leading wildcard means the database cannot generally use a normal B-tree index to jump directly to the beginning of the desired value.

It may need to inspect many candidate values.

---

# 21.2 Prefix Search vs Substring Search

Prefix:

```sql
LIKE 'spring%'
```

is more index-friendly than:

```sql
LIKE '%spring%'
```

because the beginning of the value is known.

---

# 21.3 Search Evolution

Our learning progression:

```text
LIKE
  ↓
FULLTEXT
  ↓
Elasticsearch
```

---

# 22. MySQL FULLTEXT Search

We added:

```sql
CREATE FULLTEXT INDEX idx_questions_fulltext
ON questions(title, content);
```

Search:

```sql
SELECT *
FROM questions
WHERE MATCH(title, content)
AGAINST ('spring');
```

---

# 22.1 Why FULLTEXT?

FULLTEXT is designed for word-based text search.

It is more appropriate than:

```text
LIKE '%word%'
```

for larger natural-language text fields.

---

# 22.2 Relevance

FULLTEXT can return a relevance score.

Conceptually:

```text
Question A -> relevance 5.4
Question B -> relevance 2.1
Question C -> relevance 0.8
```

Then:

```sql
ORDER BY relevance DESC
```

can prioritize more relevant results.

---

# 22.3 FULLTEXT vs Specifications

These solve different problems.

### Specification

Good for:

```text
Optional filters
Tag
Date
User
Status
```

### FULLTEXT

Good for:

```text
Text search
```

They can be combined.

---

# 23. Specifications and Dynamic Filtering

Imagine we need:

```text
search
tag
date
author
status
```

Creating a repository method for every combination would explode:

```text
findByTagAndDate()
findByTagAndAuthor()
findByDateAndStatus()
findByTagAndDateAndAuthor()
...
```

Specifications solve this by composing predicates dynamically.

---

# 23.1 Mental Model

```text
Request filters
      ↓
Build predicates
      ↓
Specification
      ↓
JPA query
      ↓
SQL
```

---

# 23.2 Why Specifications?

They prevent repository-method explosion.

### Remember

```text
Many optional filters
        ↓
Specifications
```

---

# 24. Elasticsearch

Elasticsearch is a search engine based on Apache Lucene.

We use it as a search/read model, not as the source of truth.

Architecture:

```text
MySQL
  |
  | source of truth
  v
Application

Elasticsearch
  |
  | derived search model
  v
Search
```

---

# 24.1 Why Elasticsearch?

Compared with ordinary relational queries, Elasticsearch is designed for:

```text
Full-text search
Relevance
Text analysis
Ranking
Filtering
Large search workloads
```

---

# 24.2 Why Not Replace MySQL?

Because they solve different problems.

```text
MySQL
    ↓
Transactions
Relationships
Source of truth
Strong consistency for business data

Elasticsearch
    ↓
Search
Relevance
Text analysis
Fast search-oriented access
```

---

# 24.3 Elasticsearch Document

Our search document contains fields such as:

```text
id
title
content
userId
username
tags
version
createdAt
updatedAt
```

---

# 24.4 Same UUID as Elasticsearch `_id`

We use:

```java
.id(document.getId().toString())
```

This gives natural idempotency.

Same Question UUID:

```text
same ES document
```

---

# 25. Elasticsearch Internals

# 25.1 Inverted Index

Traditional database lookup:

```text
row -> fields
```

Search engines use an inverted index conceptually like:

```text
word -> documents containing the word
```

Example:

```text
spring -> [Q1, Q5, Q9]
java   -> [Q1, Q2, Q9]
```

This makes text search efficient.

---

# 25.2 Analyzer

An analyzer processes text before indexing/searching.

Conceptually:

```text
"Spring Boot is Great"
        ↓
Tokenization
        ↓
spring
boot
is
great
```

It may also perform normalization such as lowercasing depending on configuration.

---

# 25.3 `text` vs `keyword`

Important:

```text
text
```

is analyzed for full-text search.

```text
keyword
```

is treated as an exact value.

Example:

```text
title -> text

tags -> keyword
id -> keyword
userId -> keyword
```

---

# 25.4 `match`

For analyzed text:

```text
match
```

is generally used for full-text matching.

---

# 25.5 `term`

For exact values:

```text
term
```

is appropriate for keyword fields.

Our tag filter:

```java
.term(t -> t
    .field("tags")
    .value(request.getTag())
)
```

works because tags are mapped as keyword values.

---

# 25.6 `bool`

Boolean queries combine conditions.

Conceptually:

```text
must
filter
should
must_not
```

---

# 25.7 `must` vs `filter`

`must` contributes to matching/scoring.

`filter` is for yes/no filtering and generally does not contribute to relevance scoring.

Example:

```text
must:
  title/content search

filter:
  tag
  date
```

This is a good separation for our search use case.

---

# 25.8 BM25 / Relevance

Elasticsearch uses relevance scoring mechanisms such as BM25 for text queries.

The important mental model:

```text
Text query
   ↓
matching documents
   ↓
relevance score
   ↓
rank results
```

---

# 25.9 Date Filtering

Our search document uses:

```java
Instant createdAt;
Instant updatedAt;
```

`Instant` represents an unambiguous moment on the global timeline.

This is useful in distributed systems.

Our API can accept an application-level date such as:

```text
fromDate
toDate
```

and convert the boundaries using:

```text
Asia/Kolkata
```

before creating the Instant used by Elasticsearch.

### Remember

`LocalDateTime` itself contains no timezone.

`Instant` represents a specific moment.

---

# 26. Search Architecture

Current search progression:

```text
LIKE
 ↓
MySQL FULLTEXT
 ↓
Elasticsearch
```

Our current Elasticsearch architecture:

```text
Question
   |
   v
MySQL
   |
   | event synchronization
   v
Elasticsearch
   |
   v
Search API
```

The database remains authoritative.

---

# 27. Authentication vs Authorization

This distinction is critical.

## Authentication

```text
Who are you?
```

Example:

```text
Login with email + password
```

## Authorization

```text
What are you allowed to do?
```

Example:

```text
Can this user delete this question?
```

---

# 28. Spring Security

Security flow:

```text
Client
  ↓
JWT
  ↓
Security Filter Chain
  ↓
Authentication
  ↓
SecurityContext
  ↓
Authorization
  ↓
Controller / Service
```

---

# 28.1 SecurityContext

Spring Security stores the authenticated principal in:

```java
SecurityContextHolder
```

Our `CurrentUserService` can obtain the current user from the security context.

### Important rule

Do not trust:

```text
userId
```

from a request body for ownership-sensitive operations.

Instead:

```text
JWT
 ↓
SecurityContext
 ↓
Current authenticated user
```

---

# 28.2 UserDetails

Spring Security uses `UserDetails` as a representation of authenticated user information.

Our:

```text
CustomUserDetails
```

contains information such as:

```text
userId
email
password
authorities
```

Our `getUsername()` returns email because email is our login identifier.

---

# 28.3 AuthenticationManager

AuthenticationManager coordinates authentication.

Conceptually:

```text
email + password
       ↓
AuthenticationManager
       ↓
UserDetailsService
       ↓
PasswordEncoder
       ↓
authenticated
```

---

# 28.4 UserDetailsService

Loads the user based on login identity.

Our application uses:

```text
email
```

as login identity.

---

# 29. Password Hashing

Never store:

```text
password = "password123"
```

in plain text.

We use BCrypt.

---

# 29.1 Hashing vs Encryption

Encryption:

```text
plaintext
   ↕
ciphertext
```

can be reversed with the key.

Password hashing:

```text
password
   ↓
hash
```

is designed as a one-way operation.

---

# 29.2 BCrypt

During registration:

```text
password
   ↓
BCrypt
   ↓
stored hash
```

During login:

```text
provided password
        ↓
BCrypt verification
        ↓
matches stored hash?
```

We do not decrypt a password.

---

# 30. JWT

JWT = JSON Web Token.

We use JWT for stateless authentication.

Conceptually:

```text
Login
  ↓
Authenticate credentials
  ↓
Create JWT
  ↓
Client stores token
  ↓
Client sends token on requests
```

---

# 30.1 JWT Structure

A JWT contains:

```text
Header
Payload
Signature
```

Conceptually:

```text
header.payload.signature
```

---

# 30.2 JWT Is Signed, Not Automatically Encrypted

This is very important.

A normal signed JWT allows the receiver to verify:

```text
Was this token modified?
```

It does not automatically hide the payload.

Therefore:

```text
Do not put sensitive secrets into ordinary JWT claims.
```

---

# 30.3 JWT Subject

Our token uses email as the subject/login identity and also carries user identity as needed by our security model.

The important idea is:

```text
JWT identifies the authenticated principal.
```

---

# 30.4 JWT Expiration

Access tokens should expire.

Why?

If a token is stolen, its usefulness is limited by its lifetime.

This is one reason refresh tokens become relevant in a more complete authentication system.

---

# 30.5 JWT Filter

For each protected request:

```text
Authorization header
       ↓
Bearer token extracted
       ↓
JWT validated
       ↓
UserDetails loaded
       ↓
Authentication created
       ↓
SecurityContext populated
```

---

# 30.6 Current User

The client should not send:

```json
{
  "userId": "..."
}
```

to decide who owns the question.

Instead:

```text
JWT
 ↓
SecurityContext
 ↓
CurrentUserService
 ↓
userId
```

---

# 31. Roles, Permissions and Authorities

We use a database-driven authorization model.

```text
User
  ↕
Role
  ↕
Permission
```

For example:

```text
ADMIN
   ↓
QUESTION_DELETE
USER_DELETE
ROLE_UPDATE
```

---

# 31.1 Role

A role groups permissions.

Examples:

```text
USER
ADMIN
MODERATOR
```

---

# 31.2 Permission

A permission represents a concrete action.

Examples:

```text
QUESTION_CREATE
QUESTION_UPDATE
QUESTION_DELETE
ANSWER_CREATE
ANSWER_UPDATE
ANSWER_DELETE
USER_DELETE
ROLE_UPDATE
```

---

# 31.3 Authority

Spring Security ultimately works with authorities.

We use:

```text
ROLE_ADMIN
```

for role authorities.

And:

```text
QUESTION_DELETE
```

for permission authorities.

---

# 31.4 `hasRole` vs `hasAuthority`

Role:

```java
@PreAuthorize("hasRole('ADMIN')")
```

Spring typically handles the `ROLE_` prefix.

Permission:

```java
@PreAuthorize("hasAuthority('QUESTION_DELETE')")
```

No role prefix is required for our permission authority.

### Remember

```text
Role:
hasRole("ADMIN")

Permission:
hasAuthority("QUESTION_DELETE")
```

---

# 31.5 Method Security

We enable:

```java
@EnableMethodSecurity
```

Then use:

```java
@PreAuthorize(...)
```

This allows authorization at method boundaries.

---

# 31.6 401 vs 403

```text
Not authenticated
    ↓
401

Authenticated but not permitted
    ↓
403
```

---

# 32. Ownership Authorization

Permissions alone are sometimes not enough.

Example:

```text
QUESTION_UPDATE
```

may mean a user is allowed to update questions.

But should every user be able to update every question?

Usually:

```text
Permission
+
Ownership
```

are both relevant.

Example:

```text
User owns question
AND
user has QUESTION_UPDATE
```

Then update is allowed.

An administrator may have a broader override policy.

---

# 32.1 Separate Authentication, Permission and Ownership

Think:

```text
Authentication
    ↓
Who?

Permission
    ↓
Can this type of action be performed?

Ownership
    ↓
Can this user perform it on this specific resource?
```

This separation makes authorization easier to reason about.

---

# 33. Events

An event represents something that happened.

Examples:

```text
QuestionCreated
QuestionUpdated
QuestionDeleted
```

The mental model:

```text
Something happened
```

rather than:

```text
Do this exact operation
```

---

# 34. Publisher and Subscriber

Publisher:

```text
Creates/publishes event
```

Subscriber:

```text
Listens for event
Processes event
```

Example:

```text
QuestionService
       |
       v
QuestionCreated
       |
       v
Search Subscriber
       |
       v
Elasticsearch
```

---

# 35. Synchronous vs Asynchronous Processing

Synchronous:

```text
A calls B
A waits for B
```

Asynchronous:

```text
A produces work
A can continue
B processes later
```

Async processing is useful for work that is:

```text
Independent
Slow
Retryable
Not required before response
```

---

# 36. Message Brokers and Kafka

A message broker sits between producers and consumers.

```text
Producer
    ↓
Broker
    ↓
Consumer
```

Kafka is a distributed event streaming platform.

---

# 36.1 Kafka Topic

A topic is a named stream/category.

Example:

```text
question-events
```

Producer:

```text
Question Service
      ↓
question-events
```

Consumer:

```text
question-events
      ↓
Question Consumer
```

---

# 36.2 Producer vs Consumer

```text
Producer
    ↓
sends event

Consumer
    ↓
reads/processes event
```

---

# 36.3 Consumer Groups

Kafka consumer groups allow multiple consumers to share processing work within a group.

The exact distribution depends on topic partitions and consumer-group configuration.

### Remember

```text
Same consumer group
    ↓
work can be distributed

Different consumer groups
    ↓
each group can independently consume the stream
```

---

# 37. Dual-Write Problem

Suppose:

```text
Save MySQL
   +
Publish Kafka
```

and they are separate operations.

Possible result:

```text
MySQL SUCCESS
Kafka FAILURE
```

or:

```text
Kafka SUCCESS
MySQL FAILURE
```

This is the dual-write problem.

---

# 38. Outbox Pattern

The Outbox Pattern solves the reliability problem by storing the event in the same database transaction as the business data.

```text
                SAME TRANSACTION

        +-------------------------+
        |                         |
        v                         v
   Question table          Outbox table
        |                         |
        +------------+------------+
                     |
                   COMMIT
                     |
                     v
                Processor
                     |
                     v
                   Kafka
                     |
                     v
                 Consumer
                     |
                     v
              Elasticsearch
```

---

# 38.1 Outbox Table

Our outbox stores information such as:

```text
id
aggregateType
aggregateId
eventType
payload
status
createdAt
processingAt
processedAt
retryCount
lastError
```

---

# 38.2 Why Not Extend `BaseEntity`?

Outbox has a different lifecycle.

Instead of generic:

```text
createdAt
updatedAt
version
```

it needs processing-specific state:

```text
processingAt
processedAt
retryCount
lastError
status
```

Therefore a separate entity is appropriate.

---

# 38.3 Outbox Status

```text
PENDING
   ↓
PROCESSING
   ↓
PROCESSED
```

On failure:

```text
PROCESSING
   ↓
PENDING
   ↓
retry
```

After maximum retries:

```text
FAILED
```

---

# 38.4 `FOR UPDATE SKIP LOCKED`

This is useful when multiple processors may run concurrently.

Conceptually:

```text
Processor A locks event 1

Processor B:
    skips locked event 1
    claims another event
```

This helps avoid multiple workers claiming the same row.

---

# 38.5 Stuck Event Recovery

Suppose:

```text
Event = PROCESSING
```

and the application crashes.

Without recovery, the event may remain stuck.

Therefore we track:

```text
processingAt
```

and can recover events that have been processing for too long.

---

# 39. Idempotency and Event Ordering

# 39.1 At-Least-Once Processing

Outbox processing commonly provides:

```text
At-least-once
```

not exactly-once.

Example:

```text
Event processed successfully
        ↓
Processor crashes before marking PROCESSED
        ↓
Event processed again
```

Therefore consumers must tolerate duplicates.

---

# 39.2 Idempotency

An operation is idempotent when repeating it produces the same effective final state.

Our Elasticsearch document uses:

```text
Question UUID
```

as the `_id`.

Therefore:

```text
same question event
       ↓
same ES document
```

---

# 39.3 Event Ordering

Suppose:

```text
V1
V2
```

but they arrive:

```text
V2
V1
```

Without protection:

```text
V1 could overwrite V2
```

We therefore use:

```text
Question.version
```

as an ordering signal.

Conceptually:

```text
incoming version > existing
    → apply

incoming version <= existing
    → ignore/handle as stale or duplicate
```

---

# 39.4 Delete Ordering Is More Difficult

A delete event creates an important edge case.

Suppose:

```text
V5 update
V6 delete
V5 update arrives late
```

If the Elasticsearch document has already been deleted, simply deleting the document does not necessarily leave a persistent application-level tombstone that can reject a later stale recreation.

Therefore strict delete ordering may require:

```text
Tombstone
```

or another version-aware deletion strategy.

### Remember

```text
Update ordering is easier than delete ordering.
```

---

# 40. Current Project Architecture

Our current architecture has evolved through several stages.

## Database

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA/Hibernate
    ↓
MySQL
```

## Security

```text
Client
    ↓
JWT
    ↓
Security Filter
    ↓
Authentication
    ↓
SecurityContext
    ↓
Authorization
```

## Search

```text
MySQL
    ↓
Search synchronization
    ↓
Elasticsearch
```

## Event architecture

Current learning stage:

```text
QuestionService
       ↓
MySQL + Outbox
       ↓
OutboxProcessor
       ↓
Elasticsearch
```

Future Kafka architecture:

```text
QuestionService
       ↓
MySQL + Outbox
       ↓
Outbox Publisher
       ↓
Kafka
       ↓
Question Event Consumer
       ↓
Elasticsearch
```

---

# 40.1 Why MySQL Remains the Source of Truth

Question data belongs to the transactional database.

Elasticsearch contains a derived representation optimized for search.

Therefore:

```text
MySQL
  =
authoritative business data

Elasticsearch
  =
search/read projection
```

If Elasticsearch is lost:

```text
Rebuild from MySQL/events
```

is possible.

If MySQL source data is lost:

```text
Elasticsearch should not be treated as the primary business database.
```

---

# 40.2 Core vs Feature Package Rule

Our final package mental model:

```text
feature.question
    Question
    QuestionService
    QuestionController
    QuestionRepository
    QuestionEventPayload

core.outbox
    OutboxEvent
    OutboxEventRepository
    OutboxEventService
    OutboxProcessor
```

Why?

Because:

```text
Question event payload
```

is question-specific.

But:

```text
Outbox
```

is generic infrastructure.

---

# 41. Critical Rules to Remember

This is the most important revision section.

## Rule 1 — `@Transactional` + managed entity

If an existing entity is loaded inside a transaction and is managed:

```java
question.setTitle(...);
```

dirty checking can generate the UPDATE.

You don't need `save()` merely because you changed a field.

---

## Rule 2 — Flush is not commit

```text
flush
    ≠
commit
```

Flush sends pending SQL to the database.

Commit completes the transaction.

---

## Rule 3 — `save()` is not "always required"

New entity:

```text
persist/save
```

Existing managed entity:

```text
modify
↓
dirty checking
```

---

## Rule 4 — Don't use EAGER to hide N+1

Use intentional fetching:

```text
EntityGraph
JOIN FETCH
DTO projection
batch fetching
```

---

## Rule 5 — Don't expose entities from APIs

Use:

```text
Request DTO
Response DTO
Mapper
```

---

## Rule 6 — Don't trust user ID from request body

Use:

```text
SecurityContext
```

to identify the current user.

---

## Rule 7 — Authentication != Authorization

```text
Authentication = Who are you?
Authorization = What can you do?
```

---

## Rule 8 — 401 != 403

```text
401 = not authenticated

403 = authenticated but forbidden
```

---

## Rule 9 — Role != Permission

```text
Role = group of permissions

Permission = specific action
```

---

## Rule 10 — Don't store passwords directly

Use:

```text
BCrypt
```

---

## Rule 11 — JWT is signed, not automatically encrypted

Do not put sensitive secrets in ordinary JWT claims.

---

## Rule 12 — Flyway owns schema changes

If migration already ran:

```text
Don't edit it.
Create a new migration.
```

---

## Rule 13 — `ddl-auto=validate`

In our architecture:

```text
Flyway → schema management

Hibernate → mapping validation
```

---

## Rule 14 — Don't blindly add indexes

Use:

```text
Query
 ↓
Index
 ↓
EXPLAIN
 ↓
Verify
```

---

## Rule 15 — Composite index order matters

For:

```text
(A, B)
```

the leftmost prefix starts with:

```text
A
```

---

## Rule 16 — `%text%` is different from `text%`

```text
LIKE 'spring%'
```

can be more index-friendly.

```text
LIKE '%spring%'
```

has a leading wildcard and is harder for a normal B-tree index to optimize.

---

## Rule 17 — FULLTEXT and Specifications solve different problems

```text
FULLTEXT
    → text search

Specification
    → dynamic filtering
```

---

## Rule 18 — MySQL and Elasticsearch have different jobs

```text
MySQL
    → transactional source of truth

Elasticsearch
    → search/read model
```

---

## Rule 19 — `text` vs `keyword`

```text
text
    → analyzed full-text search

keyword
    → exact-value matching/filtering
```

---

## Rule 20 — `match` vs `term`

```text
match
    → analyzed text

term
    → exact keyword value
```

---

## Rule 21 — `must` vs `filter`

```text
must
    → contributes to matching/scoring

filter
    → restricts documents without being a relevance clause
```

---

## Rule 22 — Same ID helps idempotency

Using:

```text
Question UUID
```

as Elasticsearch `_id` means repeated indexing targets the same document.

---

## Rule 23 — Outbox solves dual-write reliability

```text
Business data
+
Outbox event
    ↓
same DB transaction
```

Then:

```text
Outbox
 ↓
external system
```

---

## Rule 24 — Outbox is usually at-least-once

Therefore:

```text
Consumers must be idempotent.
```

---

## Rule 25 — Version protects ordering

```text
newer version
    >
older version
```

Do not allow stale events to overwrite newer state.

---

## Rule 26 — Delete ordering needs extra thought

Deleting a document removes the obvious current state.

For strict stale-event protection after deletion, consider tombstones/version-aware deletion.

---

## Rule 27 — Core infrastructure should stay generic

```text
core.outbox
```

should not depend on:

```text
feature.question
```

Question-specific event payloads belong in:

```text
feature.question.event
```

---

# 42. Common Mistakes

## Mistake 1

Putting all business logic inside controllers.

### Correct

```text
Controller
    ↓
Service
```

---

## Mistake 2

Calling `save()` after every setter.

### Correct

Understand entity state and dirty checking.

---

## Mistake 3

Making everything EAGER.

### Correct

Use LAZY + intentional fetch plans.

---

## Mistake 4

Using `CascadeType.ALL` everywhere.

### Correct

Understand ownership and lifecycle before cascading.

---

## Mistake 5

Trusting a client-provided `userId`.

### Correct

Get the authenticated user from SecurityContext.

---

## Mistake 6

Using roles for every authorization decision.

### Correct

Use:

```text
Role
+
Permission
+
Ownership
```

where needed.

---

## Mistake 7

Editing an applied Flyway migration.

### Correct

Create a new migration.

---

## Mistake 8

Assuming an index guarantees performance.

### Correct

Use `EXPLAIN` and real query measurements.

---

## Mistake 9

Using `%LIKE%` for serious large-scale text search.

### Correct

Evaluate FULLTEXT or Elasticsearch.

---

## Mistake 10

Treating Elasticsearch as the source of truth.

### Correct

Keep MySQL authoritative for transactional business data.

---

## Mistake 11

Assuming Kafka alone solves database/event consistency.

### Correct

Kafka solves messaging concerns, but MySQL → Kafka atomicity still needs a strategy such as Outbox.

---

## Mistake 12

Assuming Outbox means exactly once.

### Correct

Design for at-least-once processing and idempotent consumers.

---

# 43. Interview Revision

## Java

### What is OOP?

Object-oriented programming organizes software around objects and their behavior/state.

Important concepts:

```text
Encapsulation
Inheritance
Polymorphism
Abstraction
Composition
```

---

## Spring

### What is IoC?

Spring controls object creation/lifecycle instead of application code manually creating dependencies.

### What is DI?

Spring provides required dependencies to objects.

### Why constructor injection?

Because dependencies are explicit, required, and testable.

---

## JPA/Hibernate

### What is JPA?

A Java persistence specification.

### What is Hibernate?

A JPA implementation and ORM framework.

### What is Spring Data JPA?

An abstraction/productivity layer providing repository support over JPA.

---

## Dirty Checking

### What is dirty checking?

Hibernate detects changes made to managed entities and synchronizes them with the database during flush.

### Do we need `save()` after every setter?

No, not for an existing managed entity inside a transaction.

---

## Transactions

### What does `@Transactional` do?

It defines a transactional boundary for database work.

### Does `@Transactional` automatically include Elasticsearch?

No.

---

## Database

### Why indexes?

To make certain queries more efficient.

### Why not index everything?

Indexes consume storage and add write/update/delete overhead.

### What does EXPLAIN do?

Shows the database query execution plan.

---

## Search

### Why is `%spring%` problematic?

A leading wildcard prevents the database from efficiently using a normal B-tree index for direct substring lookup.

### Why FULLTEXT?

For word-oriented text search and relevance.

### Why Elasticsearch?

For advanced, scalable search capabilities and relevance-oriented querying.

---

## Security

### Authentication?

Who are you?

### Authorization?

What can you do?

### 401?

Not authenticated.

### 403?

Authenticated but forbidden.

---

## JWT

### Is JWT encrypted?

Not necessarily. A normal JWT is signed; its payload should not be treated as secret.

---

## Authorization

### Role vs Permission?

```text
Role = collection/group

Permission = specific action
```

---

## Events

### What is an event?

A representation of something that happened.

### Publisher?

Produces/publishes the event.

### Subscriber?

Consumes/reacts to the event.

---

## Kafka

### Producer?

Sends records to Kafka.

### Consumer?

Reads/processes records.

### Topic?

Named stream/category of records.

### Consumer group?

A group of consumers cooperating to process partitions of a topic.

---

## Outbox

### What problem does Outbox solve?

The dual-write consistency problem between a database and an external system/message broker.

### How?

Store:

```text
Business data
+
Event
```

in the same database transaction.

Then process the event asynchronously.

### Does Outbox provide exactly once?

No. Normally design for at-least-once processing.

### What does idempotency solve?

It makes repeated processing safe.

---

# Final Mental Model

When revising the entire project, remember this flow:

```text
                         CLIENT
                           |
                           v
                    REST CONTROLLER
                           |
                           v
                       VALIDATION
                           |
                           v
                        SERVICE
                           |
              +------------+------------+
              |                         |
              v                         v
        SECURITY CONTEXT          BUSINESS LOGIC
                                        |
                                        v
                                  REPOSITORY
                                        |
                                        v
                                   JPA / Hibernate
                                        |
                                        v
                                      MYSQL
                                        |
                                        |
                                  SOURCE OF TRUTH
                                        |
                                        v
                                  OUTBOX EVENT
                                        |
                                        v
                                EVENT PROCESSING
                                        |
                                  +-----+-----+
                                  |           |
                                  v           v
                                Kafka      Current
                                  |       Processor
                                  |           |
                                  v           v
                               Consumer   Elasticsearch
                                  |           |
                                  +-----+-----+
                                        |
                                        v
                                      SEARCH
```

And the most important conceptual chain is:

```text
Java
 ↓
Spring
 ↓
IoC / DI
 ↓
Layered / Feature Architecture
 ↓
JPA
 ↓
Hibernate
 ↓
Transactions
 ↓
Persistence Context
 ↓
Dirty Checking
 ↓
Relationships
 ↓
Fetch Strategies
 ↓
N+1
 ↓
EntityGraph / JOIN FETCH
 ↓
DTOs
 ↓
Validation
 ↓
Exception Handling
 ↓
Flyway
 ↓
Indexes
 ↓
EXPLAIN
 ↓
LIKE
 ↓
FULLTEXT
 ↓
Specifications
 ↓
Elasticsearch
 ↓
Authentication
 ↓
Spring Security
 ↓
BCrypt
 ↓
JWT
 ↓
Roles
 ↓
Permissions
 ↓
Authorities
 ↓
Ownership
 ↓
Events
 ↓
Publisher / Subscriber
 ↓
Async Processing
 ↓
Message Broker
 ↓
Kafka
 ↓
Dual-Write Problem
 ↓
Outbox
 ↓
Idempotency
 ↓
Event Ordering
```

# The Five Questions to Ask for Every New Concept

Whenever a new technology, annotation, pattern, or architecture decision appears, ask:

```text
1. What problem does it solve?

2. Why do we need it?

3. How does it work internally?

4. Where does it fit into our application?

5. What problem would happen if we didn't use it?
```

For revision, don't just memorize the annotation.

Remember the chain:

```text
PROBLEM
   ↓
WHY
   ↓
CONCEPT
   ↓
INTERNAL WORKING
   ↓
OUR PROJECT
   ↓
TRADE-OFF
   ↓
COMMON MISTAKE
```

That is the mental model for understanding production backend engineering rather than only memorizing Spring annotations.