package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.audit.Audit;

import java.util.List;

public interface AuditService {

    List<Audit> getAllAudits();

    Audit getAudit(long id);

    void saveAudit(Audit audit);
}
