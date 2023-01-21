package java.ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.ru.practicum.shareit.client.BaseClient;
import java.ru.practicum.shareit.item.dto.CommentDtoRequest;
import java.ru.practicum.shareit.item.dto.ItemDto;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto item, long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> update(ItemDto item, long itemId, long userId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> get(Long id, long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getByOwner(long userId, long from, long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> createComment(CommentDtoRequest comment, long userId, long itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
