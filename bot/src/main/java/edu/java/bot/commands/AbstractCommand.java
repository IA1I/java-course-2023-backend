package edu.java.bot.commands;

import edu.java.bot.dao.Dao;
import edu.java.bot.dto.User;
import edu.java.bot.processors.UserMessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCommand implements Command {
    protected final UserMessageProcessor processor;
    protected final Dao<User, Long> userDao;

    @Autowired
    public AbstractCommand(UserMessageProcessor processor, Dao<User, Long> userDao) {
        this.processor = processor;
        this.userDao = userDao;
    }

    protected boolean isRegistered(long id) {
        return userDao.contains(id);
    }
}
