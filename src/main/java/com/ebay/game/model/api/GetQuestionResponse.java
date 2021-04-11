package com.ebay.game.model.api;

import java.util.List;
import java.util.UUID;

public class GetQuestionResponse extends BaseResponse {

    private int gameId;

    private UUID questionId;

    private String text;

    private List<String> answers;

    /** Default constructor. */
    public GetQuestionResponse() {
        super();
    }

    public GetQuestionResponse(int gameId, UUID questionId, String text, List<String> answers) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.text = text;
        this.answers = answers;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "GetQuestionResponse{" +
                "gameId=" + gameId +
                ", questionId=" + questionId +
                ", text='" + text + '\'' +
                ", answers=" + answers +
                '}';
    }
}
