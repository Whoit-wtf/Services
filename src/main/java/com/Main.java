package com;

import java.util.Scanner;

import com.database.ActionStands;
import com.kafka.RebootKafka;
import com.ufoservice.Ufoservice;
import com.update_stands.UpdateStands;




public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        if (args.length >= 2) {
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
            System.out.println("1. Обновление стенда\n" +
                    "2. (Process)Обновление стенда с java8 + jetty9\n" +
                    "3. Запуск Ufoservice\n" +
                    "4. Перезапуск Kafka\n" +
                    "5. (Process)Выдача доступов в PG\n" +
                    "6. Работа с DataBase\n" +
                    "7. (Process)Установка Kafka\n" +
                    "8. (Process)Установка Consul\n" +
                    "\0. Выход");
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
