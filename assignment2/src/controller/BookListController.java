package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.BookModel;

public class BookListController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(BookListController.class);
	
	@FXML private ListView<BookModel> listviewBooks;
	
	private List<BookModel> listData;
	/*
	public BookListController(ObservableList<BookModel> books) {
		this.listData = books;
	}
	*/
	
	public BookListController(List<BookModel> books) {
		this.listData = books;
	}
	
	@FXML
	void bookSelected(MouseEvent event) {
		if(event.getClickCount() == 2 ) {
			
			BookModel selected = listviewBooks.getSelectionModel().getSelectedItem();
			AppController.getInstance().switchView(ViewType.VIEW2, selected); // selected
			logger.info("Book title pressed." + selected);
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// listviewBooks.setItems((ObservableList<BookModel>) listData);
		
		ObservableList<BookModel> books = listviewBooks.getItems();
		for(BookModel book: listData) {
			books.add(book);
		}
		
	}
}