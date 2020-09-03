package com.unifun.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPDataSource {

	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;

	public HikariCPDataSource() {
		config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/sms_notification_babilon_tj");
		config.setUsername("root");
		config.setPassword("HVVUgOJ7AOWPcXUW");
		config.setMaximumPoolSize(10);
		config.setAutoCommit(true);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setDriverClassName("com.mysql.jdbc.Driver");
		ds = new HikariDataSource(config);
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

}