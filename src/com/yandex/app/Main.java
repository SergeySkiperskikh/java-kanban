package com.yandex.app;

import com.yandex.app.service.*;


public class Main {
    private static MainMenu mainMenu;

    public static void main(String[] args) {
        mainMenu = MainMenu.getInstance();
        mainMenu.choseTask();

    }


}
