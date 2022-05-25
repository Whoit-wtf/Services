package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ConnectionSsh;

public class UpdateStands {

    String host = null;
    String host2 = null;
    // Конструктор с инициализацией переменных
    public UpdateStands() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Введите адрес первой ноды: ");
            this.host = reader.readLine();
            System.out.println("Введите адрес второй ноды: ");
            System.out.println("(Если второй ноды нет,оставьте поле пустым)");
            this.host2 = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //точка входа
    public void runUpdate(){
        //переопределяем метод в зависимости от кол-ва нод
        if (host2 == null) update(host); else update(host,host2);

    }

    private void update(String host){
        String command ="";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host);

        result = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: "+ result[1]);

    }

    private void update(String host, String host2){
        String command ="";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host);

        result = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: "+ result[1]);

    }
}
