package com.ebay.game.model;

import com.ebay.game.utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Player {

    private UUID id;

    private String username;

    private Map<UUID, Integer> pointsPerQuestion = new HashMap<>();

    public Player(String username) {
        this.username = username;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<UUID, Integer> getPointsPerQuestion() {
        return pointsPerQuestion;
    }

    public void setPointsPerQuestion(Map<UUID, Integer> pointsPerQuestion) {
        this.pointsPerQuestion = pointsPerQuestion;
    }

    public void addPointsPerQuestion(UUID questionId) {
        pointsPerQuestion.put(questionId, Constants.POINTS_FOR_RIGHT_ANSWER);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", pointsPerQuestion=" + pointsPerQuestion +
                '}';
    }
}
