package com.Y_LAB.homework.dao;

import com.Y_LAB.homework.model.audit.Audit;

import java.util.List;

public interface AuditDAO {

    List<Audit> getAllAudits();

    Audit getAudit(long id);

    void saveAudit(Audit audit);
}
