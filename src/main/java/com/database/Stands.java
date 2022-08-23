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
    public Stands(int id, String port, String node1, String node2, String owner, String folder ) {
        this.id = id;
        this.port = port;
        this.node1 = node1;
        this.node2 = node2;
        this.owner = owner;
        this.folder = folder;

    }

    // Выводим информацию
    @Override
    public String toString() {
        return String.format("ID: %s | Порт: %s | Нода1: %s | Нода2: %s | Владелец: %s | Папка стенда: %s ",
                this.id, this.port, this.node1, this.node2, this.owner, this.folder );
    }
}
