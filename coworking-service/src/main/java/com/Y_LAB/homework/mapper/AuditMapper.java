package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.model.dto.response.AuditResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Интерфейс для преобразования сущностей аудитов в DTO Response объект
 * @author Денис Попов
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface AuditMapper {

    /**
     * Преобразует объект аудита в DTO Response объект аудита
     * @param audit Аудит
     * @return DTO Response объект аудита
     */
    AuditResponseDTO toAuditResponseDTO(Audit audit);

    /**
     * Преобразует список объектов аудита в список DTO Response объектов аудита
     * @param audits Список аудитов
     * @return Список DTO Response объектов аудита
     */
    List<AuditResponseDTO> toAuditResponseDTOList(List<Audit> audits);
}
