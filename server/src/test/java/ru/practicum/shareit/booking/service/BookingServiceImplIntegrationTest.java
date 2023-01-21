package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingService service;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void createTest() {
        User user = userRepository.save(new User(1L, "name1", "email1@gmail.com"));
        User booker = userRepository.save(new User(30L, "booker", "email133@gmail.com"));
        Item item = itemRepository.save(new Item(1L, "username1", "description1", true,
                user, null));
        BookingDtoRequest bookingDto = toBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        service.create(bookingDto, booker.getId());

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b WHERE b.item.id = :itemId",
                Booking.class);
        Booking booking = query.setParameter("itemId", bookingDto.getItemId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker(), equalTo(booker));
    }

    private BookingDtoRequest toBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return BookingDtoRequest.builder()
                .start(start)
                .end(end)
                .itemId(itemId).build();
    }
}
