package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId, Sort sort);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.description) LIKE %?1% OR LOWER(i.name) LIKE %?1%)" +
            "AND i.available=TRUE")
    List<Item> search(String text, Pageable pageable);

    List<Item> findAllByItemRequestIn(List<ItemRequest> itemRequests);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);
}
