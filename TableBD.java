package br.com.vener.javafx.csvimport;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
@author Vener Fruet da Silveira
* @version 1.0.0
*/

public class TableBD extends TableView<String> {

	private List<String> linesData;
	private String delimiter;

	public TableBD(String tableName) {

		DataUtils.processTableRecords(tableName);
		linesData = DataUtils.getListData();
		delimiter = ";";

		defineTable();

	}

	private void defineTable() {
		this.getColumns().clear();

		// Captura a primeira linha
		String[] firstRow = linesData.get(0).split(delimiter);

		// Cria as colunas e insere os valores de cada registro
		for (String columnName : firstRow) {

			TableColumn<String, String> column;

			// Se exite nome das colunas define a primeira linha como tal
			column = new TableColumn<>(columnName);

			// Adiciona a nova coluna a tabela
			this.getColumns().add(column);

			// Define o valor das células da nova coluna
			column.setCellValueFactory(cdf -> {

				// cdf = fábrica de valores das células
				String values = cdf.getValue();
				String[] cells = values.split(delimiter);

				// Retorna o índice da coluna
				int indexColumn = cdf.getTableView().getColumns().indexOf(cdf.getTableColumn());

				// Define o texto da célula
				if (indexColumn >= cells.length) {
					return new SimpleStringProperty("");
				} else {
					return new SimpleStringProperty(cells[indexColumn]);
				}

			});

			// Indica a lista de valores da tabela
			this.setItems(FXCollections.observableArrayList(linesData));

			// Remove o nome das colunas da lista
			this.getItems().remove(0);

		}
	}

}
