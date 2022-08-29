package com.kafka;

import com.ConnectionSsh;
import com.ResultCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RebootKafka {

    String host;

    public RebootKafka() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Введите адрес ноды: ");
            this.host = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runKafka() {
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.println("Останавливаем kafka-zookeeper.service...");
        result = connectionSsh.runCommand("sudo systemctl stop kafka-zookeeper.service");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return;
        }
        System.out.println("Останавливаем kafka.service...");
        result = connectionSsh.runCommand("sudo systemctl stop kafka.service");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return;
        }
        System.out.println("Запускаеи kafka.service...");
        result = connectionSsh.runCommand("sudo systemctl start kafka-zookeeper.service");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return;
        }
        System.out.println("Запускаем kafka.service...");
        result = connectionSsh.runCommand("sudo systemctl start kafka.service");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return;
        }
        System.out.println("Ребут кафки выполнен");
    }
}
