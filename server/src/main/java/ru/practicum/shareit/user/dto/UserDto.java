package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.util.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Value
@Builder
public class UserDto {
    @Null(groups = BasicConstraint.class)
    Long id;

    @NullOrNotBlank(groups = BasicConstraint.class)
    @NotBlank(groups = AdvancedConstraint.class)
    String name;
    @Email(groups = {AdvancedConstraint.class, BasicConstraint.class})
    @NotBlank(groups = AdvancedConstraint.class)
    @NullOrNotBlank(groups = BasicConstraint.class)
    String email;


    public interface AdvancedConstraint {
    }

    public interface BasicConstraint {
    }
}
