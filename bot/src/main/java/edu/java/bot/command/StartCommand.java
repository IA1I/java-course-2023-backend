package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ChatClient;
import edu.java.bot.processor.UserMessageProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class StartCommand extends AbstractCommand {
    private final ChatClient chatClient;

    @Autowired
    public StartCommand(UserMessageProcessor processor, ChatClient chatClient) {
        super(processor);
        this.chatClient = chatClient;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Register a user";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        Mono<String> responseMono = chatClient.registerChat(chatId);
        String response = "";
        try {
            response = responseMono.block();
        } catch (Exception e) {

        }


        return new SendMessage(chatId, response);
    }

    private String getText(Update update) {
        StringBuilder text = new StringBuilder();
        text.append("Hello, ")
                .append(update.message().from().firstName())
                .append(LINE_SEPARATOR)
                .append("You are registered!");

        log.info("Created text with successful registration for: /start");
        return text.toString();
    }
}
