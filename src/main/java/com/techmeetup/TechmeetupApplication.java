package com.techmeetup;

import com.techmeetup.event.Event;
import com.techmeetup.event.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TechmeetupApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechmeetupApplication.class, args);
    }

    @Bean
    CommandLineRunner seedEvents(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() > 0) {
                return;
            }

            eventRepository.saveAll(List.of(
                    new Event("Java Developers Meetup", "Jul 25, 2026", "Lagos Tech Hub", 50),
                    new Event("Spring Boot Workshop", "Aug 12, 2026", "Victoria Island Innovation Center", 30),
                    new Event("Cloud Native Night", "Sep 03, 2026", "Remote", 75)
            ));
        };
    }
}
