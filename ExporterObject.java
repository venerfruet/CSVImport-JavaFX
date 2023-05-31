package br.com.vener.javafx.csvimport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ExporterObject extends Button {

	public ExporterObject(Stage stage) {

		// Define o texto do botão
		setText("Exportar uma tabela para arquivo CSV");

		// Define o evento clique do mouse
		setOnMouseClicked((ev) -> {

			// Exibe o dialago selecionar tabela
			Dialog<String> listTables = DataUtils.listTables();
			listTables.getDialogPane().getButtonTypes().addAll(ButtonType.NEXT, ButtonType.OK, ButtonType.CANCEL);

			// Define o botão ver tabela
			Button showTable = (Button) listTables.getDialogPane().lookupButton(ButtonType.NEXT);
			showTable.setText("Ver Tabela");
			showTable.setOnAction(e -> {

				// Recupera o painel do dialago
				Pane pane = (Pane) listTables.getDialogPane().getContent();

				// Recupera a lista de tabelas
				@SuppressWarnings("unchecked")
				ListView<String> listView = (ListView<String>) pane.getChildren().get(1);

				// Recupera do nome da tabela
				String tableName = listView.getSelectionModel().getSelectedItem();

				// Sai se table não selecionada
				if (tableName == null) {
					DataUtils.messageError("Nenhuma tabale selecionada");
					return;
				}

				// Exibe a tabela
				DataUtils.showTable(tableName);

				listTables.show();

			});

			// Define o evento do botão OK
			Button confirm = (Button) listTables.getDialogPane().lookupButton(ButtonType.OK);
			confirm.setText("Exportar");
			confirm.setOnAction(e -> {
				// Recupera o painel do dialago
				Pane pane = (Pane) listTables.getDialogPane().getContent();

				// Recupera a lista de tabelas
				@SuppressWarnings("unchecked")
				ListView<String> listView = (ListView<String>) pane.getChildren().get(1);

				// Recupera do nome da tabela
				String tableName = listView.getSelectionModel().getSelectedItem();

				// Sai se table não selecionada
				if (tableName == null) {
					DataUtils.messageError("Nenhuma tabale selecionada");
					return;
				}

				// Define o selecionador de arquivo
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Exportar uma tabela");

				// Define a pasta para salvar o arquivo
				File newFile = fileChooser.showSaveDialog(stage);

				// Se não existir um arquivo sai do método
				if (newFile == null)
					return;

				// Deleta o arquivo caso já exista
				if (newFile.exists())
					newFile.delete();

				try {

					// Cria o novo arquivo
					if (newFile.createNewFile()) {

						writeInFile(tableName, newFile);

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle(Environments.APP_TITTLE);
						alert.setHeaderText(tableName + " exportada para o arquivo " + newFile.getAbsolutePath());
						alert.show();

					}

				} catch (IOException e1) {
					DataUtils.messageError(e1.getLocalizedMessage());
				}

			});

			listTables.show();

		});

	}

	private void writeInFile(String tableName, File file) throws IOException {

		// Recupera os dados da tabela
		DataUtils.processTableRecords(tableName);
		List<String> listData = DataUtils.getListData();

		// Abre o arquivo para inserção de dados
		FileWriter fileWriter = new FileWriter(file);

		// Percore a lista de dados e insere no arquivo
		for (String data : listData) {
			fileWriter.write(data + "\n");
		}

		// Fecha o arquivo
		fileWriter.close();

	}

}
