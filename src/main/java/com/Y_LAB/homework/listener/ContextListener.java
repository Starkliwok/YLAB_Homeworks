package com.Y_LAB.homework.listener;

import com.Y_LAB.homework.service.*;
import com.Y_LAB.homework.service.implementation.*;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import static com.Y_LAB.homework.in.controller.constants.ControllerContextConstants.*;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnection());
        ServletContext servletContext = event.getServletContext();

        UserService userService = new UserServiceImpl();
        servletContext.setAttribute(USER_SERVICE, userService);

        ReservationPlaceService reservationPlaceService = new ReservationPlaceServiceImpl();
        servletContext.setAttribute(RESERVATION_PLACE_SERVICE, reservationPlaceService);

        FreeReservationSlotService freeReservationSlotService = new FreeReservationSlotServiceImpl();
        servletContext.setAttribute(FREE_RESERVATION_SLOT_SERVICE, freeReservationSlotService);

        ReservationService reservationService = new ReservationServiceImpl();
        servletContext.setAttribute(RESERVATION_SERVICE, reservationService);

        AuditService auditService = new AuditServiceImpl();
        servletContext.setAttribute(AUDIT_SERVICE, auditService);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(new JavaTimeModule());
        servletContext.setAttribute(OBJECT_MAPPER, objectMapper);
    }
}
