package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Constants {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    public static final Sort SORT_BY_START_ASC = Sort.by("start");

    public static final Sort SORT_BY_ID_DESC = Sort.by(Sort.Direction.DESC, "id");

    public static final Sort SORT_BY_ID_ASC = Sort.by("id");
}
