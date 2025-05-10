package navi.navi_be.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentMessageCountResponse {
    private final String department;
    private final Long count;
}
