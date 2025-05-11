package il.cshaifasweng.OCSFMediatorExample.client;

public class ButtonUpdateEvent {
    private final String buttonId;
    private final String symbol;

    public ButtonUpdateEvent(String buttonId, String symbol) {
        this.buttonId = buttonId;
        this.symbol = symbol;
    }

    public String getButtonId() {
        return buttonId;
    }

    public String getSymbol() {
        return symbol;
    }
}
