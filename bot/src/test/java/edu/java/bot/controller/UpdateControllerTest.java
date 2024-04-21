package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.dto.response.ApiErrorResponse;
import edu.java.bot.service.UpdateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UpdateService updateService;

    @Test
    void shouldReturnApiErrorResponseForEmptyPostRequest() throws Exception {
        ApiErrorResponse response = new ApiErrorResponse(
            "Invalid request parameters",
            "400 BAD_REQUEST",
            "org.springframework.http.converter.HttpMessageNotReadableException",
            "Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.String> edu.java.bot.controller.UpdateController.newUpdate(edu.java.bot.dto.request.LinkUpdateRequest)",
            null
        );

        mockMvc.perform(
            post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value(response.description()))
            .andExpect(jsonPath("$.code").value(response.code()))
            .andExpect(jsonPath("$.exception_name").value(response.exceptionName()))
            .andExpect(jsonPath("$.exception_message").value(response.exceptionMessage()));
    }

    @Test
    void shouldReturnOkForCorrectRequest() throws Exception {
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            "url",
            "description",
            new Long[] {440439212L}
        );
        Mockito.doNothing().when(updateService).sendUpdate(request);

        mockMvc.perform(
                post("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().string("Updates received"));
    }
}

