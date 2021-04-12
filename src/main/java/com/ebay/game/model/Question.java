package com.ebay.game.model;

import java.util.*;

public class Question {

    private UUID id;

    private Status status;

    private String text;

    private Map<Integer, String> answers;

    private Map<UUID, Integer> playersAnswers = new HashMap<>();

    private int currentPlayersNumber;

    public Question(UUID id, String text, Map<Integer, String> answers) {
        this.id = id;
        this.text = text;
        this.answers = answers;
        status = Status.Pending;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<Integer, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, String> answers) {
        this.answers = answers;
    }

    public Map<UUID, Integer> getPlayersAnswers() {
        return playersAnswers;
    }

    public void setPlayersAnswers(Map<UUID, Integer> playersAnswers) {
        this.playersAnswers = playersAnswers;
    }

    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public void setCurrentPlayersNumber(int currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public void incrementPlayersNumber() {
        this.currentPlayersNumber++;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", status=" + status +
                ", text='" + text + '\'' +
                ", answers=" + answers +
                ", playersAnswers=" + playersAnswers +
                ", currentPlayersNumber=" + currentPlayersNumber +
                '}';
    }
}
