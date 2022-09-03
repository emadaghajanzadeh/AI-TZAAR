import models.*;

import java.util.*;
import java.util.Collections;

public class Ai extends Player {

    private int doneActions = 0;
    private int maxDepth = 2;
    private double nodeOrderingRation = 0.2;
    public Ai(PlayerType type) {
        super(type);
    }

    @Override
    public Action forceAttack(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        System.out.println(" Branch Factor: "+ actions.size());
        Main.branchfactor.add(actions.size());


        ArrayList<Action> scoredActions = nodeOrdering(actions , game  , true);
        if(scoredActions.size()!=0) {
            ArrayList<Action> selectedActions = new ArrayList<>();
            double ratio = nodeOrderingRation;
            for (int i = 0; i < ratio * actions.size(); i++) {
                selectedActions.add(scoredActions.get(i));
//                System.out.println(selectedActions.get(i) + " and its value : " + selectedActions.get(i).getScore());
            }
            actions = selectedActions;
        }else{

        }

        if (doneActions == 0 && getType() == PlayerType.white) {
            for (Action action : actions) {
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, minForceAttack(copyGame, 0 , Integer.MIN_VALUE , Integer.MAX_VALUE));
                        if (temp > maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        } else {
            for (Action action : actions) {
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, maxSecondMove(copyGame, 0, Integer.MIN_VALUE , Integer.MAX_VALUE));
                        if (temp > maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        }
        doneActions++;
        return bestAction;
    }

    public ArrayList<Action> nodeOrdering(ArrayList<Action> actions , Game game , boolean isAttack){
        for(Action action:actions){
            if(isAttack) {
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, isAttack)) {
                        continue;
                    }
                    action.setScore(nodeOrderingEval(copyGame));
                }else{
                    action.setScore(-100);
                }
            }else{
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(this, action, isAttack)) {
                    continue;
                }
                action.setScore(nodeOrderingEval(copyGame));
            }
        }
        ArrayList<Action> scoredActions = actions;
        for (int i = 0 ; i < scoredActions.size()-1 ; i++){
            for(int j = i+1 ; j < scoredActions.size() ; j++){
                if(scoredActions.get(i).getScore()< scoredActions.get(j).getScore()){
                    Action temp = scoredActions.get(j);
                    scoredActions.set(j , scoredActions.get(i));
                    scoredActions.set(i , temp);
                }
            }
        }
        return scoredActions;
    }

    private int nodeOrderingEval(Game game){
        int bCount = 0;
        int wCount = 0;
        ArrayList<Action> bActions = game.getBlack().getAllActions(game.getBoard());
        for (Action action : bActions) {

            if (action.getType() == Action.ActionType.attack) {
                bCount++;
//                System.out.println(bCount);
            }
        }
        ArrayList<Action> wActions = game.getWhite().getAllActions(game.getBoard());
        for (Action action : wActions) {
            if (action.getType() == Action.ActionType.attack) {
                wCount++;
            }
        }
//        System.out.println(bCount-wCount);
//        System.out.println("bAction size is : " + bActions.size());
//        System.out.println("wAction size is : " + wActions.size());
//        System.out.println(bCount-wCount);
        return bCount-wCount;
    }

    private int eval(Game game) {
        int bCount = 0;
        int wCount = 0;
        ArrayList<Action> bActions = game.getBlack().getAllActions(game.getBoard());
        for (Action action : bActions) {
            if (action.getType() == Action.ActionType.attack) {
                bCount++;
            }
        }
        ArrayList<Action> wActions = game.getWhite().getAllActions(game.getBoard());
        for (Action action : wActions) {
            if (action.getType() == Action.ActionType.attack) {
                wCount++;
            }
        }



        int criteria1 = (bCount - wCount);
        if(game.getWinner()== null)
            return criteria1;
        int criteria2 =  ((game.getWinner().getType().toString() == "black") ? 100 : -100);

        double ecriteria1 =  Math.exp(criteria1);
        double ecriteria2 =  Math.exp(criteria2);
        double sum = ecriteria1 + ecriteria2 ;

        double finalcriteria1 = criteria1/sum;
        double finalcriteria2 = criteria2 / sum ;

        return (int)(finalcriteria1 + finalcriteria2);
    }

    @Override
    public Action secondAction(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        System.out.println(" Branch Factor: "+ actions.size());
        Main.branchfactor.add(actions.size());


        ArrayList<Action> scoredActions = nodeOrdering(actions , game , false);
        if(scoredActions.size()!=0) {
            ArrayList<Action> selectedActions = new ArrayList<>();
            double ratio = nodeOrderingRation;
            for (int i = 0; i < ratio * actions.size(); i++) {
                selectedActions.add(scoredActions.get(i));
//                System.out.println(selectedActions.get(i) + " and its value : " + selectedActions.get(i).getScore());
            }
            actions = selectedActions;
        }else{

        }

        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return action;
                }
            } else {
                int temp = Math.max(maxValue, minForceAttack(copyGame, 0, Integer.MIN_VALUE , Integer.MAX_VALUE));
                if (temp > maxValue) {
                    maxValue = temp;
                    bestAction = action;
                }
            }
        }
        doneActions++;
        return bestAction;
    }

    private int maxForceAttack(Game game, int depth , int alpha , int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }

        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(this, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType()) {
                        return Integer.MAX_VALUE;
                    }
                } else {
                    maxValue = Math.max(maxValue, maxSecondMove(copyGame, depth + 1 , alpha , betha));
                    if(maxValue >= betha) return maxValue;
                    alpha = Math.max(alpha , maxValue);
                }

            }
        }
        return maxValue;
    }

    private int maxSecondMove(Game game, int depth , int alpha , int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }

        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return Integer.MAX_VALUE;
                }else{
                    return  Integer.MIN_VALUE;
                }
            } else {
                maxValue = Math.max(maxValue, minForceAttack(copyGame, depth + 1 , alpha , betha));
                if(maxValue >= betha) return maxValue;
                alpha = Math.max(alpha , maxValue);
            }

        }
        return maxValue;
    }

    private int minForceAttack(Game game, int depth , int alpha , int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }

        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(this, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType().reverse()) {
                        return Integer.MIN_VALUE;
                    }
                } else {
                    minValue = Math.min(minValue, minSecondMove(copyGame, depth + 1 , alpha , betha));
                    if(minValue <= alpha) return minValue;
                    betha = Math.min(betha , minValue);
                }
            }

        }
        return minValue;

    }

    private int minSecondMove(Game game, int depth , int alpha , int betha) {
        if (depth == maxDepth) {
            return eval(game);
        }

        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType().reverse()) {
                    return Integer.MIN_VALUE;
                }else{
                    return Integer.MAX_VALUE;
                }
            } else {
                minValue = Math.min(minValue, maxForceAttack(copyGame, depth + 1 , alpha , betha));
                if(minValue <= alpha) return minValue;
                betha = Math.min(betha , minValue);
            }

        }

        return minValue;
    }

    class SortbyPoints implements Comparator<Action>{
        @Override
        public int compare(Action o1, Action o2) {
            if(o1.getScore()>= o2.getScore()) return 1;
            return 0;
        }
    }


}
