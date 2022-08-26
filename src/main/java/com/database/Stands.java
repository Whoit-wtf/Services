package com.database;

public class Stands {
    // Поля класса
    public int id;
    public String port;
    public String node1;
    public String node2;
    public String owner;
    public String folder;


    // Конструктор
    public Stands(int id, String node1, String node2, String port, String folder, String owner) {
        this.id = id;
        this.node1 = node1;
        this.node2 = node2;
        this.port = port;
        this.folder = folder;
        this.owner = owner;

    }

    // Выводим информацию
    @Override
    public String toString() {
        return String.format("ID: %s | Порт: %s | Нода1: %s | Нода2: %s | Папка стенда: %s | Владелец: %s ",
                this.id, this.port, this.node1, this.node2, this.folder, this.owner);
    }
}
