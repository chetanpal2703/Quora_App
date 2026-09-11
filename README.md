# Quora_App

# Quora App — Java & Spring Boot Learning Notes

This project is a Quora-like application built with **Java 21 + Spring Boot**.

The main purpose of this project is not only to build an application, but to understand how a production-style backend is designed and why different technologies and architectural decisions are used.

---

# Table of Contents

1. [Project Goal](#1-project-goal)
2. [Technology Stack](#2-technology-stack)
3. [Project Architecture](#3-project-architecture)
4. [Java Fundamentals Used](#4-java-fundamentals-used)
5. [Spring Framework Fundamentals](#5-spring-framework-fundamentals)
6. [Spring Boot](#6-spring-boot)
7. [IoC, DI and DIP](#7-ioc-di-and-dip)
8. [Controller → Service → Repository Architecture](#8-controller--service--repository-architecture)
9. [DTOs](#9-dtos)
10. [Entity and JPA](#10-entity-and-jpa)
11. [ORM and Hibernate](#11-orm-and-hibernate)
12. [Entity vs Table](#12-entity-vs-table)
13. [BaseEntity](#13-baseentity)
14. [JPA Auditing](#14-jpa-auditing)
15. [Lombok](#15-lombok)
16. [Database and MySQL](#16-database-and-mysql)
17. [Flyway Database Migrations](#17-flyway-database-migrations)
18. [JPA Relationships](#18-jpa-relationships)
19. [Owning Side and mappedBy](#19-owning-side-and-mappedby)
20. [FetchType and Lazy Loading](#20-fetchtype-and-lazy-loading)
21. [N+1 Problem](#21-n1-problem)
22. [EntityGraph](#22-entitygraph)
23. [JOIN FETCH](#23-join-fetch)
24. [SUBSELECT and Batch Fetching](#24-subselect-and-batch-fetching)
25. [Cascade and orphanRemoval](#25-cascade-and-orphanremoval)
26. [Repositories](#26-repositories)
27. [JPQL vs SQL](#27-jpql-vs-sql)
28. [Validation](#28-validation)
29. [Exception Handling](#29-exception-handling)
30. [Pagination and Sorting](#30-pagination-and-sorting)
31. [Generic PageResponse](#31-generic-pageresponse)
32. [Authentication vs Authorization](#32-authentication-vs-authorization)
33. [Spring Security](#33-spring-security)
34. [Password Hashing with BCrypt](#34-password-hashing-with-bcrypt)
35. [UserDetails and CustomUserDetails](#35-userdetails-and-customuserdetails)
36. [AuthenticationManager](#36-authenticationmanager)
37. [JWT](#37-jwt)
38. [JWT Authentication Flow](#38-jwt-authentication-flow)
39. [SecurityContext](#39-securitycontext)
40. [401 vs 403](#40-401-vs-403)
41. [Roles and Permissions](#41-roles-and-permissions)
42. [RBAC Database Design](#42-rbac-database-design)
43. [Authorities](#43-authorities)
44. [hasRole vs hasAuthority](#44-hasrole-vs-hasauthority)
45. [Method Security](#45-method-security)
46. [Current User](#46-current-user)
47. [Ownership vs Permission](#47-ownership-vs-permission)
48. [Question Feature](#48-question-feature)
49. [Answer Feature](#49-answer-feature)
50. [Comment Feature](#50-comment-feature)
51. [Tag Feature](#51-tag-feature)
52. [Dynamic Filtering](#52-dynamic-filtering)
53. [Spring Data JPA Specifications](#53-spring-data-jpa-specifications)
54. [Why Specifications Are Needed](#54-why-specifications-are-needed)
55. [Combining Specifications](#55-combining-specifications)
56. [Database Indexes](#56-database-indexes)
57. [Composite Indexes](#57-composite-indexes)
58. [Leftmost Prefix Rule](#58-leftmost-prefix-rule)
59. [EXPLAIN](#59-explain)
60. [What Comes Next](#60-what-comes-next)

---

# 1. Project Goal

The application is a simplified version of a platform such as Quora.

Users can:

* Register
* Login
* Ask questions
* Answer questions
* Comment on questions/answers
* Add tags to questions
* Search questions
* Filter questions
* Vote
* Manage their own content
* Have different roles and permissions

The important goal is learning how these features are implemented using production-style backend concepts.

---

# 2. Technology Stack

| Technology      | Purpose                        |
| --------------- | ------------------------------ |
| Java 21         | Programming language           |
| Spring Boot     | Backend application framework  |
| Spring Web      | REST APIs                      |
| Spring Data JPA | Database access                |
| Hibernate       | JPA implementation / ORM       |
| MySQL           | Relational database            |
| Flyway          | Database schema migrations     |
| Spring Security | Authentication & authorization |
| JWT             | Stateless authentication       |
| Lombok          | Reduce boilerplate             |
| Bean Validation | Request validation             |
| Testcontainers  | Integration testing            |

---

# 3. Project Architecture

The project uses a **package-by-feature** structure.

```text
com.example.quora_app

├── feature
│   ├── auth
│   ├── user
│   ├── question
│   ├── answer
│   ├── comment
│   ├── tag
│   └── authorization
│
└── core
    ├── common
    ├── security
    └── config
```

Instead of organizing everything like:

```text
controller/
service/
repository/
entity/
```

we organize primarily around business features.

For example:

```text
feature/question/
    Question.java
    QuestionRepository.java
    QuestionService.java
    QuestionServiceImpl.java
    QuestionController.java
    QuestionMapper.java
    QuestionResponse.java
```

This keeps everything related to a feature together.

---

# 4. Java Fundamentals Used

Some important Java concepts used throughout the project:

## Interface

An interface defines a contract.

```java
public interface QuestionService {
    QuestionResponse createQuestion(...);
}
```

The implementation provides the actual behavior.

```java
@Service
public class QuestionServiceImpl implements QuestionService {
}
```

This allows the rest of the application to depend on the abstraction rather than the implementation.

---

## Collections

We use:

```java
List<T>
Set<T>
Map<K,V>
```

Examples:

```java
List<Question>
Set<Role>
Set<Permission>
```

### List

Allows duplicates and maintains order.

### Set

Does not allow duplicates.

This is useful for:

```java
Set<Role>
Set<Permission>
Set<Tag>
```

because the same role/permission/tag should not normally appear multiple times.

---

## Stream API

Streams allow collection data to be processed declaratively.

Example:

```java
Set<String> permissions = user.getRoles()
        .stream()
        .flatMap(role -> role.getPermissions().stream())
        .map(Permission::getName)
        .collect(Collectors.toSet());
```

Conceptually:

```text
User
 |
 ├── USER
 │    ├── QUESTION_CREATE
 │    └── ANSWER_CREATE
 │
 └── ADMIN
      ├── QUESTION_DELETE
      └── USER_DELETE

flatMap
     ↓

QUESTION_CREATE
ANSWER_CREATE
QUESTION_DELETE
USER_DELETE
```

`flatMap()` is used when each item itself contains another collection/stream and we want one flattened stream.

---

# 5. Spring Framework Fundamentals

Spring is primarily a framework for managing objects and their dependencies.

Instead of manually creating everything:

```java
QuestionRepository repository = new QuestionRepository();
QuestionService service = new QuestionService(repository);
```

Spring creates and manages application objects.

These managed objects are called **beans**.

---

# 6. Spring Boot

Spring Boot is built on top of Spring.

Spring provides the core framework.

Spring Boot makes configuration and application setup easier through:

* Auto-configuration
* Starter dependencies
* Embedded server
* Application configuration
* Production-friendly defaults

For example:

```java
@SpringBootApplication
public class QuoraApplication {
}
```

`@SpringBootApplication` combines important Spring functionality including:

* Configuration
* Component scanning
* Auto-configuration

---

# 7. IoC, DI and DIP

## IoC — Inversion of Control

Normally our code creates objects.

With Spring, the framework controls object creation and lifecycle.

```text
Without Spring:

Application
   ↓
creates objects

With Spring:

Spring Container
   ↓
creates/manages objects
   ↓
injects dependencies
```

---

## DI — Dependency Injection

Dependency Injection means an object's dependencies are provided from outside instead of the object creating them itself.

Example:

```java
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl {

    private final QuestionRepository questionRepository;
}
```

The service needs `QuestionRepository`.

Spring provides it.

---

## DIP — Dependency Inversion Principle

High-level code should depend on abstractions rather than concrete implementations.

Example:

```java
private final QuestionRepository questionRepository;
```

or:

```java
private final QuestionService questionService;
```

rather than manually creating implementation objects.

---

# 8. Controller → Service → Repository Architecture

Our request normally follows:

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Hibernate/JPA
  ↓
MySQL
```

## Controller

Responsible for HTTP.

Examples:

* URL
* HTTP method
* request body
* response status

Example:

```java
@PostMapping
public QuestionResponse createQuestion(
        @Valid @RequestBody QuestionCreateRequest request) {
    return questionService.createQuestion(request);
}
```

---

## Service

Contains business logic.

Example:

```text
Create question
 ↓
get current user
 ↓
validate data
 ↓
resolve tags
 ↓
create Question entity
 ↓
save
 ↓
map to response
```

---

## Repository

Responsible for persistence/database access.

```java
public interface QuestionRepository
        extends JpaRepository<Question, UUID> {
}
```

The repository abstracts database operations.

---

# 9. DTOs

DTO = Data Transfer Object.

We don't expose JPA entities directly through our API.

Instead:

```text
HTTP Request
     ↓
Request DTO
     ↓
Service
     ↓
Entity
     ↓
Database
     ↓
Entity
     ↓
Response DTO
     ↓
HTTP Response
```

Example:

```java
public class QuestionCreateRequest {
    private String title;
    private String content;
    private Set<String> tags;
}
```

Response:

```java
public class QuestionResponse {
    private UUID id;
    private String title;
    private String content;
    private UUID userId;
    private String username;
    private Set<String> tags;
}
```

### Why DTOs?

1. Prevent exposing internal entity structure.
2. Control what the API accepts.
3. Control what the API returns.
4. Allow API and database models to evolve independently.
5. Avoid accidentally exposing fields such as password.
6. Allow request-specific validation.

---

# 10. Entity and JPA

JPA = Java Persistence API.

JPA is a specification for mapping Java objects to relational database tables.

Example:

```java
@Entity
@Table(name = "questions")
public class Question {
}
```

An entity represents persistent data.

---

# 11. ORM and Hibernate

ORM = Object Relational Mapping.

The problem:

Java uses objects:

```java
Question
User
Answer
```

Database uses:

```text
tables
rows
columns
```

ORM maps between them.

```text
Java Object
     ↕
   Hibernate
     ↕
Database Row
```

Hibernate is the ORM implementation used by Spring Data JPA in this project.

---

# 12. Entity vs Table

These are different concepts.

```java
@Entity
@Table(name = "questions")
public class Question
```

`@Entity` tells JPA:

> This Java class is a persistent entity.

`@Table` tells JPA:

> Store it in this database table.

The entity name is mainly relevant to JPQL.

The table name is a physical database concept.

---

# 13. BaseEntity

Common fields should not be repeated in every entity.

We created:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

Entities extend it:

```java
public class User extends BaseEntity
```

```java
public class Question extends BaseEntity
```

```java
public class Answer extends BaseEntity
```

This gives every entity:

```text
id
createdAt
updatedAt
```

---

# 14. JPA Auditing

Instead of manually setting:

```java
createdAt = ...
updatedAt = ...
```

we use:

```java
@CreatedDate
private LocalDateTime createdAt;

@LastModifiedDate
private LocalDateTime updatedAt;
```

and:

```java
@EnableJpaAuditing
```

Spring Data automatically manages these values.

### Why auditing?

It gives consistent lifecycle timestamps across entities.

---

# 15. Lombok

Lombok reduces boilerplate.

Common annotations:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
```

For example:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
}
```

---

## Why not `@Data` on JPA entities?

`@Data` generates:

* getters
* setters
* `toString`
* `equals`
* `hashCode`
* required constructor

For JPA entities, automatically generated `equals()`, `hashCode()` and `toString()` can cause problems with:

* lazy relationships
* bidirectional relationships
* recursive calls
* unintended database access

Therefore explicit Lombok annotations are preferred for entities.

---

# 16. Database and MySQL

The application uses MySQL as the relational database.

Relational databases store information in tables.

Example:

```text
users
questions
answers
roles
permissions
comments
tags
```

Relationships are represented using foreign keys.

Example:

```text
questions.user_id
        ↓
users.id
```

---

# 17. Flyway Database Migrations

Initially Hibernate can create database tables.

But for production-style development, schema changes should be controlled explicitly.

Flyway manages database migrations.

Example:

```text
V1__create_users_table.sql
V2__create_questions_table.sql
V3__create_answers_table.sql
...
```

Flyway executes migrations in order.

```text
V1
 ↓
V2
 ↓
V3
 ↓
V4
 ↓
...
```

After a migration has been applied, it should not normally be edited.

Instead create a new migration.

---

## Hibernate ddl-auto

We use:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means:

> Hibernate validates whether the entity mappings match the existing database schema.

Hibernate does NOT own schema creation.

Flyway owns the schema.

```text
Flyway
   ↓
creates/changes schema

Hibernate
   ↓
validates mapping against schema
```

This separation is much safer for production.

---

# 18. JPA Relationships

Our application contains several relationships.

## User → Questions

One user can create many questions.

```text
User 1 ─────── * Question
```

Java:

```java
@OneToMany(mappedBy = "user")
private List<Question> questions;
```

Question:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

---

## User → Answers

```text
User 1 ─────── * Answer
```

---

## Question → Answers

```text
Question 1 ─────── * Answer
```

---

## User → Roles

A user can have multiple roles.

A role can belong to multiple users.

```text
User * ─────── * Role
```

Implemented using:

```text
user_roles
```

---

## Role → Permissions

A role can contain multiple permissions.

A permission can belong to multiple roles.

```text
Role * ─────── * Permission
```

Implemented using:

```text
role_permissions
```

---

## Question → Tags

A question can have multiple tags.

A tag can be reused by multiple questions.

```text
Question * ─────── * Tag
```

Implemented using:

```text
question_tags
```

---

# 19. Owning Side and mappedBy

In a bidirectional relationship, one side owns the database relationship.

Example:

```java
Question:

@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

This is the owning side because `questions` contains the foreign key:

```text
questions.user_id
```

User:

```java
@OneToMany(mappedBy = "user")
private List<Question> questions;
```

`mappedBy = "user"` means:

> The `user` field inside Question owns this relationship.

Important:

`mappedBy` refers to the **Java field name**, not the database column.

---

# 20. FetchType and Lazy Loading

FetchType answers:

> When should related data be loaded?

Two important values:

```java
FetchType.LAZY
FetchType.EAGER
```

### LAZY

Load relationship only when needed.

```java
@ManyToOne(fetch = FetchType.LAZY)
private User user;
```

### EAGER

Load relationship immediately.

We generally prefer LAZY for relationships because loading everything automatically can create unnecessary database queries.

Important:

LAZY does NOT mean:

> Never load this relationship.

It means:

> Don't load it until it is actually accessed.

---

# 21. N+1 Problem

Suppose we fetch 10 questions.

```sql
SELECT * FROM questions;
```

Then while converting each question to response we access:

```java
question.getUser().getUsername();
```

Hibernate may execute:

```text
1 query → questions

+ 10 queries → users

= 11 queries
```

This is the **N+1 query problem**.

```text
1 parent query
+
N child queries
=
N+1
```

This can become very expensive.

---

# 22. EntityGraph

`@EntityGraph` allows us to specify relationships that should be fetched for a particular repository operation.

Example:

```java
@EntityGraph(attributePaths = "user")
Page<Question> findAll(Pageable pageable);
```

Normally:

```text
Question
   |
   └── user = LAZY
```

With EntityGraph:

```text
Question
   |
   └── user fetched for this query
```

The important advantage is that the entity can remain globally LAZY.

We don't need to change:

```java
@ManyToOne(fetch = FetchType.EAGER)
```

just because one query needs the user.

---

# 23. JOIN FETCH

JPQL can explicitly fetch relationships:

```java
@Query("""
    SELECT q
    FROM Question q
    JOIN FETCH q.user
""")
```

`JOIN FETCH` means:

> Fetch the relationship as part of this query.

This can solve N+1 problems.

However, fetching collections together with pagination can create duplicate rows and inefficient queries, so it must be used carefully.

---

# 24. SUBSELECT and Batch Fetching

These are techniques for reducing excessive relationship queries.

## SUBSELECT

For example:

```text
Load 20 questions
       ↓
access answers
       ↓
Hibernate can load answers for those parents
using one additional query
```

Instead of:

```text
1 question query
+
20 answer queries
```

we may get:

```text
1 question query
+
1 answer query
```

---

## Batch Fetching

Instead of loading relationships one-by-one:

```text
User 1
User 2
User 3
...
```

Hibernate can load several IDs together.

This reduces the number of database round trips.

---

# 25. Cascade and orphanRemoval

## Cascade

Cascade controls whether operations on one entity propagate to related entities.

Example:

```java
CascadeType.PERSIST
CascadeType.REMOVE
CascadeType.ALL
```

We should not blindly use:

```java
CascadeType.ALL
```

especially for shared entities.

Example:

```text
Question
   ↓
Tag
```

Tags are reusable.

Deleting a question should NOT delete the Tag itself.

Therefore tags are explicitly resolved/persisted instead of using `CascadeType.ALL`.

---

## orphanRemoval

`orphanRemoval = true` means a child removed from the parent's relationship can be deleted from the database.

This is useful when the child truly belongs exclusively to the parent.

It should not be confused with general cascading.

---

# 26. Repositories

Spring Data JPA provides repository implementations automatically.

Example:

```java
public interface UserRepository
        extends JpaRepository<User, UUID> {
}
```

We get operations such as:

```java
save()
findById()
findAll()
delete()
existsById()
```

We don't need to implement these ourselves.

Also, we don't normally need:

```java
@Repository
```

on Spring Data repository interfaces because Spring Data creates the repository bean automatically.

---

# 27. JPQL vs SQL

JPQL works with:

* Entity names
* Java field names
* Entity relationships

Example:

```java
SELECT q
FROM Question q
WHERE q.title LIKE :title
```

It does NOT use physical table names in the same way SQL does.

SQL:

```sql
SELECT *
FROM questions
WHERE title LIKE ?
```

JPQL:

```text
Question
q.title
q.user
```

SQL:

```text
questions
title
user_id
```

Hibernate converts JPQL into SQL.

---

# 28. Validation

Validation protects our API from invalid input.

Example:

```java
@NotBlank
private String name;
```

```java
@Size(min = 3, max = 50)
private String username;
```

```java
@Email
private String email;
```

Controller:

```java
public ResponseEntity<?> register(
        @Valid @RequestBody UserRegistrationRequest request) {
}
```

Flow:

```text
HTTP request
     ↓
@Valid
     ↓
Bean Validation
     ↓
valid?
  ↙     ↘
yes      no
 ↓        ↓
service   validation error
```

Validation belongs primarily at the API boundary.

Business rules still belong in the service layer.

---

# 29. Exception Handling

Instead of writing:

```java
try {
   ...
} catch (...) {
   ...
}
```

inside every controller, we use centralized exception handling.

Example custom exceptions:

```text
ResourceNotFoundException
UnauthorizedException
ForbiddenException
```

A global exception handler converts them into appropriate HTTP responses.

Example:

```text
ResourceNotFoundException
        ↓
HTTP 404

UnauthorizedException
        ↓
HTTP 401

ForbiddenException
        ↓
HTTP 403
```

This keeps controllers clean and provides consistent API errors.

---

# 30. Pagination and Sorting

Returning thousands of database rows at once is inefficient.

Instead:

```text
page = 0
size = 10
```

means:

> Give me the first 10 results.

Example:

```java
Pageable pageable =
        PageRequest.of(page, size, sort);
```

API:

```text
GET /api/v1/questions?page=0&size=10
```

Sorting:

```text
sortBy=createdAt
sortDir=desc
```

---

# 31. Generic PageResponse

Spring returns a `Page<T>`.

Instead of exposing Spring's entire Page structure, we created our own response:

```java
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
```

This gives a consistent API response across:

```text
Questions
Answers
Comments
Users
...
```

---

## Generic PageMapper

Instead of writing the same pagination conversion repeatedly:

```java
@Component
public class PageMapper {

    public <T, R> PageResponse<R> toPageResponse(
            Page<T> page,
            Function<T, R> mapper) {

        return PageResponse.<R>builder()
                .content(
                    page.getContent()
                        .stream()
                        .map(mapper)
                        .toList()
                )
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
```

This demonstrates the use of Java generics and functional interfaces.

---

# 32. Authentication vs Authorization

These are different concepts.

## Authentication

Answers:

> Who are you?

Example:

```text
Email + Password
        ↓
Authentication
        ↓
User identified
```

---

## Authorization

Answers:

> What are you allowed to do?

Example:

```text
User
 ↓
QUESTION_CREATE
QUESTION_UPDATE
ANSWER_CREATE
```

Authentication comes before authorization.

```text
Authentication
      ↓
Who are you?
      ↓
Authorization
      ↓
What can you do?
```

---

# 33. Spring Security

Spring Security handles:

* Authentication
* Authorization
* Security context
* Password encoding
* Security filters
* Method-level authorization

Our API is configured so:

```text
/api/v1/auth/**
```

is public.

Everything else requires authentication.

---

# 34. Password Hashing with BCrypt

Passwords should NEVER be stored as plain text.

Bad:

```text
password = "mypassword123"
```

Instead:

```java
PasswordEncoder encoder =
        new BCryptPasswordEncoder();
```

During registration:

```java
String encoded =
        passwordEncoder.encode(request.getPassword());
```

Database stores:

```text
$2a$...
```

During login, Spring Security uses:

```java
passwordEncoder.matches(
    rawPassword,
    encodedPassword
)
```

BCrypt is a password hashing algorithm designed to make password cracking expensive.

---

# 35. UserDetails and CustomUserDetails

Spring Security needs a representation of the authenticated user.

Spring provides:

```java
UserDetails
```

We created:

```java
CustomUserDetails implements UserDetails
```

It contains:

```text
userId
email
password
authorities
```

Important:

```java
@Override
public String getUsername() {
    return email;
}
```

In our application:

```text
name     = display name
username = public handle
email    = login identifier
```

So Spring Security's `getUsername()` returns email.

---

# 36. AuthenticationManager

Login flow:

```text
email + password
       ↓
AuthenticationManager
       ↓
UserDetailsService
       ↓
load user by email
       ↓
PasswordEncoder
       ↓
compare password
       ↓
authentication successful
```

Spring Security performs the actual authentication process.

We don't need to manually compare passwords inside our controller.

---

# 37. JWT

JWT = JSON Web Token.

It allows us to represent authenticated information in a signed token.

Example:

```text
Authorization: Bearer <token>
```

JWT consists conceptually of:

```text
Header.Payload.Signature
```

Important:

> JWT is signed, not encrypted.

Therefore the payload should not contain secrets.

Never put:

```text
password
secret keys
```

inside a JWT.

---

# 38. JWT Authentication Flow

Login:

```text
Client
  ↓
POST /auth/login
  ↓
AuthenticationManager
  ↓
UserDetailsService
  ↓
Password verification
  ↓
Authentication successful
  ↓
JwtService
  ↓
JWT generated
  ↓
Client
```

Subsequent request:

```text
Client
  ↓
Authorization: Bearer JWT
  ↓
JwtAuthenticationFilter
  ↓
extract token
  ↓
validate signature/expiration
  ↓
load user
  ↓
create Authentication
  ↓
SecurityContext
  ↓
Controller
```

---

# 39. SecurityContext

Spring Security stores the authenticated user in:

```java
SecurityContextHolder
```

Conceptually:

```text
Request
  ↓
JWT Filter
  ↓
Authentication
  ↓
SecurityContext
  ↓
available to application
```

This allows us to know who is making the request without trusting a user ID sent in the request body.

---

# 40. 401 vs 403

These are very important.

## 401 Unauthorized

Means:

> You are not authenticated.

Examples:

```text
No token
Invalid token
Expired token
```

---

## 403 Forbidden

Means:

> You are authenticated, but you are not allowed to perform this action.

Example:

```text
User is logged in
       ↓
tries ADMIN-only operation
       ↓
403 Forbidden
```

Matrix:

| Situation                             | Result |
| ------------------------------------- | ------ |
| No token                              | 401    |
| Invalid token                         | 401    |
| Expired token                         | 401    |
| Valid token + insufficient permission | 403    |
| Valid token + authorized              | 2xx    |

---

# 41. Roles and Permissions

We deliberately did NOT use:

```java
private Role role;
```

inside User.

Instead we use a database-backed RBAC model.

RBAC = Role-Based Access Control.

Roles group permissions.

Example:

```text
USER
 ├── QUESTION_CREATE
 ├── QUESTION_UPDATE
 ├── ANSWER_CREATE
 └── COMMENT_CREATE

ADMIN
 ├── USER_DELETE
 ├── QUESTION_DELETE
 ├── ROLE_UPDATE
 └── ...
```

A user can have multiple roles.

---

# 42. RBAC Database Design

We have:

```text
users
roles
permissions

user_roles
role_permissions
```

Relationships:

```text
User * ─────── * Role
              |
              |
              ↓
       Role * ─────── * Permission
```

`user_roles`:

```text
user_id
role_id
```

`role_permissions`:

```text
role_id
permission_id
```

This design allows permissions to be changed without changing Java code.

---

# 43. Authorities

Spring Security ultimately works with:

```java
GrantedAuthority
```

Our user can have authorities such as:

```text
ROLE_USER
ROLE_ADMIN

QUESTION_CREATE
QUESTION_UPDATE
QUESTION_DELETE

ANSWER_CREATE

USER_DELETE
ROLE_UPDATE
```

We construct these from the user's roles and permissions.

Example:

```java
authorities.add(
    new SimpleGrantedAuthority(
        "ROLE_" + role.getName()
    )
);
```

And:

```java
authorities.add(
    new SimpleGrantedAuthority(
        permission.getName()
    )
);
```

---

# 44. hasRole vs hasAuthority

## Role

```java
@PreAuthorize("hasRole('ADMIN')")
```

Spring checks:

```text
ROLE_ADMIN
```

Spring automatically applies the `ROLE_` prefix for `hasRole`.

---

## Permission

```java
@PreAuthorize("hasAuthority('QUESTION_DELETE')")
```

This checks the exact authority:

```text
QUESTION_DELETE
```

Therefore:

```text
hasRole("ADMIN")
       ↓
ROLE_ADMIN

hasAuthority("QUESTION_DELETE")
       ↓
QUESTION_DELETE
```

---

# 45. Method Security

To use:

```java
@PreAuthorize(...)
```

we enable method security:

```java
@EnableMethodSecurity
```

Example:

```java
@PreAuthorize("hasAuthority('QUESTION_DELETE')")
public void deleteQuestion(UUID questionId) {
}
```

This gives fine-grained authorization.

Roles provide broad access.

Permissions provide specific capabilities.

---

# 46. Current User

We should NOT trust:

```json
{
    "userId": "..."
}
```

from the client when determining who performs an operation.

Instead:

```text
JWT
 ↓
Authentication
 ↓
SecurityContext
 ↓
CustomUserDetails
 ↓
userId
```

We created:

```java
CurrentUserService
```

which retrieves the current user's ID from the SecurityContext.

Example:

```java
UUID userId =
        currentUserService.getCurrentUserId();
```

This is much safer.

---

# 47. Ownership vs Permission

These are two different authorization concepts.

### Permission

Answers:

> Is this type of operation allowed?

Example:

```text
QUESTION_UPDATE
```

### Ownership

Answers:

> Does this particular question belong to this user?

Example:

```text
User A
  ↓
Question 123
```

User B may have:

```text
QUESTION_UPDATE
```

but that does not automatically mean User B can edit User A's question.

Therefore authorization can require:

```text
Permission
   +
Ownership
```

An ADMIN may later be given an override.

---

# 48. Question Feature

Question contains:

```text
id
title
content
user
answers
tags
createdAt
updatedAt
```

Relationship:

```text
User
 ↓
Questions
 ↓
Answers
```

Question creation does not accept `userId`.

The user comes from:

```text
SecurityContext
```

This prevents a client from pretending to be another user.

---

# 49. Answer Feature

Answer contains:

```text
id
content
user
question
createdAt
updatedAt
```

Relationship:

```text
Question
   ↓
Answers
```

Creating an answer:

```text
Authenticated User
        ↓
CurrentUserService
        ↓
Question exists?
        ↓
Create Answer
        ↓
Save
```

Nested endpoint:

```text
POST /api/v1/questions/{questionId}/answers
```

Listing:

```text
GET /api/v1/questions/{questionId}/answers
```

The question ID is therefore part of the URL rather than being unnecessarily duplicated in the request body.

---

# 50. Comment Feature

A comment can belong to either:

```text
Question
```

or:

```text
Answer
```

Model:

```text
Comment
 ├── user
 ├── question (optional)
 └── answer   (optional)
```

Database constraint should enforce:

```text
exactly one target
```

Meaning:

```text
question_id != null
AND
answer_id = null
```

OR:

```text
question_id = null
AND
answer_id != null
```

but not both and not neither.

Endpoints:

```text
POST /api/v1/questions/{questionId}/comments

POST /api/v1/answers/{answerId}/comments

GET /api/v1/questions/{questionId}/comments

GET /api/v1/answers/{answerId}/comments

PATCH /api/v1/comments/{commentId}

DELETE /api/v1/comments/{commentId}
```

---

# 51. Tag Feature

Tags are reusable.

Example:

```text
java
spring
spring-boot
hibernate
mysql
```

A question can contain many tags.

```text
Question * ───── * Tag
```

Database:

```text
tags
question_tags
```

We deliberately don't use `CascadeType.ALL` because tags are shared entities.

---

## Tag Resolution

Request:

```json
{
    "title": "What is Spring Boot?",
    "content": "...",
    "tags": ["Java", "Spring", "java"]
}
```

We:

1. Trim names.
2. Ignore blank values.
3. Normalize case.
4. Find existing tag.
5. Create tag if it doesn't exist.
6. Store the relationship.

Conceptually:

```text
" Java "
   ↓
"java"
   ↓
find existing
   ↓
use existing Tag
```

This prevents unnecessary duplicate tags.

---

# 52. Dynamic Filtering

Initially, we can write repository methods such as:

```java
findByTitleContainingIgnoreCase(...)
```

But as requirements grow, query methods become difficult to maintain.

Imagine we need:

```text
search
tag
author
date range
status
category
...
```

A repository could become:

```text
findByTitle...
findByTitleAndTag...
findByTitleAndTagAndAuthor...
findByTitleAndTagAndAuthorAndDate...
...
```

This does not scale well.

This is the problem that **Specifications** solve.

---

# 53. Spring Data JPA Specifications

A Specification represents a database predicate that can be composed with other predicates.

Repository:

```java
public interface QuestionRepository
        extends JpaRepository<Question, UUID>,
                JpaSpecificationExecutor<Question> {
}
```

A specification can represent:

```text
title/content contains search
```

or:

```text
question has tag
```

---

# 54. Why Specifications Are Needed

Suppose our API supports:

```text
GET /api/v1/questions?search=spring
```

Then:

```text
search = spring
```

But maybe the client also sends:

```text
tag = java
```

Now we need:

```text
search
AND
tag
```

Later:

```text
search
AND
tag
AND
author
AND
date range
```

Specifications allow us to build these conditions dynamically.

Instead of creating many repository methods, we create small reusable specifications.

---

# 55. Specifications Example

Search specification:

```java
public static Specification<Question> containsSearch(
        String search) {

    return (root, query, criteriaBuilder) -> {

        if (search == null || search.isBlank()) {
            return criteriaBuilder.conjunction();
        }

        String pattern =
                "%" + search.trim().toLowerCase() + "%";

        return criteriaBuilder.or(
                criteriaBuilder.like(
                    criteriaBuilder.lower(
                        root.get("title")
                    ),
                    pattern
                ),
                criteriaBuilder.like(
                    criteriaBuilder.lower(
                        root.get("content")
                    ),
                    pattern
                )
        );
    };
}
```

This represents:

```sql
WHERE
LOWER(title) LIKE '%spring%'
OR
LOWER(content) LIKE '%spring%'
```

---

## Tag Specification

```java
public static Specification<Question> hasTag(
        String tagName) {

    return (root, query, criteriaBuilder) -> {

        if (tagName == null || tagName.isBlank()) {
            return criteriaBuilder.conjunction();
        }

        query.distinct(true);

        return criteriaBuilder.equal(
                criteriaBuilder.lower(
                    root.join("tags").get("name")
                ),
                tagName.trim().toLowerCase()
        );
    };
}
```

This joins:

```text
Question
   ↓
tags
   ↓
Tag.name
```

and checks the tag name.

`distinct(true)` helps avoid duplicate Question results caused by joins.

---

# 56. Combining Specifications

Specifications can be combined.

```java
Specification<Question> specification =
        Specification.where(
            QuestionSpecification.containsSearch(search)
        ).and(
            QuestionSpecification.hasTag(tag)
        );
```

Conceptually:

```text
containsSearch(search)
        AND
hasTag(tag)
```

Then:

```java
Page<Question> questions =
        questionRepository.findAll(
            specification,
            pageable
        );
```

This gives us:

```text
GET /api/v1/questions
```

```text
GET /api/v1/questions?search=spring
```

```text
GET /api/v1/questions?tag=java
```

```text
GET /api/v1/questions?search=spring&tag=java
```

without creating separate repository methods for every combination.

---

# 57. Specification Mental Model

Think of a Specification as a reusable piece of:

```text
WHERE condition
```

For example:

```text
Specification A
    ↓
title/content contains "spring"

Specification B
    ↓
tag = "java"

Specification C
    ↓
author = "chetan"
```

Then compose:

```text
A AND B AND C
```

or:

```text
A OR B
```

This becomes especially useful when filters are optional.

---

# 58. Database Indexes

An index is a database data structure that helps the database find rows faster.

Without an appropriate index, the database may need to inspect many rows.

Imagine:

```text
1,000,000 questions
```

Query:

```sql
WHERE user_id = ?
```

Without an index, MySQL may scan many rows.

With an index:

```text
user_id index
```

MySQL can find matching rows much more efficiently.

---

# 59. Index Trade-off

Indexes improve reads but have costs.

They require:

* Disk space
* Memory/cache
* Maintenance during INSERT
* Maintenance during UPDATE
* Maintenance during DELETE

Therefore:

> Don't create an index on every column.

Indexes should be created based on actual query patterns.

---

# 60. Primary Keys and Unique Indexes

A primary key normally has an index.

Example:

```sql
PRIMARY KEY (id)
```

Unique constraints also create/use unique indexes.

Example:

```java
@Column(unique = true)
private String email;
```

The database creates a unique constraint/index for it.

This means duplicate emails are prevented at the database level.

---

# 61. Foreign-Key Indexes

Foreign keys are frequently used for lookups and joins.

Examples:

```text
questions.user_id
answers.user_id
answers.question_id
comments.user_id
comments.question_id
comments.answer_id
```

These columns often become good candidates for indexes.

Example:

```sql
CREATE INDEX idx_answers_question_id
ON answers(question_id);
```

This helps:

```sql
SELECT *
FROM answers
WHERE question_id = ?;
```

---

# 62. Composite Indexes

Sometimes queries filter using multiple columns.

Example:

```sql
SELECT *
FROM question_votes
WHERE user_id = ?
AND question_id = ?;
```

Instead of two separate indexes, a composite index may be useful:

```sql
CREATE INDEX idx_question_votes_user_question
ON question_votes(user_id, question_id);
```

The order matters.

```text
(user_id, question_id)
```

is different from:

```text
(question_id, user_id)
```

The best order depends on the query patterns.

---

# 63. Leftmost Prefix Rule

For:

```text
INDEX(user_id, question_id)
```

the database can efficiently use the index for queries involving the leftmost columns.

Good:

```sql
WHERE user_id = ?
```

Good:

```sql
WHERE user_id = ?
AND question_id = ?
```

Potentially not useful in the same way:

```sql
WHERE question_id = ?
```

because `question_id` is not the leftmost column.

Therefore:

```text
INDEX(A, B)
```

primarily supports:

```text
A
A + B
```

not simply:

```text
B
```

---

# 64. Index Selectivity

A useful index usually has good selectivity.

Selectivity means roughly:

> How effectively does a column narrow down the result set?

Example:

```text
gender
```

may have very few distinct values.

An index on it may not be very useful for some queries.

But:

```text
email
```

usually has many distinct values.

Therefore it is highly selective and commonly useful as an index.

Unique columns are often highly selective.

---

# 65. EXPLAIN

Creating indexes is not enough.

We need to understand whether MySQL actually uses them.

We use:

```sql
EXPLAIN
```

Example:

```sql
EXPLAIN
SELECT *
FROM answers
WHERE question_id = 'some-id';
```

It helps us understand the query execution plan.

Important information includes:

```text
possible_keys
key
rows
type
Extra
```

### possible_keys

Indexes MySQL could potentially use.

### key

The index MySQL actually selected.

### rows

Estimated number of rows MySQL expects to examine.

### type

Provides information about the access strategy.

### Extra

Provides additional execution details.

The goal is not:

> Every query must use an index.

The goal is:

> The database should have an efficient execution plan for important queries.

---

# 66. Indexing and Our Quora Application

We should look at actual queries before adding indexes.

Examples:

### Answers by question

```sql
SELECT *
FROM answers
WHERE question_id = ?;
```

Candidate:

```text
answers(question_id)
```

---

### Questions by user

```sql
SELECT *
FROM questions
WHERE user_id = ?;
```

Candidate:

```text
questions(user_id)
```

---

### Vote lookup

```sql
SELECT *
FROM question_votes
WHERE user_id = ?
AND question_id = ?;
```

Candidate:

```text
question_votes(user_id, question_id)
```

The important principle is:

```text
Application query
      ↓
Analyze query pattern
      ↓
Choose index
      ↓
EXPLAIN
      ↓
Verify execution plan
```

---

# 67. LIKE Search and Its Limitation

Our current search uses something similar to:

```sql
WHERE LOWER(title) LIKE '%spring%'
```

This is convenient but has limitations.

Especially:

```text
%spring%
```

starts with a wildcard.

A normal B-tree index generally cannot efficiently use the index to jump directly to arbitrary substrings in the same way it can for prefix searches.

For a large question table, this type of search can become expensive.

This leads to our next database/search topic:

```text
LIKE
  ↓
FULLTEXT
  ↓
Elasticsearch/OpenSearch
```

---

# 68. Overall Architecture Learned So Far

Our current application can be visualized as:

```text
                         CLIENT
                           │
                           ▼
                    REST CONTROLLER
                           │
                           ▼
                      VALIDATION
                           │
                           ▼
                       SERVICE
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
       CURRENT USER                BUSINESS LOGIC
              │                         │
              ▼                         ▼
      SECURITY CONTEXT             REPOSITORY
                                        │
                                        ▼
                                  SPRING DATA JPA
                                        │
                                        ▼
                                    HIBERNATE
                                        │
                                        ▼
                                      MYSQL
```

Security sits across the request:

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
  ↓
Controller/Service
```

Database schema is managed separately:

```text
Flyway
  ↓
MySQL Schema

Hibernate
  ↓
validates mappings
```

---

# 69. Complete Request Flow

Example:

```text
POST /api/v1/questions
Authorization: Bearer <JWT>
```

Flow:

```text
1. HTTP request
       ↓
2. Spring Security filter chain
       ↓
3. JWT extracted
       ↓
4. JWT signature/expiration validated
       ↓
5. User loaded
       ↓
6. CustomUserDetails created
       ↓
7. Authorities created
       ↓
8. Authentication stored in SecurityContext
       ↓
9. Authorization checked
       ↓
10. Controller receives request
       ↓
11. @Valid validates DTO
       ↓
12. Service executes business logic
       ↓
13. CurrentUserService identifies user
       ↓
14. Question entity created
       ↓
15. Tags resolved
       ↓
16. Repository saves entity
       ↓
17. Hibernate generates SQL
       ↓
18. MySQL executes SQL
       ↓
19. Entity mapped to DTO
       ↓
20. Response returned
```

This is the complete picture we have been building toward.

---

# 70. Main Design Principles Learned

## 1. Don't put business logic in controllers

Controller should handle HTTP.

Business logic belongs in services.

---

## 2. Don't expose entities directly

Use DTOs.

---

## 3. Don't trust client-provided user IDs

Use the authenticated user from SecurityContext.

---

## 4. Don't store passwords as plain text

Use BCrypt.

---

## 5. Don't confuse authentication and authorization

Authentication:

```text
Who are you?
```

Authorization:

```text
What can you do?
```

---

## 6. Don't use only roles for every authorization decision

Use:

```text
Roles
+
Permissions
+
Ownership
```

where appropriate.

---

## 7. Don't make every relationship EAGER

Prefer LAZY and fetch relationships intentionally.

---

## 8. Don't blindly use CascadeType.ALL

Especially for shared entities such as Tags.

---

## 9. Don't create a repository method for every filter combination

Use Specifications for dynamic filtering.

---

## 10. Don't blindly create indexes

First understand the query.

Then:

```text
Index
 ↓
EXPLAIN
 ↓
Verify
```

---

# 71. Current Project Learning Progress

The project has progressed roughly like this:

```text
Java Fundamentals
       ↓
Spring
       ↓
Spring Boot
       ↓
IoC / DI / DIP
       ↓
Controller-Service-Repository
       ↓
DTOs
       ↓
JPA
       ↓
Hibernate / ORM
       ↓
Entities
       ↓
Relationships
       ↓
Lazy Loading
       ↓
N+1
       ↓
EntityGraph / JOIN FETCH
       ↓
Auditing
       ↓
Flyway
       ↓
Validation
       ↓
Exception Handling
       ↓
Pagination
       ↓
Authentication
       ↓
Spring Security
       ↓
BCrypt
       ↓
UserDetails
       ↓
AuthenticationManager
       ↓
JWT
       ↓
SecurityContext
       ↓
RBAC
       ↓
Roles
       ↓
Permissions
       ↓
Authorities
       ↓
Method Security
       ↓
Ownership
       ↓
Questions
       ↓
Answers
       ↓
Comments
       ↓
Tags
       ↓
Dynamic Filtering
       ↓
Specifications
       ↓
Database Indexes
       ↓
Composite Indexes
       ↓
EXPLAIN
```

---

# 72. What Comes Next

The next database/search concepts to learn are:

```text
Database Indexes
        ↓
Composite Indexes
        ↓
EXPLAIN
        ↓
Query Optimization
        ↓
LIKE limitations
        ↓
MySQL FULLTEXT
        ↓
MATCH ... AGAINST
        ↓
Search relevance
        ↓
Elasticsearch / OpenSearch
        ↓
Database + Search Engine architecture
        ↓
Keeping DB and Search Index synchronized
```

After that, production-level improvements can include:

```text
Refresh Tokens
       ↓
JWT Revocation
       ↓
Advanced Authorization
       ↓
Object-level Authorization
       ↓
DTO Projections
       ↓
Batch Fetching
       ↓
Query Optimization
       ↓
Testing
       ↓
Caching
       ↓
Rate Limiting
       ↓
API Documentation
       ↓
Logging / Monitoring
       ↓
Production Security
       ↓
Deployment
```

---

# 73. The Most Important Mental Model

When learning a new backend concept, always ask these five questions:

```text
1. What problem does it solve?

2. Why do we need it?

3. How does it work internally?

4. Where does it fit into our application?

5. What problem would happen if we didn't use it?
```

For example, Specifications:

```text
Problem:
Many optional filters.

Without it:
Too many repository methods.

Solution:
Specification.

How:
Build reusable predicates.

Our application:
Question search + tag filtering.

Result:
Dynamic queries without repository-method explosion.
```

This same approach should be used for every new technology or annotation added to this project.
