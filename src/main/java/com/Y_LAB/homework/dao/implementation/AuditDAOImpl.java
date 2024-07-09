package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.AuditDAO;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.dao.constants.SQLConstants.*;

/**
 * Класс ДАО слоя для взаимодействия с аудитами
 * @author Денис Попов
 * @version 1.0
 */
@AllArgsConstructor
public class AuditDAOImpl implements AuditDAO {

    private final Connection connection;

    public AuditDAOImpl() {
        connection = ConnectionToDatabase.getConnection();
    }

    /** {@inheritDoc}*/
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
                String action = auditResultSet.getString(4);
                audit = new Audit(id, userID, date, action);
                audits.add(audit);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return audits;
    }

    /** {@inheritDoc}*/
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
                String action = auditResultSet.getString(3);
                audit = new Audit(id, userID, date, action);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return audit;
    }

    /** {@inheritDoc}*/
    @Override
    public void saveAudit(Audit audit) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(AUDIT_FULL_INSERT);
            preparedStatement.setLong(1, audit.getUserId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(audit.getLocalDateTime()));
            preparedStatement.setString(3, audit.getAction());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }
}
