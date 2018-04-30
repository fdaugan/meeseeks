# Meeseeks (simple tasks!)
<img align="right" width="25%" src="docs/meeseeks.png">
A Spring Boot application designed to test a container by simulating memory, CPU, kill, etc. behaviors

# Build
```
mvn package
docker build -t fabdouglas/meeseeks:1.0.0 --build-arg WAR="target/app.jar" .
```

# Run
```
docker run --rm -d --name meeseeks -p 8080:8080 -p 9010:9010 fabdouglas/meeseeks:1.0.0 
```

# Simple tasks
All tasks use `GET` and have default values when parameters are not provided.

Echo [/api/echo?m=Hello](http://localhost:8080/echo)  
Sleep a thread [/api/kill?millis=1000](http://localhost:8080/sleep)  
100% CPU a thread [/api/cpu?millis=1000](http://localhost:8080/cpu)  
Counter in the context [/api/counter](http://localhost:8080/counter)  
Counter in the JVM [/api/counter-static](http://localhost:8080/counter-static)  
Restart context [/api/restart](http://localhost:8080/restart)  
Exit [/api/kill?code=1](http://localhost:8080/exit)  

# Endpoints
[API](http://localhost:8080/api)  
[Swagger](http://localhost:8080/swagger-ui.html)  
[Health](http://localhost:8080/manage/health)  
[Info](http://localhost:8080/manage/info) (includes git and meven versions)  
[Heapdump](http://localhost:8080/manage/heapdump)  
[HTTP Logs](http://localhost:8080/manage/httptrace)  
[Metrics](http://localhost:8080/manage/metrics) : 
[Threads](http://localhost:8080/manage/metrics/jvm.threads.live) 
[Memory](http://localhost:8080/manage/metrics/jvm.memory.used) 
[Memory Committed](http://localhost:8080/manage/metrics/jvm.memory.committed) [Start](http://localhost:8080/manage/metrics/process.start.time)


[(...)](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)

