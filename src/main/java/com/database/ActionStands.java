package com.database;

import java.sql.SQLException;
import java.util.List;

public class ActionStands {
    public void checkStands(){
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

    public void delStands(int id){
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            List<Stands> stands = dbHandler.getAllStands();

            // Удаление записи с id
            dbHandler.deleteStands(id);
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStands(int id,String port, String node1, String node2, String owner, String folder){
        try {
            // Создаем экземпляр по работе с БД
            DbHandler dbHandler = DbHandler.getInstance();
            List<Stands> stands = dbHandler.getAllStands();

            // Добавляем запись
            dbHandler.addStands(new Stands(id, port, node1, node2, owner, folder));
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }


}
