package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import controller.BookGateway;
import controller.AuthorViewController;
import model.AuditTrailModel;
import model.AuthorBookModel;
import model.AuthorModel;
import model.BookModel;
import model.PublisherModel;

// the overall controller for the application, handles the connection to database, and changing of views
public class AppController implements Initializable {

	private static Logger logger = LogManager.getLogger(AppController.class);

	private static AppController instance = null;
	private static DetailedController currentCon;

	private AppController() {

	}

	// creates single instance for the application
	public static AppController getInstance() {
		if (instance == null) {
			instance = new AppController();
			currentCon = null;
		}
		return instance;
	}
	// must declare fxml objects with tag

	private BorderPane borderPane;

	@FXML
	private MenuItem mClose;
	@FXML
	private MenuItem mList;
	@FXML
	private MenuItem mAdd;

	// method to handle view switching on single screen
	public boolean switchView(ViewType viewType, Object data) {
		if (currentCon != null) {
			if (currentCon.hasChanged()) {

				Alert alert = new Alert(AlertType.CONFIRMATION);

				alert.getButtonTypes().clear();
				ButtonType buttonTypeOne = new ButtonType("Yes");
				ButtonType buttonTypeTwo = new ButtonType("No");
				ButtonType buttonTypeThree = new ButtonType("Cancel");
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

				alert.setTitle("Save Changes?");
				alert.setHeaderText("The current view has unsaved changes.");
				alert.setContentText("Do you wish to save them before switching to a different view?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get().getText().equalsIgnoreCase("Yes")) {
					currentCon.save();
					logger.info("*** saving the view");
				} else if (result.get().getText().equalsIgnoreCase("Cancel")) {
					return false;
				}

			}

		}

		String viewString = ""; // used to store fxml file names
		MyController controller = null; // need a blank controller to populate

		// switch to determine which view to set, also sets controller
		switch (viewType) {

		case VIEW1:
			List<BookModel> list = BookGateway.getBooks();
			viewString = "/View/BookListView.fxml";
			controller = new BookListController(list);
			break;

		case VIEW2:
			List<PublisherModel> pubs = BookGateway.getPublishers();
			viewString = "/View/DetailedView.fxml";
			currentCon = new DetailedController((BookModel) data, pubs);
			break;

		case VIEW3:
			BookModel book = new BookModel();
			List<PublisherModel> publishers = BookGateway.getPublishers();
			viewString = "/View/DetailedView.fxml";
			currentCon = new DetailedController(book, publishers);
			break;

		case VIEW4:
			int id = ((BookModel) data).getID();
			List<AuditTrailModel> audits = BookGateway.getAuditTrail(id);
			viewString = "/View/AuditView.fxml";
			controller = new AuditViewController(audits, (BookModel) data);
			break;
			
		case VIEW5:
			viewString = "/View/AuthorBookView.fxml";
			controller = new AuthorBookController((AuthorBookModel) data);
			break;
		}

		// tries to set the new view with fxml loader
		if (controller == null) {
			try {
				URL url = this.getClass().getResource(viewString);
				FXMLLoader loader = new FXMLLoader(url);
				loader.setController(currentCon);
				Parent viewNode = loader.load();

				borderPane.setCenter(viewNode);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Empty view or controller.");
			}
			return true;
		} else {
			try {
				URL url = this.getClass().getResource(viewString);
				FXMLLoader loader = new FXMLLoader(url);
				loader.setController(controller);
				Parent viewNode = loader.load();

				borderPane.setCenter(viewNode);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Empty view or controller.");
			}
			return true;
		}
	}

	// handles menu actions
	@FXML
	void onMenuClick(ActionEvent event) {
		if (event.getSource() == mClose) {
			if (currentCon != null) {
				if (currentCon.hasChanged()) {

					Alert alert = new Alert(AlertType.CONFIRMATION);

					alert.getButtonTypes().clear();
					ButtonType buttonTypeOne = new ButtonType("Yes");
					ButtonType buttonTypeTwo = new ButtonType("No");
					ButtonType buttonTypeThree = new ButtonType("Cancel");
					alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

					alert.setTitle("Save Changes?");
					alert.setHeaderText("The current view has unsaved changes.");
					alert.setContentText("Do you wish to save them before switching to a different view?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get().getText().equalsIgnoreCase("Yes")) {
						currentCon.save();
						logger.info("*** saving the view");
					} else if (result.get().getText().equalsIgnoreCase("Cancel")) {
						return;
					}
				}

			}
			Platform.exit();

		} else if (event.getSource() == mList) {
			switchView(ViewType.VIEW1, null);
			return;

		} else if (event.getSource() == mAdd) {
			switchView(ViewType.VIEW3, null);
			return;
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
