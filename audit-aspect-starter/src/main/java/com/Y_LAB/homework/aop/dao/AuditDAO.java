package com.Y_LAB.homework.aop.dao;

import com.Y_LAB.homework.aop.model.audit.Audit;

import java.util.List;

/**
 * Интерфейс описывает ДАО слой для взаимодействия с аудитами
 * @author Денис Попов
 * @version 1.0
 */
public interface AuditDAO {

    /**
     * Метод для получения коллекции всех аудитов из базы данных
     * @return коллекция всех аудитов
     */
    List<Audit> getAllAudits();

    /**
     * Метод для получения аудита по переданному идентификатору из базы данных
     * @param id уникальный идентификатор аудита
     * @return найденный аудит
     */
    Audit getAudit(long id);

    /**
     * Метод для сохранения аудита в базе данных
     * @param audit объект аудита
     */
    void saveAudit(Audit audit);
}
