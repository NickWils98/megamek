/*
 * MegaMek - Copyright (C) 2007-2008 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.server.victory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import megamek.common.Game;
import megamek.common.IGame;
import megamek.common.IPlayer;
import megamek.common.Report;

/**
 * quick implementation of a Victory.Result stores player scores and a flag if
 * game-ending victory is achieved or not
 */
public class VictoryResult implements IResult {
    protected boolean victory;
    protected Throwable tr;
    protected ArrayList<Report> reports = new ArrayList<>();
    protected HashMap<Integer, Double> playerScore = new HashMap<>();
    protected HashMap<Integer, Double> teamScore = new HashMap<>();
    protected double hiScore = 0;

    protected VictoryResult(boolean win) {
        this.victory = win;
        tr = new Throwable();
    }
    
    protected VictoryResult(boolean win, int player, int team) {
    	this.victory = win;
    	tr = new Throwable();
        if (player != IPlayer.PLAYER_NONE) {
            addPlayerScore(player, 1.0);
        }
        if (team != IPlayer.TEAM_NONE) {
            addTeamScore(team, 1.0);
        }
    }
    
    protected static VictoryResult noResult() {
    	return new VictoryResult(false, IPlayer.PLAYER_NONE, IPlayer.TEAM_NONE);
    }
    
    protected static VictoryResult drawResult() {
        return new VictoryResult(true, IPlayer.PLAYER_NONE, IPlayer.TEAM_NONE);
    }

    public int getWinningEntity(Map<Integer, Double> entityScore, int entity) {
        double max = Double.MIN_VALUE;
        int maxEntity = entity;
        boolean draw = false;
        for (Map.Entry<Integer, Double> entry:  entityScore.entrySet()) {
            if (entry.getValue() == max) {
                draw = true;
            }
            if (entry.getValue() > max) {
                draw = false;
                max = entry.getValue();
                maxEntity = entry.getKey();
            }
        }
        if (draw)
            return entity;
        return maxEntity;
    }

    public int getWinningPlayer() {
        return getWinningEntity(playerScore, IPlayer.PLAYER_NONE);
    }

    public int getWinningTeam() {
        return getWinningEntity(teamScore, IPlayer.TEAM_NONE);
    }

    protected void updateHiScore() {
        // used to calculate winner
        hiScore = Double.MIN_VALUE;
        for (Double d : playerScore.values()) {
            if (d > hiScore)
                hiScore = d;
        }
        for (Double d : teamScore.values()) {
            if (d > hiScore)
                hiScore = d;
        }
    }

    public void addPlayerScore(int id, double score) {
        playerScore.put(id, score);
        updateHiScore();
    }

    public void addTeamScore(int id, double score) {
        teamScore.put(id, score);
        updateHiScore();
    }

    public boolean isWinningPlayer(int id) {
        double d = getPlayerScore(id);
        // two decimal compare..
        return ((d * 100) % 100) == ((hiScore * 100) % 100);
    }

    public boolean isWinningTeam(int id) {
        double d = getTeamScore(id);
        // two decimal compare..
        return ((d * 100) % 100) == ((hiScore * 100) % 100);
    }

    public boolean victory() {
        return victory;
    }

    public void setVictory(boolean b) {
        this.victory = b;
    }

    public double getPlayerScore(int id) {
        if (playerScore.get(id) == null)
            return 0.0;
        return playerScore.get(id);
    }

    public int[] getPlayers() {
        return intify(playerScore.keySet().toArray(new Integer[0]));
    }

    public double getTeamScore(int id) {
        if (teamScore.get(id) == null)
            return 0.0;
        return teamScore.get(id);
    }

    public int[] getTeams() {
        return intify(teamScore.keySet().toArray(new Integer[0]));
    }

    public void addReport(Report r) {
        reports.add(r);
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    protected String getTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pr = new PrintWriter(sw);
        tr.printStackTrace(pr);
        pr.flush();
        return sw.toString();
    }

    private int[] intify(Integer[] ar) {
        int[] ret = new int[ar.length];
        for (int i = 0; i < ar.length; i++)
            ret[i] = ar[i];
        return ret;
    }

    @Override
    public String toString() {
        return "victory provided to you by:" + getTrace();
    }

    public boolean isDraw() {
        return (getWinningPlayer() == IPlayer.PLAYER_NONE && getWinningTeam() == IPlayer.TEAM_NONE);
    }

    public List<Report> handleReports(IGame game) {
        if (victory()) {
            return handleReportsVictory(game);
        } else {
            return handleReportsLoss(game);
        }
    }

    public List<Report> handleReportsVictory(IGame game) {
        ArrayList<Report> proccessingReports = getReports() ;
        boolean draw = isDraw();
        int wonPlayer = getWinningPlayer();
        int wonTeam = getWinningTeam();

        if (wonPlayer != IPlayer.PLAYER_NONE) {
            Report r = new Report(7200, Report.PUBLIC);
            r.add(game.getPlayer(wonPlayer).getColorForPlayer());
            proccessingReports.add(r);
        }
        if (wonTeam != IPlayer.TEAM_NONE) {
            Report r = new Report(7200, Report.PUBLIC);
            r.add("Team " + wonTeam);
            proccessingReports.add(r);
        }
        if (draw) {
            // multiple-won draw
            game.setVictory(IPlayer.PLAYER_NONE, IPlayer.TEAM_NONE);
        } else {
            // nobody-won draw or
            // single player won or
            // single team won
            game.setVictory(wonPlayer, wonTeam);
        }
        return proccessingReports;
    }

    public List<Report> handleReportsLoss(IGame game) {
        game.setVictory(IPlayer.PLAYER_NONE, IPlayer.TEAM_NONE);
        if (game.isForceVictory()) {
            game.cancelVictory();
        }
        return getReports();
    }


    /**
     * combine scores
     * @param res victory result
     */
    public void combineScore(VictoryResult res){
        for (Report r : res.getReports()) {
            addReport(r);
        }
        for (int pl : res.getPlayers()) {
            addPlayerScore(pl, getPlayerScore(pl) + res.getPlayerScore(pl));
        }
        for (int t : res.getTeams()) {
            addTeamScore(t, getTeamScore(t) + res.getTeamScore(t));
        }
    }
    public void updateElo(IGame game){
        List<Integer> winningList = new ArrayList<Integer>();
        List<Integer> losingList = new ArrayList<Integer>();

        for (int pl: getPlayers()){
            if (isWinningPlayer(pl)){
                winningList.add(pl);
            } else {
                losingList.add(pl);
            }
        }
        int averageWinningElo = getAverageElo(game, winningList);
        int averageLosingElo = getAverageElo(game, losingList);

        int updatscore = 5;
        if (averageWinningElo<averageLosingElo) updatscore = 10;
        if (averageWinningElo>averageLosingElo) updatscore = 2;

        updateEloScore(game, winningList, updatscore);
        updateEloScore(game, losingList, -updatscore);

    }

    private int getAverageElo(IGame game, List<Integer> playerList) {
        if (playerList.size() == 0) return 0;
        int averageElo = 0;
        for (int pl: playerList) {
            averageElo += game.getPlayer(pl).getEloScore();
        }
        return averageElo/playerList.size();
    }

    private void updateEloScore(IGame game, List<Integer> playerList, int updateScore) {
        for (int pl: playerList) {
            game.getPlayer(pl).setEloScore(updateScore);
        }
    }
}