package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetMyCommands;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.command.Command;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.processor.UserMessageProcessor;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class LinkTrackerBot implements Bot {
    private final TelegramBot bot;
    private final UserMessageProcessor processor;

    @Autowired
    public LinkTrackerBot(ApplicationConfig config, UserMessageProcessor processor) {
        this.bot = new TelegramBot(config.telegramToken());
        this.processor = processor;
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        log.info("Executing the request {}", request.getContentType());
        bot.execute(request);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            execute(processor.process(update));
        }

        return CONFIRMED_UPDATES_ALL;
    }

    @PostConstruct
    @Override
    public void start() {
        bot.setUpdatesListener(this);
        log.info("Set updates listener");

        createMenu();
        log.info("The bot has started working");
    }

    @Override
    public void close() {
        bot.shutdown();
        log.info("The bot has stopped working");
    }

    private void createMenu() {
        var response = bot.execute(new GetMyCommands());
        BotCommand[] actualCommands = response.commands();
        log.info("Getting the current list of bot commands");

        if (actualCommands != null && actualCommands.length != processor.commands().size()) {
            bot.execute(createCommandsForMenu());
            log.info("Creating a bot command menu");
        }
    }

    private SetMyCommands createCommandsForMenu() {
        return new SetMyCommands(
            processor.commands().stream()
                .map(Command::toApiCommand)
                .toArray(BotCommand[]::new)
        );
    }
}
