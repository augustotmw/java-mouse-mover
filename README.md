# MouseMover

A simple Java Swing application that moves the mouse cursor cyclically between two points after a configurable time interval.

## Features

- Set pixel distance and time interval via input fields
- Start/Stop controls
- Collapsible error log panel
- Cross-platform (Java 8–21 compatible)

## Requirements

- Java JDK or JRE 8–21

## Build

This project uses the **Gradle Wrapper**, so you do not need to install Gradle manually.

### Windows

```cmd
.\gradlew.bat build
```

### Linux / macOS

```bash
./gradlew build
```

The JAR will be generated at:

```
build/libs/MouseMover-1.0.0.jar
```

## Run

After building, run the application with:

```bash
java -jar build/libs/MouseMover-1.0.0.jar
```

Or on Windows:

```cmd
java -jar build\libs\MouseMover-1.0.0.jar
```

## Project Structure

```
MouseMover/
├── src/main/java/br/com/atmw/mousemover/
│   └── MouseMover.java
├── build.gradle
├── settings.gradle
├── gradle/
│   └── wrapper/
├── gradlew
├── gradlew.bat
└── README.md
```

## Compatibility

The project is configured to compile with Java 8 source/target compatibility, ensuring it runs on any JVM from version 8 through 21.
