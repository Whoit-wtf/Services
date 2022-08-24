package com.database;

import com.ConnectionSsh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchInfo {

    String port = null;
    String node1 = null;
    String node2 = null;
    String[] info;

    public SearchInfo() {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Введите адрес первой ноды: ");
            this.node1 = reader.readLine();
            System.out.println("Введите адрес второй ноды: ");
            this.node2 = reader.readLine();
            System.out.println("Введите рабочий порт стенда: ");
            this.port = reader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.info = run(node1, node2, port);
    }

    public String[] run(String node1, String node2, String port){
        String[] info = new String[5];
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        result = connectionSsh.runCommand("ps aux | grep " + port + " | grep -v grep");
        if(result[1] == null){
            System.out.println("ps aux | grep " + port);
            System.out.println("не дал результата, похоже стенд выключен...");

        }else {
            //System.out.println("Код: " + result[0]);
            //System.out.println("Вывод: " + result[1]);
            Pattern pattern = Pattern.compile("-Djetty.home=\\S+");
            Matcher matcher = pattern.matcher(result[1]);
            if(matcher.find()) {
                //System.out.println("Папка стенда " + matcher.group().split("=")[1]);
                info[0] = node1;
                info[1] = node2;
                info[2] = port;
                info[3] = matcher.group().split("=")[1];
            }
            result = connectionSsh.runCommand("ls -lh " + info[3] + "| awk 'NR==2{{print $3}}'");
            //System.out.println("Код: " + result[0]);
            // System.out.println("Вывод: " + result[1]);
            info[4] = result[1];
            System.out.println("Собранная информация:");
            System.out.println("Первая нода: "+info[0]);
            System.out.println("Вторая нода: "+info[1]);
            System.out.println("Рабочий порт: "+info[2]);
            System.out.println("Рабочая папка: "+info[3]);
            System.out.println("Владелец : "+info[4]);

        }
        return info;
    }
}
