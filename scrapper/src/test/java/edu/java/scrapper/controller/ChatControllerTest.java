package edu.java.scrapper.controller;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dto.Chat;
import edu.java.scrapper.dto.response.ApiErrorResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.access-type=jdbc")
@AutoConfigureMockMvc
public class ChatControllerTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    @Rollback
    void shouldRegisteredChat() throws Exception {
        Long tgChatId = 1L;
        Chat expected = new Chat(0, tgChatId);

        mockMvc.perform(
                post("/tg-chat/{id}", tgChatId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andExpect(content().string("Chat registered"));

        Chat actual = chatRepository.getByTgChatId(1L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForReRegistrationChat() throws Exception {
        ApiErrorResponse response = new ApiErrorResponse(
            "Chat re-registration",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ReRegistrationException",
            "Chat re-registration",
            null
        );
        Long tgChatId = 2L;
        Chat chat = new Chat(0, tgChatId);

        chatRepository.save(chat);
        mockMvc.perform(
                post("/tg-chat/{id}", tgChatId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(response.description()))
            .andExpect(jsonPath("$.code").value(response.code()))
            .andExpect(jsonPath("$.exception_name").value(response.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(response.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldUnregisteredChat() throws Exception {
        Long tgChatId = 3L;
        Chat expected = new Chat(0, tgChatId);

        chatRepository.save(expected);
        mockMvc.perform(
                delete("/tg-chat/{id}", tgChatId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("Chat deleted"));

        assertThrows(
            EmptyResultDataAccessException.class,
            () -> chatRepository.getByTgChatId(2L)
        );
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForNotRegisteredChat() throws Exception {
        ApiErrorResponse response = new ApiErrorResponse(
            "Chat is not registered",
            "409 CONFLICT",
            "edu.java.scrapper.exception.ChatIsNotRegisteredException",
            "Chat is not registered",
            null
        );
        Long tgChatId = 4L;

        mockMvc.perform(
                delete("/tg-chat/{id}", tgChatId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.description").value(response.description()))
            .andExpect(jsonPath("$.code").value(response.code()))
            .andExpect(jsonPath("$.exception_name").value(response.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(response.exceptionMessage()));
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnApiErrorResponseForBadRequest() throws Exception {
        ApiErrorResponse response = new ApiErrorResponse(
            "Invalid request parameters",
            "400 BAD_REQUEST",
            "org.springframework.web.method.annotation.MethodArgumentTypeMismatchException",
            "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"tgChatId\"",
            null
        );

        mockMvc.perform(
                delete("/tg-chat/{id}", "tgChatId")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value(response.description()))
            .andExpect(jsonPath("$.code").value(response.code()))
            .andExpect(jsonPath("$.exception_name").value(response.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(response.exceptionMessage()));
    }

}
