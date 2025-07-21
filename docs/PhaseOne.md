# TodoBoard Application Requirements - Phase 1 (MVP)

## 1. Core Entity Requirements

### 1.1 TodoItem
- **Title**: Non-empty string, maximum 100 characters
- **Description**: Optional text, maximum 500 characters
- **Priority**: One of {Low, Medium, High, Urgent}
- **Completed**: Boolean flag (true/false)
- **Created Date**: Automatically set to current timestamp
- **Completed Date**: Only present when item is completed

### 1.2 TodoBoard
- **Name**: Non-empty string, maximum 50 characters
- **Capacity**: Positive integer between 1 and 100
- **Current Item Count**: Always ≤ capacity
- **Created Date**: Automatically set to current timestamp

## 2. Business Rules and Constraints

### 2.1 Capacity Management
- Cannot add items when board is at maximum capacity
- Board must have at least 1 item capacity
- Maximum capacity is 100 items for MVP

### 2.2 Item Validation
- Title cannot be empty or whitespace only
- Created date cannot be in the future
- Completed date must be after created date when present
- Completed items must have a completion timestamp

### 2.3 Board Validation
- Board name cannot be empty or whitespace only
- Board can be deleted only when empty
- Item count must never exceed capacity

## 3. Data Persistence

### 3.1 File-based Storage
- Store boards as JSON files in `data/boards/` directory
- One file per board: `{board-id}.json`
- Atomic file operations for data integrity
- Simple backup: copy entire data directory

### 3.2 Data Format
```json
{
  "id": "board-uuid",
  "name": "My Todo Board",
  "capacity": 10,
  "createdAt": "2025-07-21T20:30:35Z",
  "items": [
    {
      "id": "item-uuid",
      "title": "Complete MVP",
      "description": "Build basic TodoBoard functionality",
      "priority": "High",
      "completed": false,
      "createdAt": "2025-07-21T20:30:35Z",
      "completedAt": null
    }
  ]
}
```
## 4. API Requirements

### 4.1 REST Endpoints (Basic)

- GET /boards - List all boards
- POST /boards - Create new board
- GET /boards/{id} - Get board with items
- DELETE /boards/{id} - Delete board (only if empty)
- POST /boards/{id}/items - Add item to board
- PUT /items/{id} - Update item
- DELETE /items/{id} - Delete item
- POST /items/{id}/complete - Mark item as completed

### 4.2 Error Responses
- 400 Bad Request - Invalid input data
- 404 Not Found - Board or item not found
- 409 Conflict - Capacity exceeded or constraint violation
- 500 Internal Server Error - System error

## 5. User Interface Requirements

### 5.1 Board View

- Simple list view of items grouped by priority
- Add new item form at the top
- Mark items as complete with checkbox
- Delete items with confirmation dialog
- Show item counts: "5/10 items"

### 5.2 Board Management
- Create new board form
- List of existing boards
- Delete empty boards
- Basic board statistics (total items, completed items)

## 6. Implementation Constraints

### 6.1 Type Safety (Compile-time)

- Impossible to violate capacity constraints
- Invalid item states prevented at compile time
- All string inputs validated for length and content

### 6.2 Error Handling

- All external input validated
- Clear error messages for constraint violations
- Graceful handling of file system errors

## 7. Testing Requirements

### 7.1 Property-Based Testing

- Board capacity never exceeded
- Completed items always have completion timestamps
- Created dates never in the future
- Board item count matches actual items

### 7.2 Unit Testing

- CRUD operations for boards and items
- Validation logic for all constraints
- File persistence and retrieval
- Error handling scenarios

## 8. Performance Requirements

### 8.1 MVP Targets

- Board loading: < 500ms for up to 50 items
- Item operations: < 200ms
- File operations: < 100ms
- Support up to 20 boards per instance
- 
## 9. Success Criteria

### 9.1 Functional

- ✓ Create and manage multiple todo boards
- ✓ Add, edit, complete, and delete todo items
- ✓ Respect capacity constraints
- ✓ Persist data between application restarts
- ✓ Basic web interface for all operations
- 
### 9.2 Technical
 
- ✓ Type-safe domain model
- ✓ Comprehensive test coverage (>90%)
- ✓ Clean separation of concerns
- ✓ File-based persistence working reliably