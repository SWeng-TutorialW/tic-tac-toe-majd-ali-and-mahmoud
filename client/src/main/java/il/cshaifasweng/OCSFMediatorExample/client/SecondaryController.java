package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private Button ConnectBtn;

    @FXML
    private TextField HostID;

    @FXML
    private TextField PortID;

    @FXML
    public void TryConnect(ActionEvent event) {
        try {
            EventBus.getDefault().post(new ConnectToServerEvent(HostID.getText(), Integer.parseInt(PortID.getText())));
        } catch (RuntimeException e) {
            e.printStackTrace(); // For debugging in the console

            // Show alert to the user
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Failed to connect to the server");
                alert.setContentText("Please check the host and port and try again.");
                alert.showAndWait();
            });
        }
    }

}
