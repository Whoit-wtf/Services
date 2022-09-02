package com.database;

import com.Main;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbHandler {
    private static DbHandler instance = null;
    // Объект, в котором будет храниться соединение с БД
    private final Connection connection;

    private DbHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        String CON_STR = "jdbc:sqlite:" + Main.config.getPathDB();
        this.connection = DriverManager.getConnection(CON_STR);
    }

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    public List<Stand> getAllStands() {
        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать данные
            List<Stand> stands = new ArrayList<Stand>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()
            ResultSet resultSet = statement.executeQuery("SELECT id, port, node1, node2, owner," +
                    " folder FROM standsInfo");
            // Проходимся по нашему resultSet и заносим данные
            while (resultSet.next()) {
                stands.add(new Stand(
                        resultSet.getInt("id"),
                        resultSet.getString("port"),
                        resultSet.getString("node1"),
                        resultSet.getString("node2"),
                        resultSet.getString("owner"),
                        resultSet.getString("folder")));
            }
            return stands;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Stand> checkStands(String node) {
        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать данные
            List<Stand> stands = new ArrayList<>();
            // В resultSet будет храниться результат нашего запроса,
            // который выполняется командой statement.executeQuery()
            ResultSet resultSet = statement.executeQuery("SELECT id, node1, node2 , port, folder, owner " +
                    " FROM standsInfo WHERE node1 = '" + node + "'");
            // Проходимся по нашему resultSet и заносим данные
            while (resultSet.next()) {
                stands.add(new Stand(
                        resultSet.getInt("id"),
                        resultSet.getString("node1"),
                        resultSet.getString("node2"),
                        resultSet.getString("port"),
                        resultSet.getString("folder"),
                        resultSet.getString("owner")));
            }
            return stands;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Добавление в БД
    public void addStands(String node1, String node2, String port, String folder, String owner) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO standsInfo('node1', 'node2', 'port', 'folder', 'owner')" +
                        "VALUES(?, ?, ?, ?, ?)")) {
            statement.setObject(1, node1);
            statement.setObject(2, node2);
            statement.setObject(3, port);
            statement.setObject(4, folder);
            statement.setObject(5, owner);


            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Удаление по ID
    public void deleteStands(String node1) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM standsInfo WHERE node1 = ?")) {
            statement.setObject(1, node1);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
