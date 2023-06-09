package br.com.vener.javafx.csvimport;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/

public class ProgressView extends Dialog<String> {

	private ProgressBar progressBar;
	private Label labelInformation;
	private Label labelProgress;

	public ProgressView(String information) {

		setResizable(true);

		progressBar = new ProgressBar();
		progressBar.setMaxWidth(Double.MAX_VALUE);

		// Define o rótulo de informação
		labelInformation = new Label(information);
		labelInformation.setTextAlignment(TextAlignment.CENTER);

		// Define a saida de progresso
		labelProgress = new Label();
		labelProgress.setWrapText(true);
		labelProgress.setMaxHeight(Double.MAX_VALUE);
		HBox panelProgress = new HBox();
		panelProgress.setAlignment(Pos.CENTER);
		panelProgress.getChildren().addAll(new Label("Processo atual: "), labelProgress);

		// Define o layout do dialago
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(10.0));
		box.setSpacing(10.0);

		// Adiciona o rótulo e a barra de progressão ao layout
		box.getChildren().addAll(labelInformation, progressBar, labelProgress);

		// define o painel do dialago
		DialogPane dialogPane = new DialogPane();
		dialogPane.setContent(box);
		dialogPane.setPrefWidth(300.0);
		dialogPane.setPrefHeight(200.0);
		dialogPane.setPadding(new Insets(10.0));

		// Define a janela do dialogo para exibir a barra de progressão
		setTitle(Environments.APP_TITTLE);
		dialogPaneProperty().set(dialogPane);

	}

	public void setInformation(String information) {
		labelInformation.setText(information);
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public Label getLabelProgress() {
		return labelProgress;
	}

}
