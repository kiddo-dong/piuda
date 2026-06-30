package project.piuda.domain.carejudgment.domain;

public enum UrgencyLevel {
    NORMAL,             // 일반
    NEEDS_OBSERVATION,  // 관찰필요
    IMMEDIATE           // 즉시공유 (보호자에게 즉시 FCM 알림)
}
