package java.ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.ru.practicum.shareit.util.NullOrNotBlank;


@Value
@Builder
public class ItemDto {

    Long id;
    @NotBlank(groups = AdvancedConstraint.class)
    @NullOrNotBlank(groups = BasicConstraint.class)
    String name;
    @NotBlank(groups = AdvancedConstraint.class)
    @NullOrNotBlank(groups = BasicConstraint.class)
    String description;
    @NotNull(groups = AdvancedConstraint.class)
    Boolean available;

    Long requestId;

    public interface AdvancedConstraint {
    }

    public interface BasicConstraint {
    }
}
