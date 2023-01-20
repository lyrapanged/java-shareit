package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService service;
    private final UserRepository userRepository;

    @Test
    void create() {

        User user = userRepository.save(new User(1L, "name1", "email1@gmail.com"));
        ItemDto itemDto = makeItemDto("item_name", "text", true);
        service.create(itemDto, user.getId());

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i WHERE i.name = :itemName AND i.owner= :owner",
                Item.class);
        Item item = query.setParameter("itemName", itemDto.getName())
                .setParameter("owner", user).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getOwner(), equalTo(user));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }
}
