package com.Y_LAB.homework.aspect;

import com.Y_LAB.homework.dao.ReservationDAO;
import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.dao.implementation.ReservationDAOImpl;
import com.Y_LAB.homework.dao.implementation.ReservationPlaceDAOImpl;
import com.Y_LAB.homework.dao.implementation.UserDAOImpl;
import com.Y_LAB.homework.mapper.ReservationMapper;
import com.Y_LAB.homework.mapper.ReservationMapperImpl;
import com.Y_LAB.homework.model.audit.Audit;
import com.Y_LAB.homework.model.dto.request.ReservationRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.reservation.Reservation;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.service.AuditService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.AuditServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;

import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.SESSION_USER;

@Aspect
public class AuditableAspect {

    private final ReservationPlaceDAO reservationPlaceDAO = new ReservationPlaceDAOImpl();

    private final ReservationDAO reservationDAO = new ReservationDAOImpl();

    private final AuditService auditService = new AuditServiceImpl();

    private final UserDAO userDAO = new UserDAOImpl();

    @Pointcut("within(@com.Y_LAB.homework.annotation.Auditable *) && execution(* com.Y_LAB.homework..* (..))")
    public void annotatedByAuditable() {}

    @Before("annotatedByAuditable()")
    public void logMethod(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();

        switch (methodName) {
            case "saveUser" -> {
                String username = args[0].toString();
                Long userId = userDAO.getUserId(username);
                if(userId != null) {
                    auditService.saveAudit(new Audit(userId, "зарегистрировался под логином " + username));
                }
            }
            case "getAllUserReservations" -> {
                long userId = (long) args[0];
                auditService.saveAudit(new Audit(userId, "посмотрел все свои брони"));
            }
            case "getReservation" -> {
                long reservationId = (long) args[0];
                long userId = reservationDAO.getReservation(reservationId).getUserId();
                auditService.saveAudit(new Audit(userId, "посмотрел свою бронь с id " + reservationId));
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
                    auditService.saveAudit(new Audit(userId, "добавил бронь с id с id " + reservationId));
                }
            }
            case "updateReservation" -> {
                Reservation reservation = (Reservation) args[0];
                auditService.saveAudit(new Audit(reservation.getUserId(),
                        "обновил бронь с id " + reservation.getId()));
            }
            case "deleteReservation" -> {
                long reservationId = (long) args[0];
                long userId = reservationDAO.getReservation(reservationId).getUserId();
                auditService.saveAudit(new Audit(userId, "удалил свою бронь с id " + reservationId));
            }
        }
    }
}