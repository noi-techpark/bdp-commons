package it.bz.noi.a22elaborations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class Utility
{

	static String readResourceText(Class relativeTo, String name) throws IOException
	{
		InputStream in = (InputStream) relativeTo.getResourceAsStream(name);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		in.close();
		out.close();
		String result = new String(out.toByteArray(), "utf-8");
		return result;
	}

	public static Connection createConnection() throws IOException, SQLException
	{
		// db connection
		String USER = null;
		String PASS = null;
		String HOST = null;
		String PORT = null;
		String DBNAME = null;

		try (InputStream in = Utility.class.getResourceAsStream("db.properties"))
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

}
