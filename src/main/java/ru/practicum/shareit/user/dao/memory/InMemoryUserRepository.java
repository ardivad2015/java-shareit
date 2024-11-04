package ru.practicum.shareit.user.dao.memory;

import org.springframework.stereotype.Repository;
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
        if (Objects.nonNull(user)) {
            updateInStorage(user.getId(), user);
        }
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            return Optional.empty();
        }
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