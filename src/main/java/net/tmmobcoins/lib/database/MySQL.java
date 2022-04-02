package net.tmmobcoins.lib.database;

import net.tmmobcoins.lib.base.ColorAPI;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQL {
    private Connection connection;
    public SQL sqlIO = new SQL(this);
    String host;
    String user;
    String password;
    String database;
    String port;
    String driver;

    public MySQL(String host, String user, String password, String database, String port, String driver) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
        this.driver = driver;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection() {
        if (host != null && user != null && password != null && database != null) {
            this.disconnect();
            try {
                if (driver.length() == 0) {
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cSQL driver is missing. Use: &fMySql"));
                } else {
                    this.connection = DriverManager.getConnection("jdbc:" + driver + "://" + host + ":" + port + "/" + database + "?autoReconnect=true&maxReconnects=10", user, password);
                    Bukkit.getLogger().log(Level.INFO, ColorAPI.process("&7(( &aDONE &7)) &aConnected successful to &f" + driver));
                }
            } catch (Exception var7) {
                Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase connection error. More info: &f" + var7.getMessage()));
            }
        }

    }

    public void connect() {
        if (!this.isConnected()) {
            if (host.length() == 0) {
                Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase host is blank!"));
            } else if (user.length() == 0) {
                Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase user is blank!"));
            } else if (database.length() == 0) {
                Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase database is blank!"));
            } else if (port.length() == 0) {
                Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase port is blank!"));
            } else {
                this.setConnection();
            }
        }

    }

    private void disconnect() {
        try {
            if (this.isConnected()) {
                this.connection.close();
            }
        } catch (Exception var2) {
            Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &cERROR &7)) &cDatabase disconnecting error. More info: &f" + var2.getMessage()));
        }

        this.connection = null;
    }

    public boolean isConnected() {
        if (this.connection != null) {
            try {
                return !this.connection.isClosed();
            } catch (Exception var2) {
            }
        }

        return false;
    }

    public boolean update(String command) {
        if (command == null) {
            return false;
        } else {
            boolean result = false;
            this.connect();

            try {
                if (this.connection != null) {
                    Statement statement = this.connection.createStatement();
                    statement.executeUpdate(command);
                    statement.close();
                    result = true;
                }
            } catch (Exception var5) {
                String message = var5.getMessage();
                if (message != null) {
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cUnable to send an update to the database more info:"));
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cCommand: " + command));
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cError: " + message));
                }
            }

            this.disconnect();
            return result;
        }
    }

    public ResultSet query(String command) {
        if (command == null) {
            return null;
        } else {
            this.connect();
            ResultSet set = null;

            try {
                if (this.connection != null) {
                    Statement statement = this.connection.createStatement();
                    set = statement.executeQuery(command);
                }
            } catch (Exception var4) {
                if (var4.getMessage() != null) {
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cUnable to query the database more info:"));
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cCommand: " + command));
                    Bukkit.getLogger().log(Level.SEVERE, ColorAPI.process("&7(( &4SQL ERROR &7)) &cError: " + var4.getMessage()));
                }
            }

            return set;
        }
    }
}