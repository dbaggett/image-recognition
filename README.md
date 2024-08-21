# Image Object Detection Example
This is a contrived API that provides image object detection to consumers. While not a pure implementation, this
project strives to implement all the features below in a more functional manner.

## High Level Features
* Integrates with [Imagga](https://imagga.com/) for image recognition and tagging
* Stores image metadata into a local database
* Provides APIs to query the image metadata' (specs available in reference folder)

## Requirements
* Docker

## Tech
* Kotlin
* ArrowKt (for more FP goodness)
* Springboot
* Docker Compose
* Testcontainers
* Postgres

## How to Run
First, ensure you have the [requirements](#requirements) installed. *A postgres database is made available via docker compose.*

Execute the following to run `./gradlew bootRun`

## How to Run Tests
First, ensure you have the [requirements](#requirements) installed. *Testcontainers is used to provision a disposable postgres
database for integration tests, so a docker environment is required.*

Execute the following `./gradlew test`