package br.com.vener.javafx.csvimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/
public class ConnectorDB {

	private Connection connection;
	private static ConnectorDB connector;

	private ConnectorDB() {

		// String de conexão com o bando de dados
		String strConn = "jdbc:sqlite:" + getClass().getResource("database/CSVImportDB.db");

		try {

			// Realiza a conexão com o BD
			connection = DriverManager.getConnection(strConn);

		} catch (SQLException e) {
			DataUtils.messageError(e.getLocalizedMessage());
			connection = null;
		}

	}

	// Design pattern singleton
	// A classe deve garantir apenas uma instância
	public static ConnectorDB getConnector() {

		// Caso não haja uma conexão realiza a tal
		if (connector == null)
			connector = new ConnectorDB();

		return connector;

	}

	public Connection getConnection() {
		return connection;
	}

}
