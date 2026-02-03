# World Cup Scoreboard Library

A simple Java library to manage football World Cup scores.

# Assumptions & Design Decisions
* Data Processing: The library is designed to handle pre-processed data (e.g., from a stream). Methods are strictly defined to accept and process specific data types.

* Simple Deletion: To keep the library lightweight and performant, the delete function performs a hard delete. We intentionally avoid "soft deletes" (flags) to eliminate the need for additional filtering logic.

* Pure Java & TDD: This project was developed using Test-Driven Development (TDD). To maintain high code quality and transparency:

    * I decided not to use frameworks like Lombok.

    * This ensures that every line of code is explicitly tested and verified by the suite, rather than relying on third-party code generation.

    * The focus remains on testing business logic rather than framework behavior.
    * 
* This is a library for a football World Cup scoreboard. So i assume that sum of goals in a match will not exceed Integer.MAX_VALUE.

## Requirements

- Java 21
- JUnit 5 (version 5.14.2)
- Jacoco (version 0.8.14)

## Installation

You can install the library from the GitHub repository:

```bash
git clone https://github.com/Szymon-Stasiak/world-cup-scoreboard-lib.git
cd world-cup-scoreboard-lib

````

## Build the project
This project uses Maven for dependency management. To download and install all required dependencies defined in the pom.xml, run:

```bash
mvn clean install
```

### Usage (Installation)
To use this library in your own project, you can add it as a dependency. First, ensure you have built the project locally using mvn clean install.
Then, add the following dependency to your project's `pom.xml`:
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>world-cup-scoreboard-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```
`