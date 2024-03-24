package edu.java.bot.command;

import edu.java.bot.processor.UserMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCommand implements Command {
    protected final UserMessageProcessor processor;

    @Autowired
    public AbstractCommand(UserMessageProcessor processor) {
        this.processor = processor;
    }
}
