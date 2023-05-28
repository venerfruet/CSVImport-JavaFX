package br.com.vener.javafx.csvimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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
		getChildren().addAll(hasColumnNames, panelDelimiter, panelDB);

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
		Label labelDelimiters = new Label("Delimitador de campos: (requer nova importação)");

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

		otherText.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				other.setSelected(true);
		});

		// Define o evento de alteração do texto da opção outro
		otherText.textProperty().addListener((observable, oldValue, newValue) -> {

			// Sai do método caso o novo valor seja vazio
			if (newValue.equals("")) {
				otherText.setText(oldValue);
				return;
			}

			// Não permite o literal de escape para o delimitador
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

	private void defineOptionsDB(Pane panelDB) {

		// Define o botão adicionar em tabela existente
		Button buttonAddInTable = new Button("Adicionar em uma tabela existente.");
		buttonAddInTable.setOnMouseClicked(e -> {
			listTables();
		});

		// Define o botão criar tabela
		Button buttonAddInNewTable = new Button("Adicionar em uma nova tabela.");
		buttonAddInNewTable.setOnMouseClicked(e -> newTable());

		// Define o tamanho dos botões
		buttonAddInTable.setMaxWidth(Double.MAX_VALUE);
		buttonAddInNewTable.setMaxWidth(Double.MAX_VALUE);

		// Define o rótulo do painel de banco de dados
		Label labelBD = new Label("Operações do Banco de Dados:");

		// Adiciona o rótulo e os botões ao painel de banco de dados
		panelDB.getChildren().addAll(labelBD, buttonAddInTable, buttonAddInNewTable);

	}

	private void newTable() {

		// Solicita o nome para a tabela
		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setTitle(Environments.APP_TITTLE);
		nameDialog.setHeaderText("Digite uma nome para a tabela");

		// Retorna a string nome da tabela
		String nameTable = nameDialog.showAndWait().get();

		// Sai do método caso o nome da tabela esteja vazio
		if (nameTable.equals(""))
			return;

		// Define a declarção dos campos
		String fields = "";
		for (TableColumn<String, ?> column : tableData.getColumns()) {
			fields += "`" + column.getText() + "` varchar(255),";
		}

		// Necessário excluir a última vírgula
		fields = fields.substring(0, fields.length() - 1);

		// Define a declaração SQL DDL para criar tabela
		String sql = "create table `" + nameTable + "` (" + fields + ")";

		try {
			// Prepara a declarção SQL DDL
			PreparedStatement statement = connector().prepareStatement(sql);

			// Executa a declaração
			statement.execute();

			// Insere os dados na nova tabela
			fileToDB(nameTable);

		} catch (SQLException e) {
			DataUtils.messageError(e.getLocalizedMessage());
		}

	}

	private void listTables() {

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
			fileToDB(tableName);

		});

		listTables.show();

	}

	private void fileToDB(String tableName) {

		// Define a barra de progressão
		ProgressView progressView = new ProgressView("Aguarde... importando dados.");
		ProgressBar progressBar = progressView.getProgressBar();
		progressView.show();

		double progressMax = tableData.getItems().size() - 1;
		double progressCur = 0.0;

		// Lopping nos valores da tabela
		for (String item : tableData.getItems()) {

			double step = progressCur;
			Platform.runLater(() -> progressBar.setProgress(step / progressMax));

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

			try {
				// Prepara a declarção SQL DDL
				PreparedStatement statement = connector().prepareStatement(sql);
				// Executa a declaração
				statement.execute();
			} catch (SQLException e) {
				DataUtils.messageError(e.getLocalizedMessage());
				progressView.setInformation("Importação não concluída.");
				progressView.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
				return;
			}

			progressCur++;

		}

		progressView.setInformation("Importação concluída.");
		progressView.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

		DataUtils.showTable(tableName);

	}

	private Connection connector() {
		// Obtém a conexão com o banco de dados
		return ConnectorDB.getConnector().getConnection();
	}

	public CheckBox getHasColumnNames() {
		return hasColumnNames;
	}

	public ToggleGroup getDelimiterGroup() {
		return delimiterGroup;
	}

}
