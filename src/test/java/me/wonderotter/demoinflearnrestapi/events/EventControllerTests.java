package me.wonderotter.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.wonderotter.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 18, 15, 32))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 19, 15, 32))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 20, 15, 32))
                .endEventDateTime(LocalDateTime.of(2020, 11, 21, 15, 32))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("오부자로 15 한림풀에버")
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8) // 보내는 타입
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))) // 받고 싶은 응답
                .andDo(print())
                .andExpect(status().isCreated()) //isCreated: 201
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(101)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(101)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 18, 15, 32))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 19, 15, 32))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 20, 15, 32))
                .endEventDateTime(LocalDateTime.of(2020, 11, 21, 15, 32))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("오부자로 15 한림풀에버")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED) //default: EventStatus.DRAFT
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8) // 보내는 타입
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))) // 받고 싶은 응답
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 18, 15, 32))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 17, 15, 32))
                .beginEventDateTime(LocalDateTime.of(2020, 11, 16, 15, 32))
                .endEventDateTime(LocalDateTime.of(2020, 11, 15, 15, 32))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("오부자로 15 한림풀에버")
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
}
