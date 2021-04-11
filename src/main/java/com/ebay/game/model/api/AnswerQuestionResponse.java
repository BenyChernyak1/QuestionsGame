package com.ebay.game.model.api;

import com.ebay.game.model.Status;

public class AnswerQuestionResponse extends BaseResponse{

    Status status;

    int points;

    /** Default constructor. */
    public AnswerQuestionResponse() {
        super();
    }

    public AnswerQuestionResponse(Status status, int points) {
        this.status = status;
        this.points = points;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "QuestionResponse{" +
                "status=" + status +
                ", points=" + points +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
