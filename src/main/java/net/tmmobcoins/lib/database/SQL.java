package net.tmmobcoins.lib.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class SQL {

    MySQL MySQL;

    public SQL(MySQL mySQL) {
        this.MySQL = mySQL;
    }

    public boolean tableExists(String table) {
        try {
            Connection connection = MySQL.getConnection();
            if (connection == null) {
                return false;
            }

            DatabaseMetaData metadata = connection.getMetaData();
            if (metadata == null) {
                return false;
            }

            ResultSet rs = metadata.getTables((String)null, (String)null, table, (String[])null);
            if (rs.next()) {
                return true;
            }
        } catch (Exception ignored) {}

        return false;
    }

    public void insertData(String columns, String values, String table) {
        MySQL.update("INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ");");
    }

    public boolean deleteData(String column, String logic_gate, String data, String table) {
        if (data != null) {
            data = "'" + data + "'";
        }

        return MySQL.update("DELETE FROM " + table + " WHERE " + column + logic_gate + data + ";");
    }

    public boolean exists(String column, String data, String table) {
        if (data != null) {
            data = "'" + data + "'";
        }

        try {
            ResultSet rs = MySQL.query("SELECT * FROM " + table + " WHERE " + column + "=" + data + ";");
            if (rs.next()) {
                return true;
            }
        } catch (Exception var4) {
        }

        return false;
    }

    public void deleteTable(String table) {
        MySQL.update("DROP TABLE " + table + ";");
    }

    public void truncateTable(String table) {
        MySQL.update("TRUNCATE TABLE " + table + ";");
    }

    public void createTable(String table, String columns) {
        MySQL.update("CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ");");
    }

    public void upsert(String selected, Object object, String column, String data, String table) {
        if (object != null) {
            object = "'" + object + "'";
        }

        if (data != null) {
            data = "'" + data + "'";
        }

        try {
            ResultSet resultSet = MySQL.query("SELECT * FROM " + table + " WHERE " + column + "=" + data + ";");
            if (resultSet.next()) {
                MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + column + "=" + data + ";");
            } else {
                insertData(column + ", " + selected, data + ", " + object, table);
            }
        } catch (Exception ignored) {}
    }

    public void set(String selected, Object object, String column, String logic_gate, String data, String table) {
        if (object != null) object = "'" + object + "'";

        if (data != null) data = "'" + data + "'";

        MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + column + logic_gate + data + ";");
    }

    public void set(String selected, Object object, String[] where_arguments, String table) {
        StringBuilder arguments = new StringBuilder();

        for (String argument : where_arguments) {
            arguments.append(argument).append(" AND ");
        }

        if (arguments.length() <= 5) {
        } else {
            arguments = new StringBuilder(arguments.substring(0, arguments.length() - 5));
            if (object != null) object = "'" + object + "'";

            MySQL.update("UPDATE " + table + " SET " + selected + "=" + object + " WHERE " + arguments + ";");
        }
    }

    public Object get(String selected, String[] where_arguments, String table) {
        StringBuilder arguments = new StringBuilder();

        for (String argument : where_arguments)
            arguments.append(argument).append(" AND ");

        if (arguments.length() <= 5) {
            return false;
        } else {
            arguments = new StringBuilder(arguments.substring(0, arguments.length() - 5));
            try {
                ResultSet result = MySQL.query("SELECT * FROM " + table + " WHERE " + arguments + ";");
                if (result.next()) {
                    return result.getObject(selected);
                }
            } catch (Exception ignored) {}
            return null;
        }
    }

    public Object get(String selected, String column, String logic, String data, String table) {
        if (data != null)
            data = "'" + data + "'";

        try {
            ResultSet result = MySQL.query("SELECT * FROM " + table + " WHERE " + column + logic + data + ";");
            if (result.next()) {
                return result.getObject(selected);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public int countRows(String table) {
        int rows = 0;
        if (table != null) {
            try {
                while (MySQL.query("SELECT * FROM " + table + ";").next()) {
                    ++rows;
                }
            } catch (Exception ignored) {
            }
        }
        return rows;
    }
}