# Growing Software Guided By Types
*A Journey from Runtime Validation to Compile-Time Guarantees*

## Table of Contents

1. [Introduction: Learning Through Pain](#introduction)
2. [The Evolution of Software Design](#evolution)
3. [From Tests to Types: A New Approach](#from-tests-to-types)
4. [Building a TODO Application: A Practical Journey](#practical-journey)
5. [Domain-Driven Design and Type Safety](#ddd-types)
6. [Advanced Patterns and Techniques](#advanced-patterns)

---

## Introduction: Learning Through Pain {#introduction}

Software development is a discipline learned through experience, often painful experience. I've come to believe that while there are many paths to learning, the most effective—though not always the most pleasant—is through encountering problems, feeling the pain they cause, and adapting our approach accordingly.

The challenge with learning through pain is that it primarily teaches us what to avoid, not necessarily what we should do instead. Good practices emerge through a kind of Darwinian process: error after error eliminates the bad approaches until only the viable paths remain.

This book distills thirty years of writing software—sometimes bad, sometimes better, but always eventually working. It represents a synthesis of hard-won lessons, successful patterns, and the evolution of my thinking about how to design robust, maintainable systems.

### The Genesis of This Approach

My thinking has been profoundly shaped by "Growing Object-Oriented Software, Guided by Tests" (GOOS), along with concepts from Domain-Driven Design (DDD) and various industry practices. These influences helped me develop a way to design software that effectively separates business logic from concrete implementation details—whether that's user interface concerns, database interactions, or inter-service communication.

However, I began to notice a pattern: while this approach works well for incremental changes, significant new features often required substantial refactoring. What should have been evolutionary changes sometimes felt more like rewrites. The design, despite following best practices, seemed brittle when faced with certain types of change.

### The Limitation of Runtime Invariants

The root of this brittleness, I realized, lay in how we establish and maintain invariants in our systems. Traditional test-driven development creates invariants through runtime validation—we write tests that verify our data structures contain valid values and that our methods behave correctly for specific inputs.

But runtime testing has a fundamental limitation: it can only prove correctness for the cases we test. It cannot guarantee that our invariants hold for all possible inputs or states. A test might pass for the scenarios we've considered, but fail spectacularly for edge cases we haven't imagined.

### The Promise of Type-Driven Design

What if we could move these invariants from runtime to compile time? What if, instead of testing that values are set correctly, we could make it impossible to set them incorrectly in the first place?

This is the central thesis of this book: by leveraging Java's static type system, we can encode our business rules and invariants directly into the structure of our code. The compiler becomes our first line of defense against invalid states, and refactoring becomes safer because compilation errors guide us toward correct implementations.

---

## Chapter 1: The Evolution of Software Design {#evolution}

### From Chaos to Structure

In the early days of programming, code organization was often an afterthought. Functions were written to solve immediate problems, and structure emerged organically—if at all. As systems grew larger and more complex, the need for better organization became apparent.

Object-oriented programming promised to bring order to this chaos by organizing code around data and the operations that manipulate it. Design patterns provided vocabulary for common solutions. Test-driven development offered a methodology for ensuring correctness while driving design.

### The GOOS Revolution

"Growing Object-Oriented Software, Guided by Tests" represented a significant evolution in thinking about software design. The book introduced several key concepts that fundamentally changed how many developers approach building systems:

**The Walking Skeleton**: Start with end-to-end infrastructure before writing business logic. This ensures that your deployment pipeline, monitoring, and basic architecture are in place from day one.

**Mock Objects You Own**: Only mock interfaces under your control. For external systems, use real integrations in your tests, even if they're more complex to set up.

**Outside-In Development**: Start with acceptance tests that describe the desired behavior from the user's perspective, then work inward, discovering the objects and interfaces needed to fulfill those requirements.

These principles led to systems with clear separation of concerns, well-defined interfaces, and comprehensive test coverage. But they also revealed some limitations.

### The Refactoring Challenge

Despite following GOOS principles religiously, I found that certain types of changes remained difficult. Adding new features that cut across existing boundaries often required extensive refactoring. The test suite, which was supposed to provide safety during refactoring, sometimes became an obstacle to change.

The issue wasn't with the tests themselves, but with what they were testing. By verifying behavior through runtime assertions, we were encoding assumptions about the system's structure into our test suite. When those assumptions needed to change, both the implementation and the tests required updates.

### Toward Compile-Time Guarantees

The insight that led to this book was recognizing that many of the properties we verify through tests could be verified by the type system instead. If we can make invalid states unrepresentable in our type system, we eliminate entire classes of bugs without writing a single test.

Consider a simple example: instead of testing that a user's email address is valid, what if we had an `EmailAddress` type that could only be constructed with valid email strings? The compiler would prevent us from accidentally using an invalid email anywhere in our system.

---

## Chapter 2: From Tests to Types {#from-tests-to-types}

### Understanding Invariants

An invariant is a condition that must always be true during the execution of a program. In traditional object-oriented design, we establish invariants through:

1. **Constructor validation**: Checking that objects are created in valid states
2. **Method preconditions**: Verifying inputs before processing
3. **Method postconditions**: Ensuring outputs meet expectations
4. **Class invariants**: Maintaining consistency throughout an object's lifetime

Tests verify these invariants by exercising the code with various inputs and checking that the expected conditions hold. This approach works, but has several limitations:

**Incomplete Coverage**: Tests can only verify invariants for the specific cases we test. Edge cases and unexpected input combinations might violate invariants without being detected.

**Runtime Failures**: Invariant violations are discovered when the code runs, potentially in production.

**Maintenance Burden**: As requirements change, both the implementation and the tests need updates to maintain invariant checking.

### Java's Type System as Invariant Enforcer

Java's static type system can encode many invariants directly, making violations impossible rather than merely detectable. Let's explore how:

**Value Objects with Validation**:
```java
// Instead of runtime validation everywhere
public class User {
    private final String email;
    private final int age;
    
    public User(String email, int age) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        this.email = email;
        this.age = age;
    }
}

// Use value objects to make invalid states unrepresentable
public final class EmailAddress {
    private final String value;
    
    private EmailAddress(String value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static Optional<EmailAddress> of(String input) {
        return isValidEmail(input) 
            ? Optional.of(new EmailAddress(input))
            : Optional.empty();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmailAddress other && 
               Objects.equals(value, other.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

public final class Age {
    private final int value;
    
    private Age(int value) {
        this.value = value;
    }
    
    public static Optional<Age> of(int input) {
        return input >= 0 
            ? Optional.of(new Age(input))
            : Optional.empty();
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Age other && value == other.value;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}

public final class User {
    private final EmailAddress email;
    private final Age age;
    
    public User(EmailAddress email, Age age) {
        this.email = Objects.requireNonNull(email);
        this.age = Objects.requireNonNull(age);
    }
    
    // No validation needed - impossible to construct with invalid values
    public EmailAddress getEmail() { return email; }
    public Age getAge() { return age; }
}
```

**State Machines with Sealed Classes (Java 17+)**:
```java
// Model valid state transitions in the type system
public sealed interface OrderState 
    permits PendingOrder, ConfirmedOrder, ShippedOrder, DeliveredOrder {
}

public record PendingOrder(
    List<Item> items
) implements OrderState {}

public record ConfirmedOrder(
    List<Item> items,
    PaymentMethod paymentMethod
) implements OrderState {}

public record ShippedOrder(
    List<Item> items,
    PaymentMethod paymentMethod,
    String trackingNumber
) implements OrderState {}

public record DeliveredOrder(
    List<Item> items,
    PaymentMethod paymentMethod,
    String trackingNumber,
    Instant deliveredAt
) implements OrderState {}

// Operations that maintain type safety
public class OrderService {
    public ConfirmedOrder confirmOrder(PendingOrder order, PaymentMethod paymentMethod) {
        return new ConfirmedOrder(order.items(), paymentMethod);
    }
    
    public ShippedOrder shipOrder(ConfirmedOrder order, String trackingNumber) {
        return new ShippedOrder(
            order.items(), 
            order.paymentMethod(), 
            trackingNumber
        );
    }
    
    // Compiler prevents invalid transitions like shipping a pending order
}
```

### The Benefits of Type-Driven Design

**Compile-Time Error Detection**: Invalid operations are caught by the compiler, not discovered at runtime.

**Refactoring Safety**: When you change a type definition, the compiler identifies all locations that need updates.

**Self-Documenting Code**: The type system serves as executable documentation of your domain model.

**Reduced Test Burden**: You don't need to test invariants that are enforced by the type system.

**Enhanced IDE Support**: Better autocomplete, refactoring tools, and error detection.

---

## Chapter 3: Building a TODO Application - A Practical Journey {#practical-journey}

To demonstrate these principles in action, let's build a TODO list application from scratch, evolving it requirement by requirement while maintaining type safety.

### Starting with the Walking Skeleton

Following GOOS principles, we begin with infrastructure before business logic:

**Project Structure**:
```
todo-app/
├── src/
│   ├── main/java/com/example/todo/
│   │   ├── domain/           # Core business logic
│   │   ├── infrastructure/   # External system adapters
│   │   ├── application/      # Use cases and orchestration
│   │   └── presentation/     # REST controllers, CLI, etc.
│   └── test/java/
├── docker-compose.yml        # Local development environment
├── Dockerfile
├── pom.xml                   # Maven configuration
└── deploy/                   # Deployment configurations
```

**Basic Value Objects**:
```java
// src/main/java/com/example/todo/domain/TodoId.java
package com.example.todo.domain;

import java.util.Objects;
import java.util.UUID;

public final class TodoId {
    private final String value;
    
    private TodoId(String value) {
        this.value = Objects.requireNonNull(value, "TodoId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("TodoId cannot be empty");
        }
    }
    
    public static TodoId generate() {
        return new TodoId(UUID.randomUUID().toString());
    }
    
    public static TodoId of(String value) {
        return new TodoId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TodoId other && Objects.equals(value, other.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "TodoId{" + value + "}";
    }
}
```

```java
// src/main/java/com/example/todo/domain/BoardId.java
package com.example.todo.domain;

import java.util.Objects;
import java.util.UUID;

public final class BoardId {
    private final String value;
    
    private BoardId(String value) {
        this.value = Objects.requireNonNull(value, "BoardId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("BoardId cannot be empty");
        }
    }
    
    public static BoardId generate() {
        return new BoardId(UUID.randomUUID().toString());
    }
    
    public static BoardId of(String value) {
        return new BoardId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BoardId other && Objects.equals(value, other.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "BoardId{" + value + "}";
    }
}
```

**Core Domain Types**:
```java
// src/main/java/com/example/todo/domain/TodoItem.java
package com.example.todo.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class TodoItem {
    private final TodoId id;
    private final String title;
    private final String description;
    private final TodoStatus status;
    private final Instant createdAt;
    private final Optional<Instant> completedAt;
    
    public TodoItem(
        TodoId id,
        String title,
        String description,
        TodoStatus status,
        Instant createdAt,
        Optional<Instant> completedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.completedAt = Objects.requireNonNull(completedAt);
        
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
    }
    
    public static TodoItem createNew(String title, String description) {
        return new TodoItem(
            TodoId.generate(),
            title,
            description,
            TodoStatus.PENDING,
            Instant.now(),
            Optional.empty()
        );
    }
    
    public TodoItem complete() {
        if (status == TodoStatus.COMPLETED) {
            return this; // Already completed
        }
        
        return new TodoItem(
            id,
            title,
            description,
            TodoStatus.COMPLETED,
            createdAt,
            Optional.of(Instant.now())
        );
    }
    
    // Getters
    public TodoId getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TodoStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Optional<Instant> getCompletedAt() { return completedAt; }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TodoItem other &&
               Objects.equals(id, other.id) &&
               Objects.equals(title, other.title) &&
               Objects.equals(description, other.description) &&
               Objects.equals(status, other.status) &&
               Objects.equals(createdAt, other.createdAt) &&
               Objects.equals(completedAt, other.completedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, createdAt, completedAt);
    }
}
```

```java
// src/main/java/com/example/todo/domain/TodoStatus.java
package com.example.todo.domain;

public enum TodoStatus {
    PENDING,
    COMPLETED
}
```

```java
// src/main/java/com/example/todo/domain/TodoBoard.java
package com.example.todo.domain;

import java.time.Instant;
import java.util.*;

public final class TodoBoard {
    private final BoardId id;
    private final String name;
    private final Map<TodoId, TodoItem> items;
    private final Instant createdAt;
    
    private TodoBoard(
        BoardId id,
        String name,
        Map<TodoId, TodoItem> items,
        Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.items = new HashMap<>(Objects.requireNonNull(items));
        this.createdAt = Objects.requireNonNull(createdAt);
    }
    
    public static TodoBoard create(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        
        return new TodoBoard(
            BoardId.generate(),
            name,
            new HashMap<>(),
            Instant.now()
        );
    }
    
    public TodoBoard addItem(String title, String description) {
        TodoItem newItem = TodoItem.createNew(title, description);
        Map<TodoId, TodoItem> newItems = new HashMap<>(this.items);
        newItems.put(newItem.getId(), newItem);
        
        return new TodoBoard(id, name, newItems, createdAt);
    }
    
    public TodoBoard completeItem(TodoId todoId) {
        TodoItem item = items.get(todoId);
        if (item == null) {
            throw new IllegalArgumentException("Todo item not found: " + todoId);
        }
        
        TodoItem completedItem = item.complete();
        Map<TodoId, TodoItem> newItems = new HashMap<>(this.items);
        newItems.put(todoId, completedItem);
        
        return new TodoBoard(id, name, newItems, createdAt);
    }
    
    public Optional<TodoItem> findItem(TodoId todoId) {
        return Optional.ofNullable(items.get(todoId));
    }
    
    // Getters that return immutable views
    public BoardId getId() { return id; }
    public String getName() { return name; }
    public List<TodoItem> getItems() { 
        return List.copyOf(items.values()); 
    }
    public Instant getCreatedAt() { return createdAt; }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof TodoBoard other &&
               Objects.equals(id, other.id) &&
               Objects.equals(name, other.name) &&
               Objects.equals(items, other.items) &&
               Objects.equals(createdAt, other.createdAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, items, createdAt);
    }
}
```

### Evolution Through Requirements

Let's trace how our type-driven approach handles evolving requirements:

**Requirement 1: Priority Levels**
```java
// src/main/java/com/example/todo/domain/Priority.java
package com.example.todo.domain;

public enum Priority {
    LOW(1),
    MEDIUM(2), 
    HIGH(3),
    URGENT(4);
    
    private final int level;
    
    Priority(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
    
    public boolean isHigherThan(Priority other) {
        return this.level > other.level;
    }
}
```

```java
// Updated TodoItem with priority
public final class TodoItem {
    private final TodoId id;
    private final String title;
    private final String description;
    private final TodoStatus status;
    private final Priority priority;  // New field
    private final Instant createdAt;
    private final Optional<Instant> completedAt;
    
    // Constructor and methods updated...
    public static TodoItem createNew(String title, String description, Priority priority) {
        return new TodoItem(
            TodoId.generate(),
            title,
            description,
            TodoStatus.PENDING,
            priority,
            Instant.now(),
            Optional.empty()
        );
    }
    
    // The compiler will flag all places where TodoItem.createNew is called
    // without the priority parameter, guiding our refactoring
}
```

**Requirement 2: Due Dates with Business Rules**
```java
// src/main/java/com/example/todo/domain/DueDate.java
package com.example.todo.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class DueDate {
    private final Instant value;
    
    private DueDate(Instant value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static Optional<DueDate> of(Instant date) {
        if (date.isAfter(Instant.now())) {
            return Optional.of(new DueDate(date));
        }
        return Optional.empty(); // Due dates must be in the future
    }
    
    public Instant getValue() {
        return value;
    }
    
    public boolean isOverdue() {
        return value.isBefore(Instant.now());
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DueDate other && Objects.equals(value, other.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### Handling Complex State Transitions with Sealed Classes

As requirements become more complex, we can model valid state transitions using sealed classes:

```java
// src/main/java/com/example/todo/domain/TodoState.java
package com.example.todo.domain;

import java.time.Instant;
import java.util.Optional;

public sealed interface TodoState 
    permits PendingTodo, InProgressTodo, CompletedTodo, CancelledTodo {
    
    TodoId getTodoId();
}

public record PendingTodo(
    TodoId todoId,
    Optional<UserId> assignedTo
) implements TodoState {
    
    @Override
    public TodoId getTodoId() {
        return todoId;
    }
}

public record InProgressTodo(
    TodoId todoId,
    UserId assignedTo,
    Instant startedAt
) implements TodoState {
    
    @Override
    public TodoId getTodoId() {
        return todoId;
    }
}

public record CompletedTodo(
    TodoId todoId,
    UserId assignedTo,
    Instant completedAt
) implements TodoState {
    
    @Override
    public TodoId getTodoId() {
        return todoId;
    }
}

public record CancelledTodo(
    TodoId todoId,
    String reason,
    Instant cancelledAt
) implements TodoState {
    
    @Override
    public TodoId getTodoId() {
        return todoId;
    }
}
```

```java
// Operations that maintain valid state transitions
public class TodoStateService {
    
    public InProgressTodo startTodo(PendingTodo pending, UserId assignedTo) {
        return new InProgressTodo(
            pending.getTodoId(),
            assignedTo,
            Instant.now()
        );
    }
    
    public CompletedTodo completeTodo(InProgressTodo inProgress) {
        return new CompletedTodo(
            inProgress.getTodoId(),
            inProgress.assignedTo(),
            Instant.now()
        );
    }
    
    public CancelledTodo cancelTodo(TodoState state, String reason) {
        return new CancelledTodo(
            state.getTodoId(),
            reason,
            Instant.now()
        );
    }
    
    // Compiler prevents invalid transitions like completing a pending todo
    // without going through in-progress state
}
```

This approach ensures that invalid state transitions become impossible to express in our code, making our domain model much more robust.

---

## Chapter 4: Domain-Driven Design and Type Safety {#ddd-types}

### Bounded Contexts and Package Boundaries

Domain-Driven Design emphasizes the importance of bounded contexts—areas where a particular ubiquitous language holds true and consistent. Java packages provide an excellent mechanism for enforcing these boundaries.

**Separate Packages for Different Contexts**:
```java
// User Management Context
package com.example.usermanagement.domain;

public final class UserId {
    private final String value;
    // Implementation...
}

public final class User {
    private final UserId id;
    private final EmailAddress email;
    private final HashedPassword password;
    private final Set<Role> roles;
    // Implementation...
}
```

```java
// TODO Management Context  
package com.example.todo.domain;

public final class UserId {
    private final String value;
    // Same structure but different package - different meaning
}

public final class User {
    private final UserId id;
    private final String displayName;
    // Note: no password or email - different concerns
}
```

```java
// Translation between contexts
package com.example.todo.infrastructure;

import com.example.usermanagement.domain.User as UserMgmtUser;
import com.example.todo.domain.User as TodoUser;

public class UserTranslationService {
    public TodoUser translateUser(UserMgmtUser userMgmtUser) {
        return new TodoUser(
            TodoUserId.of(userMgmtUser.getId().getValue()),
            extractDisplayName(userMgmtUser.getEmail())
        );
    }
}
```

### Aggregate Roots and Invariants

DDD aggregates become natural units for type-based invariant enforcement:

```java
package com.example.todo.domain;

import java.time.Instant;
import java.util.*;

public final class TodoBoard {
    private final BoardId id;
    private final String name;
    private final Map<TodoId, TodoItem> items;
    private final Instant createdAt;
    private final int maxItems;
    
    private TodoBoard(
        BoardId id,
        String name,
        Map<TodoId, TodoItem> items,
        Instant createdAt,
        int maxItems
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.items = new HashMap<>(Objects.requireNonNull(items));
        this.createdAt = Objects.requireNonNull(createdAt);
        this.maxItems = maxItems;
        
        // Invariant: board cannot exceed max items
        if (items.size() > maxItems) {
            throw new IllegalStateException(
                "Board cannot contain more than " + maxItems + " items"
            );
        }
    }
    
    // Factory method ensures valid construction
    public static TodoBoard create(String name, int maxItems) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        if (maxItems <= 0) {
            throw new IllegalArgumentException("Max items must be positive");
        }
        
        return new TodoBoard(
            BoardId.generate(),
            name,
            new HashMap<>(),
            Instant.now(),
            maxItems
        );
    }
    
    // Type-safe operations that maintain invariants
    public Result<TodoBoard, String> addItem(
        String title, 
        String description, 
        Priority priority
    ) {
        if (items.size() >= maxItems) {
            return Result.failure("Board is at maximum capacity");
        }
        
        TodoItem newItem = TodoItem.createNew(title, description, priority);
        Map<TodoId, TodoItem> newItems = new HashMap<>(this.items);
        newItems.put(newItem.getId(), newItem);
        
        return Result.success(new TodoBoard(id, name, newItems, createdAt, maxItems));
    }
    
    public Result<TodoBoard, String> removeItem(TodoId todoId) {
        if (!items.containsKey(todoId)) {
            return Result.failure("Todo item not found");
        }
        
        Map<TodoId, TodoItem> newItems = new HashMap<>(this.items);
        newItems.remove(todoId);
        
        return Result.success(new TodoBoard(id, name, newItems, createdAt, maxItems));
    }
    
    // Prevent external mutation
    public List<TodoItem> getItems() {
        return List.copyOf(items.values());
    }
    
    public BoardId getId() { return id; }
    public String getName() { return name; }
    public Instant getCreatedAt() { return createdAt; }
    public int getMaxItems() { return maxItems; }
    public int getCurrentItemCount() { return items.size(); }
}
```

**Result Type for Error Handling**:
```java
package com.example.todo.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public sealed interface Result<T, E> permits Success, Failure {
    
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }
    
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }
    
    boolean isSuccess();
    boolean isFailure();
    
    Optional<T> getValue();
    Optional<E> getError();
    
    <U> Result<U, E> map(Function<T, U> mapper);
    <F> Result<T, F> mapError(Function<E, F> mapper);
    <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper);
}

record Success<T, E>(T value) implements Result<T, E> {
    public Success {
        Objects.requireNonNull(value);
    }
    
    @Override
    public boolean isSuccess() { return true; }
    
    @Override
    public boolean isFailure() { return false; }
    
    @Override
    public Optional<T> getValue() { return Optional.of(value); }
    
    @Override
    public Optional<E> getError() { return Optional.empty(); }
    
    @Override
    public <U> Result<U, E> map(Function<T, U> mapper) {
        return Result.success(mapper.apply(value));
    }
    
    @Override
    public <F> Result<T, F> mapError(Function<E, F> mapper) {
        return Result.success(value);
    }
    
    @Override
    public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
        return mapper.apply(value);
    }
}

record Failure<T, E>(E error) implements Result<T, E> {
    public Failure {
        Objects.requireNonNull(error);
    }
    
    @Override
    public boolean isSuccess() { return false; }
    
    @Override
    public boolean isFailure() { return true; }
    
    @Override
    public Optional<T> getValue() { return Optional.empty(); }
    
    @Override
    public Optional<E> getError() { return Optional.of(error); }
    
    @Override
    public <U> Result<U, E> map(Function<T, U> mapper) {
        return Result.failure(error);
    }
    
    @Override
    public <F> Result<T, F> mapError(Function<E, F> mapper) {
        return Result.failure(mapper.apply(error));
    }
    
    @Override
    public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
        return Result.failure(error);
    }
}
```

### The Repository Pattern with Types

Repositories can leverage types to ensure data consistency:

```java
package com.example.todo.domain;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TodoBoardRepository {
    CompletableFuture<Result<Void, String>> save(TodoBoard board);
    CompletableFuture<Optional<TodoBoard>> findById(BoardId id);
    CompletableFuture<List<TodoBoard>> findByUserId(UserId userId);
    CompletableFuture<Result<Void, String>> delete(BoardId id);
}
```

```java
// Implementation enforces type contracts
package com.example.todo.infrastructure.persistence;

import com.example.todo.domain.*;
import java.util.concurrent.CompletableFuture;

public class JpaTodoBoardRepository implements TodoBoardRepository {
    
    private final EntityManager entityManager;
    private final BoardEntityMapper mapper;
    
    public JpaTodoBoardRepository(EntityManager entityManager, BoardEntityMapper mapper) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.mapper = Objects.requireNonNull(mapper);
    }
    
    @Override
    public CompletableFuture<Result<Void, String>> save(TodoBoard board) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // The type system ensures we're working with a valid TodoBoard
                BoardEntity entity = mapper.toEntity(board);
                entityManager.persist(entity);
                return Result.success(null);
            } catch (Exception e) {
                return Result.failure("Failed to save board: " + e.getMessage());
            }
        });
    }
    
    @Override
    public CompletableFuture<Optional<TodoBoard>> findById(BoardId id) {
        return CompletableFuture.supplyAsync(() -> {
            BoardEntity entity = entityManager.find(BoardEntity.class, id.getValue());
            return entity != null 
                ? Optional.of(mapper.toDomain(entity))
                : Optional.empty();
        });
    }
}
```

This approach continues with application services, use cases, and presentation layers, all leveraging Java's type system to maintain consistency and catch errors at compile time rather than runtime.

---

*[The book would continue with remaining chapters covering advanced patterns, integration with external systems, performance considerations, and real-world case studies...]*