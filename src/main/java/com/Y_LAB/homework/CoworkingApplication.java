package com.Y_LAB.homework;

import com.Y_LAB.homework.in.user_panel.HomePanel;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import com.Y_LAB.homework.util.init.LiquibaseMigration;

public class CoworkingApplication {
    public static void main(String[] args) {
        LiquibaseMigration.initMigration(ConnectionToDatabase.getConnection());
        HomePanel homePanel = new HomePanel();
        homePanel.printStartPage();
    }
}