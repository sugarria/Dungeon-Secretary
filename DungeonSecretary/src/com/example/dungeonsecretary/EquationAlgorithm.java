package com.example.dungeonsecretary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.example.dungeonsecretary.model.StatData;
import com.example.dungeonsecretary.sql.DungeonDataSource;

public class EquationAlgorithm {
	DungeonDataSource dbData;
	
	public EquationAlgorithm(Context context){
		dbData = DungeonDataSource.getInstance(context);
	}
	
	public int getValue(String equationValue, long characterId){
		int result = evaluateEquation(tokenizer(equationValue),characterId);
		return result;
	}
	
	public List<String> tokenizer(String equation){
		List<String> tokens = new ArrayList<String>();
		if (equation.contains("|")) {
        	String[] values = equation.split("\\|");
        	for(int i=0;i < values.length;i++){
        		tokens.add(values[i]);
        	}
     	}
		
		return tokens;
	}
	
	public int evaluateEquation(List<String> tokens, long characterId){
		int step=0;
		int total=0;
		if(isNumeric(tokens.get(0))){
			total = Integer.parseInt(tokens.get(0));
		}else{
			String statEquation = dbData.getStat(characterId, tokens.get(0)).getValue();
			total = evaluateStat(statEquation, characterId);
		}
		for(int i=1; i < tokens.size();i+=2){
			String op = tokens.get(i);
			String value = tokens.get(i+1);
			
			if(isNumeric(value))
			{
				step = Integer.parseInt(value);
			} else
			{
				String statEquation = dbData.getStat(characterId, value).getValue();
				step = evaluateStat(statEquation, characterId);
			}
			
			if(op.equals("+")){
				total = total + step;
				
			}else if(op.equals("-")){
				total = total - step;
				
			} else if(op.equals("x")){
				total = total * step;
				
			}else// divide
			{
				total = total / step;
					
			}
				
		}
		return total;
	}
	public int evaluateStat(String statEquation, long characterId){
		List<String> tokens = tokenizer(statEquation);
		int result = evaluateEquation(tokens,characterId);
		return result;
	}
	 public static boolean isNumeric(String str)
	{
		 return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

}
