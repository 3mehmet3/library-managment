# Library Management

Course project: Java 25 classes and JUnit 5 tests for a small library lending domain.
Graded on design patterns (Strategy, Factory, Facade, Repository/DI), SOLID, custom exceptions and unit tests.

## Rules
- No main method, no System.out, no system clock. Dates always come in as a `LocalDate` parameter.
- Money is `BigDecimal`.
- No method returns null. Domain errors throw a custom exception extending `LibraryException`.
- Null or blank constructor arguments are programming errors and throw `IllegalArgumentException`.
- No extra dependencies (no Mockito, no Lombok). Tests use the in-memory repositories.
- No unnecessary comments.
- Test names follow `method_condition_expectedResult`.
- Run `mvn test` after each step; all tests must pass.
- Commit after each step, never push.

## Packages (`com.library`)
- `exception`: `LibraryException` and its 10 subclasses
- `model`: `LibraryItem` (`Book`, `Dvd`, `Magazine`), `ItemType`, `Patron`, `Loan`
- `factory`: `ItemFactory`
- `policy`: `PatronPolicy`, `StudentPolicy`, `FacultyPolicy`
- `repository`: repository interfaces and in-memory implementations
- `service`: `Library` facade

## Steps
1. Exceptions
2. Item model and factory
3. Patron policies
4. Patron
5. Loan
6. Repositories
7. Library: add item, register patron
8. Library: borrow, return
9. Library: calculate fine, pay fine
10. Test coverage check
