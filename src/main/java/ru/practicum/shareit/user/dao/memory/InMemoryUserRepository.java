package ru.practicum.shareit.user.dao.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.BaseInMemoryRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryUserRepository extends BaseInMemoryRepository<User> implements UserRepository {

    @Override
    public List<User> findAll() {
        return getFromStorage();
    }

    @Override
    public User save(User user) {
        final Long id = putInStorage(user);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        final Long userId = user.getId();
        final User currentUser = findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        final String email = user.getEmail();
        if (Objects.nonNull(email) && !email.isBlank()) {
            currentUser.setEmail(email);
        }
        final String name = user.getName();
        if (Objects.nonNull(name) && !name.isBlank()) {
            currentUser.setName(name);
        }
        return currentUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return getFromStorage().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return super.getById(id);
    }

    @Override
    public Optional<User> delete(Long id) {
        return super.delete(id);
    }
}