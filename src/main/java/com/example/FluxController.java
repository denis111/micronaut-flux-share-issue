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
    final var newFlux = Flux.<byte[]>create(fluxSink ->
            content.subscribe(fluxSink::next, fluxSink::error, fluxSink::complete))
        .publish(1)
        .autoConnect();

    return newFlux.shareNext()
        .map(bytes -> {
          System.out.println("First bytes: " + new String(bytes, StandardCharsets.UTF_8));
          return Mono.just(bytes);
        }).flatMap(o -> Flux.merge(o, newFlux).reduce(System.out, (printStream, bytes) -> {
          printStream.println("bytes: " + bytes.length);
          return printStream;
        })).then(Mono.just("ok"));
  }

}
