package navi.navi_be.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T>{
    private int code;
    private String message;
    private T body;
}
