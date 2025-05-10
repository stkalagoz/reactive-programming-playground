package com.stk.reactive.student;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final MyPublisher publisher;

  public Flux<Student> findAll() {
    return studentRepository.findAll()
        .delayElements(Duration.ofSeconds(1));
  }

  public Mono<Student> findById(Long id) {
    return studentRepository.findById(id);
  }

  public Mono<Student> save(StudentRequest request) {
    return studentRepository.save(
        Student.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .age(request.getAge())
            .build()
    ).doOnSuccess(publisher::emit);
  }

  public Flux<Student> streamMessages() {
    Flux<Student> initialData = studentRepository.findAll().subscribeOn(Schedulers.boundedElastic());          // fast fetch from DB
    Flux<Student> liveUpdates = publisher.getStream();         // future events
    return Flux.concat(initialData, liveUpdates);
  }

  public Flux<Student> findByFirstname(String firstname) {
    return studentRepository.findAllByFirstnameContainingIgnoreCase(firstname);
  }

  public void deleteById(Long id) {
    studentRepository.deleteById(id).subscribe();
  }
}