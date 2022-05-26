package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ConnectionSsh;

public class UpdateStands {

    String host = null;
    String host2 = null;
    String port = null;
    int type;

    // Конструктор с инициализацией переменных
    public UpdateStands() {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Выберите тип стенда: ");
            System.out.println("1. Одна нода\n2. Две ноды");
            this.type = Integer.parseInt(reader.readLine());
            if (type == 1) {
                System.out.println("Введите адрес ноды: ");
                this.host = reader.readLine();
            } else if (type == 2) {
                System.out.println("Введите адрес первой ноды: ");
                this.host = reader.readLine();
                System.out.println("Введите адрес второй ноды: ");
                this.host2 = reader.readLine();
            }
            System.out.println("Введите порт: ");
            this.port = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //точка входа
    public void runUpdate() {
        //переопределяем метод в зависимости от кол-ва нод
        if (type == 1) update(host, port);
        else if (type == 2) update(host, host2, port);

    }

    private void update(String host, String port) {
        String command = "";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host);

        /*result = connectionSsh.runCommand("ps aux | grep -v grep | grep STAND | grep " + port );
        if (result[1] != null){
            System.out.println("Вывод: " + result[1].replace(' ', '\n'));
        } else {
            System.out.println("Вывод: Процесс не обнаружен :(");
        }*/
        result = connectionSsh.runCommand("sudo unzip /tmp/tmp22222/*.war -d /tmp/tmp22222/");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод: " + result[1]);

    }

    private void update(String host, String host2, String port) {
        String command = "";
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(host2);

        result = connectionSsh.runCommand(command);
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: " + result[1]);

    }
}
