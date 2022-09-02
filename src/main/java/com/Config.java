package com;

public class Config {
    /*private String pathDB = "D:\\Перенос\\Java\\new_Services\\service_database.db";
    private String pathKey = "C:\\Users\\Fenrir\\Desktop\\key_rsa.pem";*/
    private String pathDB = "D:\\Перенос\\Java\\new_Services\\service_database.db";
    private String pathKey = "C:\\Users\\Fenrir\\Desktop\\key_rsa.pem";
    private String user = "petrov.aleksandr";

    public String getPathDB() {
        return this.pathDB;
    }

    public void setPathDB(String pathDB) {
        this.pathDB = pathDB;
    }

    public String getPathKey() {
        return this.pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
