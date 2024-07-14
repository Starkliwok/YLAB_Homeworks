package com.Y_LAB.homework.aspect;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.audit.AuditResult;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Аспект для обработки действий пользователя
 * @author Денис Попов
 * @version 1.0
 */
@Aspect
@RequiredArgsConstructor
@Component
public class AuditableAspect {

    /**Поле ДАО слоя мест для бронирования*/

    private final ReservationPlaceDAO reservationPlaceDAO;

    /**Поле ДАО слоя броней*/
    private final ReservationDAO reservationDAO;

    /**Поле ДАО слоя аудитов*/
    private final AuditService auditService;

    /**Поле ДАО слоя пользователей*/
    private final UserDAO userDAO;

    /**Срез для обнаружения методов помеченных аннотацией {@link com.Y_LAB.homework.annotation.Auditable}*/
    @Pointcut("within(@com.Y_LAB.homework.annotation.Auditable *) || " +
            "execution(@com.Y_LAB.homework.annotation.Auditable * *(..))) && execution(* *(..))")
    public void annotatedByAuditable() {}

    /**
     * Метод обрабатывает методы помеченные аннотацией {@link com.Y_LAB.homework.annotation.Auditable},
     * в случае успешного завершения метода сохраняет аудит пользователя с результатом {@link AuditResult#SUCCESS}
     * если во время выполнения метода выбрасывается исключение, то метод сохраняет аудит пользователя с результатом
     * {@link AuditResult#FAIL}
     * @param jp JoinPoint
     * @return Возвращаемое значение обрабатываемого метода
     * @throws Throwable Пробрасывает исключение если его выбрасывает обрабатываемый метод
     */
    @Around("annotatedByAuditable()")
    public Object logMethod(ProceedingJoinPoint jp) throws Throwable {
        String className = jp.getSignature().getDeclaringTypeName();
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        Object result = null;
        Throwable throwable = null;
        AuditResult auditResult = AuditResult.SUCCESS;
        try {
            result = jp.proceed();
        } catch (Throwable e) {
            auditResult = AuditResult.FAIL;
            throwable = e;
        }
        switch (methodName) {
            case "saveUser" -> {
                String username = args[0].toString();
                Long userId = userDAO.getUserId(username);
                if(userId != null) {
                    auditService.saveAudit(new Audit(userId, className, methodName, auditResult));
                }
            }
            case "getAllUserReservations" -> {
                long userId = (long) args[0];
                auditService.saveAudit(new Audit(userId, className, methodName, auditResult));
            }
            case "getReservation", "deleteReservation" -> {
                long reservationId = (long) args[0];
                long userId = reservationDAO.getReservation(reservationId).getUserId();
                auditService.saveAudit(
                        new Audit(userId, className, methodName + "(" + reservationId + ")", auditResult));
            }
            case "saveReservation" -> {
                if(jp.getSignature().getDeclaringType() == ReservationRequestDTO.class) {
                    long userId = (long) args[1];
                    ReservationRequestDTO reservationRequestDTO = (ReservationRequestDTO) args[0];
                    Reservation reservation = Reservation.builder()
                            .startDate(reservationRequestDTO.getStartDate())
                            .endDate(reservationRequestDTO.getEndDate())
                            .reservationPlace(reservationPlaceDAO.getReservationPlace(reservationRequestDTO.getReservationPlaceId()))
                            .userId(userId).build();
                    long reservationId = reservationDAO.getReservationId(reservation);
                    auditService.saveAudit(
                            new Audit(userId, className, methodName + "(" + reservationId + ")", auditResult));
                }
            }
            case "updateReservation" -> {
                Reservation reservation = (Reservation) args[0];
                auditService.saveAudit(
                        new Audit(reservation.getUserId(), className,
                                methodName + "(" + reservation.getId() + ")", auditResult));
            }
        }
        if (auditResult.equals(AuditResult.FAIL)) {
            throw throwable;
        }
        return result;
    }
}