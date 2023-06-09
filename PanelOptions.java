package br.com.vener.javafx.csvimport;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/

public class PanelOptions extends VBox {

	private CheckBox hasColumnNames;
	private ToggleGroup delimiterGroup;
	private TableFile tableData;

	public PanelOptions(TableFile tableData) {

		this.tableData = tableData;

		setSpacing(15.0);

		// Define a opção cabeçalho das colunas
		defineHasColunmsName();

		// Define o grupo de opções de delimitadores
		defineDelimiterGroup();

		// Define uma painel para as opções de delimitação
		VBox panelDelimiter = new VBox();
		panelDelimiter.setSpacing(3.0);
		definePanelDelimiters(panelDelimiter);

		// Define o painel para as operações de banco de dados
		VBox panelDB = new VBox();
		panelDB.setSpacing(3.0);
		defineOptionsDB(panelDB);

		// Adiona os objetos ao painel de opções
		getChildren().addAll(hasColumnNames, panelDelimiter, defineTreatData(), panelDB);

	}

	private void onToggle() {
		if (DataUtils.getListData().size() > 0)
			tableData.populateColumns(DataUtils.getListData(), hasColumnNames.isSelected(),
					delimiterGroup.getSelectedToggle().getUserData().toString());
	}

	private void defineHasColunmsName() {
		hasColumnNames = new CheckBox("A primeira linha contém nome das colunas.");
		hasColumnNames.setIndeterminate(false);

		hasColumnNames.setOnMouseClicked((e) -> {
			tableData.populateColumns(DataUtils.getListData(), hasColumnNames.isSelected(),
					delimiterGroup.getSelectedToggle().getUserData().toString());
		});

	}

	private void defineDelimiterGroup() {
		delimiterGroup = new ToggleGroup();

		delimiterGroup.selectedToggleProperty().addListener(o -> {
			onToggle();
		});
	}

	private void definePanelDelimiters(Pane panelDelimiter) {

		// Define a opção de ponto e vírgula e define como valor padrão;
		RadioButton semicolon = new RadioButton("ponto e vírgula(;)");
		semicolon.setUserData(";");
		semicolon.setSelected(true);

		// Define a opção de vírgula;
		RadioButton comma = new RadioButton("vírgula(,)");
		comma.setUserData(",");

		// Define a opção de cerquilha;
		RadioButton hash = new RadioButton("cerquilha(#)");
		hash.setUserData("#");

		// Define a opção de tabulação;
		RadioButton tabulation = new RadioButton("tabulação");
		tabulation.setUserData("\t");

		// Define a opção outro
		Pane other = optionOther("&");

		// Associa as opções ao grupo
		semicolon.setToggleGroup(delimiterGroup);
		comma.setToggleGroup(delimiterGroup);
		hash.setToggleGroup(delimiterGroup);
		tabulation.setToggleGroup(delimiterGroup);

		// Define o rótulo do painel de delimitação
		Label labelDelimiters = new Label("Delimitador de campos:");

		// Adiciona o rótulo e as opções ao painel
		panelDelimiter.getChildren().addAll(labelDelimiters, semicolon, comma, hash, tabulation, other);

	}

	private Pane optionOther(String delimiter) {

		// Define um painel para opção outro
		HBox panelOther = new HBox();
		panelOther.setSpacing(3.0);

		// Define entrada do texto da opção outro;
		TextField otherText = new TextField();
		otherText.setMaxWidth(30.0);
		otherText.setText(delimiter);

		// Define a opção outro
		RadioButton other = new RadioButton("outro");
		other.setUserData(otherText.getText());
		other.setToggleGroup(delimiterGroup);

		// Define o evento ao entrar na caixa de texto
		otherText.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				// Marca opção outros como selecionado
				other.setSelected(true);
		});

		// Define o evento de alteração do texto da opção outro
		otherText.textProperty().addListener((observable, oldValue, newValue) -> {

			// Sai do método caso o novo valor seja vazio
			if (newValue.equals("")) {
				otherText.setText(oldValue);
				return;
			}

			// Não permite o caracteres especiais para o delimitador
			if (newValue.substring(0, 1).equals("\\")) {
				otherText.setText(oldValue);
				return;
			}

			// Limita o tamanho do texto a 1
			otherText.setText(newValue.substring(0, 1));

			// Define o valor da opção outro
			other.setUserData(otherText.getText());

			// Sai do método caso o último valor seja vazio
			if (oldValue.equals(""))
				return;

			// Executa o metodo onToggle caso mude o valor
			if (!oldValue.subSequence(0, 1).equals(newValue))
				onToggle();

		});

		// Adiciona a opção e o texto para outro ao painel outro
		panelOther.getChildren().addAll(other, otherText);

		// Retorna o objeto opção outro
		return panelOther;
	}

	private Button defineTreatData() {

		// Define o botão adicionar em tabela existente
		Button buttonTreatData = new Button("Tratar os dados da lista");
		buttonTreatData.setMaxWidth(Double.MAX_VALUE);

		// Evento onClick do botão
		buttonTreatData.setOnMouseClicked(e -> {

			// Exibe o dialogo de tratamento dos daos
			Dialog<Void> dialog = new Dialog<>();
			dialog.setTitle(Environments.APP_TITTLE);
			dialog.setDialogPane(new TreatDataPane(delimiterGroup.getSelectedToggle().getUserData().toString()));
			dialog.showAndWait();

			// Redefine a tabela dos dados importados
			tableData.populateColumns(DataUtils.getListData(), hasColumnNames.isSelected(),
					delimiterGroup.getSelectedToggle().getUserData().toString());

		});

		return buttonTreatData;

	}

	private void defineOptionsDB(Pane panelDB) {

		// Define o botão adicionar em tabela existente
		Button buttonAddInTable = new Button("Adicionar em uma tabela existente");
		buttonAddInTable.setOnMouseClicked(e -> {
			listTables(false);
		});

		// Define o botão criar tabela
		Button buttonAddInNewTable = new Button("Adicionar em uma nova tabela");
		buttonAddInNewTable.setOnMouseClicked(e -> DataUtils.newTable(tableData, delimiterGroup));

		// Define o tamanho dos botões
		buttonAddInTable.setMaxWidth(Double.MAX_VALUE);
		buttonAddInNewTable.setMaxWidth(Double.MAX_VALUE);

		// Define o rótulo do painel de banco de dados
		Label labelBD = new Label("Operações do Banco de Dados:");

		// Adiciona o rótulo e os botões ao painel de banco de dados
		panelDB.getChildren().addAll(labelBD, buttonAddInTable, buttonAddInNewTable);

	}

	public void listTables(boolean removeTableOnError) {

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
		confirm.setText("Importar");
		confirm.setOnAction(e -> {

			// Recupera o painel do dialago
			Pane pane = (Pane) listTables.getDialogPane().getContent();

			// Recupera a lista de tabelas
			@SuppressWarnings("unchecked")
			ListView<String> listView = (ListView<String>) pane.getChildren().get(1);

			// Recupera do nome da tabela
			String tableName = listView.getSelectionModel().getSelectedItem();

			// Adiciona os valores da tabela no banco de dados
			DataUtils.fileToDB(tableName, tableData, delimiterGroup, removeTableOnError);

		});

		listTables.show();

	}

	public CheckBox getHasColumnNames() {
		return hasColumnNames;
	}

	public ToggleGroup getDelimiterGroup() {
		return delimiterGroup;
	}

}
