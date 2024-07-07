package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.response.AuditResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AuditMapper {
    AuditResponseDTO toAuditResponseDTO(Audit audit);

    List<AuditResponseDTO> toAuditResponseDTOList(List<Audit> audits);
}
