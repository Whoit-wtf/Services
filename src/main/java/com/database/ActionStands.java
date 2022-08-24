package com.database;

import com.database.SearchInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ActionStands {

    public void main(){
        Scanner scan = new Scanner(System.in);
        String s = "";
        int x = 0;
        String[] info;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        while (!"0".equals(s)) {
            System.out.println("1. Просмотр всех записей в БД");
            System.out.println("2. Проверить есть ли стенд в БД");
            System.out.println("3. Удалить запись");
            System.out.println("4. Добавить запись");
            System.out.println("0. Выход");
            s = scan.next();
            try {
                x = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод");
            }
            try {
            switch (x) {
                case 1:
                    showStands();
                    break;
                case 2:
                    System.out.println("Введите адрес ноды без otr.ru");
                    String node = reader.readLine();
                    checkStands(node);
                    break;
                case 3:
                    System.out.println("Введите адрес ноды без otr.ru");
                    String node1 = reader.readLine();
                    delStands(node1);
                    break;
                case 4:
                    SearchInfo searchInfo = new SearchInfo();
                    info = searchInfo.info;
                    if(checkStands(info[0])){
                        addStands(info[0], info[1], info[2], info[3], info[4]);
                    }else {
                        System.out.println("Стенд уже имеется в БД");
                        break;
                    }

                    break;

            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void showStands(){
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            List<Stands> stands = dbHandler.getAllStands();
            // Получаем все записи и выводим их на консоль
            for (Stands stand: stands) {
                System.out.println(stand.toString());
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void delStands(String node1){
        boolean result = false;
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            result = dbHandler.checkStands(node1).isEmpty();
            if (result){
                System.out.println(node1 + " не найден в БД");
            }else{
                dbHandler.deleteStands(node1);
                System.out.println("Стенд "+node1+" удалён");
            }
            // Удаление записи с node1

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStands( String node1, String node2, String port, String folder, String owner){
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();

            // Добавляем запись
            dbHandler.addStands(node1, node2, port, folder, owner);
            System.out.println("Стенд "+node1+" добавлен");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean checkStands(String node1){
        boolean result = false;
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            result = dbHandler.checkStands(node1).isEmpty();
            if (result){
                System.out.println(node1 + " не найден в БД");
            }else System.out.println(node1 + " найден в БД");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
