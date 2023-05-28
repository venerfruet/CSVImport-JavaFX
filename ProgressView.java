package br.com.vener.javafx.csvimport;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class ProgressView extends Dialog<String> {

	private ProgressBar progressBar;
	private Label label;

	public ProgressView(String information) {

		progressBar = new ProgressBar();
		progressBar.setMaxWidth(Double.MAX_VALUE);

		// define o rótulo de informação
		label = new Label(information);

		// Define o layout do dialago
		VBox box = new VBox();
		box.setPadding(new Insets(10.0));
		box.setSpacing(10.0);

		// Adiciona o rótulo e a barra de progressão ao layout
		box.getChildren().addAll(label, progressBar);

		// define o painel do dialago
		DialogPane dialogPane = new DialogPane();
		dialogPane.setContent(box);
		dialogPane.setPrefWidth(300.0);
		dialogPane.setPadding(new Insets(10.0));

		// Define a janela do dialogo para exibir a barra de progressão
		setTitle(Environments.APP_TITTLE);
		dialogPaneProperty().set(dialogPane);

	}

	public void setInformation(String information) {
		label.setText(information);
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

}
