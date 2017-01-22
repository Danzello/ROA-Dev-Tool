package com.danzello.main.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.danzello.main.data.CharacterModel;
import com.danzello.main.data.Move;
import com.danzello.main.data.MoveProperty;

public class FileReadWriter {
	
	
	public static final byte ABSA 	    = 0;
	public static final byte ETALUS     = 1;
	public static final byte FORSBURN   = 2;
	public static final byte GENERAL    = 3;
	public static final byte KRAGG      = 4;
	public static final byte MAYPUL     = 5;
	public static final byte ORCANE     = 6;
	public static final byte WRASTOR    = 7;
	public static final byte ZETTERBURN = 8;
	
	private static final String[] files = {
		"custom_absa.ini",
		"custom_etalus.ini",
		"custom_forsburn.ini",
		"custom_general.ini",
		"custom_kragg.ini",
		"custom_maypul.ini",
		"custom_orcane.ini",
		"custom_wrastor.ini",
		"custom_zetterburn.ini"
	};
	
	public static FileResults getCharacterFromFile(String directory, byte fileIndex){
		FileReader fr = null;
		int lineNumber = 0;
		
		try {
			fr = new FileReader(directory + File.separatorChar + files[fileIndex]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fr);
		CharacterModel character = new CharacterModel(fileIndex);
		
		try{
			String line;
			Move move = null;
			while((line = reader.readLine()) != null){
				lineNumber++;
				if(line.startsWith("[")){
					move = new Move(line);
					character.addMove(move);
				}else if(Character.isLowerCase(line.charAt(0))){
					String[] subStrings = line.split("=");
					String value = subStrings[1].split("\"")[1];
					
					move.addProperty(new MoveProperty(subStrings[0], Float.parseFloat(value)));
				}else{
					return new FileResults(null, lineNumber);
				}
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e){
			return new FileResults(null, lineNumber);
		}
		return new FileResults(character, 0);
	}
	
	public static void save(String directory, CharacterModel chara){
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(directory + File.separatorChar + files[chara.charID]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedWriter writer = new BufferedWriter(fw);
		Move move = null;
		MoveProperty prprty = null;
		
		try{
			for(int m = 0; m < chara.getAmountofMoves(); m++){
				move = chara.getMove(m);
				writer.write(move.name + "\r\n");
				
				for(int v = 0; v < chara.getAmountofProperties(m); v++){
					prprty = move.getProperty(v);
					writer.write(prprty.name + "=\"" + prprty.getValue() + getRemainingDigits(prprty.getValue()) + "\"\r\n");
				}
			}
			
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	private static String getRemainingDigits(float num){
		String numStr = "" + num;
		int numDigitsLeft = 6 - numStr.split("\\.")[1].length();
		
		String str = new String(new char[numDigitsLeft]).replace("\0", "0");
		
		return str;
	}
	
	public static String getFileName(byte charaIndex){
		return files[charaIndex];
	}
}
