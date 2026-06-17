package project.piuda.domain.user.domain;

public enum Role {
    // 보호자(보호자 및 가족), 간병인(요양보호사, 간병인 등등), 의료진(의료관련 사용자)
    PROTECTOR, CAREGIVER, MEDICAL_STAFF,
    // 관리자 — DB에서 직접 role 컬럼을 'ADMIN'으로 변경하여 부여
    ADMIN
}