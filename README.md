# car-parking-system

[![Build Status](https://app.travis-ci.com/iamaldren/car-parking-system.svg?branch=main)](https://app.travis-ci.com/iamaldren/car-parking-system)
[![codecov](https://codecov.io/gh/iamaldren/car-parking-system/branch/main/graph/badge.svg?token=sin0WurYo2)](https://codecov.io/gh/iamaldren/car-parking-system)

## Prerequisites

1. Git - Follow [here](https://www.digitalocean.com/community/tutorials/how-to-install-git-on-ubuntu-16-04) for installation instructions
2. Java 11 - Follow [here](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04) for installation instructions
3. Maven - Follow [here](https://www.rosehosting.com/blog/how-to-install-maven-on-ubuntu-16-04/) for installtion instructions
4. Redis

### 3 ways to run Redis

Redis is used by the application for temporary storage.

#### Embedded Redis

The application comes with an embedded Redis, and it as advised to use this instead for this package version. 

To enable embedded Redis, the property below in the `application.yml` file must be set to true:
```yaml
app:
  redis:
    embedded: true
```
Every application restart, the Redis instance will be considered as fresh, so no data from previous runs will linger.

#### Redis by Docker

Redis can also be installed without the trouble of manual setup by using Docker.

To install Docker, follow [here](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04)

After Docker has been successfully installed (make sure user has necessary permission to execute/run docker commands), execute the command below to get and deploy Redis image.
```shell
$ cd <project_dir>
$ docker-compose up
```

#### Manual installation of Redis

Please follow [here](https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-redis-on-ubuntu-16-04)

## Car Parking System Application

### Requirement

Your task is to design an automated valet car parking system where you manage a parking space
for vehicles as they enter/exit it and manage its revenue. The data provided to you(refer to the
Data Description section below) would include the number of parking lots available and the
entry/exit details of each vehicle. For the purpose of this question you can assume that Cars and
Motorcycles are the only two types of vehicles.

Each vehicle upon entry can only park in a lot available for that vehicle type. If there are no lots
available for that vehicle type, it should be denied an entry into the space. As we’re building a
valet car park, all the lots in the parking space can be considered as being distinctly numbered
eg: CarLot1, CarLot2,..., MotorcycleLot1, MotorcycleLot2,.... Each vehicle upon entering is allotted
to the lot with the lowest number for that vehicular type eg: a car entering a parking space with
the available lots CarLot2, CarLot4, CarLot5 would be assigned to CarLot2. When a vehicle wants
to exit the car park, the system will return the parking lot that the vehicle will be removed from
and charge them an appropriate parking fee(rounded up to the nearest hour, i.e., 1hr 1min is
charged as 2hr): $1/hour for a motorcycle and $2/hour for a car.

You may use any language/framework for this exercise.

#### Data Description

Your program accepts an input file as an argument when running. The format of the file is
described below:

```text
3 4
Enter motorcycle SGX1234A 1613541902 
Enter car SGF9283P 1613541902
Exit SGX1234A 1613545602
Enter car SGP2937F 1613546029
Enter car SDW2111W 1613549730
Enter car SSD9281L 1613549740
Exit SDW2111W 1613559745
```

The first line indicates the number of parking slots that are present in the space for Cars and
Motorcycles respectively in the parking lot.

For each subsequent line, there would be two types of events:
- Vehicle entering the space: Enter <motorcycle|car> <vehicle number>
<timestamp>. The program should print out either Accept or Reject based on the
availability of lots in the parking space. If the vehicle is accepted, the program also returns
the name of the lot being occupied by it. 
- Vehicle exiting the space: Exit <vehicle number> <timestamp>. The program
prints out the released lot and the parking fee.

Given the example above, the program output would look like:

```text
Accept MotorcycleLot1
Accept CarLot1
MotorcycleLot1 2
Accept CarLot2
Accept CarLot3
Reject
CarLot3 6
```

Any erroneous cases should be handled with an appropriate error message.

### Assumptions

1. Only 2 types of vehicle are supported, Car and Motorcycle. Any other vehicles would not get processed.
2. Only 2 types of events are supported, Enter and Exit. Any other type of events would not get processed.
3. File can be placed in any folder within the system (as long as it can be accessed by the app)
4. Multiple files can be used, and all files must be processed.
5. If multiple files are there, each file will be considered as a new setup.
6. The data and results don't need to get persisted in any storage system.
7. Result must be in the same order as the input was processed.
8. If the first line of the file is more than 2, or not in numeric format. The whole file will be skipped and not get processed. This part must be accurate, as it will be the main factor to identify the number of lots available.
9. If any event (subsequent lines after the first line) has any problems, only the line will get skipped and process the next one. 
10. Application will not flag duplicate files. If there are multiple files, each file will be processed independently regardless if contents are same.
11. The application will always assume that an Enter Event has 4 parameters, in any case the parameters are not equal to 4, it will not be treated as an Enter event.
12. The application will always assume that an Exit Event has 4 parameters, in any case the parameters are not equal to 3, it will not be treated as an Exit event.

### Edge Cases

1. If the first line of the file is more than 2, or not in numeric format. The whole file will be skipped and not get processed.
2. If the event is neither Enter nor Exit. The data will be skipped, and proceed to process the next line.
3. If the vehicle is neither Car nor Motorcycle. The data will be skipped, and proceed to process the next line.
4. Duplicate processing of plate number for the same event is not allowed. E.g. If plate number had entered previously and not yet exited, the plate number would not get processed for another Enter event until he exits the current one.
5. Exit event without an Enter event would not get processed. Data would be skipped.
6. Exit event whose time is earlier than Enter event would not get processed. Data would be skipped.
7. If the parameters count for both Enter and Exit event are not equal to 4 and 3 respectively, the event would not get processed. Data would be skipped.

### Services

The application has 5 main services that are responsible for the whole logic flow.

#### Parking Service

The class is located in the package as described below.

```textmate
app
|--src
|----main
|------java
|--------com.aldren
|------------parking.service
```
The parking service is basically the first class that will be triggered to invoke the parking system process.

This service will be responsible for invoking the services listed below, and for checking if the file can be processed or will be skipped.

#### Input Service

The class is located in the package as described below.

```textmate
app
|--src
|----main
|------java
|--------com.aldren
|------------input.service
```
The input service is the one responsible for consuming the input data for processing.

For this specific requirement, the input will be coming from a file. This is being handled by `FileInputService` which implements the `InputService` interface.

The service will look up the file from the location defined in the property below:
```yaml
app:
  input:
    file:
      location: "/inputs"
```

#### Lot Service

The class is located in the package as described below.

```textmate
app
|--src
|----main
|------java
|--------com.aldren
|------------lot.service
```
This service is responsible for setting up how many lots are available for the supported vehicle types, and for managing the available lots for each event. In such cases that the output file failed to get created, then it will just print the output in the logs.

#### Output Service

The class is located in the package as described below.

```textmate
app
|--src
|----main
|------java
|--------com.aldren
|------------output.service
```
This service is responsible for printing out the output.

If printing the output to a file is also enabled, then this service will be the one responsible in creating the output file too.

#### Event Service

The class is located in the package as described below.

```textmate
app
|--src
|----main
|------java
|--------com.aldren
|------------event.service
```
The event service is mainly responsible for processing the data per Enter or Exit event.

#### Enter Event Service

This service is responsible for allocating the vehicle (via plate number) to a lot. If there is no more lot available for the vehicle type, then it will reject.

#### Exit Event Service

This service is responsible for calculating the total fee, and de-allocating the vehicle to the occupied lot.

## How to run the application

### Preparation

1. All items in the Prerequisites section must be installed
2. Prepare the test input files.
3. You can choose to put your input files in any folder you want in your system, or you could also put it under `<project_dir>\src\main\resources\inputs`.

### Clone the project from Github

You can clone the project from github by using the command below:
```shell
$ git clone https://github.com/iamaldren/car-parking-system.git
```

### Configuration

All these configurations are located in the `application.yml` file, which can be found in the folder structure defined below.
```textmate
app
|--src
|----main
|------resources
```

#### Redis

Set the configuration properties below for Redis:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    timeout: 60000
```
The `password` field only needs to get populated if in your Redis setup, you set a password for access.

If you want to run the embedded version of Redis, make sure the property below is set to true:
```yaml
app:
  redis:
    embedded: true
```

#### File Location

By default, the app will try to look first for the file/s in the classpath resource. Default name of the folder is `inputs`. If the app can't find the files there, it will try to look in the server's folders.

The file location can be set in the below property:
```yaml
app:
  system:
    input:
      file:
        location: "/inputs"
```
#### File Output Location

To enable the output file creation, the property `app.system.output.file.enabled` must be set to true.

The output file properties can be set in the application.yml as sample below.

```yaml
app:
  system:
    output:
      file:
        enabled: true
        location: "/outputs"
```
Where:
- location = The folder where the output file will get created.

Do note that the location will always get created in the `user.home` directory. The name of the output file will be the same as the input file. So if the input file is `sample.txt`, the output file named `sample.txt` will get created in the output location defined.

So taking the sample above, and `user.home = /home/aldren`

The output file will be created in `/home/aldren/outputs/sample.txt`

#### Vehicle types and fees

The supported vehicle types, and fees are configured in the application properties as well.

```yaml
app:
  system:
    vehicle:
      types:
        - kind: "car"
          fee: 2.00
          index: 0
          lotName: "CarLot"
        - kind: "motorcycle"
          fee: 1.00
          index: 1
          lotName: "MotorcycleLot"
```
Where:
- kind = Vehicle type
- fee =  Fee/hr for the vehicle type
- index = Index location of the available lot in the file. In the requirement Car lot is index 0 with value of 3 and motorcyle lot is index 1 with value of 4.
- lotName = The name of the lot for the vehicle type

#### Mode of Execution

There are 2 ways to execute/invoke the process for the parking system.

##### Via API

To invoke the process via API, the below property must be set:
```yaml
app:
  system:
    output:
      method: api
```

##### Via Scheduler

To invoke the process via scheduler, the below property must be set:
```yaml
app:
  system:
    output:
      method: scheduler
```

### Building the package

The program can be package by executing the commands below in a Command Line interface. Do note one must be in the project directory before executing this. The project directory is where the `pom.xml` of the code is located.

#### With running JUnit

Execute the command below to build the package:
```shell
$ mvn package
```

#### Without running JUnit
Execute the command below to build the package without executing the unit tests:
```shell
$ mvn package -Dmaven.test.skip=true
```

### Running the program

The program can be run by executing the commands below in a Command Line interface. Do note one must be in the project directory before executing this. The project directory is where the pom.xml of the code is located.

```shell
$ java -jar target/car-parking-system-1.0-SNAPSHOT.jar
```

#### Executing via API

If you configured to execute the process by API, the below URL must get invoked.

`Curl Command`:
```shell
$ curl http://localhost:8080/api/parking
```

If your system doesn't have the curl command installed, please follow [here](https://sourcedigit.com/22835-ubuntu-16-04-install-curl-curl-ubuntu-example/) for installation instructions.

The URL can also be executed via a web browser.

Output can be seen in the logs, or in the output file (if enabled).

#### Executing via Scheduler

If you configured to execute the process via scheduler, the program will automatically consume the files based on a `fixedDelay` configuration.

The scheduler has a `fixedDelay` of 1 min, with an `initialDelay` of 5 seconds.

Output can be seen in the logs, or in the output file (if enabled).

#### Output

Output would be in below format:

For files without any errors:
```text
Accept MotorcycleLot1
Accept CarLot1
MotorcycleLot1 2
Accept CarLot2
Accept CarLot3
Reject
CarLot3 6
```

For files with format errors:

`Sample 1`:
```text
Accept MotorcycleLot1
Accept CarLot1
Bad Data:: Skipping event, wrong format. Recognized events are [ENTER, EXIT]. Data length is expected to be 4 for ENTER and 3 for EXIT. [Exit SGX1234A 1613545602 AAAAAAAAAAAA]
Accept CarLot2
Accept CarLot3
Reject
CarLot3 6
```

`Sample 2`:
```text
Accept MotorcycleLot1
Accept CarLot1
Bad Data:: Exit event time is earlier than Enter event time for plate number SGX1234A.
Accept CarLot2
Accept CarLot3
Reject
CarLot3 6
```

`Sample 3`:
```text
Bad Data:: Vehicle truck is not yet supported.
Accept CarLot1
Bad Data:: Vehicle with plate number of SGX1234A is not parked or not in the system.
Accept CarLot2
Accept CarLot3
Reject
CarLot3 6
```
For rejected files:

`Sample 1`:
```text
Bad Data:: Skipping file, lot count is not numeric [3 B].
```

`Sample 2`:
```text
Bad Data:: Skipping file, wrong format for lot count [3 4 A].
```