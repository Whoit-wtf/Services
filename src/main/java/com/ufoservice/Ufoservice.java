package com.ufoservice;

import com.ConnectionSsh;
import com.ResultCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ufoservice {

    String host;

    // Конструктор с инициализацией переменных
    public Ufoservice() {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Введите адрес ноды: ");
            this.host = reader.readLine();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void runUfoservice() {
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.println("Ищем папку ufoservice...");
        result = connectionSsh.runCommand("sudo find /oracle/ -type d -name \"ufoservice\"");
        System.out.println("Найденные директории:\n" + result.getOutLog());
        result.setOutLog(result.getOutLog());
        String[] dirServicesList = result.getOutLog().split(" ");

        if (dirServicesList.length >= 2) {
            System.out.println("начинаем перебор...");
            for (int i = 0; i <= dirServicesList.length; i++) {
                String dirService = dirServicesList[i];
                System.out.println("Выбрана директория: " + dirService);
                System.out.println("Попытка запуска...");
                run(dirService);
                System.out.println("1.Сервис запустился\n2.Сервис не запустился\n");
                int input = 0;
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(System.in));
                    input = Integer.parseInt(reader.readLine());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (input == 1) {
                    System.out.println("ufoservice запущен, если сервис не поднялся напишите в скуп spawn5525");
                    break;
                }
            }

        } else {
            run(dirServicesList[0]);
            System.out.println("ufoservice запущен, если сервис не поднялся напишите в скуп spawn5525");
        }

    }

    void run(String dirService) {
        ResultCommand result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.println("Определяем владельца...");
        result = connectionSsh.runCommand("sudo ls -l " + dirService + " | awk 'NR==2{{print $3}}'");
        String owner = result.getOutLog().replace("\n", "");
        System.out.println("Владелец: " + owner);
        System.out.println("Становимся владельцем и запускаем ufoservice...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + dirService +
                "\nnohup ./ufoservice.sh service-start &" +
                "\nEOF");
    }
}
