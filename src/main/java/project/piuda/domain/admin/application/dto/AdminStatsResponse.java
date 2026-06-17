package project.piuda.domain.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsResponse {
    private final long totalUsers;
    private final long protectorCount;
    private final long caregiverCount;
    private final long medicalStaffCount;
    private final long totalPosts;
    private final long totalDevices;
}
