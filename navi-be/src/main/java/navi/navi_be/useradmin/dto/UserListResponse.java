package navi.navi_be.useradmin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListResponse {
    private final Long id;
    private final String employeeId;
    private final String name;
    private final String email;
    private final String department;
    private final String role;
    private final Double averageRating;
}
