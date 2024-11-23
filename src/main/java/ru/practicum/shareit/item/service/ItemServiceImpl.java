package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public Item getById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id = %d не найдена", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public void existsById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameLikeOrDescriptionLikeAndAvailable(text);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getByOwner(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Transactional
    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Item updatedItem, Long userId) {
        final Item item = getById(updatedItem.getId());
        if (!userIsOwner(item, userId)) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    String.format("Владелец вещи не совпадает с пользователем %d", userId));
        }
        final String name = updatedItem.getName();
        if (Objects.nonNull(name) && !name.isBlank()) {
            item.setName(name);
        }
        final String description = updatedItem.getDescription();
        if (Objects.nonNull(description) && !description.isBlank()) {
            item.setDescription(description);
        }
        final Boolean available = updatedItem.getAvailable();
        if (Objects.nonNull(available)) {
            item.setAvailable(available);
        }
        return itemRepository.save(item);
    }

    @Override
    public boolean userIsOwner(Item item, Long userId) throws ConditionsNotMetException {
        final Optional<Long> optOwnerId = Optional.ofNullable(item.getOwner()).map(User::getId);
        return optOwnerId.map(userId::equals).orElse(false);
    }
}
