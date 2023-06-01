// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.a22elaborations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("classpath:it/bz/noi/a22elaborations/db.properties")
@PropertySource("classpath:it/bz/noi/a22elaborations/elaborations.properties")
public class Utility
{
	static String readResourceText(Class<?> relativeTo, String name) throws IOException
	{
		InputStream in = relativeTo.getResourceAsStream(name);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		in.close();
		out.close();
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}

	public static Connection createConnection() throws IOException, SQLException
	{
		// db connection
		String USER = null;
		String PASS = null;
		String HOST = null;
		String PORT = null;
		String DBNAME = null;

		try (InputStream in = Utility.class.getResourceAsStream("db-local.properties"))
		{
			Properties prop = new Properties();
			prop.load(in);
			USER = prop.getProperty("USER");
			PASS = prop.getProperty("PASSWORD");
			DBNAME = prop.getProperty("DBNAME");
			HOST = prop.getProperty("HOST");
			PORT = prop.getProperty("PORT");
		}

		return DriverManager.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DBNAME, USER, PASS);
	}

	@Value("${HOST}")
	private String host;

	@Value("${USER}")
	private String user;

	@Value("${PASSWORD}")
	private String password;

	@Value("${PORT}")
	private String port;

	@Value("${DBNAME}")
	private String dbname;

	public Connection getConnection() throws SQLException {

		return DriverManager.getConnection(
			"jdbc:postgresql://" + host + ":" + port + "/" + dbname,
			user,
			password
		);
	}


}
