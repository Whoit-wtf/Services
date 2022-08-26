package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.database.*;
import com.ConnectionSsh;

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
                ;
            } else if (type == 2) {
                System.out.println("Введите адрес первой ноды: ");
                this.node1 = reader.readLine().replace(" ", "");
                ;
                System.out.println("Введите адрес второй ноды: ");
                this.node2 = reader.readLine().replace(" ", "");
                ;
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
        else if (type == 2) update(node1, node2, port);

    }

    private void update(String node1, String port) {
        String[] result;

        ConnectionSsh connectionSsh = new ConnectionSsh(node1);


    }

    private void update(String node1, String node2, String port) {
        String[] result;
        String[] info;
        List<Stands> stands;

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
        String pid;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Получаем pid процесса на первой ноде");
        result = connectionSsh.runCommand("sudo ps aux | grep " + port + " " +
                "| grep [S]TAND | awk 'NR==1{{print $2}}'");
        pid = result[1].replace("\n", "");
        //System.out.println("Код:" + result[0]);
        System.out.println("Вывод: \n" + result[1]);
        if (result[1].equals("null")) {
            System.out.println("Процесс не запущен");
        } else {
            System.out.println("Останавливаем процесс...");
            result = connectionSsh.runCommand("bash << EOF" +
                    "\nsudo su - " + owner +
                    "\nkill -9 " + pid + "");
            //System.out.println("Код:" + result[0]);
            System.out.println("Вывод: " + result[1]);
            System.out.println("OK");
        }
        ConnectionSsh connectionSsh2 = new ConnectionSsh(node2);
        System.out.println("Получаем pid процесса на второй ноде");
        result = connectionSsh2.runCommand("sudo ps aux | grep " + port + " " +
                "| grep [S]TAND | awk 'NR==1{{print $2}}'");
        pid = result[1].replace("\n", "");
        //System.out.println("Код:" + result[0]);
        System.out.println("Вывод: \n" + result[1]);
        if (result[1].equals("null")) {
            System.out.println("Процесс не запущен");
        } else {
            System.out.println("Останавливаем процесс...");
            result = connectionSsh2.runCommand("bash << EOF" +
                    "\nsudo su - " + owner +
                    "\nkill -9 " + pid + "");
            //System.out.println("Код:" + result[0]);
            System.out.println("Вывод: " + result[1]);
            System.out.println("OK");
        }

        if (url_server != null) {
            System.out.println("Начинаем обновлять ядро");
            if (sufd_server(node1, url_server, "/oracle/share", owner)) {
                System.out.println("");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        if (url_libs != null) {
            System.out.println("Начинаем обновлять либы");
            if (sufd_libs(node1, url_libs, "/oracle/share", owner)) {
                System.out.println("");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        if ((url_patch != null)) {

        }

        /*result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - "+owner+"" +
                "\ncd "+folder+"" +
                "\nrm nohup.out");
        System.out.println("Код:" + result[0]);
        System.out.println("Вывод: " + result[1]);*/
    }


    private boolean sufd_server(String node1, String url, String path, String owner) {
        String[] result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Бекапим ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + path + "/webapps" +
                "\nrename .war .war.bk *.war" +
                "\nEOF");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод: " + result[1]);
        if (result[0].equals("0")) {
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
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод: " + result[1]);
        if (result[0].equals("0")) {
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

        String[] result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Скачиваем либы в tmp и проверяем содержимое архива");
        result = connectionSsh.runCommand("bash << EOF" +
                "\n sudo su - " + owner +
                "\nmkdir /tmp/showlibs/" +
                "\ncd /tmp/showlibs/" +
                "\nwget --no-check-certificate " + url +
                "\nunzip *.zip" +
                "\nEOF");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод: " + result[1]);
        result = connectionSsh.runCommand("ls /tmp/showlibs/");
        System.out.println("Проверяем что распаковалось...");
        System.out.println("Код: " + result[0]);
        System.out.println("Вывод:\n" + result[1]);
        System.out.println("Это похоже на sufd ?");
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            System.out.println("1.Да\n2.Нет:(");
            String input = reader.readLine().replace(" ", "");
            if (input.equals("1")) {
                System.out.println("Продолжаем...");
                System.out.println("Бекапим старые либы...");
                result = connectionSsh.runCommand("mv " + path + "/lib/ext/sufd " +
                        path + "/lib/ext/sufd_" + formatter.format(date));
                if ("0".equals(result[0])) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Переносим либы в папку стенда");
                result = connectionSsh.runCommand("mv /tmp/showlibs/sufd " + path + "/lib/ext/");
                if ("0".equals(result[0])) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("rm -f /tmp/showlibs");
                if ("0".equals(result[0])) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
            } else {
                System.out.println("Прерывание...");
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("rm -f /tmp/showlibs");
                if ("0".equals(result[0])) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
