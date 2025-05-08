package navi.navi_be.document.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentListResponse {
    private final List<DocumentResponse> documents;
}
