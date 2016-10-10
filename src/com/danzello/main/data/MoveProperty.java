package com.danzello.main.data;

public class MoveProperty {

	private float value;
	public String name;
	public boolean isDisplayed = true;
	
	public MoveProperty(String name, float value){
		this.name = name;
		this.value = value;
	}
	
	public String getName(){ return name; }
	public void setName(String name){ this.name = name; }
	
	public float getValue(){ return this.value; }
	public void setValue(float value){ this.value = value; }
	
}
