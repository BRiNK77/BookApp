package controller;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;
import model.AuthorBookModel;
import model.AuthorModel;
import model.BookModel;
import javafx.scene.control.TextField;

public class NewAuthorBookController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger();
	private AuthorBookModel authorBook;

	@FXML
	private ComboBox<AuthorModel> AuthorList;
	@FXML
	private ComboBox<BookModel> BookList;
	@FXML
	TextField royalty;
	@FXML
	private Button saveB, backB;

	public List<AuthorModel> authors;
	public List<BookModel> books;

	public NewAuthorBookController(AuthorBookModel arg) {
		this.authorBook = arg;
		AuthorList = new ComboBox<AuthorModel>();
		BookList = new ComboBox<BookModel>();
		try {
			authors = BookGateway.getAllAuthors();
		} catch (GatewayException e) {
			e.printStackTrace();
		}
		books = BookGateway.getBooks();

	}
	/*
	public AuthorBookController() {
		authorBook = new AuthorBookModel();
		AuthorList = new ComboBox<AuthorBookModel>();
		BookList = new ComboBox<BookModel>();
		try {
			authors = BookGateway.getAllAuthors();
		} catch (GatewayException e) {
			e.printStackTrace();
		}
		books = BookGateway.getBooks();
	}
*/
	@FXML
	void save() {
		
		authorBook.setAuthor(AuthorList.getValue());
		authorBook.setBook(BookList.getValue());
		authorBook.setRoyalty(Integer.parseInt(royalty.getText())); 
		
		if (!authorBook.isValidAuthor(authorBook.getAuthor())) {
			
			logger.error("Invalid Author " + authorBook.getAuthor().getFirst() + " "
					+ authorBook.getAuthor().getLast());
			AlertHelper.showWarningMessage("Error", "Author Error",
					"There seems to be an error with your author please double check and try again");
			return;
		}
		if (!authorBook.isValidBook(authorBook.getBook())) {
			logger.error("Invalid Book " + authorBook.getBook().getTitle());
			AlertHelper.showWarningMessage("Error", "Book Error",
					"There seems to be an error with your book please double check and try again");
			return;
		}
		if (!authorBook.isValidRoyalty(authorBook.getRoyalty())) {
			logger.error("Invalid Royalty " + authorBook.getRoyalty());
			AlertHelper.showWarningMessage("Error", "Royalty Error",
					"There seems to be an error with your royalty please double check and try again");
			return;
		}
		System.out.println(authorBook);
		authorBook.saveAuthorBook();
	}

	@FXML
	void back() {
		if(this.authorBook.getNewRecord() == false) {
			try {
				AppController.getInstance().switchView(ViewType.VIEW2, BookGateway.getBookById(this.authorBook.getBook().getID()));
			} catch (GatewayException e) {
				e.printStackTrace();
			
			}
		} else {
			AppController.getInstance().switchView(ViewType.VIEW1, null);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//AuthorList.valueProperty().bindBidirectional(authorBook.getAuthorProperty());
		//BookList.valueProperty().bindBidirectional(authorBook.getBookProperty());
		//royalty.textProperty().bindBidirectional(authorBook.getRoyaltyProperty(), new NumberStringConverter());
		//System.out.println(authors);
		AuthorList.getItems().addAll(authors);
		AuthorList.setValue(this.authorBook.getAuthor());
		
		BookList.getItems().addAll(books);
		BookList.setValue(this.authorBook.getBook());
		
		royalty.setText(Integer.toString(this.authorBook.getRoyalty()));
		
		
		
		
	}
}