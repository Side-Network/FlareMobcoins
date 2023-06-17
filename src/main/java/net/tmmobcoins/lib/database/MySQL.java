package net.tmmobcoins.lib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MySQL {

    private static HikariDataSource hikari;
    private RowSetFactory factory;

    public MySQL(String server, String user, String password, String database, String port) {
        HikariConfig hikariConfig = new HikariConfig();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            hikariConfig.setJdbcUrl("jdbc:mysql://" + server + ":" + port + "/" + database + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf8");
            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        hikari = new HikariDataSource(hikariConfig);
    }

    public boolean isConnected() {
        return hikari.isRunning();
    }

    public PreparedStatement prepareStatement(String query, String... vars) throws SQLException {
        PreparedStatement statement = hikari.getConnection().prepareStatement(query);

        if (vars.length > 0) {
            for (int i = 1; i < vars.length + 1; i++) {
                if (!query.contains("?"))
                    break;

                if (vars[i - 1].equalsIgnoreCase("$TIMe"))
                    statement.setTimestamp(i, new Timestamp(System.currentTimeMillis()));
                else
                    statement.setString(i, vars[i - 1]);
            }
        }

        return statement;
    }

    public HikariDataSource getDatabase() {
        return hikari;
    }

    private CachedRowSet getCachedRowSet() throws SQLException {
        if (factory == null)
            factory = RowSetProvider.newFactory();

        return factory.createCachedRowSet();
    }

    public synchronized ResultSet execute(final PreparedStatement statement, boolean needReturn) throws SQLException {
        CachedRowSet cachedSet = null;

        try {
            if (needReturn) {
                cachedSet = getCachedRowSet();
                cachedSet.populate(statement.executeQuery());
            }

            statement.execute();
        } finally {
            if (statement != null)
                statement.close();
            if (statement.getConnection() != null)
                statement.getConnection().close();
        }

        if (needReturn)
            return cachedSet;

        return null;
    }

    public void close() {
        hikari.close();
    }
}