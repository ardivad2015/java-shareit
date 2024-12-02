package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestMapperImpl;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Captor
    ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    public void addNewItemRequest_whenUserFound_callRepository() {
        final ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userService, userMapper, itemRequestMapper, itemMapper);
        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");

        final User author = ObjectsFactory.newUser("email", "name");
        author.setId(1L);

        when(userService.getById(1L)).thenReturn(userMapper.toUserDto(author));

        final ItemRequestDto actualItemRequest = itemRequestService.addNewItemRequest(itemRequestDto, 1L);

        verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());
        final ItemRequest savedItemRequest = itemRequestArgumentCaptor.getValue();

        verify(itemRequestRepository, Mockito.times(1))
                .save(any(ItemRequest.class));
        assertEquals(savedItemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(savedItemRequest.getAuthor().getId(), author.getId());
    }

    @Test
    public void addNewItemRequest_whenUserNotFound_NotFoundExceptionThrown() {
        final ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userService, userMapper, itemRequestMapper, itemMapper);
        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");

        when(userService.getById(1L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.addNewItemRequest(itemRequestDto, 1L));
        verify(itemRequestRepository, Mockito.never())
                .save(any(ItemRequest.class));
    }

    @Test
    public void getByAuthor_whenUserFound_returnItemRequestWithItems() {
        final ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userService, userMapper, itemRequestMapper, itemMapper);

        final ItemRequest itemRequest = new ItemRequest();
        final User author = ObjectsFactory.newUser("email", "name");
        final Item item1 = ObjectsFactory.newItem("item1", "item1");
        final User owner1 = ObjectsFactory.newUser("emailowner1", "owner1");
        final Item item2 = ObjectsFactory.newItem("item2", "item2");
        final User owner2 = ObjectsFactory.newUser("emailowner2", "owner2");

        author.setId(1L);
        itemRequest.setId(1L);
        itemRequest.setAuthor(author);
        item1.setId(1L);
        item1.setOwner(owner1);
        item1.setItemRequest(itemRequest);
        item2.setId(2L);
        item2.setOwner(owner2);
        item2.setItemRequest(itemRequest);

        when(itemRequestRepository.findAllByAuthorIdWithAuthorEagerly(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByItemRequestIdIn(Set.of(1L))).thenReturn(List.of(item1, item2));

        List<ItemRequestResponseDto> actualItemResponse = itemRequestService.getByAuthor(1L);

        assertEquals(actualItemResponse.size(), 1);
        List<IdBasedEntityDto> actualItems = actualItemResponse.getFirst().getItems();
        assertEquals(actualItemResponse.getFirst().getId(), 1L);
        assertEquals(actualItems.size(), 2);
    }

    @Test
    public void getById_whenCall_returnItemRequestWithItems() {
        final ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userService, userMapper, itemRequestMapper, itemMapper);

        final ItemRequest itemRequest = new ItemRequest();
        final User author = ObjectsFactory.newUser("email", "name");
        final Item item1 = ObjectsFactory.newItem("item1", "item1");
        final User owner1 = ObjectsFactory.newUser("emailowner1", "owner1");
        final Item item2 = ObjectsFactory.newItem("item2", "item2");
        final User owner2 = ObjectsFactory.newUser("emailowner2", "owner2");

        author.setId(1L);
        itemRequest.setId(1L);
        itemRequest.setAuthor(author);
        item1.setId(1L);
        item1.setOwner(owner1);
        item1.setItemRequest(itemRequest);
        item2.setId(2L);
        item2.setOwner(owner2);
        item2.setItemRequest(itemRequest);

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequestId(1L)).thenReturn(List.of(item1, item2));

       ItemRequestResponseDto actualItemResponse = itemRequestService.getById(1L);

        assertEquals(actualItemResponse.getId(), 1);
        List<IdBasedEntityDto> actualItems = actualItemResponse.getItems();
        assertEquals(actualItems.size(), 2);
    }

    @Test
    public void getAll_whenCall_repositoryCall() {
        final ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userService, userMapper, itemRequestMapper, itemMapper);

        List<ItemRequestDto> actualItemRequestsDto = itemRequestService.getAll();
        verify(itemRequestRepository, times(1)).findAll(any(Sort.class));
    }
}