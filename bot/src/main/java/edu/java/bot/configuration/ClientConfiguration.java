package edu.java.bot.configuration;

import edu.java.bot.client.scrapper.ChatClient;
import edu.java.bot.client.scrapper.DefaultChatClient;
import edu.java.bot.client.scrapper.DefaultLinkClient;
import edu.java.bot.client.scrapper.LinkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public ChatClient httpChatClient(ApplicationConfig config) {
        return new DefaultChatClient(config);
    }

    @Bean
    public LinkClient httpLinkClient(ApplicationConfig config) {
        return new DefaultLinkClient(config);
    }
}
