package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ResultCommand;
import com.database.*;
import com.ConnectionSsh;
import org.jetbrains.annotations.NotNull;

public class UpdateStands {

    String node1 = null;
    String node2 = null;
    String port = null;
    int type;

    String url_server = null;
    String url_libs = null;
    String url_patch = null;

    // Конструктор с инициализацией переменных
    public UpdateStands() {

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Выберите тип стенда: ");
            System.out.println("1. Одна нода\n2. Две ноды");
            this.type = Integer.parseInt(reader.readLine());
            if (type == 1) {
                System.out.println("Введите адрес ноды: ");
                this.node1 = reader.readLine().replace(" ", "");
            } else if (type == 2) {
                System.out.println("Введите адрес первой ноды: ");
                this.node1 = reader.readLine().replace(" ", "");
                System.out.println("Введите адрес второй ноды: ");
                this.node2 = reader.readLine().replace(" ", "");
            }
            System.out.println("Введите порт: ");
            this.port = reader.readLine().replace(" ", "");
            System.out.println("Введите ссылку на ядро (Нажмите Enter, если это не нужно обновлять)");
            this.url_server = reader.readLine().replace(" ", "");
            System.out.println("Введите ссылку на либы (Нажмите Enter, если это не нужно обновлять)");
            this.url_libs = reader.readLine().replace(" ", "");
            System.out.println("Введите ссылку на приклад (Нажмите Enter, если это не нужно обновлять)");
            this.url_patch = reader.readLine().replace(" ", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //точка входа
    public void runUpdate() {
        //переопределяем метод в зависимости от кол-ва нод
        if (type == 1) update(node1, port);
        else if (type == 2) update();

    }

    private void update(String node1, String port) {
        ResultCommand result = new ResultCommand();

        ConnectionSsh connectionSsh = new ConnectionSsh(node1);


    }

    private void update() {
        String[] info;
        List<Stand> stands;

        ActionStands actionStands = new ActionStands();
        System.out.println("Проверяем стенд " + node1 + " в БД...");
        //Проверяем наличие стенда в БД
        if (actionStands.checkStands(node1)) {
            System.out.println("Стенд не найден");
            //запускаем поиск информации на стенде
            SearchInfo searchInfo = new SearchInfo(false);
            System.out.println("Собираем информацию...");
            info = searchInfo.run(node1, node2, port);
            System.out.println("OK");
            System.out.println("Добавляем стенд в БД");
            //Запускаем добавление информации в БД
            actionStands.addStands(info[0], info[1], info[2], info[3], info[4]);
        }
        stands = actionStands.getStands(node1);
        if (stands.isEmpty()) {
            System.out.println("Пришел пустой список стендов");
            System.out.println("Прерывание...");
            return;
        }
        //Получаем список информации по стенду

        String folder = stands.get(0).folder;
        String owner = stands.get(0).owner;
        toDoRenameMe(stands, node1, port);
        toDoRenameMe(stands, node2, port);

        if (!"".equals(url_server)) {
            System.out.println("Начинаем обновлять ядро");
            if (sufd_server(node1, url_server, "/oracle/share", owner)) {
                System.out.println("");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        if (!"".equals(url_libs)) {
            System.out.println("Начинаем обновлять либы");
            if (sufd_libs(node1, url_libs, "/oracle/share", owner)) {
                System.out.println("");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        if (("".equals(url_patch))) {

        }

        /*result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - "+owner+"" +
                "\ncd "+folder+"" +
                "\nrm nohup.out");
        System.out.println("Код:" + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());*/
    }

    private static void toDoRenameMe(@NotNull List<Stand> stands, String host, String port){
        ResultCommand result = new ResultCommand();
        String owner = stands.get(0).owner;
        String pid = null;
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.printf("Получаем pid процесса на ноде [%s]\n", host);
        result = connectionSsh.runCommand("sudo ps aux | grep " + port + " " +
                "| grep [S]TAND | awk 'NR==1{{print $2}}'");
        if (null == result.getOutLog()) {
            System.out.println("Процесс не запущен");
            return;
        }
        pid = result.getOutLog().replace("\n", "");
        //System.out.println("Код:" + result.exitStatus);
        System.out.println("Вывод: \n" + result.getOutLog());
        System.out.println("Останавливаем процесс...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\nkill -9 " + pid + "");
        //System.out.println("Код:" + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());
        System.out.println("OK");

    }


    private boolean sufd_server(String node1, String url, String path, String owner) {
        ResultCommand result = new ResultCommand();
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Бекапим ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + path + "/webapps" +
                "\nrename .war .war.bk *.war" +
                "\nEOF");
        System.out.println("Код: " + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());
        if (result.exitStatus.equals("0")) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return false;
        }
        System.out.println("Скачиваем ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + path + "/webapps" +
                "\nwget --no-check-certificate " + url +
                "\nEOF");
        System.out.println("Код: " + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());
        if (result.exitStatus.equals("0")) {
            System.out.println("OK");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return false;
        }
        System.out.println("OK");
        System.out.println("Ядро установлено");
        return true;
    }

    private void sufd_stand_patch(String node1, String url, String path, String owner) {


    }

    private boolean sufd_libs(String node1, String url, String path, String owner) {

        ResultCommand result = new ResultCommand();
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Скачиваем либы в tmp и проверяем содержимое архива");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\nmkdir "+path+"/lib/ext/showlibs"+
                "\ncd "+path+"/lib/ext/showlibs" +
                "\nwget --no-check-certificate " + url +
                "\nEOF");
        System.out.println("Код: " + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());
        //out sufd
        result = connectionSsh.runCommand("sudo unzip -l "+path+"/lib/ext/showlibs/*.zip " +
                "| awk 'NR==4{{print $4}}' ");
        result.setOutLog(result.getOutLog().replace("\n","");
        System.out.println("Код: " + result.exitStatus);
        System.out.println("Вывод: " + result.getOutLog());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        if ("sufd/".equals(result.getOutLog())) {
            System.out.println("Продолжаем...");
            System.out.println("Бекапим старые либы...");
            result = connectionSsh.runCommand("sudo mv " + path + "/lib/ext/sufd " +
                    path + "/lib/ext/sufd.bk_" + formatter.format(date));
            System.out.println(result.getOutLog());
            if ("0".equals(result.exitStatus)) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            System.out.println("Переносим либы в папку стенда");
            result = connectionSsh.runCommand("sudo unzip "+path+"/lib/ext/showlibs/*.zip -d "+path+"/lib/ext/");
            if ("0".equals(result.exitStatus)) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            System.out.println("Удаляем временные файлы...");
            result = connectionSsh.runCommand("sudo rm -rf "+path+"/lib/ext/showlibs");
            if ("0".equals(result.exitStatus)) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
        } else {
            System.out.println("Прерывание...");
            System.out.println("Удаляем временные файлы...");
            result = connectionSsh.runCommand("sudo rm -f "+path+"/lib/ext/showlibs");
            if ("0".equals(result.exitStatus)) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            return false;
        }
        return true;
    }
}
