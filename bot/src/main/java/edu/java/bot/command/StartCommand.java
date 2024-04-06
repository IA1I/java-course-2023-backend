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
    private static final String COMMAND_NAME = "/start";
    private static final String COMMAND_DESCRIPTION = "Register a user";
    private final ChatClient chatClient;

    @Autowired
    public StartCommand(UserMessageProcessor processor, ChatClient chatClient) {
        super(processor);
        this.chatClient = chatClient;
    }

    @Override
    public String command() {
        return COMMAND_NAME;
    }

    @Override
    public String description() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        Mono<String> responseMono = chatClient.registerChat(chatId);
        String response = "";
        try {
            response = responseMono.doOnError(throwable -> log.error(throwable)).block();
        } catch (Exception e) {
            response = "Error";
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
