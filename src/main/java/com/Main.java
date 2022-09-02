package com;

import com.database.ActionStands;
import com.kafka.RebootKafka;
import com.ufoservice.Ufoservice;
import com.update_stands.UpdateStands;

import java.util.Scanner;


public class Main {
    public static Config config = new Config();

    public static void main(String[] args) {

        if (args.length >= 3) {
            config.setPathDB(args[0]);
            config.setPathKey(args[1]);
            config.setUser(args[2]);
        }
        Scanner scan = new Scanner(System.in);
        int x = 0;
        String s = "";
        while (!"0".equals(s)) {
            System.out.println("" +
                    "░░░░░░░▄░░▄░░░░░░░░░░░░░░░░░░\n" +
                    "░░░░░░██▄██░░░░░░░░░░░░░░░░░░\n" +
                    "░░░░▄██████░░░░░░░░░░░░░░░░░░\n" +
                    "░░░▄█████▄██▄▄▄▄░░░░░░░░▄▄▄▄▄\n" +
                    "░░████████████▀░░░░░░░▄█████░\n" +
                    "░▄████████▀▀▀░░░░░░░▄██████░░\n" +
                    "░█████████▄▄░░░░░░░░███████░░\n" +
                    "░░██████████▄░░░░░░░███████░░\n" +
                    "░░▀████████████░░░░░███████░░\n" +
                    "░░░█████████████▄░░░███████░░\n" +
                    "░░░██████████████▄░░███████░░\n" +
                    "░░░███▀███████████░░███████░░\n" +
                    "░░▄██░░░███████████░█████▀░░░\n" +
                    "███▀░░████████████▀██▀▀▀░░░░░");
            System.out.println("1. Обновление стенда\n" +
                    "2. (in process)Обновление стенда с java8 + jetty9\n" +
                    "3. Запуск Ufoservice\n" +
                    "4. Перезапуск Kafka\n" +
                    "5. (in process)Выдача доступов в PG\n" +
                    "6. (in process)Установка Kafka\n" +
                    "7. (in process)Установка Consul\n" +
                    "9. Работа с Базой приложения\n" +
                    "0. Выход");
            s = scan.next();
            try {
                x = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод");
            }

            switch (x) {
                case 1:
                    UpdateStands updateStands = new UpdateStands();
                    updateStands.update();
                    break;
                case 2:
                    // вызов метода 2
                    break;
                case 3:
                    Ufoservice ufoservice = new Ufoservice();
                    ufoservice.runUfoservice();
                    break;
                case 4:
                    RebootKafka rebootKafka = new RebootKafka();
                    rebootKafka.runKafka();
                    break;
                case 5:
                    // вызов метода 5
                    break;
                case 6:
                    //code
                    break;
                case 7:
                    //code
                    break;
                case 8:
                    //code
                    break;
                case 9:
                    ActionStands actionStands = new ActionStands();
                    actionStands.main();

            }
        }
        System.out.println("Goodbye (>_<)");

    }
}
