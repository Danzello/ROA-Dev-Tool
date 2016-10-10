package com.danzello.main.data;

import java.util.ArrayList;
import java.util.List;

public class CharacterModel {

	public final byte charID;
	List<Move> moves = new ArrayList<Move>();
	
	public CharacterModel(byte charID){
		this.charID = charID;
	}
	
	public void addMove(Move move){
		Move mve = move;
		this.moves.add(mve);
	}
	
	public Move getMove(String name){
		for(Move move : moves){
			if(move.name.equals(name)){
				return move;
			}
		}
		System.err.println(name + " is an invalid Move");
		return null;
	}
	
	public Move getMove(int index){
		return moves.get(index);
	}
	
	public int getAmountofMoves(){
		return moves.size();
	}
	
	public int getAmountofProperties(int index){
		return moves.get(index).getAmountofProperties();
	}
	
	public float getPrpValue(String mveName, String prprtyName){
		return getMove(mveName).getProperty(prprtyName).getValue();
	}
	
	public void setPrpValue(String mveName, String prprtyName, float value){
		getMove(mveName).getProperty(prprtyName).setValue(value);
	}

	public List<Move> getMoves() {
		return moves;
	}
	
	
}
