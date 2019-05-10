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
import model.AuthorModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AuthorListController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(AuthorListController.class);
	
	@FXML
	private ListView<AuthorModel> listviewAuthors;
	@FXML
	private Button deleteB;
	
	private List<AuthorModel> listData;
	
	public AuthorListController(List<AuthorModel> authors) {
		this.listData = authors;
	}

	// handles delete button action
		@FXML
		void deleteButtonPressed(ActionEvent event) {
			if (event.getSource() == deleteB) {
				logger.info("Delete button pressed.");
				AuthorModel selected = listviewAuthors.getSelectionModel().getSelectedItem();

				try {
					AuthorGateway.deleteAuthor(selected);
				} catch (GatewayException e) {
					e.printStackTrace();
				}
				AppController.getInstance().switchView(ViewType.VIEW8, null);

			}
		}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<AuthorModel> authors = listviewAuthors.getItems();
		for (AuthorModel author: listData) {
			authors.add(author);
			}
		
		listviewAuthors.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {

					AuthorModel selected = listviewAuthors.getSelectionModel().getSelectedItem();
					logger.info("Author pressed. " + selected);
					AppController.getInstance().switchView(ViewType.VIEW9, selected);

				}

			}
		});
	}
	
}