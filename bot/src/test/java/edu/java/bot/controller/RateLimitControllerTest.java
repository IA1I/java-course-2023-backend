package edu.java.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.dto.request.LinkUpdateRequest;
import java.util.Objects;
import edu.java.bot.service.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "bucket4j.enabled = true",
    "spring.cache.type=jcache",
    "spring.cache.cache-names=rate-limit-buckets",
    "spring.cache.caffeine.spec=maximumSize=100000,expireAfterAccess=3600s",
    "spring.cache.jcache.provider=com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider"
})
@AutoConfigureMockMvc
public class RateLimitControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UpdateService updateService;

    @BeforeEach
    public void beforeEach() {
        Objects.requireNonNull(cacheManager.getCache("rate-limit-buckets")).clear();
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTooManyRequestsStatusInAShortTimeRequestsForChatController() throws Exception {
        LinkUpdateRequest request = new LinkUpdateRequest(
            1L,
            "url",
            "description",
            new Long[] {440439212L}
        );
        Mockito.doNothing().when(updateService).sendUpdate(request);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/updates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        }

        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isTooManyRequests());
    }
}
