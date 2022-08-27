package com;

public class Config {
    private String pathDB = "D:\\Перенос\\Java\\new_Services\\service_database.db";;
    private String pathKey = "C:\\Users\\Fenrir\\Desktop\\key_rsa.pem"; ;

    public String getPathDB() {
        return pathDB;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathDB(String pathDB) {
        this.pathDB = pathDB;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }
}
