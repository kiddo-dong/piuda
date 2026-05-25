package project.piuda.domain.calendar.domain;

public enum CalendarCategory {
    OUTING, // 외출/동행 (병원, 미용실, 산책 등 어르신과 밖으로 나가는 일)
    VISIT,  // 방문/돌봄 (방문요양보호사 오는 날, 다른 가족이 교대하러 오는 날 등)
    SUPPLY, // 용품/약물 (치매 약 타오는 날, 기저귀/영양제 등 돌봄 용품 구매일)
    EVENT,  // 행사/기념 (어르신 생신, 가족 모임 등 중요한 가족 이벤트)
    ETC     // 기타 일반 일상 스케줄
}