package il.cshaifasweng.OCSFMediatorExample.client;

public class GameEvent {
    private final String symbol;

    public GameEvent(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
