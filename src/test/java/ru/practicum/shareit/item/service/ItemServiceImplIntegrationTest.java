package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;

    @Test
    void findItemById() {

        User user = userRepository.save(new User(1L, "name1", "email1@gmail.com"));
        ItemRequestDtoRequest requestDto = toItemDto("description", user.getId(), LocalDateTime.now());
        itemRequestService.create(requestDto, user.getId());

        TypedQuery<ItemRequest> query = em.createQuery("SELECT r FROM ItemRequest r WHERE r.description = " +
                ":description and r.requester.id = :userId", ItemRequest.class);
        ItemRequest request = query.setParameter("description", requestDto.getDescription())
                .setParameter("userId", user.getId()).getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getRequester(), equalTo(user));
    }

    private ItemRequestDtoRequest toItemDto(String description, Long requesterId, LocalDateTime created) {
        return ItemRequestDtoRequest.builder()
                .description(description)
                .requesterId(requesterId)
                .created(created)
                .build();
    }
}
