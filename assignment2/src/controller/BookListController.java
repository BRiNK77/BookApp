package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import model.BookModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// controller for the book list view
public class BookListController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(BookListController.class);

	@FXML
	private ListView<BookModel> listviewBooks;
	@FXML
	private Button deleteB, searchB, firstB, nextB, lastB, prevB;
	@FXML
	TextField searchField, pageNum1, pageNum2;

	private List<BookModel> listData;
	private int searchFlag = 0;
	private int pageNumber;
	private int lastPageNumber;
	private final int pageSize = 50;
	private String search;

	public BookListController(List<BookModel> books) {
		this.listData = books;
		this.pageNumber = 1;
		try {
			this.lastPageNumber = BookGateway.getPageNumber() / 50;
		} catch (GatewayException e) {
			e.printStackTrace();
		}
	}
	
	public BookListController(List<BookModel> books, int num) {
		this.listData = books;
		this.pageNumber = num;
		try {
			this.lastPageNumber = BookGateway.getPageNumber() / 50;
		} catch (GatewayException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void search(ActionEvent event) {
		if(searchField.getText() != null ) {
			searchFlag = 1;
			search = searchField.getText();
			pageNumber = 1;
			try {
				lastPageNumber = BookGateway.getPageNumberSearch(search) / 50;
				this.listData = BookGateway.searchBooks(search, pageNumber, pageSize);
			} catch (GatewayException e) {
				e.printStackTrace();
			}
			
		} else {
			pageNumber = 1;
			searchFlag = 0;
			try {
			lastPageNumber = BookGateway.getPageNumber() / 50;
			this.listData = BookGateway.getBooks(1,pageSize);
			} catch (GatewayException e1) {
				e1.printStackTrace();
			}
		}
		AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
	}
	
	@FXML void first() throws GatewayException {
		logger.info(searchFlag);
		if (searchFlag == 1) {
			logger.info("first page pressed with a search field");
			this.listData = BookGateway.searchBooks(search, 1, pageSize);
			AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
		}
		else {
			logger.info("first page pressed");
			this.listData = BookGateway.getBooks(1,pageSize);
			AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
		}

	}
	
	@FXML void prev() throws GatewayException {
		if (searchFlag == 1) {
			logger.info("previous page pressed with a search field");
			if (pageNumber == 1) {
				logger.info("already in first page cant go back");
			}
			else {
				pageNumber--;
				this.listData = BookGateway.searchBooks(search, pageNumber, pageSize);
				AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
			}
		}
		else {
			logger.info("previous page pressed");
			if (pageNumber == 1) {
				logger.info("already in first page cant go back");
			}
			else {
				pageNumber--;
				this.listData = BookGateway.getBooks(pageNumber,pageSize);
				AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
			}
		}
	}
	
	@FXML void next() throws GatewayException {
		if (searchFlag == 1) {
			logger.info("next page pressed");
			if (pageNumber == lastPageNumber) {
				logger.info("already in last page cant go forward");
			}
			else {
				pageNumber++;
				this.listData = BookGateway.searchBooks(search,pageNumber,pageSize);
				AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
			}
		} else {
			logger.info("next page pressed");
			if (pageNumber == lastPageNumber) {
				logger.info("already in last page cant go forward");
			}
			else {
				
				pageNumber++;
				this.listData = BookGateway.getBooks(pageNumber,pageSize);
				AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
			}
		}
		
	}
	@FXML void last() throws GatewayException {
		
		if (searchFlag == 1) {
			logger.info("last page pressed with search filed");
			this.listData = BookGateway.searchBooks(search,lastPageNumber,pageSize);
			AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
		} else {
			//logger.info("last page pressed with search filed");
			this.listData = BookGateway.getBooks(lastPageNumber,pageSize);
			AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, listData);
		}
	}
	
	// handles delete button action
	@FXML
	void deleteButtonPressed(ActionEvent event) {
		// must check clearance
		if(!AppController.checkPermissions(AppController.clearance, "delete")){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Invalid permissions.");
			alert.setContentText("Access denied.");
			alert.showAndWait();
		}
		if (event.getSource() == deleteB) {
			logger.info("Delete button pressed.");
			BookModel selected = listviewBooks.getSelectionModel().getSelectedItem();

			try {
				BookGateway.deleteBook(selected);
			} catch (GatewayException e) {
				e.printStackTrace();
			}
			AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW1, null);

		}
	}

	// sets up the list with given data
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//System.out.println(this.pageNumber + " " + lastPageNumber);
		pageNum1.setText("" + pageNumber);
		pageNum2.setText("" + lastPageNumber);
		ObservableList<BookModel> books = listviewBooks.getItems();
		for (BookModel book : listData) {
			books.add(book);
		}

		listviewBooks.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {

					BookModel selected = listviewBooks.getSelectionModel().getSelectedItem();
					logger.info("Book title pressed. " + selected);
					AppController.getInstance(AppController.clearance).switchView(ViewType.VIEW2, selected);

				}

			}
		});

	} // end initialize
} // end list controller