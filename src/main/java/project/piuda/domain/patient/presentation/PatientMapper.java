package project.piuda.domain.patient.presentation;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.domain.patient.domain.Patient;

@Mapper(componentModel = "spring") // Spring Bean으로 등록
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientResponse toResponseDto(Patient patient);
}