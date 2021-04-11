package com.ebay.game.model.queue;

public class AnswerMessage {

    private String username;

    private int gameId;

    private int answerId;

    private String questionId;

    /** Default constructor. */
    public AnswerMessage() {
        super();
    }

    public AnswerMessage(String username, int gameId, int answerId, String questionId) {
        this.username = username;
        this.gameId = gameId;
        this.answerId = answerId;
        this.questionId = questionId;
    }

    public String getUsername() {
        return username;
    }

    public int getGameId() {
        return gameId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getQuestionId() {
        return questionId;
    }
}
