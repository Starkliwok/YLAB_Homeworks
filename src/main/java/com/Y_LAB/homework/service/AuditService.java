package com.Y_LAB.homework.service;

import com.Y_LAB.homework.model.audit.Audit;

import java.util.List;

/**
 * Интерфейс описывает сервис для получения аудитов
 * @author Денис Попов
 * @version 2.0
 */
public interface AuditService {

    /**
     * Метод для получения коллекции всех аудитов
     * @return коллекция всех броней
     */
    List<Audit> getAllAudits();

    /**
     * Метод для получения аудита по указанному id
     * @param id уникальный идентификатор аудита
     * @return объект аудита
     */
    Audit getAudit(long id);

    /**
     * Метод для сохранения аудита
     * @param audit объект аудита
     */
    void saveAudit(Audit audit);
}
