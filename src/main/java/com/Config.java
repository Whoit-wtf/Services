package com;
import com.diogonunes.jcolor.AnsiFormat;
import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

public class Config {
    private String pathDB = "D:\\Перенос\\Java\\new_Services\\service_database.db";
    private String pathKey = "C:\\Users\\Fenrir\\Desktop\\key_rsa.pem";
    /*public AnsiFormat fInfo = new AnsiFormat(CYAN_TEXT());
    public AnsiFormat fError = new AnsiFormat(YELLOW_TEXT(), RED_BACK());
    public AnsiFormat fFox = new AnsiFormat(TEXT_COLOR(208));
    public AnsiFormat fMenu = new AnsiFormat(TEXT_COLOR(202));
    public AnsiFormat fOk = new AnsiFormat(GREEN_TEXT());*/


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
