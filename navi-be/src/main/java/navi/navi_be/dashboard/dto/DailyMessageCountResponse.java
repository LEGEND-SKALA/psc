package navi.navi_be.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyMessageCountResponse {
    private final String date;
    private final Long count;
}