package com.unifun.db;

import com.unifun.utils.PropertyReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariCPDataSource {

	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;
	private PropertyReader reader = new PropertyReader();
	private String url ;
	private String username;
	private String password;
	private int connectionPoolSize;
	public HikariCPDataSource() {
		final Properties properties = reader.readParamFromFile();
		url = properties.getProperty("dbConnectionUrl");
		username = properties.getProperty("dbUsername");
		password = properties.getProperty("dbPassword");
		connectionPoolSize = Integer.parseInt(properties.getProperty("dbConnectionPoolSize"));

		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaximumPoolSize(connectionPoolSize);
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