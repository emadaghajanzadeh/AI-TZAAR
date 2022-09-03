package models;

import java.io.Serializable;

public class Action implements Serializable ,  Comparable<Action> {

    @Override
    public int compareTo(Action o) {
        System.out.println("Comparison between: "+ o.getScore() + " and " + this.getScore());
        if(this.getScore()> o.getScore()) return 1;
        return 0;
    }

    public enum ActionType {
        reinforce, attack, nothing
    }

    private final ActionType type;
    private final Board.BoardCell start;
    private final Board.BoardCell target;
    private boolean isForce;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Action(ActionType type, Board.BoardCell start, Board.BoardCell target) {
        this.type = type;
        this.start = start;
        this.target = target;
    }

    public void setForce(Boolean isForce){
        this.isForce  = isForce;
    }

    public boolean getForce() {
        return isForce;
    }

    public ActionType getType() {
        return type;
    }

    public Board.BoardCell getStart() {
        return start;
    }

    public Board.BoardCell getTarget() {
        return target;
    }



    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", start=" + start +
                ", target=" + target +
                '}';
    }
}
