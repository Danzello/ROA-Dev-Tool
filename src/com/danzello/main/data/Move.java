package com.danzello.main.data;

import java.util.ArrayList;
import java.util.List;

public class Move {

	public final String name;
	private List<MoveProperty> properties = new ArrayList<MoveProperty>();
	
	public Move(String name){
		this.name = name;
	}
	
	public void addProperty(MoveProperty prprty){
		this.properties.add(prprty);
	}
	
	public void addPropertyAfter(String element, MoveProperty prprty){
		for(int i = 0; i < properties.size(); i++){
			if(properties.get(i).getName().equals(element)){
				this.properties.add(i + 1, prprty);
				//System.out.println(prprty.getName());
				return;
			}
		}
		System.out.println(element + "-------------------------------------------");
	}
	
	public MoveProperty getProperty(String name){
		for(MoveProperty prprty : properties){
			if(prprty.name.equals(name)){
				return prprty;
			}
		}
//		System.err.println(name + " is an invalid Property");
		return null;
	}
	
	public List<MoveProperty> filter(String filterText){
		
		List<MoveProperty> result = new ArrayList<MoveProperty>();
		
		for(MoveProperty p : properties){
			if(p.getName().contains(filterText))
				result.add(p);
		}
		
		return result;
	}
	
	public MoveProperty getProperty(int index){
		return properties.get(index);
	}
	
	public void setProperties(List<MoveProperty> properties2){
		properties = properties2;
	}
	
	public ArrayList<MoveProperty> getProperties(){
		return (ArrayList<MoveProperty>) properties;
	}
	
	public int getAmountofProperties(){
		return properties.size();
	}

	public String getName() {
		return name;
	}
	
}
