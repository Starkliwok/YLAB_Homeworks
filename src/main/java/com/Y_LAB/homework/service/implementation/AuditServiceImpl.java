package com.Y_LAB.homework.service.implementation;

import com.Y_LAB.homework.dao.AuditDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.implementation.AuditDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.service.AuditService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AuditServiceImpl implements AuditService {

    /**Поле ДАО слоя для взаимодействия с местами для бронирований*/
    private final AuditDAO auditDAO;

    public AuditServiceImpl() {
        auditDAO = new AuditDAOImpl();
    }

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
