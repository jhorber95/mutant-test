# Magneto mutant system

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Gradle][]: We use gradle to install all project dependencies.

After installing gradle, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [build.gradle](build.gradle).

```
gradle clean assemble
```


## Building for production

### Packaging as jar

To build the final jar and optimize the aeroTaxi application for production, run:

```

./gradlew -Pprod clean bootJar


```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```

java -jar build/libs/*.jar


```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.


## Testing

To launch your application's tests, run:

```
./gradlew test integrationTest jacocoTestReport
```

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./gradlew -Pprod clean check jacocoTestReport sonarqube
```
If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./gradlew sonarqube
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

```
docker-compose -f src/main/docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/postgresql.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./gradlew bootJar jibDockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

[using docker and docker-compose]: https://docs.docker.com/compose/
[code quality page]: https://docs.sonarqube.org/latest/
[Gradle]: https://gradle.org/
