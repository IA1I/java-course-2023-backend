package edu.java.scrapper.controller;

import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.service.ChatService;
import edu.java.scrapper.service.LinkService;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private JdbcChatRepository chatRepository;
    @Autowired
    private CacheManager cacheManager;
    @MockBean
    private ChatService chatService;
    @MockBean
    private LinkService linkService;

    @BeforeEach
    public void beforeEach() {
        Objects.requireNonNull(cacheManager.getCache("rate-limit-buckets")).clear();
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTooManyRequestsStatusInAShortTimeRequestsForChatController() throws Exception {
        Mockito.doNothing().when(chatService).register(1L);
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/tg-chat/1"))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(post("/tg-chat/1"))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnTooManyRequestsStatusInAShortTimeRequestsForLinkController() throws Exception {
//        Chat chat = new Chat(0, 1L);
//        chatRepository.save(chat);
        Mockito.when(linkService.listAll(1L)).thenReturn(List.of());
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/links").header("Tg-Chat-Id", 1L))
                .andExpect(status().isOk());
        }
        mockMvc.perform(get("/links").header("Tg-Chat-Id", 1L))
            .andExpect(status().isTooManyRequests());
    }

}
