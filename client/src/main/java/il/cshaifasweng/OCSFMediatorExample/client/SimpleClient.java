package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg instanceof String) {
			String message = (String) msg;
			System.out.println("Message from server: " + message);

			if (message.startsWith("SetButtonText:")) {
				String[] parts = message.split(":");

				if (parts.length == 3) {
					String buttonId = parts[1];
					String symbol = parts[2];
					EventBus.getDefault().post(new ButtonUpdateEvent(buttonId, symbol));
				}
			}else if (message.startsWith("Turn:")) {
				String[] parts = message.split(" ");
				String symbol = parts[1];
				System.out.println("Turn: " + symbol);
				EventBus.getDefault().post(new UpdateTurn(symbol));
			}else if (message.startsWith("client added successfully , Symbol: ")) {
				String[] parts = message.split(" ");
				String symbol = parts[5];
				EventBus.getDefault().post(new UpdateSymbol(symbol));
				System.out.println("client added successfully , Symbol: " + symbol);
			}else if(message.startsWith("Winner:")){
				String[] parts = message.split(" ");
				String symbol = parts[1];
				System.out.println("Message from server: " + message);
				EventBus.getDefault().post(new GameEvent(symbol));
			} else if (message.startsWith("Waiting for another")) {
				EventBus.getDefault().post(new GameWaiting(message));
			}else if (message.startsWith("Tie")) {
				EventBus.getDefault().post(new GameEvent(message));
			}else if (message.startsWith("Game full. Cannot join.")) {
				EventBus.getDefault().post(new GameWaiting("Spectator"));
			}else if (message.startsWith("Label message:")) {
				String[] parts = message.split("message:");
				if (parts.length > 1) {
					String content = parts[1].trim();  // Removes any leading spaces
					EventBus.getDefault().post(new ChangeLabelTxt(content));
				}
			}
		}else if (msg instanceof Character[][]) {
			//incase someone joins after the game started , he spectates and see the updtated table of XO
			Character[][] table = (Character[][]) msg;
			EventBus.getDefault().post(new SpectateTable(table));
		}else if(msg instanceof boolean[][]) {
			boolean[][] table = (boolean[][]) msg;
			EventBus.getDefault().post(new WinTable(table));
		}
	}

	public static SimpleClient getClient(String host, int port) throws IOException {
		if (client == null) {
			client = new SimpleClient(host, port);
		} else {
			// If existing client has different host/port, create new
			if (!client.getHost().equals(host) || client.getPort() != port) {
				client.closeConnection(); // Cleanup old connection
				client = new SimpleClient(host, port);
			}
		}

		if (!client.isConnected()) {
			client.openConnection();
		}

		return client;
	}



	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}
