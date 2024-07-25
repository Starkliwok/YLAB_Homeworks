package com.Y_LAB.homework.aop.service.impl;

import com.Y_LAB.homework.aop.dao.AuditDAO;
import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Класс для получения аудитов
 * @author Денис Попов
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    /**Поле ДАО слоя для взаимодействия с местами для бронирований*/
    private final AuditDAO auditDAO;

    @Override
    public List<Audit> getAllAudits() {
        return auditDAO.getAllAudits();
    }

    @Override
    public Audit getAudit(long id) {
        return auditDAO.getAudit(id);
    }

    @Override
    public void saveAudit(Audit audit) {
        auditDAO.saveAudit(audit);
    }
}