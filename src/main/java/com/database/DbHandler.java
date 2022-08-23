package com.database;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbHandler {

    private static final String CON_STR = "jdbc:sqlite:D:\\Перенос\\Java\\new_Services\\service_database.db";
    //File file = new File(getClass().getClassLoader().getResource("/template.docx").getFile())
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }
    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    private DbHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(CON_STR);
    }
    public List<Stands> getAllStands() {
        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать данные
            List<Stands> stands = new ArrayList<Stands>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()
            ResultSet resultSet = statement.executeQuery("SELECT id, port, node1, node2, owner,"+
                    " folder FROM standsInfo");
            // Проходимся по нашему resultSet и заносим данные
            while (resultSet.next()) {
                stands.add(new Stands(
                        resultSet.getInt("id"),
                        resultSet.getString("port"),
                        resultSet.getString("node1"),
                        resultSet.getString("node2"),
                        resultSet.getString("owner"),
                        resultSet.getString("folder")));
            }
            return stands;
        } catch (SQLException e ) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    // Добавление в БД
    public void addStands(Stands stands) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO standsInfo('port','node1', 'node2', 'owner', 'folder')"+
                        "VALUES(?, ?, ?, ?, ?)")) {
            statement.setObject(1, stands.port);
            statement.setObject(2, stands.node1);
            statement.setObject(3, stands.node2);
            statement.setObject(4, stands.owner);
            statement.setObject(5, stands.folder);


            statement.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Удаление лога по ID
    public void deleteStands(int id){
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM standsInfo WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
