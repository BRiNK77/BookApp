package controller;

/* Assignment 3 By: Tristan Zaleski
 *   All aspects of the assignment are working correctly.
 * */
import java.net.URL;
import controller.BookGateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

// main file for the application, sets up the initial view and controller. Also establishes connection with database apon start
public class Main extends Application {

	private static final Logger logger = LogManager.getLogger();

	@Override
	public void start(Stage stage) throws Exception {
		
		// need to change so login view is first thing seen, then moved to MainView on success
		
		 URL url = this.getClass().getResource("/View/MainView.fxml");

		//URL url = this.getClass().getResource("/View/LoginView.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		
		// gets instance of controller to handle functions of the main screen
		AppController controller = AppController.getInstance(0);
		//LoginController controller = new LoginController();
		loader.setController(controller);

		Parent mainNode = loader.load();
		controller.setBorderPane((BorderPane) mainNode);

		stage.setScene(new Scene(mainNode)); // sets scene here

		stage.setTitle("Book Organizer"); // sets title of stage
		stage.setWidth(600);
		stage.setHeight(500);
		stage.show();
		AppController.getInstance(0).switchView(ViewType.VIEW10, null);
		
	}

	// on start of app, will attempt to make a connection with the database
	public void init() throws Exception {
		super.init();
		logger.info("Creating Connection with DB...");

		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://easel2.fulgentcorp.com/ior298");
		ds.setUser("ior298");
		ds.setPassword("Ok6rP1rmmDF7EjyLILwq");
		Connection connection = ds.getConnection();

		// sets the active connection to necessary gateways
		BookGateway.getInstance().setConnection(connection);
		AuthorBookGateway.getInstance().setConnection(connection);
		AuthorGateway.getInstance().setConnection(connection);
		
	}

	// on app close, will close the connection to database
	public void stop() throws Exception {
		super.stop();

		logger.info("Closing connection to DB...");

		BookGateway.getInstance().getConnection().close();
		AuthorGateway.getInstance().getConnection().close();
		AuthorBookGateway.getInstance().getConnection().close();
	}

	// main that launches the application
	public static void main(String[] args) {
		launch(args);
	}
}
