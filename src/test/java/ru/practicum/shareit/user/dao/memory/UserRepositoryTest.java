package ru.practicum.shareit.user.dao.memory;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository userRepository = new InMemoryUserRepository();

    @Test
    public void addNewUser() {
        final User user = ObjectsFactory.newUser("aaa@dd.com", "name1");
        final User newUser = userRepository.save(user);
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getName(), newUser.getName());
        assertNotNull(newUser.getId());
        assertEquals(userRepository.findAll().size(), 1);
    }

    @Test
    public void updateUser() {
        final User user = ObjectsFactory.newUser("aaa@dd.com", "name1");
        final User userNewData = ObjectsFactory.newUser("bbb@cc.com", "name11");
        final User newUser = userRepository.save(user);
        userNewData.setId(newUser.getId());
        final  User updatedUser = userRepository.update(userNewData);
        assertEquals(updatedUser.getEmail(), userNewData.getEmail());
        assertEquals(updatedUser.getName(), userNewData.getName());
        assertEquals(userRepository.findAll().size(), 1);
    }

    @Test
    public void deleteUser() {
        final User user = ObjectsFactory.newUser("aaa@dd,com", "name1");
        final User newUser = userRepository.save(user);
        assertEquals(userRepository.findAll().size(), 1);
        userRepository.delete(newUser.getId());
        assertEquals(userRepository.findAll().size(), 0);
    }
}