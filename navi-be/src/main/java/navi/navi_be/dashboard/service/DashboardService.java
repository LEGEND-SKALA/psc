package navi.navi_be.dashboard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import navi.navi_be.chat.dto.DailyCountProjection;
import navi.navi_be.chat.dto.DepartmentCountProjection;
import navi.navi_be.chat.repository.ChatMessageRepository;
import navi.navi_be.dashboard.dto.DailyMessageCountResponse;
import navi.navi_be.dashboard.dto.DepartmentMessageCountResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ChatMessageRepository chatMessageRepository;

    public List<DailyMessageCountResponse> getDailyMessageCount() {
        List<DailyCountProjection> projections = chatMessageRepository.countMessagesGroupedByDate();
        return projections.stream()
                .map(p -> new DailyMessageCountResponse(p.getDate(), p.getCount()))
                .collect(Collectors.toList());
    }

    public List<DepartmentMessageCountResponse> getDepartmentMessageCount() {
        List<DepartmentCountProjection> projections = chatMessageRepository.countMessagesGroupedByDepartment();

        return projections.stream()
                .map(p -> new DepartmentMessageCountResponse(p.getDepartment(), p.getCount()))
                .collect(Collectors.toList());
    }

    public Long getTotalMessageCount() {
        return chatMessageRepository.countByDeletedAtIsNull();
    }
}
