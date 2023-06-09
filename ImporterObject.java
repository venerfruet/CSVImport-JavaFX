package br.com.vener.javafx.csvimport;

import java.io.File;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/

public class ImporterObject extends Button {

	public ImporterObject(TableFile tableData, Stage stage, CheckBox columnNames, Node panelResult,
			ToggleGroup delimiterOptions) {

		// Define o texto do botão
		setText("Importar um arquivo CSV");

		// Define o evento clique do mouse
		setOnMouseClicked((e) -> {

			// Define o selecionador de arquivo
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Importar arquivo");

			// Define o arquivo a ser lido
			File file = fileChooser.showOpenDialog(stage);

			// Se existir um arquivo continua
			if (file != null) {

				// Associa os dados do arquivo a classe estática para serem usados em toda a
				// aplicação
				DataUtils.processFileRecords(file);

				// Exibe os dados na tabela
				tableData.populateColumns(DataUtils.getListData(), columnNames.isSelected(),
						delimiterOptions.getSelectedToggle().getUserData().toString());

				// Ativa o painel da tabela e as opções de manipulação dos dados
				panelResult.setDisable(false);

			}

		});

	}
}
