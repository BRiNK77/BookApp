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
import javafx.event.EventHandler;

public class BookListController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(BookListController.class);
	
	@FXML private ListView<BookModel> listviewBooks;
	private List<BookModel> listData;

	public BookListController(List<BookModel> books) {
		this.listData = books;
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// listviewBooks.setItems((ObservableList<BookModel>) listData);
		
		ObservableList<BookModel> books = listviewBooks.getItems();
		for(BookModel book: listData) {
			books.add(book);
		}
		
		listviewBooks.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2 ) {
					
					BookModel selected = listviewBooks.getSelectionModel().getSelectedItem();
					logger.info("Book title pressed." + selected);
					AppController.getInstance().switchView(ViewType.VIEW2, selected); 
					
				}
				
			}	
		});
	}
}