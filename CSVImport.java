package br.com.vener.javafx.csvimport;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CSV Import
 * Programa para importar para banco de dados arquivos delimitados e exportar para arquivo delimitado.
 * @author Vener Fruet da Silveira
 * @version 1.0.0
 */
public class CSVImport extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Define um painel vertical como nó principal
		VBox root = new VBox();

		// Define o espaço de preenchimeto, o espaçamento dos objetos e as dimensões do
		// nó principal
		root.setPadding(new Insets(10.0));
		root.setSpacing(10.0);
		root.setPrefWidth(800.0);
		root.setPrefHeight(300.0);

		// Define um painel horizontal para alinhar tabela e o painel de opções na tela
		HBox panelTable = new HBox();
		panelTable.setSpacing(10.0);
		panelTable.setDisable(true);

		// Define a tabela para exibir dados
		TableFile tableData = new TableFile();
		tableData.setPrefWidth(500.0);

		// Define um painel para as opções de manipulação dos dados
		PanelOptions panelOptions = new PanelOptions(tableData);

		// Adicona a lista de dados ao painel
		panelTable.getChildren().addAll(tableData, panelOptions);

		// Define o botão de importação
		ImporterObject buttonImport = new ImporterObject(tableData, primaryStage, panelOptions.getHasColumnNames(),
				panelTable, panelOptions.getDelimiterGroup());

		// Define o botão de exportação
		ExporterObject buttonExport = new ExporterObject(primaryStage);

		// Define o painel para os botões;
		HBox panelButtons = new HBox();
		panelButtons.setSpacing(10.0);

		// Adiciona os botões ao painel de botões
		panelButtons.getChildren().addAll(buttonImport, buttonExport);

		// Adiciona os paineis ao nó principal
		root.getChildren().addAll(panelButtons, panelTable);

		// Cria a cena da aplicação
		Scene scene = new Scene(root);

		// define a exibição da aplicação
		primaryStage.setTitle(Environments.APP_TITTLE);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
