package br.com.vener.javafx.csvimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/

public class TreatDataPane extends DialogPane {

	private Label labelSample;
	private String delimiter;
	private History history;
	private List<String[]> actions;

	public TreatDataPane(String delimiter) {

		// Inicia os campos
		labelSample = new Label();
		this.delimiter = delimiter;
		history = new History();
		actions = new ArrayList<>();

		// Define a margem interna e o botação
		setPadding(new Insets(10.0));
		getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Define o botão OK
		defineApply();

		// Define o texto de conteúdo
		Label labelTools = new Label("Tratar Dados Importados");
		labelTools.setFont(new Font("Arial", 20));

		// Define layout das ferramentas
		VBox contentTools = new VBox();
		contentTools.setAlignment(Pos.CENTER);
		contentTools.setPrefWidth(500.0);
		contentTools.setSpacing(10.0);
		contentTools.getChildren().addAll(labelTools, sample(), panelRemoveChar(), painelButtons());

		// Define o texto de histórico
		Label labelHistory = new Label("Histórico");
		labelHistory.setFont(new Font("Arial", 20));

		// Define o layou do histórico
		VBox contentHistory = new VBox();
		contentHistory.setAlignment(Pos.CENTER);
		contentHistory.setSpacing(10.0);
		contentHistory.getChildren().addAll(labelHistory, history, buttonRemoveHistory());

		// Define o conteúdo
		HBox content = new HBox();
		content.setSpacing(10.0);
		content.getChildren().addAll(contentTools, contentHistory);

		// Define o conteúdo do diálago
		setContent(content);

	}

	private void defineApply() {

		// Retorna o botão OK do dialago
		Button buttonOK = (Button) lookupButton(ButtonType.OK);

		// Renomeia o botão OK para Aplicar
		buttonOK.setText("Aplicar");

		// Define a ação clique do botão OK
		buttonOK.setOnAction(e -> {

			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(Environments.APP_TITTLE);
					alert.setHeaderText("A ação aplicar é irreversível.");
					alert.setContentText("Deseja continuar?");
					Optional<ButtonType> button = alert.showAndWait();

					if (button.get() == ButtonType.OK) {
						List<String> list = DataUtils.getListData();
						List<String> newList = new ArrayList<>();

						for (String data : list) {
							newList.add(executeHistory(data));
						}

						DataUtils.setListData(newList);

					}

					return null;
				}
			};

			task.setOnFailed(te -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle(Environments.APP_TITTLE);
				alert.setHeaderText("Erro no processamento");
				alert.show();
			});

			new Thread(task).run();

		});
	}

	private Pane sample() {

		// Define o layout
		VBox box = new VBox();
		box.setSpacing(5.0);
		box.setPadding(new Insets(10.0));
		box.setStyle("-fx-border: 2; -fx-border-style: dashed;");

		// Rótulo de indicação
		Label label = new Label("Amostra:");

		// Retorna uma linha de amostra da lista
		labelSample.setText(DataUtils.getListData().get(0));

		box.getChildren().addAll(label, labelSample);

		return box;
	}

	private Pane panelRemoveChar() {

		// Define ferramenta remover caracter
		HBox pane = new HBox();
		pane.setSpacing(5.0);

		// define o rótulo
		Label label = new Label("Remover caracter: ");

		// Define a caixa de texto
		TextField textField = new TextField();
		textField.setMaxWidth(30.0);

		// Define o evento da caixa de texto
		textField.textProperty().addListener((obsevable, oldValue, newValue) -> {

			// Testa a ocorrência de caracater especial
			boolean isMetachar = newValue.matches("[\\\\\\$\\%\\?\\(\\)\\[\\{\\*\\+\\.]+");
			if (isMetachar)
				// Transforma em metacaracter
				newValue = "\\" + newValue;

			// A caixa de texto não pode estar vazia nem ser maior que 1 caracter
			if (textField.getLength() == 1) {

				// Limita o tamanho do texto a 1
				textField.setText(textField.getText().substring(0, 1));

				// Sustitui o caracter
				labelSample.setText(labelSample.getText().replaceAll(newValue, ""));

				// Define a matriz da ação
				String[] action = { "remover todas as ocorrências de " + textField.getText(), newValue };

				// Adiciona a ação ao histórico
				history.newAction(action);

			} else if (textField.getLength() == 2) {

				textField.setText(textField.getText().substring(0, 1));

			}

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					textField.selectAll();
				}
			});

		});

		pane.getChildren().addAll(label, textField);

		return pane;
	}

	private Pane painelButtons() {
		HBox box = new HBox();
		box.setSpacing(5.0);
		box.getChildren().addAll(buttonLTrim(), buttonRTrim());
		return box;
	}

	private Button buttonRTrim() {

		Button button = new Button("Remover espaços da direita");
		button.setMaxWidth(Double.MAX_VALUE);

		// Exclui todos os espações da direita usando regex
		button.setOnMouseClicked(e -> {

			// Gera uma matriz dos dados
			String[] array = labelSample.getText().split(delimiter);

			int index = 0;
			String str = "";

			// Percorre a matriz para tratar os dados
			for (String item : array) {

				// Usa regex para remover espaços do fim
				str = item.replaceAll("\\s+$", "");

				// Concatena os itens tratados da matriz
				if (index > 0)
					str = labelSample.getText() + delimiter + str;

				// Define o texto da amostra
				labelSample.setText(str);
				index++;

			}

			// Adiciona a ação ao histórico;
			String[] action = { "remover todos os espaços da direita.", "\\s+$" };
			history.newAction(action);

		});

		return button;
	}

	private Button buttonLTrim() {
		Button button = new Button("Remover espaços da esquerda");
		button.setMaxWidth(Double.MAX_VALUE);

		// Exclui todos os espações da esquerda usando regex
		button.setOnMouseClicked(e -> {

			// Gera uma matriz dos dados
			String[] array = labelSample.getText().split(delimiter);

			int index = 0;
			String str = "";

			// Percorre a matriz para tratar os dados
			for (String item : array) {

				// Usa regex para remover espaços do inicio
				str = item.replaceAll("^\\s+", "");

				// Concatena os itens tratados da matriz
				if (index > 0)
					str = labelSample.getText() + delimiter + str;

				// Define o texto da amostra
				labelSample.setText(str);
				index++;

			}

			// Adiciona a ação ao histórico;
			String[] action = { "remover todos os espaços da esquerda.", "^\\s+" };
			history.newAction(action);

		});

		return button;
	}

	private Button buttonRemoveHistory() {

		Button button = new Button("Remover item do histórico");
		button.setMaxWidth(Double.MAX_VALUE);

		button.setOnMouseClicked(e -> {

			// Retorna o indice da seleção
			int indice = history.getSelectionModel().getSelectedIndex();

			// Se não houver histórico encerra o método
			if (history.getItems().size() == 0)
				return;

			// Remove o item do histórico
			history.getItems().remove(indice);

			// Remove o item da lista de ações
			actions.remove(indice);

			// Redefinir a amostra
			labelSample.setText(executeHistory(DataUtils.getListData().get(0)));

		});

		return button;

	}

	private String executeHistory(String data) {

		// Executa todas as ações da lista de ações
		for (String[] action : actions) {

			// Transforma a amostra em matriz
			String[] array = data.split(delimiter);

			boolean iniConcat = true;

			for (String value : array) {

				String newValue = value.replaceAll(action[1], "");
				data = iniConcat ? newValue : data + delimiter + newValue;

				iniConcat = false;

			}

		}

		// Exibe o resultado
		return data;

	}

	private class History extends ListView<String> {

		public History() {
			setPrefHeight(140.0);
		}

		public void newAction(String[] action) {

			// Adiciona a ação a lista de ações
			actions.add(action);
			// Adiciona a ação ao histórico
			getItems().add(action[0]);
			// Seleciona o último item do histórico
			getSelectionModel().select(getItems().size() - 1);
			// Rola para o último item do histórico
			history.scrollTo(getItems().size() - 1);

		}

	}

}
