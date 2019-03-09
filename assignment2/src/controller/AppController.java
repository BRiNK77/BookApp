package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import controller.BookGateway;
import model.BookModel;

public class AppController implements Initializable {
	
	private static Logger logger = LogManager.getLogger(AppController.class);

	private static AppController instance = null;
	
	private AppController() {
		
	}
	
	public static AppController getInstance() {
		if(instance == null) {
			instance = new AppController();
		}
		return instance;
	}
	// must declare fxml objects with tag
	
	private BorderPane borderPane;
	
	@FXML
	private MenuItem mClose;
	
	@FXML 
	private MenuItem mList;
	
	public void switchView(ViewType viewType, Object data) {
		String viewString = ""; // used to store fxml file names
		MyController controller = null; // need a blank controller to populate
		
		List<BookModel> list = BookGateway.getBooks();  // list to pass to BookListController
		
		
		// switch to determine which view to set, also sets controller
		switch(viewType) {
			case VIEW1:
				viewString = "/View/BookListView.fxml";
				controller = new BookListController(list);
				break;
				
			case VIEW2:
				viewString = "/View/DetailedView.fxml";
				controller = new DetailedController((BookModel) data);
				break;
		}
		
		// tries to set the new view with fxml loader
		try {
			URL url = this.getClass().getResource(viewString);
			FXMLLoader loader = new FXMLLoader(url);
			loader.setController(controller);
			Parent viewNode = loader.load();
			
			
			borderPane.setCenter(viewNode);
			// throwing exceptions even though everything is going through
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Empty view or controller.");
		}
	}
	
	// handles menu actions
	@FXML
	void onMenuClick(ActionEvent event) {
		//List<BookModel> list = BookGateway.getBooks();
		if(event.getSource() == mClose) {
			
			Platform.exit();
			
		} else if(event.getSource() == mList) {
			switchView(ViewType.VIEW1, "NA");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public BorderPane getBorderPane() {
		return borderPane;
	}
	
	public void setBorderPane(BorderPane borderPane) {
		this.borderPane = borderPane;
	}
	
	
	
	
}
