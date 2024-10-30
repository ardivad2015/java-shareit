package ru.practicum.shareit.item.dao.memory;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {

    ItemRepository itemRepository = new InMemoryItemRepository();

    @Test
    public void addNewItem() {
        final Item item = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        final Item newItem = itemRepository.save(item);
        assertEquals(item.getName(), newItem.getName());
        assertEquals(item.getDescription(), newItem.getDescription());
        assertNotNull(newItem.getId());
    }

    @Test
    public void updateItem() {
        final Item item = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        final Item newItem = itemRepository.save(item);
        final Item itemNewData = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 2L);
        itemNewData.setId(newItem.getId());
        final Item updatedItem = itemRepository.update(itemNewData);
        assertEquals(itemNewData.getName(), updatedItem.getName());
        assertEquals(itemNewData.getDescription(), updatedItem.getDescription());
        assertNotEquals(itemNewData.getOwner(), updatedItem.getOwner());
    }

    @Test
    public void findByOwner() {
        final Item item = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        itemRepository.save(item);
        final Item item2 = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 2L);
        itemRepository.save(item2);
        List<Item> items = itemRepository.findByOwner(item.getId());
        assertEquals(items.size(), 1);
        Item ownersItem = items.getFirst();
        assertEquals(ownersItem.getId(), item.getId());
    }

    @Test
    public void findByText() {
        final String text = "text_search";
        final Item item = ObjectsFactory.newItem(
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        item.setAvailable(false);
        itemRepository.save(item);
        final Item item2 = ObjectsFactory.newItem(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(), 1L);
        item2.setAvailable(true);
        itemRepository.save(item2);
        final Item item3 = ObjectsFactory.newItem(
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        item3.setAvailable(true);
        itemRepository.save(item3);
        final Item item4 = ObjectsFactory.newItem(ObjectsFactory.newStringValue(), ObjectsFactory.newStringValue(),
                1L);
        item4.setAvailable(true);
        itemRepository.save(item4);

        List<Item> items = itemRepository.findByText(text);
        assertEquals(items.size(), 2);
        assertTrue(items.contains(item2) && items.contains(item3));
    }


}