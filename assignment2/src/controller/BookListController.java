package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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
	private Button deleteB;

	private List<BookModel> listData;

	public BookListController(List<BookModel> books) {
		this.listData = books;
	}

	// handles delete button action
	@FXML
	void deleteButtonPressed(ActionEvent event) {
		if (event.getSource() == deleteB) {
			logger.info("Delete button pressed.");
			BookModel selected = listviewBooks.getSelectionModel().getSelectedItem();

			try {
				BookGateway.deleteBook(selected);
			} catch (GatewayException e) {
				e.printStackTrace();
			}
			AppController.getInstance().switchView(ViewType.VIEW1, null);

		}
	}

	// sets up the list with given data
	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
					AppController.getInstance().switchView(ViewType.VIEW2, selected);

				}

			}
		});

	} // end initialize
} // end list controller