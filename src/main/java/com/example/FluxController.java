package com.example;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import java.nio.charset.StandardCharsets;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller("/test")
public class FluxController {

  @Put(consumes = MediaType.ALL)
  @Produces(MediaType.APPLICATION_JSON)
  public Mono<String> upload(@Body final Flux<byte[]> content) {
    return content.shareNext()
        .map(bytes -> {
          System.out.println("First bytes: " + new String(bytes, StandardCharsets.UTF_8));
          return "ok";
        }).flatMap(o -> content.reduce(System.out, (printStream, bytes) -> {
          printStream.println("bytes: " + new String(bytes, StandardCharsets.UTF_8));
          return printStream;
        }).then(Mono.just("ok")));
  }
}
