# Hangman Game

Implementation of popular guessing game in Java. Application is built as a web-app on top of [Spring Boot]
and [MapDB] embedded database engine.

Example screenshot:


<img src="screenshot.png" style="width:800px;"/>

## Requirements
 * JDK8
 * JAVA_HOME variable pointing to active java directory

## Running the App


* If gradle is installed execute following command:

  `$ gradle bootRun`


* If gradle is not installed, on *nix OS, execute following command:

  `$ ./gradlew bootRun`


* If gradle is not installed, on Windows OS, execute following command:

  `gradlew.bat bootRun`

Then point your browser at [http://localhost:8080]

## How to play

#### Starting a new game

Click any of the top options
* New Game - Animals
* New Game - Fruits
* New Game - Vegetables

#### Continuing started game
1. Paste game token into input
2. Click 'Load Game' button

## Project description

The project uses:
* [Spring Boot]
* [Spring MVC]
* [Spring Hateoas]
* [MapDB]
* [Gradle]
* [Thymeleaf]
* [jQuery]
* [Bootstrap]
* [MultithreadedTC]
* [Mockito]





## Tested on:

* firefox
* chrome
* opera
* IE



[Spring Boot]:http://projects.spring.io/spring-boot
[Spring MVC]:http://projects.spring.io/spring-framework/
[Spring Hateoas]:http://projects.spring.io/spring-hateoas
[MapDB]:http://www.mapdb.org
[Gradle]:https://gradle.org
[Thymeleaf]:http://www.thymeleaf.org
[jQuery]:http://jquery.com
[Bootstrap]:http://getbootstrap.com
[MultithreadedTC]:http://www.cs.umd.edu/projects/PL/multithreadedtc
[Mockito]:http://mockito.org
[http://localhost:8080]:http://localhost:8080
