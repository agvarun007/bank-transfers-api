# Banking Transfers API

A secure and robust banking transfers service built with Java 21 and Spring Boot. This application provides REST endpoints for managing accounts and executing money transfers with full concurrency safety and idempotency support.

## Features

- **Account Management**: Create accounts with optional initial balances
- **Money Transfers**: Execute transfers between accounts with validation
- **Concurrency Safety**: Thread-safe operations using per-account locks
- **Idempotency**: Prevent duplicate transfers with idempotency keys
- **Transaction History**: View transaction history for accounts
- **Comprehensive Error Handling**: Structured error responses with appropriate HTTP status codes
- **Validation**: Input validation for all API endpoints

## Prerequisites

- Java 21 or higher
- Maven (or use the included Maven wrapper)

## Quick Start

### 1. Start the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 2. Verify the Application is Running

```bash
curl http://localhost:8080/health
# Expected response: ok
```

## API Endpoints

### Account Management

#### Create Account
- **POST** `/accounts`
- Creates a new account with an optional initial balance
- **Request Body:**
  ```json
  {
    "name": "Alice",
    "initialBalance": "100.00"
  }
  ```
- **Response (201 Created):**
  ```json
  {
    "accountId": "123e4567-e89b-12d3-a456-426614174000",
    "balance": 100.0
  }
  ```

#### Get Account Details
- **GET** `/accounts/{accountId}`
- Retrieves account information including current balance
- **Response (200 OK):**
  ```json
  {
    "accountId": "123e4567-e89b-12d3-a456-426614174000",
    "balance": 100.0
  }
  ```

### Money Transfers

#### Execute Transfer
- **POST** `/transfers`
- Transfers money between two accounts
- **Request Body:**
  ```json
  {
    "fromAccountId": "123e4567-e89b-12d3-a456-426614174000",
    "toAccountId": "987fcdeb-51a2-43d1-9c45-123456789abc",
    "amount": "25.00",
    "idempotencyKey": "optional-unique-key"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "transactionId": "456e7890-e12b-34c5-d678-901234567890",
    "status": "SUCCESS",
    "timestamp": "2025-08-22T15:30:00Z"
  }
  ```

#### Get Transaction History
- **GET** `/accounts/{accountId}/transactions`
- Retrieves transaction history for an account (newest first)
- **Response (200 OK):**
  ```json
  [
    {
      "id": "456e7890-e12b-34c5-d678-901234567890",
      "from": "123e4567-e89b-12d3-a456-426614174000",
      "to": "987fcdeb-51a2-43d1-9c45-123456789abc",
      "amount": 25.0,
      "timestamp": "2025-08-22T15:30:00Z",
      "status": "SUCCESS"
    }
  ]
  ```

### Health Check

#### Application Health
- **GET** `/health`
- Returns application status
- **Response (200 OK):** `ok`

## Error Handling

The API returns structured error responses with appropriate HTTP status codes:

### Error Response Format
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable error description"
}
```

### Common Error Scenarios

| Error Scenario | HTTP Status | Error Code | Example Message |
|----------------|-------------|------------|-----------------|
| Invalid request data | 400 | BAD_REQUEST | "amount must be > 0" |
| Same account transfer | 400 | BAD_REQUEST | "same account" |
| Validation failure | 400 | VALIDATION | "validation failed" |
| Account not found | 404 | NOT_FOUND | "account not found" |
| Insufficient funds | 409 | CONFLICT | "insufficient funds" |

## Idempotency

To prevent duplicate transfers, you can provide an idempotency key in two ways:

1. **Request Body Field:**
   ```json
   {
     "fromAccountId": "...",
     "toAccountId": "...",
     "amount": "25.00",
     "idempotencyKey": "unique-transfer-id-123"
   }
   ```

2. **HTTP Header:**
   ```bash
   curl -X POST http://localhost:8080/transfers 
     -H "Content-Type: application/json" 
     -H "X-Idempotency-Key: unique-transfer-id-123" 
     -d '{"fromAccountId":"...","toAccountId":"...","amount":"25.00"}'
   ```

If a transfer with the same idempotency key is submitted multiple times, the API returns the original transaction without creating a duplicate.

## Example Usage

Here's a complete example showing how to create accounts and transfer money:

```bash
# Create Alice's account
ALICE_RESPONSE=$(curl -s -X POST http://localhost:8080/accounts 
  -H "Content-Type: application/json" 
  -d '{"name":"Alice","initialBalance":"100.00"}')

ALICE_ID=$(echo $ALICE_RESPONSE | jq -r '.accountId')

# Create Bob's account
BOB_RESPONSE=$(curl -s -X POST http://localhost:8080/accounts 
  -H "Content-Type: application/json" 
  -d '{"name":"Bob","initialBalance":"50.00"}')

BOB_ID=$(echo $BOB_RESPONSE | jq -r '.accountId')

# Transfer $25 from Alice to Bob
curl -s -X POST http://localhost:8080/transfers 
  -H "Content-Type: application/json" 
  -d '{"fromAccountId":"$ALICE_ID","toAccountId":"$BOB_ID","amount":"25.00"}' 
  | jq .

# Check Alice's balance (should be $75)
curl -s http://localhost:8080/accounts/$ALICE_ID | jq .

# Check Bob's balance (should be $75)
curl -s http://localhost:8080/accounts/$BOB_ID | jq .

# View Alice's transaction history
curl -s http://localhost:8080/accounts/$ALICE_ID/transactions | jq .
```

## Architecture Overview

### Components

- **Controllers** (`controller/`): REST API endpoints with input validation
- **Services** (`service/`): Business logic and transaction management
- **Repositories** (`repo/`): Data access layer with in-memory implementations
- **Models** (`model/`): Core domain objects (Account, Transaction)
- **DTOs** (`dto/`): Request/response data transfer objects
- **Web** (`web/`): Global error handling and HTTP configuration

### Data Flow

1. **Request Reception**: Controllers receive and validate HTTP requests
2. **Business Logic**: Services process transfers with concurrency controls
3. **Data Persistence**: Repositories manage in-memory data storage
4. **Response Formation**: DTOs structure response data
5. **Error Handling**: Global error handler provides consistent error responses

### Concurrency Safety

The application uses sophisticated locking mechanisms to ensure thread safety:

- **Per-Account Locks**: Each account has its own `ReentrantLock`
- **Deadlock Prevention**: Locks are acquired in consistent UUID order
- **Atomic Operations**: Balance updates and transaction creation are atomic

### Design Decisions

- **BigDecimal for Money**: Ensures precise decimal arithmetic
- **UUID Identifiers**: Provides globally unique account and transaction IDs  
- **Immutable DTOs**: Record classes ensure data integrity
- **Validation**: Jakarta Validation annotations enforce business rules
- **In-Memory Storage**: `ConcurrentHashMap` provides thread-safe operations

## Testing

### Run All Tests
```bash
./mvnw test
```

### Test Structure

- **Unit Tests**: Service layer logic and business rules
  - `AccountServiceTest`: Account creation and management
  - `TransferServiceTest`: Transfer logic, validation, and concurrency
  
- **Integration Tests**: End-to-end API testing
  - `ApiIntegrationTest`: Full REST API workflow

### Test Coverage

The test suite covers:
- Account creation with and without initial balances
- Successful money transfers with balance updates
- Insufficient funds validation
- Same-account transfer prevention
- Idempotency key behavior
- Complete end-to-end API workflows

## Configuration

The application uses minimal configuration in `application.properties`:

- **Application Name**: `banking-transfers-api`
- **Error Handling**: Stack traces disabled in responses
- **Message Inclusion**: Error messages included in responses

## Assumptions and Limitations

- **Single Currency**: All amounts are in the same currency
- **In-Memory Storage**: Data persists only during application runtime
- **No Authentication**: API endpoints are publicly accessible
- **Decimal Precision**: Supports standard monetary precision (2 decimal places)
- **Process Lifetime**: All data is lost when the application stops

## Future Enhancements

- **Database Integration**: Replace in-memory storage with persistent database
- **Multi-Currency Support**: Add currency conversion capabilities
- **Authentication & Authorization**: Implement user authentication
- **Transaction Pagination**: Add pagination for transaction history
- **Audit Logging**: Comprehensive audit trail
- **Rate Limiting**: API rate limiting and throttling
- **Metrics & Monitoring**: Application performance metrics
- **OpenAPI Documentation**: Auto-generated API documentation
- **Async Processing**: Background transaction processing
- **Event Sourcing**: Complete transaction event history
