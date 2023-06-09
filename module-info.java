/**
 * 
 */
/**
 * @author Vener Fruet da Silveira
 * @version 2023-05-16
 */
module CSVImport {

	requires javafx.controls;
	requires javafx.media;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires java.sql;

	opens br.com.vener.javafx.csvimport to javafx.controls, javafx.media, javafx.fxml, javafx.graphics;

}