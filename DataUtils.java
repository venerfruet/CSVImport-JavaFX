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
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class DataUtils {

	private static List<String> listData = new ArrayList<>();

	public static void processFileRecords(File file) {

		// Define a informação de progresso
		ProgressView progressView = new ProgressView("Importando os dados do arquivo " + file.getName());

		// Cria uma nova tarefa em segundo plano
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

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
					messageError(e.getLocalizedMessage());
				}

				return null;
			}

		};

		// Falha da tarefa
		task.setOnFailed(te -> {
			progressView.setInformation(te.getSource().getException().getLocalizedMessage());
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);
		});

		// Tarefa executada
		task.setOnSucceeded(te -> {
			progressView.setInformation("Arquivo importado com sucesso.");
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);
			progressView.close();
		});

		new Thread(task).start();

		progressView.showAndWait();

	}

	public static void processTableRecords(String tableName) {

		// Define a informação de progresso
		ProgressView progressView = new ProgressView("Processando os dados da tabela " + tableName);

		// Cria uma nova tarefa em segundo plano

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

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
					messageError(e.getLocalizedMessage());
					return null;
				}

				return null;
			}
		};

		// Falha da tarefa
		task.setOnFailed(te -> {
			progressView.setInformation(te.getSource().getException().getLocalizedMessage());
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);
		});

		// Tarefa executada
		task.setOnSucceeded(te -> {
			progressView.setInformation("Dados da tabela " + tableName + " processados com sucesso.");
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);
			progressView.close();
		});

		new Thread(task).start();

		progressView.showAndWait();

	}

	public static void fileToDB(String tableName, TableFile tableData, ToggleGroup delimiterGroup) {

		// Retorna da quantidade de itens
		final int totalItems = tableData.getItems().size();
		;

		// Define a informação de progresso
		final ProgressView progressView = new ProgressView("Enviando dados para a tabela " + tableName);
		final ProgressBar progressBar = progressView.getProgressBar();
		final Label labelProgress = progressView.getLabelProgress();

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				// Define qual o item atual
				int curItem = 0;

				// Lopping nos valores da tabela
				for (String item : tableData.getItems()) {

					// Incrementa o item atual
					curItem++;

					// Atualiza a progressão
					updateProgress(curItem, totalItems);
					updateMessage(curItem + " de " + totalItems);

					// Separa a linha em valores de campos
					String[] values = item.split(delimiterGroup.getSelectedToggle().getUserData().toString(), -1);

					// Define os valores para a declaração DML
					String sqlValues = "";
					for (String value : values) {
						sqlValues += "\"" + value + "\",";
					}

					// Necessário remover a última vírgula
					sqlValues = sqlValues.substring(0, sqlValues.length() - 1);

					// Define a declaração SQL DDL para criar tabela
					String sql = "insert into `" + tableName + "` values(" + sqlValues + ")";

					// Retorna a conexão com o banco de dados
					Connection connector = ConnectorDB.getConnector().getConnection();

					try {

						// Prepara a declarção SQL DDL
						PreparedStatement statement = connector.prepareStatement(sql);
						// Executa a declaração
						statement.execute();

					} catch (SQLException e) {
						DataUtils.messageError(e.getLocalizedMessage());
						return null;
					}

				}

				updateMessage("");
				return null;

			}
		};

		// Falha da tarefa
		task.setOnFailed(te -> {
			progressView.setInformation(te.getSource().getException().getLocalizedMessage());
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);
		});

		// Tarefa executada
		task.setOnSucceeded(te -> {

			progressView.setInformation(totalItems + " registros inseridos na tabela " + tableName);
			progressView.getDialogPane().getButtonTypes().add(ButtonType.OK);

			showTable(tableName);

		});

		// Define informações de progressão da operação
		progressBar.progressProperty().bind(task.progressProperty());
		labelProgress.textProperty().bind(task.messageProperty());

		new Thread(task).start();

		// Exibe a progressão da operação
		progressView.showAndWait();

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
