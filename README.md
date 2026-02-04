# World Cup Scoreboard Library

A simple Java library to manage football World Cup scores.

# Assumptions & Design Decisions

* Data Processing: The library is designed to handle pre-processed data (e.g., from a stream). Methods are strictly defined to accept and process specific data types.

* Simple Deletion: To keep the library lightweight and performant, the delete function performs a hard delete. We intentionally avoid "soft deletes" (flags) to eliminate the need for additional filtering logic.

* Pure Java & TDD: This project was developed using Test-Driven Development (TDD). To maintain high code quality and transparency:

    * I decided not to use frameworks like Lombok.

    * This ensures that every line of code is explicitly tested and verified by the suite, rather than relying on third-party code generation.

    * The focus remains on testing business logic rather than framework behavior.
  
* This is a library for a football World Cup scoreboard. So I assume that sum of goals in a match will not exceed Integer.MAX_VALUE.

* I assume that names of teams are unique. So there will not be two teams with the same name. And also that names of teams are case-sensitive. So "Team A" and "team a" will be considered different teams. I assume that names cannot have the "-" character in them. Because i use that character as separator in some methods.

* I assume that providing additional non-sensitive data (like match creation time) is acceptable, as there were no constraints against it.
# Documentation – Library Usage

This library provides a simple API for managing live football match scores during the World Cup or similar tournaments.

It allows you to:

* Start new matches
* Update scores
* Finish matches
* Retrieve ordered match summaries

All operations are performed in-memory and are optimized for simplicity and performance.

---

## Main Components

### ScoreBoard

The main entry point of the library.
It manages all ongoing matches.

**Responsibilities:**

* Stores active matches
* Prevents duplicate or conflicting games
* Validates team names
* Provides match summaries

Note: The getSummary() method returns matches ordered by their total score. If the total score is the same, the most recently started match is displayed first.

---

### Match

Represents a single football match.

**Stores:**

* Home team name
* Away team name
* Current score
* Match start time

Each match is immutable from the outside. Updates are handled through ScoreBoard.

## Usage Example

```java
public static void main(String[] args) {

  ScoreBoard board = new ScoreBoard();

  // Start new matches
  board.startNewMatch("Mexico", "Canada");
  board.startNewMatch("Spain", "Brazil");

  // Update scores
  board.updateScore("Mexico", "Canada", 0, 5);
  board.updateScore("Spain", "Brazil", 2, 2);

  // Finish a match
  board.finishMatch("Mexico", "Canada");

  // Get summary
  List<Match> summary = board.getSummary();

  for (Match match : summary) {
    System.out.println(
            match.getHomeTeam() + " " +
                    match.getHomeTeamPoints() + " - " +
                    match.getAwayTeamPoints() + " " +
                    match.getAwayTeam()
    );
  }
}
```

In this example, we create a `ScoreBoard`, start two matches, update their scores, finish one match, and then print the summary of ongoing matches.


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
  <groupId>org.example</groupId>
  <artifactId>world-cup-scoreboard-lib</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
`