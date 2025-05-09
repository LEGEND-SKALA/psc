package navi.navi_be.useradmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserRoleRequest {
    
    @NotNull(message = "변경할 역할은 필수입니다.")
    private String role;
}
