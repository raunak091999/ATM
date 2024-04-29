# Atm Implementation

# Objective

## Problem Statement

- Write compiling code for "Cash Withdrawal" function, from an ATM which based on user specified amount,
  dispenses bank-notes.

Ensure that the code takes care of the following:

- Minimum number of bank-notes are used while dispensing the amount
- Availability of various denominations in the ATM machine is maintained
- Code should be flexible to take care of any bank denomination as long as it is multiple of 10
- Code should support parallel Cash Withdrawals i.e. two or more customer's should be able to withdraw money
- Takes care of exceptional situations
- Write appropriate unit test cases too
- State any assumptions made
- Write the compiling code using choice of your IDE (Eclipse, IntelliJ)
- Provide Unit Test Cases using JUNIT (if you are not conversant with JUNIT, just list down unit test cases)

## NFRs

- Duration of this exercise is 90 minutes. Please manage your time accordingly,
- Make any necessary assumption, and clearly state the assumptions made.
- Do not seek any help online or through any other source.

## Evaluation Criteria

- **Code Completeness/ Correctness**
- **Code Structure**: Modularity, usage of 00Ps principles and design patterns, size of classes, and functions, n-path
  complexity of functions.
- **Code Quality**: class/function/variable naming conventions, package/class structure, log messages - please do not
  provide comments unless necessary
- **Choice of data structures**
- **Efficiency of code** (leverage multi-threading wherever it makes sense)
- **Code test Cases and follow TDD** (if know)

# Solution

## Implementation

1. Code starts with `AtmService` class, which sets the contract for the functionality of the ATM.
2. `AtmServiceImpl` class implements the `AtmService` interface, which actually provides the implementation of the
   ATM functionality.
3. It uses `Atm` class to maintain the ATM state.
4. `Atm` class maintains the ATM state, like available denominations and their count in actual storage.
5. This `Atm` class can be implemented with any storage solution like `HashMap`, `TreeMap`, or even a Database, etc.
6. `MapBasedAtm` class is an implementation of the `Atm` class, which uses a `ConcurrentHashMap` to store the
   denominations and
   their count.
7. Additionally, same could be achieved by using a database in a separate implementation of the `Atm` class. Like
   `DatabaseBasedAtm` which could use a database to store the denominations and their count and provide the
   implementation for the methods of the `Atm` class. Like:
    1. `getDenominations()` can be implemented
       by `SELECT denomination FROM atm_denominations ORDER BY denomination DESC;`
8. Finally, a basic `Main` class is provided to demonstrate the usage of the ATM.

## How to Run ?

### Pre-requisites:

Tested with:

- Maven 3.9.6
- Java 21 (Should support till 17)

### Steps:

1. Clone the repository
2. Run `mvn clean install` to build the project
3. Run `mvn exec:java` to run the project
4. On execution, it will initialize the ATM state and then will demonstrate the ATM functionality.
5. And output will be displayed on the console something like below:
    ```
    13:02:42.684 [main] INFO  o.a.experiment.Main 
    Started execution
    13:02:42.686 [main] INFO  o.a.experiment.Main 
    Ended execution
    13:02:42.687 [Thread-1] INFO  o.a.experiment.Main transactionId=2
    Withdrawal Authorization for 10200
    13:02:42.687 [Thread-0] INFO  o.a.experiment.Main transactionId=1
    Withdrawal Authorization for 100
    13:02:42.710 [Thread-1] INFO  o.a.experiment.Main transactionId=2
    Withdrawal Authorization for 10200: Amount(amount={2000=5, 200=1}, authorizationId=14249d1d-1e28-49e9-ac23-33cf24f953ba)
    13:02:42.710 [Thread-0] INFO  o.a.experiment.Main transactionId=1
    Withdrawal Authorization for 100: Amount(amount={100=1}, authorizationId=fae0db23-a393-4d9f-a819-fe21a475c501)
    ```

### Assumptions:

1. ATM can have any number of denominations, but all denominations should be multiple of 10.
2. ATM can have multiple denominations of different values and count.
3. User can withdraw any amount, but it should be multiple of 10 and of minimum available denomination.

### Future Scope:

1. Implementation can be made more available and efficient by using a database to store the ATM state.
2. Implementation can be made more efficient by using approach of Backtracking and Memoization to solve it.
3. Each held transaction will be cleared automatically after a certain period of time.