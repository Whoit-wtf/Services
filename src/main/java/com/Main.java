package com;

import java.util.Scanner;

import com.database.ActionStands;
import com.kafka.RebootKafka;
import com.ufoservice.Ufoservice;
import com.update_stands.UpdateStands;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            Config config = new Config();
            config.setPathDB(args[0]);
            config.setPathKey(args[1]);
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
            System.out.println("1. Обновление стенда");
            System.out.println("2. (Process)Обновление стенда с java8 + jetty9");
            System.out.println("3. Запуск Ufoservice");
            System.out.println("4. Перезапуск Kafka");
            System.out.println("5. (Process)Выдача доступов в PG");
            System.out.println("6. Работа с DataBase");
            System.out.println("7. (Process)Установка Kafka");
            System.out.println("8. (Process)Установка Consul");
            System.out.println("0. Выход");
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
                    ActionStands actionStands = new ActionStands();
                    actionStands.main();


            }
        }
        System.out.println("Давай до свидания (>_<)");

    }
}
