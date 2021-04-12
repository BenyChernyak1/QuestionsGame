package com.ebay.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private int id;

    private Question question;

    private List<Player> players = new ArrayList<>();

    private Map<String, Integer> leaderboard = new HashMap<>();

    public Game(int id, Question question) {
        this.id = id;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public Map<String, Integer> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(Map<String, Integer> players) {
        this.leaderboard = players;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", question=" + question +
                ", players=" + players +
                ", leaderboard=" + leaderboard +
                '}';
    }
}
