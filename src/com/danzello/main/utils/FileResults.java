package com.danzello.main.utils;

import com.danzello.main.data.CharacterModel;

public class FileResults {

	private CharacterModel model;
	private int lineError;
	
	public FileResults(CharacterModel model, int lineError){
		this.model = model;
		this.lineError = lineError;
	}

	public CharacterModel getModel() {
		return model;
	}

	public int getLineError() {
		return lineError;
	}
}
