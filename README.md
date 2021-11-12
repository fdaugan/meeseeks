# Meeseeks (simple tasks!)
<img align="right" width="25%" src="docs/meeseeks.png">
A Spring Boot application designed to test a container by simulating memory, CPU, kill, etc. behaviors

# Build
```
mvn package -Dmaven.test.skip=true
docker build -t fabdouglas/meeseeks:1.0.0 --build-arg WAR="target/app.jar" .
```

# Run
```
docker run --rm -d --name meeseeks -p 8080:8080 -p 9010:9010 fabdouglas/meeseeks:1.0.0
```

# Simple tasks
All tasks use `GET` and have default values when parameters are not provided.

Echo [/api/echo?m=Hello](http://localhost:8080/api/echo)  
Sleep a thread [/api/kill?millis=1000](http://localhost:8080/api/sleep)  
100% CPU a thread [/api/cpu?millis=1000](http://localhost:8080/api/cpu)  
1M of Memory a thread [/api/mem?millis=1000&nb_kilobytes=1000](http://localhost:8080/api/mem)  
Counter in the context [/api/counter](http://localhost:8080/api/counter)  
Counter in the JVM [/api/counter-static](http://localhost:8080/api/counter-static)  
Restart context [/api/restart](http://localhost:8080/api/restart)  
Exit [/api/kill?code=1](http://localhost:8080/api/exit)  

# Endpoints
[API](http://localhost:8080/api)  
[Swagger](http://localhost:8080/swagger-ui.html)  
[Health](http://localhost:8080/manage/health)  
[Info](http://localhost:8080/manage/info) (includes git and meven versions)  
[Heapdump](http://localhost:8080/manage/heapdump)  
[Metrics](http://localhost:8080/manage/metrics) : 
[Threads](http://localhost:8080/manage/metrics/jvm.threads.live) 
[Memory](http://localhost:8080/manage/metrics/jvm.memory.used) 
[Memory Committed](http://localhost:8080/manage/metrics/jvm.memory.committed) [Start](http://localhost:8080/manage/metrics/process.start.time)
[Other features](http://localhost:8080/manage)  


[(...)](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)

