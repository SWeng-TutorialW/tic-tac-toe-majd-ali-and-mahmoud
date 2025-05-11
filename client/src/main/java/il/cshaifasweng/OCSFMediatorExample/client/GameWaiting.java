package il.cshaifasweng.OCSFMediatorExample.client;

public class GameWaiting {
    private final String Message;

    public GameWaiting(String symbol) {
        this.Message = symbol;
    }

    public String getMessage() {
        return Message;
    }
}
