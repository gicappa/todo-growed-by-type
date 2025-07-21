# Requirement Implementation

## Creates a New Board

To create a new board we need to do a rest call that accept a json body and returns a 

- POST /boards
  - **Name**: Non-empty string, maximum 50 characters
  - **Capacity**: Positive integer between 1 and 100
  - **Current Item Count**: Always ≤ capacity
  - **Created Date**: Automatically set to current timestamp

### JSON Body
Example:
```json
{
  "name": "My Todo Board",
  "capacity": 10
}
```

### Sample Response

HTTP 201 Created with Location Header + Resource Body (Recommended)
```
HTTP/1.1 201 Created
Location: /boards/123e4567-e89b-12d3-a456-426614174000
Content-Length: 0
```