package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.ChatClient;
import edu.java.bot.client.scrapper.DefaultChatClient;
import edu.java.bot.client.scrapper.DefaultLinkClient;
import edu.java.bot.client.scrapper.LinkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {

    @Bean
    public ChatClient httpChatClient(ApplicationConfig config, Retry retry) {
        return new DefaultChatClient(config, retry);
    }

    @Bean
    public LinkClient httpLinkClient(ApplicationConfig config, Retry retry) {
        return new DefaultLinkClient(config, retry);
    }
}
