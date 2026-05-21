package project.piuda.domain.patient.presentation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.domain.patient.domain.Patient;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    // Patient엔티티 내의 device.deviceSerial 필드를 DTO의 deviceSerial에 매핑
    @Mapping(source = "device.deviceSerial", target = "deviceSerial")
    PatientResponse toResponseDto(Patient patient);
}