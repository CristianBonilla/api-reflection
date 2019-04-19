package com.db;

import org.h2.tools.Server;
import java.sql.SQLException;

public class DBLauncher {
	public static void main(String[] args) throws SQLException {
		Server.main();
		System.out.println("DB Launched");
	}
}
