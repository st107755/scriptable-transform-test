# scriptable-transform-test
This application helps you to test your scriptable transform applications from grnry.io

In order to build it, go to the root folder and write ``gradlew assemble``. This will create your jar file. Afterwards, you may execute the jar file using:
``java -jar "<path_to_jar>" --script=<name_of_script> --payload=<payload_to_pass>``

As a result the application will process your payload as if it was executed on scriptable-transform pod.

The return of your application is printed on the command line.
