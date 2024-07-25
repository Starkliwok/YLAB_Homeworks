package com.Y_LAB.homework.aop.dao.impl;

import com.Y_LAB.homework.aop.dao.AuditDAO;
import com.Y_LAB.homework.aop.model.audit.Audit;
import com.Y_LAB.homework.aop.model.audit.AuditResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.aop.dao.constants.SQLAuditConstants.*;

/**
 * Класс ДАО слоя для взаимодействия с аудитами
 * @author Денис Попов
 * @version 1.0
 */
@Repository
public class AuditDAOImpl implements AuditDAO {

    /** Поле для подключения к базе данных*/
    private final Connection connection;

    @Autowired
    public AuditDAOImpl(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }

    @Override
    public List<Audit> getAllAudits() {
        List<Audit> audits = new ArrayList<>();
        Audit audit;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_GET_ALL_ORDER_BY_ID);
            preparedStatement.execute();
            ResultSet auditResultSet = preparedStatement.getResultSet();
            while (auditResultSet.next()) {
                long id = auditResultSet.getLong(1);
                Long userID = auditResultSet.getLong(2);
                LocalDateTime date = auditResultSet.getTimestamp(3).toLocalDateTime();
                String className = auditResultSet.getString(4);
                String methodName = auditResultSet.getString(5);
                String auditResult = auditResultSet.getString(6);
                AuditResult result = auditResult.equals(AuditResult.FAIL.toString()) ? AuditResult.FAIL : AuditResult.SUCCESS;
                audit = new Audit(id, userID, date, className, methodName, result);
                audits.add(audit);
            }
            preparedStatement.close();
            auditResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return audits;
    }

    @Override
    public Audit getAudit(long id) {
        Audit audit = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_FIND_BY_ID);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            ResultSet auditResultSet = preparedStatement.getResultSet();
            if (auditResultSet.next()) {
                Long userID = auditResultSet.getLong(1);
                LocalDateTime date = auditResultSet.getTimestamp(2).toLocalDateTime();
                String className = auditResultSet.getString(3);
                String methodName = auditResultSet.getString(4);
                String auditResult = auditResultSet.getString(5);
                AuditResult result = auditResult.equals(AuditResult.FAIL.toString()) ? AuditResult.FAIL : AuditResult.SUCCESS;
                audit = new Audit(id, userID, date, className, methodName, result);
            }
            preparedStatement.close();
            auditResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return audit;
    }

    @Override
    public void saveAudit(Audit audit) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_FULL_INSERT);
            preparedStatement.setLong(1, audit.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(audit.getLocalDateTime()));
            preparedStatement.setString(3, audit.getClassName());
            preparedStatement.setString(4, audit.getMethodName());
            preparedStatement.setString(5, audit.getResult().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }
}
