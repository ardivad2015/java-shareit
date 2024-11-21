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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

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
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByTextSearch(text);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getUsersItems(Long userId) {
        userService.getById(userId);
        return itemRepository.findByOwnerId(userId);
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
        checkBeforeUpdate(item, userId);
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

    private void checkBeforeUpdate(Item item, Long userId) {
        userService.getById(userId);
        final User owner = item.getOwner();
        final List<String> errorList = new ArrayList<>();
        if (Objects.isNull(owner) || !owner.getId().equals(userId)) {
            errorList.add(String.format("Владелец вещи не совпадает с пользователем %d", userId));
        }
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }
}
