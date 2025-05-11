package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

	private String MyTurnSymbol = "-";
	private boolean Spectator = false;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label IDTurnLabel;

	@FXML
	private Button LowerCenterBTN;

	@FXML
	private Button LowerLeftBTN;

	@FXML
	private Button LowerRightBTN;

	@FXML
	private Button MiddleCenterBTN;

	@FXML
	private Button MiddleLeftBTN;

	@FXML
	private Button MiddleRightBTN;

	@FXML
	private Button UpperCenterBTN;

	@FXML
	private Button UpperLeftBTN;

	@FXML
	private Button UpperRightBTN;

	void ChoosenBtn(Button choosen){
		try {
			SimpleClient.getClient().sendToServer("Set text button " + choosen.getId() + " Symbol: " +  MyTurnSymbol);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void LowerCenterClicker(ActionEvent event) {
		ChoosenBtn(LowerCenterBTN);
	}

	@FXML
	void LowerLeftClicked(ActionEvent event) {
		ChoosenBtn(LowerLeftBTN);
	}

	@FXML
	void LowerRightClicked(ActionEvent event) {
		ChoosenBtn(LowerRightBTN);
	}

	@FXML
	void MiddleCenterClicked(ActionEvent event) {
		ChoosenBtn(MiddleCenterBTN);
	}

	@FXML
	void MiddleLeftClicked(ActionEvent event) {
		ChoosenBtn(MiddleLeftBTN);
	}

	@FXML
	void MiddleRightClicked(ActionEvent event) {
		ChoosenBtn(MiddleRightBTN);
	}

	@FXML
	void UpperCenterClicked(ActionEvent event) {
		ChoosenBtn(UpperCenterBTN);
	}

	@FXML
	void UpperLeftClicked(ActionEvent event) {
		ChoosenBtn(UpperLeftBTN);
	}

	@FXML
	void UpperRightClicked(ActionEvent event) {
		ChoosenBtn(UpperRightBTN);
	}

	@org.greenrobot.eventbus.Subscribe
	public void onTableWin(WinTable event) {
		javafx.application.Platform.runLater(()-> {
			boolean[][] tableWin = event.getWinTable();
			for (int i = 0; i < tableWin.length; i++) {
				for (int j = 0; j < tableWin[i].length; j++) {
					if (tableWin[i][j]) {
						getButtonID(i,j);
					}
				}
			}
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onTurnUpdate(UpdateTurn event) {
		javafx.application.Platform.runLater(() -> {
			if (!MyTurnSymbol.equals("-")){
				if (MyTurnSymbol.equals(event.getSymbol())) {
					if (UpperRightBTN.getText().isEmpty() || UpperRightBTN.getText().equals(" "))
						UpperRightBTN.setDisable(false);
					if (UpperLeftBTN.getText().isEmpty() || UpperLeftBTN.getText().equals(" "))
						UpperLeftBTN.setDisable(false);
					if (UpperCenterBTN.getText().isEmpty() || UpperCenterBTN.getText().equals(" "))
						UpperCenterBTN.setDisable(false);
					if (LowerCenterBTN.getText().isEmpty() || LowerCenterBTN.getText().equals(" "))
						LowerCenterBTN.setDisable(false);
					if (LowerLeftBTN.getText().isEmpty() || LowerLeftBTN.getText().equals(" "))
						LowerLeftBTN.setDisable(false);
					if (LowerRightBTN.getText().isEmpty() || LowerRightBTN.getText().equals(" "))
						LowerRightBTN.setDisable(false);
					if (MiddleCenterBTN.getText().isEmpty() || MiddleCenterBTN.getText().equals(" "))
						MiddleCenterBTN.setDisable(false);
					if (MiddleLeftBTN.getText().isEmpty() || MiddleLeftBTN.getText().equals(" "))
						MiddleLeftBTN.setDisable(false);
					if (MiddleRightBTN.getText().isEmpty() || MiddleRightBTN.getText().equals(" "))
						MiddleRightBTN.setDisable(false);
				} else {
					UpperRightBTN.setDisable(true);
					UpperLeftBTN.setDisable(true);
					UpperCenterBTN.setDisable(true);
					LowerCenterBTN.setDisable(true);
					LowerLeftBTN.setDisable(true);
					LowerRightBTN.setDisable(true);
					MiddleCenterBTN.setDisable(true);
					MiddleLeftBTN.setDisable(true);
					MiddleRightBTN.setDisable(true);
				}
			IDTurnLabel.setText((event.getSymbol().equals(MyTurnSymbol) ? "Your turn" : "Opponent turn") + " - " + event.getSymbol());
			}
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onGameEnd(GameEvent event) {
		javafx.application.Platform.runLater(() -> {
				if (event.getSymbol().equals(MyTurnSymbol)) {
					IDTurnLabel.setText(MyTurnSymbol + " Wins!");
				} else if (event.getSymbol().equals("Tie")) {
					IDTurnLabel.setText("Game Over! Tie!");
				} else {
					IDTurnLabel.setText((MyTurnSymbol.equals("X") ? "O" : "X") + " Wins!");
				}
				UpperRightBTN.setDisable(true);
				UpperLeftBTN.setDisable(true);
				UpperCenterBTN.setDisable(true);
				LowerCenterBTN.setDisable(true);
				LowerLeftBTN.setDisable(true);
				LowerRightBTN.setDisable(true);
				MiddleCenterBTN.setDisable(true);
				MiddleLeftBTN.setDisable(true);
				MiddleRightBTN.setDisable(true);
				if (MyTurnSymbol.equals("-")) {
					IDTurnLabel.setText("Spectator");
				}
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onChangeLabelTxt(ChangeLabelTxt event) {
		javafx.application.Platform.runLater(() -> {
			if(!Spectator){
				IDTurnLabel.setText(event.getText());
			}
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onGameWaiting(GameWaiting event) {
		javafx.application.Platform.runLater(() -> {
			IDTurnLabel.setText(event.getMessage());
			if (event.getMessage().equals("Spectator")) {
				Spectator = true;
				MyTurnSymbol = "-";
			}
			UpperRightBTN.setDisable(true);
			UpperLeftBTN.setDisable(true);
			UpperCenterBTN.setDisable(true);
			LowerCenterBTN.setDisable(true);
			LowerLeftBTN.setDisable(true);
			LowerRightBTN.setDisable(true);
			MiddleCenterBTN.setDisable(true);
			MiddleLeftBTN.setDisable(true);
			MiddleRightBTN.setDisable(true);
			UpperRightBTN.setText("");
			UpperLeftBTN.setText("");
			UpperCenterBTN.setText("");
			LowerCenterBTN.setText("");
			LowerLeftBTN.setText("");
			LowerRightBTN.setText("");
			MiddleCenterBTN.setText("");
			MiddleLeftBTN.setText("");
			MiddleRightBTN.setText("");
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onSpectateTable(SpectateTable event) {
		javafx.application.Platform.runLater(() -> {
			setButtonTextAndColor(UpperLeftBTN, event.getTable()[0][0]);
			setButtonTextAndColor(UpperCenterBTN, event.getTable()[0][1]);
			setButtonTextAndColor(UpperRightBTN, event.getTable()[0][2]);

			setButtonTextAndColor(MiddleLeftBTN, event.getTable()[1][0]);
			setButtonTextAndColor(MiddleCenterBTN, event.getTable()[1][1]);
			setButtonTextAndColor(MiddleRightBTN, event.getTable()[1][2]);

			setButtonTextAndColor(LowerLeftBTN, event.getTable()[2][0]);
			setButtonTextAndColor(LowerCenterBTN, event.getTable()[2][1]);
			setButtonTextAndColor(LowerRightBTN, event.getTable()[2][2]);
		});
	}

	// Helper method to apply text and color
	private void setButtonTextAndColor(Button button, Character value) {
		if (value == null || value == ' ') {
			button.setText("");
			button.setStyle(""); // Clear styling
		} else {
			button.setText(value.toString());
			if (value == 'X') {
				button.setStyle("-fx-text-fill: red;");
			} else if (value == 'O') {
				button.setStyle("-fx-text-fill: blue;");
			} else {
				button.setStyle(""); // Default styling for anything else
			}
		}
	}

	@org.greenrobot.eventbus.Subscribe
	public void onButtonUpdate(ButtonUpdateEvent event) {
		javafx.application.Platform.runLater(() -> {
			updateButtonText(event.getButtonId(), event.getSymbol());
		});
	}

	@org.greenrobot.eventbus.Subscribe
	public void onSymbolUpdate(UpdateSymbol event) {
		javafx.application.Platform.runLater(() -> {
			MyTurnSymbol = event.getSymbol();
			Spectator = false;
		});
	}

	public void getButtonID(int i, int j) {
		Button target = null;

		if (i == 0 && j == 0) target = UpperLeftBTN;
		else if (i == 0 && j == 1) target = UpperCenterBTN;
		else if (i == 0 && j == 2) target = UpperRightBTN;
		else if (i == 1 && j == 0) target = MiddleLeftBTN;
		else if (i == 1 && j == 1) target = MiddleCenterBTN;
		else if (i == 1 && j == 2) target = MiddleRightBTN;
		else if (i == 2 && j == 0) target = LowerLeftBTN;
		else if (i == 2 && j == 1) target = LowerCenterBTN;
		else if (i == 2 && j == 2) target = LowerRightBTN;

		if (target != null) {
			target.setStyle("-fx-text-fill: #ffffff;");
		}
	}


	public void updateButtonText(String buttonId, String symbol) {
		Button target = switch (buttonId) {
			case "UpperLeftBTN" -> UpperLeftBTN;
			case "UpperCenterBTN" -> UpperCenterBTN;
			case "UpperRightBTN" -> UpperRightBTN;
			case "MiddleLeftBTN" -> MiddleLeftBTN;
			case "MiddleCenterBTN" -> MiddleCenterBTN;
			case "MiddleRightBTN" -> MiddleRightBTN;
			case "LowerLeftBTN" -> LowerLeftBTN;
			case "LowerCenterBTN" -> LowerCenterBTN;
			case "LowerRightBTN" -> LowerRightBTN;
			default -> null;
		};

		if (target != null) {
			target.setText(symbol);
			if ("X".equals(symbol)) {
				target.setStyle("-fx-text-fill: red;");
			} else if ("O".equals(symbol)) {
				target.setStyle("-fx-text-fill: blue;");
			}
		}
	}

	private void setupHoverEffect(Button button) {
		button.setOnMouseEntered(event -> {
			if ((button.getText().isEmpty() || button.getText().equals(" ")) && !MyTurnSymbol.equals("-")) {
				button.setText(MyTurnSymbol);

				// Apply faded symbol color + hover visual effects
				String color = MyTurnSymbol.equals("X")
						? "rgba(255, 0, 0, 0.3)"  // faded red
						: "rgba(0, 0, 255, 0.3)"; // faded blue

				button.setStyle(
						"-fx-text-fill: " + color + ";" +
								"-fx-background-color: #404040;" +
								"-fx-cursor: hand;" +
								"-fx-effect: dropshadow(gaussian, white, 8, 0.2, 0, 0);"
				);
			}
		});

		button.setOnMouseExited(event -> {
			if ((button.getText().equals(MyTurnSymbol)) && !button.isDisable()) {
				button.setText("");
				button.setStyle(""); // Clear styles
			}
		});
	}




	@FXML
	void initialize() {
		assert IDTurnLabel != null : "fx:id=\"IDTurnLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert LowerCenterBTN != null : "fx:id=\"LowerCenterBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert LowerLeftBTN != null : "fx:id=\"LowerLeftBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert LowerRightBTN != null : "fx:id=\"LowerRightBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert MiddleCenterBTN != null : "fx:id=\"MiddleCenterBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert MiddleLeftBTN != null : "fx:id=\"MiddleLeftBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert MiddleRightBTN != null : "fx:id=\"MiddleRightBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert UpperCenterBTN != null : "fx:id=\"UpperCenterBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert UpperLeftBTN != null : "fx:id=\"UpperLeftBTN\" was not injected: check your FXML file 'primary.fxml'.";
		assert UpperRightBTN != null : "fx:id=\"UpperRightBTN\" was not injected: check your FXML file 'primary.fxml'.";
		EventBus.getDefault().register(this);
		setupHoverEffect(UpperLeftBTN);
		setupHoverEffect(UpperCenterBTN);
		setupHoverEffect(UpperRightBTN);
		setupHoverEffect(MiddleLeftBTN);
		setupHoverEffect(MiddleCenterBTN);
		setupHoverEffect(MiddleRightBTN);
		setupHoverEffect(LowerLeftBTN);
		setupHoverEffect(LowerCenterBTN);
		setupHoverEffect(LowerRightBTN);
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
