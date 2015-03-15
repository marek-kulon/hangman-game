# Hangman Game

Implementation of popular guessing game in Java. Application is built as a web-app on top of [Spring Boot] and [MapDB] embedded database engine.

<img src="screenshot.png" style="width:800px;"/>

## Features

* Implementation in Java 8
* Game state is persisted between restarts in [MapDB] database
* Game state is protected from read-modify-write race conditions
* Ajax communication between browser and server

## Requirements

 * JDK8
 * JAVA_HOME variable pointing to active Java directory

## Running the App

* If gradle is installed execute following command:

  `$ gradle bootRun`


* If gradle is not installed, on Linux OS, execute following command:

  `$ ./gradlew bootRun`


* If gradle is not installed, on Windows OS, execute following command:

  `gradlew.bat bootRun`

Then point your browser at [http://localhost:8080]

## How to play

#### Starting a new game
Click one of the top options
* New Game - Animals
* New Game - Fruits
* New Game - Vegetables

#### Continuing started game
1. Paste game token into input box
2. Click "Load Game" button

## Project description

The project uses
* [Spring Boot]
* [Spring MVC]
* [Spring Hateoas]
* [MapDB]
* [Apache Commons]
* [Guava]
* [Gradle]
* [Thymeleaf]
* [jQuery]
* [Bootstrap]
* [MultithreadedTC]
* [Mockito]

## What’s happening under the hood

#### Server-side

The main class performing all user actions is [Game]. [Game] contains reference to immutable [GameState] which is a minimum data set required to visualize game, interact with the user and determine its result. Every user action successfully performed on [Game] moves it to another state.

Game state consists of secret word, allowed number of incorrect guesses and guesses made by user.
All that information is persisted in repository as one entity. Benefit of described approach is ease of horizontal scaling.

System stores data in two repositories: containing all game state data [GameStateRepositoryMapDbFile] and
[SecretRepositoryJsonFile] storing guess secrets.

Most outer layer of back-end is a restful-ish [GameController]. For communication between [Game], [GameController]
and system repositories responsible is [GameService]. Its  implementation, [GameServiceThreadSafeImpl], additionally protects code
from race conditions.

#### Front-end

For communication between browser and server responsible is JavaScript [hangman.js] library. Three most important functions are:
* `newGame` - starts new game,
* `guess` - performs guess action,
* `load` - loads data from server based on game token.

Each of these calls `requestServer` to update game state which is accessible through any of `state` functions. Game state on browsers site is protected against simultaneous modifications by aborting any new `requestServer` operation if one is already taking place.


## Tested on:

* Firefox
* Chrome
* Opera
* IE 8+



[Spring Boot]:http://projects.spring.io/spring-boot
[Spring MVC]:http://projects.spring.io/spring-framework/
[Spring Hateoas]:http://projects.spring.io/spring-hateoas
[MapDB]:http://www.mapdb.org
[Apache Commons]:http://commons.apache.org
[Guava]:https://github.com/google/guava
[Gradle]:https://gradle.org
[Thymeleaf]:http://www.thymeleaf.org
[jQuery]:http://jquery.com
[Bootstrap]:http://getbootstrap.com
[MultithreadedTC]:http://www.cs.umd.edu/projects/PL/multithreadedtc
[Mockito]:http://mockito.org
[http://localhost:8080]:http://localhost:8080
[Game]:src/main/java/hangman/core/Game.java
[GameState]:src/main/java/hangman/core/state/GameState.java
[GameController]:src/main/java/hangman/web/GameController.java
[GameService]:src/main/java/hangman/core/GameService.java
[GameStateRepositoryMapDbFile]:src/main/java/hangman/core/state/repository/file/mapdb/GameStateRepositoryMapDbFile.java
[SecretRepositoryJsonFile]:src/main/java/hangman/core/secret/repository/file/json/SecretRepositoryJsonFile.java
[GameServiceThreadSafeImpl]:src/main/java/hangman/core/GameServiceThreadSafeImpl.java
[hangman.js]:src/main/resources/static/javascript/hangman.js
