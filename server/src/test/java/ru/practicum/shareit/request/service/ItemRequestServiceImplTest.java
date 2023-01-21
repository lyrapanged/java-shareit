package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponseWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;
    ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                userRepository, itemRepository);
    }

    @Test
    void createTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .id(1L)
                .description("text")
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        when(itemRequestRepository.save(any())).thenReturn(ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService
                .create(itemRequestDtoRequest, itemRequestDtoRequest.getRequesterId());
        assertEquals(itemRequestDtoRequest.getId(), itemRequestDtoResponse.getId());
        assertEquals(itemRequestDtoRequest.getDescription(), itemRequestDtoResponse.getDescription());
    }

    @Test
    void createUserNotExist() {
        User user = new User(1L, "name1", "email1@mail");
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .id(1L)
                .description("text")
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        when(itemRequestRepository.save(any())).thenReturn(ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user));
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDtoRequest, 20));
        assertEquals("This user with id=20 does not exist", e.getMessage());
    }

    @Test
    void getByOwnerWrongUser() {
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemRequestService.getByOwner(20));
        assertEquals("This user with id=20 does not exist", e.getMessage());
    }

    @Test
    void getByOwnerIsOk() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(20L, "name24", "email32@mail");
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .id(1L)
                .description("text")
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        Item item = new Item(1L, "item_name", "item_name", true, user2,
                ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(anyLong(), any())).thenReturn(List.of(ItemRequestMapper
                .toItemRequest(itemRequestDtoRequest, user)));
        when(itemRepository.findAllByItemRequestIn(any())).thenReturn(List.of(item));
        List<ItemRequestDtoResponseWithItems> foo = itemRequestService.getByOwner(user.getId());
        assertEquals(itemRequestDtoRequest.getId(), foo.get(0).getId());
        assertEquals(itemRequestDtoRequest.getDescription(), foo.get(0).getDescription());
        assertEquals(item.getId(), foo.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), foo.get(0).getItems().get(0).getName());
    }

    @Test
    void getAllTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(20L, "name24", "email32@mail");
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .id(1L)
                .description("text")
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        Item item = new Item(1L, "item_name", "item_name", true, user2,
                ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIsNot(any(), any())).thenReturn(List.of(ItemRequestMapper
                .toItemRequest(itemRequestDtoRequest, user)));
        when(itemRepository.findAllByItemRequestIn(any())).thenReturn(List.of(item));
        List<ItemRequestDtoResponseWithItems> foo = itemRequestService.getAll(user.getId(), Pageable.unpaged());
        assertEquals(itemRequestDtoRequest.getId(), foo.get(0).getId());
        assertEquals(itemRequestDtoRequest.getDescription(), foo.get(0).getDescription());
        assertEquals(item.getId(), foo.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), foo.get(0).getItems().get(0).getName());
    }

    @Test
    void getTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(20L, "name24", "email32@mail");
        ItemRequestDtoRequest itemRequestDtoRequest = ItemRequestDtoRequest.builder()
                .id(1L)
                .description("text")
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
        Item item = new Item(1L, "item_name", "item_name", true, user2,
                ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(ItemRequestMapper.toItemRequest(itemRequestDtoRequest, user2)));
        when(itemRepository.findAllByItemRequest(any())).thenReturn(List.of(item));
        ItemRequestDtoResponseWithItems itemDtoWithItems = itemRequestService.get(user2.getId(),
                itemRequestDtoRequest.getRequesterId());
        assertEquals(itemRequestDtoRequest.getId(), itemDtoWithItems.getId());
        assertEquals(itemRequestDtoRequest.getDescription(), itemDtoWithItems.getDescription());
        assertEquals(item.getId(), itemDtoWithItems.getItems().get(0).getId());
        assertEquals(item.getName(), itemDtoWithItems.getItems().get(0).getName());
    }
}