package controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.BookModel;

public class DetailedController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(DetailedController.class);
	
	// for part 5
	@FXML private TextField bookTitle, published, ISBN;
	@FXML private TextArea bookSum;
	
	private BookModel aBook;
	private BookModel bookCopy;
	
	@FXML private Button saveB;
	// Trying to implement step 5 here
	
	public DetailedController(BookModel book) {
		this.aBook = book;
		this.bookCopy = book;
		
	}
	
	@FXML void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
			try {
				save();
			} catch (ValidationException | GatewayException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean save() throws ValidationException, GatewayException {
		aBook.setTitle(bookCopy.getTitle());
		aBook.setSummary(bookCopy.getSummary());
		aBook.setYearPublished(bookCopy.getYearPublished());
		aBook.setISBN(bookCopy.getISBN());
		
		try {
			aBook.saveBook();
		} catch (javax.xml.bind.ValidationException e) {
			logger.error("Could not save: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Changes saved");
		alert.setHeaderText(null);
		alert.setContentText("Changes saved successfully!");
		
		alert.showAndWait();
		
		return true;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitle.setText(this.bookCopy.getTitle());
		bookSum.setText(this.bookCopy.getSummary());
		published.setText("" + this.bookCopy.getYearPublished());
		ISBN.setText(this.bookCopy.getISBN());
	}
}
