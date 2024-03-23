package edu.java.bot.dao;

import edu.java.bot.dto.User;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserLocalDaoTest {

    @Test
    void shouldAddUserInStorage() {
        UserLocalDao userLocalDao = new UserLocalDao();
        User expected = new User(1L);
        userLocalDao.save(expected);

        User actual = userLocalDao.get(1L);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteUserFromStorage() {
        UserLocalDao userLocalDao = new UserLocalDao();
        User expected = new User(1L);
        userLocalDao.save(expected);
        userLocalDao.delete(1L);

        User actual = userLocalDao.get(1L);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    void shouldContainsUserInStorage() {
        UserLocalDao userLocalDao = new UserLocalDao();
        User expected = new User(1L);
        userLocalDao.save(expected);

        boolean actual = userLocalDao.contains(1L);

        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void shouldNotContainsUserInStorage() {
        UserLocalDao userLocalDao = new UserLocalDao();

        boolean actual = userLocalDao.contains(1L);

        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnListOfUsersInStorage() {
        UserLocalDao userLocalDao = new UserLocalDao();
        User user1 = new User(1L);
        User user2 = new User(2L);
        User user3 = new User(3L);
        userLocalDao.save(user1);
        userLocalDao.save(user2);
        userLocalDao.save(user3);
        List<User> expected = List.of(user1, user2, user3);

        List<User> actual = userLocalDao.getAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
