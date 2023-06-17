package net.tmmobcoins.lib.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLActions {

    private final MySQL mySQL;

    public SQLActions(String host, String user, String password, String database, String port) {
        this.mySQL = new MySQL(host, user, password, database, port);
    }

    public void createTable(String table, String columns) {
        try {
            PreparedStatement statement = mySQL.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ");"
            );
            mySQL.execute(statement, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerDataExists(String uuid, String table) {
        try {
            PreparedStatement statement = mySQL.prepareStatement(
                    "SELECT * FROM " + table + " WHERE uuid = ?;",
                    uuid
            );
            ResultSet rs = mySQL.execute(statement, true);
            if (rs.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertData(String columns, String table, String... values) {
        try {
            StringBuilder builder = new StringBuilder("INSERT INTO " + table + " (" + columns + ") VALUES (");
            builder.append("?, ".repeat(values.length));
            builder.delete(builder.length() - 2, builder.length()).append(");");

            PreparedStatement statement = mySQL.prepareStatement(
                    builder.toString(),
                    values
            );

            mySQL.execute(statement, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerData(String selected, String data, String uuid, String table) {
        try {
            PreparedStatement statement = mySQL.prepareStatement(
                    "UPDATE " + table + " SET " + selected + " = ? WHERE uuid = ?;",
                    data, uuid
            );
            mySQL.execute(statement, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getData(String selected, String uuid, String table) {
        try {
            PreparedStatement statement = mySQL.prepareStatement(
                    "SELECT " + selected + " FROM " + table + " WHERE uuid = ?;",
                    uuid
            );
            ResultSet rs = mySQL.execute(statement, true);
            if (rs.next())
                return rs.getObject(selected);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void close() {
        mySQL.close();
    }
}