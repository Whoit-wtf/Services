package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ConnectionSsh;

public class UpdateStands {

    String host = null;
    String host2 = null;
    public UpdateStands() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Введите адрес первой ноды: ");
            this.host = reader.readLine().toString();
            System.out.println("Введите адрес второй ноды: ");
            System.out.println("(Если второй ноды нет,оставьте поле пустым)");
            String host2 = reader.readLine().toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runUpdate(){

        if (host2 == null) update(host); else update(host,host2);

    }

    private void update(String host){
        String command ="";
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        String result[] = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: "+ result[1]);

    }

    private void update(String host, String host2){
        String command ="";
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        String result[] = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: "+ result[1]);

    }
}
