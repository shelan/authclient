## Using client to access API

The client supports 3 functionalities: **register**, **authenticate** and **check last 5 login timestamps**. The 
token (JWT) returned when authenticating the user should be used to check the login attempts of the user.

Use commands below to execute different actions through the client.
  * **Register**: ```java -jar  path_to_auth-client-1.0-SNAPSHOT-jar-with-dependencies.jar -a register -u user1 -p password1 
  -url http://localhost:4567```
  * **Authenticate**: ```java -jar  path_to_auth-client-1.0-SNAPSHOT-jar-with-dependencies.jar -a auth -u user1 -p password1 
  -url http://localhost:4567```
  * **Last Logins**: ```java -jar  path_to_auth-client-1.0-SNAPSHOT-jar-with-dependencies.jar -url http://localhost:4567 -a 
  lastLogins -t token```
