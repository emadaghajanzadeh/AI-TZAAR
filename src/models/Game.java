package models;

import java.io.*;
import java.util.*;

import static models.Board.isTherePath;

public class Game implements Serializable {

    private final Player white;
    private final Player black;
    private final Board board;
    Map<Board, Action> blackTable = new HashMap<Board, Action>();
    Map<Board, Action> whiteTable = new HashMap<Board, Action>();


    public Player getWhite(){
        return this.white;
    }

    public Player getBlack(){
        return this.black;
    }


    public Game(Player whitePlayer, Player blackPlayer) {
        this.white = whitePlayer;
        this.black = blackPlayer;
        this.board = new Board(black, white);
    }

    public Game(Player white, Player black, Board board) {
        this.white = white;
        this.black = black;
        this.board = board;
    }

    public Player play(int isLearning , int level) throws FileNotFoundException, InterruptedException {      //flag=1 filling table  flag=0 contest
        Player winner;
        Action action = white.forceAttack(copy());
        if (action == null) {
            return black;
        }
        if (applyAction(white, action, true)) {
            winner = black;
            return winner;
        }
        winner = getWinner();
        if (winner != null) {
            return winner;
        }
        int levell= level;  //Control Depth in Learning Step
        int depth = 0;      //measure depth during contest
        int threshold = -2;

        while ((isLearning==0)||(isLearning==1 && (levell-- >0))){


            if(isLearning==1){      //Filling the Table or Using it in Learning Mode
                if(!isOnTable(this.board , true)) {    //Filling the table
                    action = black.forceAttack(copy());
                    if (action == null) {
                        winner = white;
                        break;
                    }
                    action.setForce(true);
                    saveBoared(new Board(this.board) , action , true);
                }else{                          //Using the table
                    action = getTableAction(this.board , true);
                    if(action.getType()!=Action.ActionType.attack){
                        action = black.forceAttack(copy());
                        if (action == null) {
                            winner = white;
                            break;
                        }
                    }
                }
            }else{                             //Using the Table in Contest mode//
                // ToDo: Check Does it need to write another if to check if we are just in initial states use table. i.e if(depth+threshold<level+1)
                if(!isOnTable(this.board , true)) {    //Obtain Unpredicted State Action
                    action = black.forceAttack(copy());
                    if (action == null) {
                        winner = white;
                        break;
                    }
                }else {                          //Using the table
                    action = getTableAction(this.board , true);
                    if (action.getType() != Action.ActionType.attack) {
                        action = black.forceAttack(copy());
                        if (action == null) {
                            winner = white;
                            break;
                        }
                    }
                }
            }


            if (applyAction(black, action, true)) {
                winner = white;
                break;
            }
            winner = getWinner();
            if (winner != null) {
                break;
            }


            if(isLearning==1){      //Filling the Table or Using it in Learning Mode
                if(!isOnTable(this.board , true)) {    //Filling the table
                    action = black.secondAction(copy());
                    if (action == null) {
                        winner = white;
                        break;
                    }
                    action.setForce(false);
                    saveBoared(new Board(this.board) , action, true);
                }else{                          //Using the table
                    action = getTableAction(this.board , true);
                    if(action.getForce()){   //It Has been put in Force Attack Mode Which is not Optimal for Second Action
                        action = black.secondAction(copy());
                    }
                }
            }else{                             //Using the Table in Contest mode
                //ToDo: Check Does it need to write another if to check if we are just in initial states use table. i.e if(depth+threshold<level+1)
                if(!isOnTable(this.board , true)) {    //Obtain Unpredicted State Action
                    action = black.secondAction(copy());
                    if (action == null) {
                        winner = white;
                        break;
                    }
                }else {                          //Using the table
                    action = getTableAction(this.board , true);
                    if(action.getForce()){   //It Has been put in Force Attack Mode Which is not Optimal for Second Action
                        action = black.secondAction(copy());
                    }
                }
            }

            if (applyAction(black, action, false)) {
                winner = white;
                break;
            }
            winner = getWinner();
            if (winner != null) {
                break;
            }




//            action = white.forceAttack(copy());
            if(isLearning==1){      //Filling the Table or Using it in Learning Mode
                if(!isOnTable(this.board , false)) {    //Filling the table
                    action = white.forceAttack(copy());
                    if (action == null) {
                        winner = black;
                        break;
                    }
                    action.setForce(true);
                    saveBoared(new Board(this.board) , action , false);
                }else{                          //Using the table
                    action = getTableAction(this.board , false);
                    if(action.getType()!=Action.ActionType.attack){
                        action = white.forceAttack(copy());
                        if (action == null) {
                            winner = black;
                            break;
                        }
                    }
                }
            }else{                             //Using the Table in Contest mode//
                // ToDo: Check Does it need to write another if to check if we are just in initial states use table. i.e if(depth+threshold<level+1)
                if(!isOnTable(this.board , false)) {    //Obtain Unpredicted State Action
                    action = white.forceAttack(copy());
                    if (action == null) {
                        winner = black;
                        break;
                    }
                }else {                          //Using the table
                    action = getTableAction(this.board , false);
                    if (action.getType() != Action.ActionType.attack) {
                        action = white.forceAttack(copy());
                        if (action == null) {
                            winner = black;
                            break;
                        }
                    }
                }
            }


            if (applyAction(white, action, true)) {
                winner = black;
                break;
            }
            winner = getWinner();
            if (winner != null) {
                break;
            }


//            action = white.secondAction(copy());
            if(isLearning==1){      //Filling the Table or Using it in Learning Mode
                if(!isOnTable(this.board , false)) {    //Filling the table
                    action = white.secondAction(copy());
                    if (action == null) {
                        winner = black;
                        break;
                    }
                    action.setForce(false);
                    saveBoared(new Board(this.board) , action, false);
                }else{                          //Using the table
                    action = getTableAction(this.board , false);
                    if(action.getForce()){   //It Has been put in Force Attack Mode Which is not Optimal for Second Action
                        action = white.secondAction(copy());
                    }
                }
            }else{                             //Using the Table in Contest mode
                //ToDo: Check Does it need to write another if to check if we are just in initial states use table. i.e if(depth+threshold<level+1)
                if(!isOnTable(this.board , false)) {    //Obtain Unpredicted State Action
                    action = white.secondAction(copy());
                    if (action == null) {
                        winner = black;
                        break;
                    }
                }else {                          //Using the table
                    action = getTableAction(this.board , false);
                    if(action.getForce()){   //It Has been put in Force Attack Mode Which is not Optimal for Second Action
                        action = white.secondAction(copy());
                    }
                }
            }
            if (action == null) {
                winner = black;
                break;
            }
            if (applyAction(white, action, false)) {
                winner = black;
                break;
            }
            winner = getWinner();
            if (winner != null) {
                break;
            }
            depth++;
        }
        board.printComplete();
        return winner;
    }

    public Player getWinner() {
        boolean oneWhite = false, twoWhite = false, threeWhite = false;
        boolean oneBlack = false, twoBlack = false, threeBlack = false;
        for (Board.BoardRow row : board.getRows()) {
            for (Board.BoardCell cell : row.boardCells) {
                Bead bead = cell.bead;
                if (bead != null) {
                    if (bead.getPlayer().getType() == PlayerType.white) {
                        switch (bead.getType()) {
                            case Tzaars : oneWhite = true;
                            case Tzarras : twoWhite = true;
                            case Totts : threeWhite = true;
                        }
                    } else {
                        switch (bead.getType()) {
                            case Tzaars : oneBlack = true;
                            case Tzarras : twoBlack = true;
                            case Totts : threeBlack = true;
                        }
                    }
                }
            }
        }
        if (!(oneWhite && twoWhite && threeWhite)) {
            return black;
        }
        if (!(oneBlack && twoBlack && threeBlack)) {
            return white;
        }
        return null;
    }

    private boolean applyAction(Player player, Action action, boolean attack) {
        System.out.println(action);
        if (attack) {
            if (action.getType() != Action.ActionType.attack) {
                return true;
            } else {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() == player.getType()) {
                    return true;
                }
            }
        } else {
            if (action.getType() == Action.ActionType.nothing) {
                board.printComplete();
                return false;
            } else if (action.getType() == Action.ActionType.reinforce) {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() != player.getType()) {
                    return true;
                }
            } else {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() == player.getType()) {
                    return true;
                }
            }
        }

        if (isTherePath(action.getStart(), action.getTarget())) {
            Bead start = board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead;
            if (action.getType() == Action.ActionType.reinforce) {
                board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead = null;
                board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead.addBead(start);
            } else {
                Bead target = board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead;
                if (start.getHeight() < target.getHeight()) {
                    return true;
                }
                board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead = null;
                board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead = start;
            }
            board.printComplete();
            return false;
        } else
            return true;
    }

    public boolean applyActionTwo(Player player, Action action, boolean attack) {
        if (attack) {
            if (action.getType() != Action.ActionType.attack) {
                return true;
            } else {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() == player.getType()) {
                    return true;
                }
            }
        } else {
            if (action.getType() == Action.ActionType.nothing) {
                return false;
            } else if (action.getType() == Action.ActionType.reinforce) {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() != player.getType()) {
                    return true;
                }
            } else {
                if (action.getStart().bead.getPlayer().getType() != player.getType() ||
                        action.getTarget().bead.getPlayer().getType() == player.getType()) {
                    return true;
                }
            }
        }

        if (isTherePath(action.getStart(), action.getTarget())) {
            Bead start = board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead;
            if (action.getType() == Action.ActionType.reinforce) {
                board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead = null;
                board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead.addBead(start);
            } else {
                Bead target = board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead;
                if (start.getHeight() < target.getHeight()) {
                    return true;
                }
                board.getRows()[action.getStart().row].boardCells[action.getStart().col].bead = null;
                board.getRows()[action.getTarget().row].boardCells[action.getTarget().col].bead = start;
            }
            return false;
        } else
            return true;
    }

    public Game copy() {
        return new Game(white, black, new Board(board));
    }

    public Board getBoard() {
        return board;
    }

    public void saveBoared(Board board , Action action , boolean isBlack){
        if(isBlack){
            if(!this.blackTable.containsKey(board)) {
                this.blackTable.put(board, action);
            }else{
                System.out.println("Reprtiivie");
            }
        }else{
            if(!this.whiteTable.containsKey(board)) {
                this.whiteTable.put(board, action);
            }else{
                System.out.println("Reprtiivie");
            }

        }
    }

    public void saveTable(String address , boolean isBlack){
        try{
            FileOutputStream fos = new FileOutputStream(address);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            if(isBlack)
                oos.writeObject(this.blackTable);
            else
                oos.writeObject(this.whiteTable);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void loadTable(String address , boolean isBlack) throws NullPointerException{
            try{
                FileInputStream fis = new FileInputStream(address);
                ObjectInputStream ois = new ObjectInputStream(fis);
                if(isBlack)
                    this.blackTable = (HashMap) ois.readObject();
                else
                    this.whiteTable = (HashMap) ois.readObject();
                ois.close();
                fis.close();
            }catch(IOException ioe) {
//                ioe.printStackTrace();
                return;
            }catch(ClassNotFoundException c){
                return;
            }
            System.out.println("Deserialized HashMap..");
            // Display content using Iterator
              Set set = null;
             if(isBlack)
                 set = this.blackTable.entrySet();
            else
                 set = this.whiteTable.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                System.out.print("key: "+ mentry.getKey() + " & Value: ");
                System.out.println(mentry.getValue());
            }

    }

    public void printTable(boolean isBlack){
        if(isBlack) {
            for (Map.Entry<Board, Action> me : this.blackTable.entrySet()) {
                System.out.print(me.getKey() + ":");
                System.out.println(me.getValue());
            }
        }else{
            for (Map.Entry<Board, Action> me : this.whiteTable.entrySet()) {
                System.out.print(me.getKey() + ":");
                System.out.println(me.getValue());
            }
        }
    }


    public boolean isOnTable(Board board , boolean isBlack){
        boolean isOn = false;
        Map<Board, Action> temp = null;
        if(isBlack)
            temp = this.blackTable;
        else
            temp = this.whiteTable;
        for (Map.Entry<Board, Action> me :temp.entrySet()) {
            if(board.compareTo(me.getKey())==0){
                isOn = true;
                continue;
            }
        }
        return isOn;
    }

    public Action getTableAction(Board board , boolean isBlack){
        Action action = null;
        Map<Board, Action> temp = null;
        if(isBlack)
            temp = this.blackTable;
        else
            temp = this.whiteTable;
        for (Map.Entry<Board, Action> me : temp.entrySet()) {
            if (board.compareTo(me.getKey()) == 0) {
                    action = me.getValue();
            }
        }
        return action;
    }


    public Map<Board, Action> getTable(boolean isBlack){
        if(isBlack)
            return this.blackTable;
        else
            return this.whiteTable;
    }

    public void setTable(Map<Board, Action> mp , boolean isBlack){
        if(isBlack)
            this.blackTable = mp;
        else
            this.whiteTable = mp;
    }

}
