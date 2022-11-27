package dev.cybo.tickets.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.cybo.tickets.DevRoomTickets;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQL {

    private final DevRoomTickets ticketBot;
    private HikariDataSource hikariDataSource;

    public PostgreSQL(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
    }

    public PostgreSQL initialize(String hostname, int port, String username, String database, String password) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        hikariConfig.addDataSourceProperty("serverName", hostname);
        hikariConfig.addDataSourceProperty("portNumber", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", username);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.setPoolName("Tickets-PostgreSQL");
        hikariDataSource = new HikariDataSource(hikariConfig);
        return this;
    }

    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}