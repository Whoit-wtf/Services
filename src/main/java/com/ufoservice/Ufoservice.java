package com.ufoservice;

import com.ConnectionSsh;

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
        String command ="";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host);

        result = connectionSsh.runCommand(command);



    }

}
