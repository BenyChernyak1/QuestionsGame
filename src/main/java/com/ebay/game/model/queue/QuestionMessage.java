package com.ebay.game.model.queue;

public class QuestionMessage {

    private String username;

    private int gameId;

    /** Default constructor. */
    public QuestionMessage() {
        super();
    }

    public QuestionMessage(String username, int gameId) {
        this.username = username;
        this.gameId = gameId;
    }

    public String getUsername() {
        return username;
    }

    public int getGameId() {
        return gameId;
    }
}
