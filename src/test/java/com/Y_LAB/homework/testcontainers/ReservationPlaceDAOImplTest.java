package com.Y_LAB.homework.testcontainers;

import com.Y_LAB.homework.dao.ReservationPlaceDAO;
import com.Y_LAB.homework.finder.ObjectFinderForTests;
import com.Y_LAB.homework.model.reservation.ConferenceRoom;
import com.Y_LAB.homework.model.reservation.ReservationPlace;
import com.Y_LAB.homework.model.reservation.Workplace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestContainerConfig.class})
class ReservationPlaceDAOImplTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ReservationPlaceDAO reservationPlaceDAO;

    private ReservationPlace reservationPlace;

    private ReservationPlace reservationPlace1;

    private ReservationPlace reservationPlace2;

    @BeforeEach
    void init() {
        reservationPlace = new Workplace("009", 543, 34, 20);
        reservationPlace1 = new Workplace(3266,"006", 12.2, 57, 22);
        reservationPlace2 = new ConferenceRoom(176,"7", 43.2, 87, 34);
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        reservationPlaceDAO.saveReservationPlace(reservationPlace1);
        reservationPlaceDAO.saveReservationPlace(reservationPlace2);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, dataSource);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace1, dataSource);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace2, dataSource);
    }

    @AfterEach
    void clear() {
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace1.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace2.getId());
    }

    @Test
    @DisplayName("Получение всех мест для бронирования из бд")
    void getAllReservationPlaces() {
        int expected = reservationPlaceDAO.getAllReservationPlaces().size() + 1;
        reservationPlaceDAO.saveReservationPlace(reservationPlace);

        int actual = reservationPlaceDAO.getAllReservationPlaces().size();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение всех мест для бронирования по типу из бд")
    void getAllReservationPlacesByTypes() {
        List<ReservationPlace> workplaces = reservationPlaceDAO.getAllReservationPlacesByTypes(new Workplace());
        List<ReservationPlace> conferenceRooms = reservationPlaceDAO.getAllReservationPlacesByTypes(new ConferenceRoom());

        for(ReservationPlace actual : workplaces) {
            assertThat(actual).isExactlyInstanceOf(Workplace.class);
        }
        for(ReservationPlace actual : conferenceRooms) {
            assertThat(actual).isExactlyInstanceOf(ConferenceRoom.class);
        }
    }

    @Test
    @DisplayName("Получение места для бронирования из бд")
    void getReservationPlace() {
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(reservationPlace);
    }

    @Test
    @DisplayName("Добавление места для бронирования в бд")
    void saveReservationPlace() {
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.saveReservationPlace(reservationPlace);
        ObjectFinderForTests.setReservationPlaceIdFromDB(reservationPlace, dataSource);
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isNull();
        assertThat(actual2).isEqualTo(reservationPlace);
    }

    @Test
    @DisplayName("Обновление места для бронирования в бд")
    void updateReservationPlace() {
        String newName = "002";

        String actual1 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();
        reservationPlace.setName(newName);
        reservationPlaceDAO.updateReservationPlace(reservationPlace);
        String actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId()).getName();

        assertThat(actual1).isNotEqualTo(newName);
        assertThat(actual2).isEqualTo(newName);
    }

    @Test
    @DisplayName("Удаление места для бронирования из бд")
    void deleteReservationPlace() {
        ReservationPlace actual = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());
        reservationPlaceDAO.deleteReservationPlace(reservationPlace.getId());
        ReservationPlace actual2 = reservationPlaceDAO.getReservationPlace(reservationPlace.getId());

        assertThat(actual).isEqualTo(reservationPlace);
        assertThat(actual2).isNull();
    }
}