# Benchmark tool of [netty sio implementation]
### Server
Simple echo server which sends 'echoback' event with same echo message to client or clients in sender's room if sender has joined a room upon 'echo' event from client

To run server

`mvn exec:java -Pserver` OR `java -jar <target_jar_file> server`
### Client
Client is written using akka with two type of actors,

- <b>BenchmarkSupervisor</b>:
    - kicks off BenchmarkActors depending on [concurrency]
    - receives return trip time from BenchmarkActors
    - periodically (set to 1s for now) calculates average return trip times for the current time frame and logs that information, and depending on whether that time meets [cutoff] requirement, adds 1 more client
- <b>BenchmarkActor</b>:
    - initiates connection with socket io server
    - join room told by supervisor (set to 2 clients per room for now)
    - sends 'echo' event every [interval] amount of ms, the event content will be system time of when message is about to be sent
    - receives 'echoback' event, calculate message return trip time by subtracting content of message from current system time of event reception
    - sends return trip time to supervisor

To run client

`mvn exec:java -Pclient` OR `java -jar <target_jar_file> client [concurrency] [interval] [cutoff]`

- <b>concurrency</b> : number of concurrent clients
- <b>interval</b>: interval in ms each client sends 'echo' event
- <b>cutoff</b>: cutoff average return trip time of a message under which a new concurrent client will be added

### Local Benchmark Result

I have a 2.4GHz i7, with 8GB memory, was able to see around ~30,000 messages sent by server before starts to backlog upon which it will never recover.

Sample Calculation: # of messages sent by server = 1000/[interval] * [concurrency] * [number of clients per room]

[netty sio implementation]:https://github.com/mrniko/netty-socketio