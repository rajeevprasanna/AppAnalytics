# AppAnalytics

#command to run main class from terminal
  1. sbt assembly
  
  2. java -jar AppAnalytics-assembly-1.0.jar (path : ./target/scala-2.12/AppAnalytics-assembly-1.0.jar)



Design :
1-Create an actor that will read a file

2-Create an actor that will parse your file

3-Create an actor that will send an email

4- Create an actor that will uses scheduling and this will be supervisor of all three actors

Superior(4 step) will create all three steps in timely manner.

Step 1 will read a file and send to step 2
step 2 will parse a messages and send to step 3