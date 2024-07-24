# Register estate hub frontend

This service is responsible for navigating the user to respective sections of their registration. It shows the progress of their registration on a task list and provides the ability to print draft and confirmations of their registraton.
It acts as a 'hub' to other parts of their registration journey.

To run locally using the microservice provided by the service manager:

***sm2 --start ESTATES_ALL***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8822 but is defaulted to that in build.sbt).

`sbt run`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
