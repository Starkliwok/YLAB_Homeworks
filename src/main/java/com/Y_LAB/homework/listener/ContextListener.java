package com.Y_LAB.homework.listener;

import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.ReservationService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.ReservationServiceImpl;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import static com.Y_LAB.homework.in.servlet.constants.ControllerContextConstants.*;

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

        ReservationService reservationService = new ReservationServiceImpl();
        servletContext.setAttribute(RESERVATION_SERVICE, reservationService);
    }
}
