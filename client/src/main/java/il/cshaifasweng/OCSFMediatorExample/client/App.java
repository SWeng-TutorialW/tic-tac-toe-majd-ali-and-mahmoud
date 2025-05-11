package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

import javafx.stage.StageStyle;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private SimpleClient client;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        EventBus.getDefault().register(this);
        // Set window icon
        stage.getIcons().add(new Image(
                getClass().getResourceAsStream("/il/cshaifasweng/OCSFMediatorExample/client/Tic-tac-toe-logo.png")
        ));
        stage.setTitle("Tic Tac Toe");
        primaryStage.setResizable(false);
        scene = new Scene(loadFXML("secondary"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showConnectionError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Error");
        alert.setHeaderText("Could not connect to the server");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @org.greenrobot.eventbus.Subscribe
    public void onTurnUpdate(ConnectToServerEvent event) {
        Platform.runLater(() -> {
            try {
                client = event.getClientId();
                client.openConnection();

                scene = new Scene(loadFXML("primary"));
                primaryStage.setScene(scene);
                primaryStage.show();

            } catch (IOException e) {
                client = null;
                e.printStackTrace();
                showConnectionError("Failed to connect to the server:\n" + e.getMessage());
            }
        });
    }



    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
        client.sendToServer("remove client");
        client.closeConnection();
		super.stop();
	}
    
    @Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }

	public static void main(String[] args) {
        launch();
    }

}