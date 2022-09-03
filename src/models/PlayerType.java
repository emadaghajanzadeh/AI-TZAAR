package models;

import java.io.Serializable;

public enum PlayerType implements Serializable {
    white, black;

    public PlayerType reverse(){
        if (this == white){
            return black;
        }else
            return white;
    }

    @Override
    public String toString() {
        if(this==white)
            return "white";
        else
            return "black";
    }
}
