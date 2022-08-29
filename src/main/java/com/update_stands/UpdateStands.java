package com.update_stands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Config;
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
    String path = null;
    String typeLibs = null;
    String typePatch = null;

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
        //запускаем апдейт
    }

    public void update() {
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
        //Проверяем место на диске
        if (checkDiskSpace(node1)) {
            System.out.println("Метод проверки места закончил работу");
        } else {
            System.out.println("Упс... Похоже места не хватает");
            System.out.println("Прерывание...");
            return;
        }
        //Получаем список информации по стенду

        String folder = stands.get(0).folder;
        String owner = stands.get(0).owner;
        if (type == 1) {
            killProcess(stands, node1, port);
            path = folder;
        } else if (type == 2) {
            killProcess(stands, node1, port);
            killProcess(stands, node2, port);
            path = findShare(node1, folder);
        }

        //Если url с ядром не пуста
        if (!"".equals(url_server)) {
            System.out.println("Начинаем обновлять ядро");
            if (sufd_server(node1, url_server, path, owner)) {
                System.out.println("Метод обновления ядра закончил работу");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        //Если url с либами не пуста
        if (!"".equals(url_libs)) {
            System.out.println("Начинаем обновлять либы");
            if (sufd_libs(node1, url_libs, path, owner)) {
                System.out.println("Метод обновления библиотек закончил работу");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        //Если url с прикладом не пуста
        if (!"".equals(url_patch)) {
            System.out.println("Начинаем обновлять приклад");
            if (sufd_patch(node1, url_patch, path, owner)) {
                System.out.println("Метод обновления приелада закончил работу");
            } else {
                System.out.println("Упс... что-то пошло не так");
                System.out.println("Прерывание...");
                return;
            }
        }
        //Стартуем стенды
        if (type == 1) {
            startStand(stands, node1);
        } else if (type == 2) {
            startStand(stands, node1);
            startStand(stands, node2);
        }

    }

    private void killProcess(@NotNull List<Stand> stands, String host, String port) {
        ResultCommand result;
        String owner = stands.get(0).owner;
        String pid;
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

    private void startStand(@NotNull List<Stand> stands, String host) {
        ResultCommand result;
        String folder = stands.get(0).folder;
        String owner = stands.get(0).owner;
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.printf("Запускаем ноду [%s]\n", host);
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + folder +
                "\nnohup ./sufd.sh > nohup.out 2>&1 < /dev/null & > /dev/null 2>&1" +
                "\nEOF");
        System.out.println("Вывод: \n" + result.getOutLog());
        if (result.getExitStatus() == 0) {
            System.out.println("Стенд запущен");
        } else {
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return;
        }

    }

    private boolean sufd_server(String node1, String url, String path, String owner) {
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Бекапим ядро...");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\ncd " + path + "/webapps" +
                "\nrename .war .war.bk *.war" +
                "\nEOF");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Код: " + result.getExitStatus());
            System.out.println("Вывод: " + result.getOutLog());
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
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Код: " + result.getExitStatus());
            System.out.println("Вывод: " + result.getOutLog());
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return false;
        }
        System.out.println("OK");
        System.out.println("Ядро установлено");
        return true;
    }

    private boolean sufd_patch(String node1, String url, String path, String owner) {
        System.out.println("Определяем тип архива");
        Pattern pattern = Pattern.compile(".\\w+$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            System.out.println("Тип архива определён " + matcher.group().replace("\n", ""));
            typePatch = matcher.group().replace("\n", "");
        } else {
            System.out.println("Что-то пошло не так...");
            return false;
        }
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        if (typePatch.equals(".zip")) {
            System.out.println("Скачиваем приклад в tmp и проверяем содержимое архива");
            result = connectionSsh.runCommand("bash << EOF" +
                    "\nsudo su - " + owner +
                    "\nmkdir " + path + "/showpatch" +
                    "\ncd " + path + "/showpatch" +
                    "\nwget --no-check-certificate " + url +
                    "\nEOF");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Код: " + result.getExitStatus());
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("Что-то пошло не так");
                System.out.println("Прерывание....");
                return false;
            }
            System.out.println("Определяем в архиве папку sql-migration");
            //out sql-migrations
            result = connectionSsh.runCommand("sudo unzip -l " + path + "/showpatch/*.zip " +
                    "| awk 'NR==4{{print $4}}' ");
            String sqlMigration = result.getOutLog().replace("\n", "");
            if (0 == result.getExitStatus()) {
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("OK");
            } else {
                System.out.println("Код: " + result.getExitStatus());
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("Что-то пошло не так");
                System.out.println("Прерывание....");
                return false;
            }
            System.out.println("Определяем в архиве папку sufd.config");
            //sufd.config
            result = connectionSsh.runCommand("sudo unzip -l " + path + "/showpatch/*.zip " +
                    "| awk 'NR==5{{print $4}}' ");
            String sufdConfig = result.getOutLog().replace("\n", "");
            if (0 == result.getExitStatus()) {
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("OK");
            } else {
                System.out.println("Код: " + result.getExitStatus());
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("Что-то пошло не так");
                System.out.println("Прерывание....");
                return false;
            }
            if ("sql-migrations/".equals(sqlMigration) && "sufd.config/".equals(sufdConfig)) {
                System.out.println("Бекапим старые sql-migrations");
                result = connectionSsh.runCommand("sudo mv " + path + "/sql-migrations " +
                        path + "/sql-migrations.bk_" + formatter.format(date));
                //System.out.println(result.getOutLog());
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Бекапим старый sufd.config");
                result = connectionSsh.runCommand("sudo mv " + path + "/sufd.config " +
                        path + "/sufd.config.bk_" + formatter.format(date));
                //System.out.println(result.getOutLog());
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Распаковываем приклад");
                result = connectionSsh.runCommand("bash << EOF" +
                        "\nsudo su - " + owner +
                        "\nunzip -uq " + path + "/showpatch/*.zip -d " + path + "" +
                        "\nEOF");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("sudo rm -rf " + path + "/showpatch");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }

            } else {
                System.out.println("Прерывание...");
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("sudo rm -rf " + path + "/showpatch");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                return false;
            }

        }
        return true;
    }

    private boolean sufd_libs(String node1, String url, String path, String owner) {
        System.out.println("Определяем тип архива");
        Pattern pattern = Pattern.compile(".\\w+$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            System.out.println("Тип архива определён " + matcher.group().replace("\n", ""));
            typeLibs = matcher.group().replace("\n", "");
        } else {
            System.out.println("Что-то пошло не так...");
            return false;
        }
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Скачиваем либы в tmp и проверяем содержимое архива");
        result = connectionSsh.runCommand("bash << EOF" +
                "\nsudo su - " + owner +
                "\nmkdir " + path + "/lib/ext/showlibs" +
                "\ncd " + path + "/lib/ext/showlibs" +
                "\nwget --no-check-certificate " + url +
                "\nEOF");
        if (0 == result.getExitStatus()) {
            System.out.println("OK");
        } else {
            System.out.println("Код: " + result.getExitStatus());
            System.out.println("Вывод: " + result.getOutLog());
            System.out.println("Что-то пошло не так");
            System.out.println("Прерывание....");
            return false;
        }
        if (typeLibs.equals(".zip")) {
            //out sufd
            result = connectionSsh.runCommand("sudo unzip -l " + path + "/lib/ext/showlibs/*.zip " +
                    "| awk 'NR==4{{print $4}}' ");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Код: " + result.getExitStatus());
                System.out.println("Вывод: " + result.getOutLog());
                System.out.println("Что-то пошло не так");
                System.out.println("Прерывание....");
                return false;
            }

            if ("sufd/".equals(result.getOutLog().replace("\n", ""))) {
                System.out.println("Продолжаем...");
                System.out.println("Бекапим старые либы...");
                result = connectionSsh.runCommand("sudo mv " + path + "/lib/ext/sufd " +
                        path + "/lib/ext/sufd.bk_" + formatter.format(date));
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Переносим либы в папку стенда");
                result = connectionSsh.runCommand("bash << EOF"+
                        "\nsudo su - "+ owner +
                        "\nunzip " + path + "/lib/ext/showlibs/*.zip -d " + path + "/lib/ext/" +
                        "\nEOF");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("sudo rm -rf " + path + "/lib/ext/showlibs");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
            } else {
                System.out.println("Прерывание...");
                System.out.println("Удаляем временные файлы...");
                result = connectionSsh.runCommand("sudo rm -rf " + path + "/lib/ext/showlibs");
                if (0 == result.getExitStatus()) {
                    System.out.println("OK");
                } else {
                    System.out.println("Что-то пошло не так");
                    return false;
                }
                return false;
            }
            return true;
        } else if (typeLibs.equals(".war")) {
            System.out.println("Бекапим старые либы");
            result = connectionSsh.runCommand("sudo mv " + path + "/lib/ext/sufd " +
                    path + "/lib/ext/sufd.bk_" + formatter.format(date));

            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                System.out.println(result.getOutLog());
                return false;
            }
            System.out.println("Распаковываем war");
            result = connectionSsh.runCommand("bash << EOF"+
                    "\nsudo su - "+owner+
                    "\ncd " + path + "/lib/ext/showlibs/" +
                    "\nunzip *.war" +
                    "\nEOF");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            System.out.println("Создаем папку для либ");
            result = connectionSsh.runCommand("bash << EOF" +
                    "\nsudo su - " + owner +
                    "\nmkdir " + path + "/lib/ext/sufd" +
                    "\nEOF");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            System.out.println("Переносим либы в папку стенда");
            result = connectionSsh.runCommand("sudo mv " + path + "/lib/ext/showlibs/WEB-INF/lib/*.jar " +
                    "" + path + "/lib/ext/sufd/");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }
            System.out.println("Удаляем временные файлы...");
            result = connectionSsh.runCommand("sudo rm -rf " + path + "/lib/ext/showlibs");
            if (0 == result.getExitStatus()) {
                System.out.println("OK");
            } else {
                System.out.println("Что-то пошло не так");
                return false;
            }

            return true;
        } else {
            System.out.println("Что-то пошло не так в определении типа архива");
            return false;
        }

    }

    public String findShare(String node1, String folder) {
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(node1);
        System.out.println("Начинаем поиск share папки");
        result = connectionSsh.runCommand("sudo ls -lh " + folder + " | grep lib | awk 'NR==1{{print $11}}'");
        if (result.getOutLog() != null) {
            System.out.println("Папка найдена: " + result.getOutLog().replace("/lib", ""));
            return result.getOutLog().replace("/lib", "").replace("\n","");
        } else {
            System.out.println("Что-то пошло не так...");
            return null;
        }
    }

    public boolean checkDiskSpace(String host) {
        ResultCommand result;
        ConnectionSsh connectionSsh = new ConnectionSsh(host);
        System.out.println("Проверка места");
        result = connectionSsh.runCommand("sudo df -h");
        System.out.println(result.getOutLog());
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.println("Хватает места?");
            System.out.println("1. Да\n2. Нет");
            int input = Integer.parseInt(reader.readLine());
            if (input == 1) {
                return true;
            } else return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
