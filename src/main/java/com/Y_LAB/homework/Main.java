package com.Y_LAB.homework;

import com.Y_LAB.homework.entity.reservation.ConferenceRoom;
import com.Y_LAB.homework.entity.reservation.Workplace;
import com.Y_LAB.homework.entity.roles.Admin;
import com.Y_LAB.homework.entity.roles.User;
import com.Y_LAB.homework.in.user_panel.HomePanel;
import com.Y_LAB.homework.service.ReservationPlaceService;
import com.Y_LAB.homework.service.UserService;
import com.Y_LAB.homework.service.implementation.ReservationPlaceServiceImpl;
import com.Y_LAB.homework.service.implementation.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        HomePanel homePanel = new HomePanel();

        User admin = new Admin("root", "root");
        User user = new User("user", "user");
        userService.addUserToUserSet(admin);
        userService.addUserToUserSet(user);
        createReservationPlaces();

        homePanel.printStartPage();
    }

    private static void createReservationPlaces() {
        ReservationPlaceService reservationPlaceService = new ReservationPlaceServiceImpl();

        reservationPlaceService.addReservationPlace(new Workplace("001", 1.74, 17.4));
        reservationPlaceService.addReservationPlace(new Workplace("002", 1.21, 12.1));
        reservationPlaceService.addReservationPlace(new Workplace("003", 1.75, 17.5));
        reservationPlaceService.addReservationPlace(new Workplace("004", 1.63, 16.3));
        reservationPlaceService.addReservationPlace(new Workplace("005", 1.32, 13.2));
        reservationPlaceService.addReservationPlace(new Workplace("006", 1.27, 12.7));

        reservationPlaceService.addReservationPlace(new ConferenceRoom("001", 174, 1740, 50));
        reservationPlaceService.addReservationPlace(new ConferenceRoom("002", 121, 1210, 35));
        reservationPlaceService.addReservationPlace(new ConferenceRoom("003", 175, 1750, 52));
        reservationPlaceService.addReservationPlace(new ConferenceRoom("004", 163, 1630, 45));
        reservationPlaceService.addReservationPlace(new ConferenceRoom("005", 132, 1320, 37));
        reservationPlaceService.addReservationPlace(new ConferenceRoom("006", 127, 1270, 36));
    }
}