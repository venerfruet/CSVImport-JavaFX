package br.com.vener.javafx.csvimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class DataUtils {

	private static List<String> listData = new ArrayList<>();

	public static void processFileRecords(File file) {

		// Limpa a lista para receber novos registros
		listData.clear();

		try {

			// Abre o arquivo com o scanner
			Scanner scn;
			scn = new Scanner(file);

			// Percorre todo o arquivo e adiciona as lista a uma lista
			String record;
			while (scn.hasNextLine()) {
				record = scn.nextLine();
				listData.add(record);
			}

			/// fecha o scanner
			scn.close();

		} catch (FileNotFoundException | NullPointerException e) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle(Environments.APP_TITTLE);
			alert.setHeaderText("Abrir arquivo");
			alert.setContentText(e.getLocalizedMessage());
			alert.showAndWait();

		}

	}

	public static void processTableRecords(String tableName) {

		// Limpa a lista para receber novos registros
		listData.clear();

		// Conecta ao banco de dados
		Connection connector = ConnectorDB.getConnector().getConnection();

		// Define a declaração SQL DDL para criar tabela
		String sql = "select * from `" + tableName + "`";

		try {

			// Prepara a declarção SQL DDL
			PreparedStatement statement = connector.prepareStatement(sql);
			// Executa a declaração
			ResultSet rs = statement.executeQuery();

			// Cria as colunas e insere os valores de cada registro
			ResultSetMetaData metaData = rs.getMetaData();
			String record = "";

			// Percorre as colunas para montar a string de nomes
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				if (i == 0) {
					record = metaData.getColumnName(i + 1);
				} else {
					record += ";" + metaData.getColumnName(i + 1);
				}
			}

			// Adicionar a string de nomes as lista de dados
			listData.add(record);

			// Percorre os registros para montar uma string de registro
			while (rs.next()) {

				for (int i = 0; i < metaData.getColumnCount(); i++) {
					if (i == 0) {
						record = rs.getString(i + 1);
					} else {
						record += ";" + rs.getString(i + 1);
					}

				}

				// Adiciona o registro as lista de dados
				listData.add(record);
			}

		} catch (

		SQLException e) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle(Environments.APP_TITTLE);
			alert.setHeaderText("Erro ao executar SQL");
			alert.setContentText(e.getLocalizedMessage());
			alert.showAndWait();

			e.printStackTrace();

			return;

		}
	}

	public static Dialog<String> listTables() {

		try {

			// Define o layout do dialago
			VBox box = new VBox();
			box.setPadding(new Insets(10.0));
			box.setSpacing(10.0);

			// define o rótulo da lista
			Label label = new Label("Selecione uma tabela:");

			// Define a lista de tabelas
			ListView<String> listView = new ListView<String>();
			listView.setPrefHeight(300.0);

			// Adiciona o tótulo e a lista ao layout
			box.getChildren().addAll(label, listView);

			// define o painel do dialago
			DialogPane dialogPane = new DialogPane();
			dialogPane.setContent(box);

			// Define o dialogo para selecionar a tabela
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle(Environments.APP_TITTLE);
			dialog.dialogPaneProperty().set(dialogPane);

			// Retorna o nome das tabelas
			Connection connector = ConnectorDB.getConnector().getConnection();
			ResultSet rs = connector.getMetaData().getTables(null, null, null, null);
			ObservableList<String> names = FXCollections.observableArrayList();
			while (rs.next()) {
				String name = rs.getString("TABLE_NAME");
				if (!name.equals("sqlite_schema"))
					names.add(name);
			}

			// Adiciona os nomes a lista de tabelas
			listView.setItems(names);

			// Retorna o dialago
			return dialog;

		} catch (SQLException e) {
			messageError(e.getLocalizedMessage());
			return null;
		}

	}

	public static void showTable(String tableName) {

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(Environments.APP_TITTLE);

		// define o painel do dialago
		DialogPane dialogPane = new DialogPane();
		dialogPane.setContent(new TableBD(tableName));
		dialogPane.setPrefWidth(500.0);
		dialogPane.setPadding(new Insets(10.0));

		dialog.dialogPaneProperty().set(dialogPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		dialog.showAndWait();

	}

	public static void messageError(String message) {

		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(Environments.APP_TITTLE);
		alert.setHeaderText("Erro de processamento.");
		alert.setContentText(message);
		alert.showAndWait();

	}

	public static List<String> getListData() {
		return listData;
	}

}
