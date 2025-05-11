package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static Character Turn = Math.random() < 0.5 ? 'X' : 'O';
	private static Character[][] Table = new Character[3][3];
	private static boolean GameEnded = false;
	private static ConnectionToClient[] playerSlots = new ConnectionToClient[2]; // index 0 for X, 1 for O

	public SimpleServer(int port) {
		super(port);
		initializeTable();
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client disconnected: " + client.getInetAddress());

		// Remove from player slots
		boolean wasPlayer = false;
		if (playerSlots[0] == client) {
			playerSlots[0] = null;
			wasPlayer = true;
		}
		if (playerSlots[1] == client) {
			playerSlots[1] = null;
			wasPlayer = true;
		}

		// Remove from subscribers
		SubscribersList.removeIf(sub -> sub.getClient().equals(client));

		// If a player left, promote spectators if available
		if (wasPlayer) {
			GameEnded = true;
			initializeTable();

			// Promote available spectators
			for (SubscribedClient sub : SubscribersList) {
				ConnectionToClient candidate = sub.getClient();

				// Skip if already a player
				if (candidate.equals(playerSlots[0]) || candidate.equals(playerSlots[1])) continue;

				// Assign to empty slot
				char symbol = assignPlayerSlot(candidate);
				if (symbol != '-') {
					try {
						candidate.sendToClient("client added successfully , Symbol: " + symbol);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// Break once both slots are filled
				if (playerSlots[0] != null && playerSlots[1] != null) break;
			}

			// If both players are available now, restart the game
			if (playerSlots[0] != null && playerSlots[1] != null) {
				resetGame();
				sendToAllClients("Turn: " + "NONE");
				String Message = "Label message: Game restarting in ";
				for (int i = 3; i >= 1; i--) {
					if(SubscribersList.size() >= 2) {
						sendToAllClients(Message + i);
						try {
							Thread.sleep(1000); // Wait for 1 second
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else break;
				}
				GameEnded = false;
			} else {
				sendToAllClients("Waiting for another player");
			}
			resetGame();
		}
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);

			char assignedSymbol = assignPlayerSlot(client);

			if (assignedSymbol == '-') {
				try {
					client.sendToClient("Game full. Cannot join.");
					client.sendToClient(Table);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			try {
				client.sendToClient("client added successfully , Symbol: " + assignedSymbol);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (SubscribersList.size() >= 2) {
				resetGame();
				GameEnded = false; //game starts
				sendToAllClients("Turn: " + Turn);
			}else {
				sendToAllClients("Waiting for another player");
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}else if(msgString.startsWith("Set text button")){
			if(!SubscribersList.isEmpty() && !GameEnded){
				for(SubscribedClient subscribedClient: SubscribersList){
					String[] parts = msgString.split(" ");

					String buttonId = parts[3];
					String symbol = Turn.toString();
					int[] indices = mapButtonIdToIndices(buttonId);
					if (indices == null) return; // Invalid button ID, ignore

					int row = indices[0];
					int col = indices[1];
					if(Table[row][col] == ' ') {
						sendToAllClients("SetButtonText:" + buttonId + ":" + symbol);
						Table[row][col] = Turn;

						// Check for win
						if (checkWin(Turn)) {
							GameEnded = true;
							sendToAllClients("Winner: " + Turn);
							sendToAllClients(WinTable());
							// Wait 3 seconds, then restart
							new Thread(() -> {
								try {
									Thread.sleep(3000);
									if (GameEnded) resetGame();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}).start();

							return;
						} if (isBoardFull()) {
							sendToAllClients("Tie");
							GameEnded = true;

							// Wait 3 seconds, then restart
							new Thread(() -> {
								try {
									Thread.sleep(3000);
									resetGame();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}).start();

							return;
						}

						Turn = Turn == 'X' ? 'O' : 'X';
						sendToAllClients("Turn: " + Turn);
					}
				}
			}
		}
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public boolean[][] WinTable(){
		boolean[][] WinnerTable = new boolean[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				WinnerTable[i][j] = false; // Initallize the table
			}
		}

		for (int i = 0; i < 3; i++) {
			if (Table[i][0] == Turn && Table[i][1] == Turn && Table[i][2] == Turn) {
				WinnerTable[i][0] = true;
				WinnerTable[i][1] = true;
				WinnerTable[i][2] = true;
				return WinnerTable;
			}
			if (Table[0][i] == Turn && Table[1][i] == Turn && Table[2][i] == Turn)  {
				WinnerTable[0][i] = true;
				WinnerTable[1][i] = true;
				WinnerTable[2][i] = true;
				return WinnerTable;
			}
		}

		// Check diagonals
		if (Table[0][0] == Turn && Table[1][1] == Turn && Table[2][2] == Turn)  {
			WinnerTable[0][0] = true;
			WinnerTable[1][1] = true;
			WinnerTable[2][2] = true;
			return WinnerTable;
		}
		if (Table[0][2] == Turn && Table[1][1] == Turn && Table[2][0] == Turn) {
			WinnerTable[2][0] = true;
			WinnerTable[1][1] = true;
			WinnerTable[0][2] = true;
			return WinnerTable;
		}

		return WinnerTable;
	}

	private int[] mapButtonIdToIndices(String buttonId) {
		return switch (buttonId) {
			case "UpperLeftBTN"     -> new int[]{0, 0};
			case "UpperCenterBTN"   -> new int[]{0, 1};
			case "UpperRightBTN"    -> new int[]{0, 2};
			case "MiddleLeftBTN"    -> new int[]{1, 0};
			case "MiddleCenterBTN"  -> new int[]{1, 1};
			case "MiddleRightBTN"   -> new int[]{1, 2};
			case "LowerLeftBTN"     -> new int[]{2, 0};
			case "LowerCenterBTN"   -> new int[]{2, 1};
			case "LowerRightBTN"    -> new int[]{2, 2};
			default -> null;
		};
	}

	private boolean checkWin(char symbol) {
		// Check rows and columns
		for (int i = 0; i < 3; i++) {
			if (Table[i][0] == symbol && Table[i][1] == symbol && Table[i][2] == symbol) return true; // row
			if (Table[0][i] == symbol && Table[1][i] == symbol && Table[2][i] == symbol) return true; // column
		}

		// Check diagonals
		if (Table[0][0] == symbol && Table[1][1] == symbol && Table[2][2] == symbol) return true; // main diag
		if (Table[0][2] == symbol && Table[1][1] == symbol && Table[2][0] == symbol) return true; // anti diag

		return false;
	}

	private boolean isBoardFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (Table[i][j] == ' ') {
					return false;
				}
			}
		}
		return true;
	}

	private char assignPlayerSlot(ConnectionToClient client) {
		// If both slots are empty
		if (playerSlots[0] == null && playerSlots[1] == null) {
			// Randomly choose X or O
			int choice = (Math.random() < 0.5) ? 0 : 1;
			playerSlots[choice] = client;
			return choice == 0 ? 'X' : 'O';
		}

		// If one slot is filled, assign the other
		if (playerSlots[0] == null) {
			playerSlots[0] = client;
			return 'X';
		}

		if (playerSlots[1] == null) {
			playerSlots[1] = client;
			return 'O';
		}

		// All slots taken
		return '-'; // or throw an exception if you want to enforce max 2 clients
	}

	private void resetGame() {
		if (playerSlots[0] != null && playerSlots[1] != null) {
			ConnectionToClient playerX;
			ConnectionToClient playerO;

			// Randomize who gets X and who gets O
			if (Math.random() < 0.5) {
				playerX = playerSlots[0];
				playerO = playerSlots[1];
			} else {
				playerX = playerSlots[1];
				playerO = playerSlots[0];
			}

			// Update player slots
			playerSlots[0] = playerX;
			playerSlots[1] = playerO;

			initializeTable();
			GameEnded = false;

			try {
				// Assign symbols
				playerX.sendToClient("client added successfully , Symbol: X");
				playerO.sendToClient("client added successfully , Symbol: O");

				// Send clean board
				sendToAllClients(Table);

				// Randomly choose who starts
				Turn = Math.random() < 0.5 ? 'X' : 'O';
				sendToAllClients("Turn: " + Turn);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initializeTable() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Table[i][j] = ' '; // Initallize the table
			}
		}
	}
}
