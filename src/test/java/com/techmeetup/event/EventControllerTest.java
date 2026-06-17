package com.techmeetup.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventRepository eventRepository;

    @Test
    void getAllEventsReturnsJsonList() throws Exception {
        Event event = new Event("Java Developers Meetup", "Jul 25, 2026", "Lagos Tech Hub", 50);
        event.setId(1L);

        given(eventRepository.findAll()).willReturn(List.of(event));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Java Developers Meetup"))
                .andExpect(jsonPath("$[0].date").value("Jul 25, 2026"))
                .andExpect(jsonPath("$[0].location").value("Lagos Tech Hub"))
                .andExpect(jsonPath("$[0].availableTickets").value(50));
    }

    @Test
    void registerForEventDecrementsTicketCount() throws Exception {
        Event event = new Event("Java Developers Meetup", "Jul 25, 2026", "Lagos Tech Hub", 2);
        event.setId(1L);

        given(eventRepository.findById(1L)).willReturn(Optional.of(event));

        mockMvc.perform(post("/api/events/1/register"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful for Java Developers Meetup."));

        verify(eventRepository).save(event);
        org.junit.jupiter.api.Assertions.assertEquals(1, event.getAvailableTickets());
    }

    @Test
    void registerForEventReturnsBadRequestWhenSoldOut() throws Exception {
        Event event = new Event("Java Developers Meetup", "Jul 25, 2026", "Lagos Tech Hub", 0);
        event.setId(1L);

        given(eventRepository.findById(1L)).willReturn(Optional.of(event));

        mockMvc.perform(post("/api/events/1/register"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No tickets available for this event."));

        verify(eventRepository, never()).save(event);
    }

    @Test
    void registerForEventReturnsNotFoundWhenMissing() throws Exception {
        given(eventRepository.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(post("/api/events/99/register"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found."));
    }
}
