package com.danzello.main.windows;

import java.util.ArrayList;
import java.util.List;



import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;



import com.danzello.main.data.Move;
import com.danzello.main.data.MoveProperty;

public class HitboxWindow {

	private static List<Float> propertyValues = new ArrayList<Float>();
	private static boolean didNotEdit = true;
	
	private static String[] uniqueProperties = {
		"hitbox_window",
		"hitbox_window_creation_frame",
		"hitbox_lifetime",
		"hitbox_position_x",
		"hitbox_position_y"
	};
	private static String[] sharingProperties ={
		"base_knockback",
		"knockback_scaling",
		"knockback_angle",
		"damage",
		"angle_flipper",
		"hit_effect",
		"asdi_modifier",
		"hitstun_modifier",
		"untechable",
		"priority",
		"base_hitpause",
		"hitpause_scaling",
		"visual_effect",
		"hit_sound_effect",
		"extra_camera_shake",
		"can_kill_projectiles",
		"hit_lockout_time",
		"causes_extended_parry_stun",
		"force_flinch",
		"hitbox_shape",
		"hitbox_size_x",
		"hitbox_size_y",
		"visual_effect_x_offset",
		"visual_effect_y_offset"
	};
	
	
	public static boolean display(Move toEdit, Stage owner){
		didNotEdit = true;
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(owner);
		
		VBox userChoicesUnique = new VBox(5);
		for(int i = 0; i < uniqueProperties.length; i++){
			userChoicesUnique.getChildren().add(addHitboxProperty(1, i));
		}
		
		Label prompt_lbl = new Label("Number of Hitboxes:");
		TextField input_txtF = new TextField();
		input_txtF.setText("1");
		input_txtF.textProperty().addListener(new ChangeListener<String>(){
			
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				try{
					Integer.parseInt(newValue);
				
					userChoicesUnique.getChildren().clear();
				
					for(int i = 0; i < uniqueProperties.length - 2; i++){
						for(int j = 1; j <= Integer.parseInt(newValue); j++){
							userChoicesUnique.getChildren().add(addHitboxProperty(j, i));
						}
					}
					for(int j = 1; j <= Integer.parseInt(newValue); j++){
						userChoicesUnique.getChildren().add(addHitboxProperty(j, 3));
						userChoicesUnique.getChildren().add(addHitboxProperty(j, 4));
					}
					
				}catch(NumberFormatException e){
					if(newValue.equals(""))
						input_txtF.setText("");
					else
						input_txtF.setText(oldValue);
				}
			}
			
		});
		
		HBox userNumHitboxes = new HBox(5);
		userNumHitboxes.setAlignment(Pos.BASELINE_CENTER);
		userNumHitboxes.getChildren().addAll(prompt_lbl, input_txtF);
		
		
		VBox userChoicesSharing = new VBox(5);
		for(int i = 0; i < sharingProperties.length; i++){
			userChoicesSharing.getChildren().add(addSharingProperty(i));
		}
		
		BorderPane hitboxPropertyDisplay = new BorderPane();
		hitboxPropertyDisplay.setTop(userNumHitboxes);
		BorderPane.setMargin(userNumHitboxes, new Insets(0, 0, 10, 0));
		hitboxPropertyDisplay.setLeft(userChoicesUnique);
		BorderPane.setMargin(userChoicesUnique, new Insets(0, 0, 0, 10));
		hitboxPropertyDisplay.setRight(userChoicesSharing);
		BorderPane.setMargin(userChoicesSharing, new Insets(0, 10, 0, 0));
		
		StackPane wrapper = new StackPane(hitboxPropertyDisplay);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setContent(wrapper);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		
		Button confirm_btn = new Button("Generate Hitbox");
		confirm_btn.setOnAction(e -> {
			int returnedNum = ConfirmWindow.display("What your about to add can only be undone manually (after you save of course), do you still want to do this?", window);
			if(returnedNum == 1){
				for(int i = 0; i < userChoicesUnique.getChildren().size(); i++){
					TextField txtF = (TextField)((HBox) userChoicesUnique.getChildren().get(i)).getChildren().get(1);
					propertyValues.add(Float.parseFloat(txtF.getText()));
				}
				for(int i = 0; i < userChoicesSharing.getChildren().size(); i++){
					TextField txtF = (TextField)((HBox) userChoicesSharing.getChildren().get(i)).getChildren().get(1);
					propertyValues.add(Float.parseFloat(txtF.getText()));
				}
				
				boolean genSuccessful = genHitbox(toEdit, Integer.parseInt(input_txtF.getText()), window);
				propertyValues.clear();
				
				if(genSuccessful)
					window.close();
			}
		});
		
		HBox footer = new HBox(confirm_btn);
		footer.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(confirm_btn, new Insets(10));
		
		VBox layout = new VBox(scrollPane, footer);
		
		Scene scene = new Scene(layout, 600, 650);
		
		window.setResizable(false);
		window.setScene(scene);
		window.setTitle("Add a Hitbox");
		window.showAndWait();
		
		return didNotEdit;
	}
	
	public static boolean genHitbox(Move mve, int submittedHitboxes, Stage subWindowOwner){
		float numHitboxes = mve.getProperty("num_hitboxes").getValue();
		float numUniqueHitboxes = mve.getProperty("num_unique_hitboxes").getValue();
		float numFinalHitboxes = mve.getProperty("num_final_hitboxes").getValue();
		float numMultiHitboxes = (numHitboxes == 1) ? 0 : getNumMultiHitboxes(mve, (int)numHitboxes);
		
		System.out.println("numMultiHitboxes: "+ numMultiHitboxes);
		
		int index = 0;
		if(submittedHitboxes == 1){//----------------------------------------------SINGLE------------------------------------------------------------------
			if(numMultiHitboxes == 0){
				if(numUniqueHitboxes == 1){ //=====================================Single on Single==================================================
					
					// Parent Hitbox
					mve.getProperty("parent_hitbox").setName("parent_hitbox_1");
					mve.addPropertyAfter("parent_hitbox_1", new MoveProperty("parent_hitbox_2", 2));
					
					// Individual properties
					for(int i = 0; i < 3; i++){
						mve.getProperty(uniqueProperties[i]).setName(uniqueProperties[i] + "_1");
						mve.addPropertyAfter(uniqueProperties[i] + "_1", new MoveProperty(uniqueProperties[i] + "_2", propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.getProperty(uniqueProperties[i]).setName(uniqueProperties[i] + "_1");
						mve.addPropertyAfter(uniqueProperties[i] + (i == 4 ? "_1" : "_2"), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_2", propertyValues.get(index++)));
					}
					
					// Sharing properties
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_1");
						mve.addPropertyAfter(sharingProperties[i] + "_1", new MoveProperty(sharingProperties[i] + "_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_1" : "_2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_1" : "_2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_2", propertyValues.get(index++)));
					}
				}else if(numUniqueHitboxes > 1){ //===============================Single on multiple Singles=======================================================
					
					// Parent Hitbox
					mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + 1), numHitboxes + 1));
					
					// Individual properties
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					// Sharing properties
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_" + (int)(numHitboxes), new MoveProperty(sharingProperties[i] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_" + (i == 21 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_" + (i == 23 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
				}
			}else if(numMultiHitboxes == 1){
				// Parent Hitbox
				mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + 1), (numHitboxes + 1)));
				if(numFinalHitboxes == 0){ //==========================================Single on MultiHit==================================================
					
					// Individual properties
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					// Sharing properties
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit", new MoveProperty(sharingProperties[i] + "_final", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit");
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_multihit" : "_final"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit");
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_multihit" : "_final"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final", propertyValues.get(index++)));
					}
					
					//Num Final Hitbox
					mve.getProperty("num_final_hitboxes").setValue(numFinalHitboxes + 1);
				}else if(numFinalHitboxes == 1){ //================================Single on MultiHit + one Final======================================
					
					// Individual properties
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					// Sharing properties
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + "_final_1", new MoveProperty(sharingProperties[i] + "_final_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_final_1" : "_final_2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_final_1" : "_final_2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final_2", propertyValues.get(index++)));
					}
					
					//Num Final Hitbox
					mve.getProperty("num_final_hitboxes").setValue(numFinalHitboxes + 1);
				}else if(numFinalHitboxes > 1){ //==================================Single on MultiHit + multiple Finals===================================
					
					//Individual Properties
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					//Sharing Properties
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_final_" + (int)(numFinalHitboxes), new MoveProperty(sharingProperties[i] + "_final_" + (int)(numFinalHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_final_" + (i == 21 ? (int)(numFinalHitboxes) : (int)(numFinalHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final_" + (int)(numFinalHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_final_" + (i == 23 ? (int)(numFinalHitboxes) : (int)(numFinalHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final_" + (int)(numFinalHitboxes + 1), propertyValues.get(index++)));
					}
					
					//Num Final Hitbox
					mve.getProperty("num_final_hitboxes").setValue(numFinalHitboxes + 1);
				}
			}else if(numMultiHitboxes > 1){
				
				mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + 1), (numHitboxes + 1)));
				
				if(numFinalHitboxes == 0){//=====================================Single on multiple Multihits================================================================================================
					
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] +"_"+ (int)(numHitboxes), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes) : (int)(numHitboxes + 1)), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes), new MoveProperty(sharingProperties[i] + "_final", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_multihit_" + (int)(numMultiHitboxes) : "_final"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_multihit_" + (int)(numMultiHitboxes) : "_final"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final", propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes == 1){//================================Single on multiple Multihits + one Final===================================================================================
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] +"_"+ (int)(numHitboxes), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] +"_"+ (int)(numHitboxes), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + "_final_1", new MoveProperty(sharingProperties[i] + "_final_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_final_1" : "_final_2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i] + "_final").setName(sharingProperties[i] + "_final_1");
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_final_1" : "_final_2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final_2", propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes > 1){//=================================Single on multiple Multihits + multiple Finals===========================================================================
					
					for(int i = 0; i < 3; i++){
						mve.addPropertyAfter(uniqueProperties[i] +"_"+ (int)(numHitboxes), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 4; i >= 3; i--){
						mve.addPropertyAfter(uniqueProperties[i] +"_"+ (int)(numHitboxes), new MoveProperty(uniqueProperties[i]+ "_" + (int)(numHitboxes + 1), propertyValues.get(index++)));
					}
					
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_final_" + (int)(numFinalHitboxes), new MoveProperty(sharingProperties[i] + "_final_" + (int)(numFinalHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_final_" + (int)(numFinalHitboxes) : "_final_" + (int)(numFinalHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_final_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_final_" + (int)(numFinalHitboxes) : "_final_" + (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_final_" + (int)(numFinalHitboxes + 1), propertyValues.get(index++)));
					}
				}
				mve.getProperty("num_final_hitboxes").setValue(numFinalHitboxes + 1);
			}
		}else if(submittedHitboxes > 1){//--------------------------------------------MULTIHIT-------------------------------------------------------------
			if(numMultiHitboxes == 0){
				if(numUniqueHitboxes == 1){//======================================Multihit on Single=================================================
					
					// Parent Hitbox
					mve.getProperty("parent_hitbox").setValue(numHitboxes + submittedHitboxes);
					mve.getProperty("parent_hitbox").setName("parent_hitbox_1");
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + i, new MoveProperty("parent_hitbox_" + (i + 1), 1));
					}
					
					// Individual Properties
					for(int i = 0; i < 3; i++){
						mve.getProperty(uniqueProperties[i]).setName(uniqueProperties[i] + "_1");
						for(int j = 1; j <= submittedHitboxes; j++){
								mve.addPropertyAfter(uniqueProperties[i] + "_" + j, new MoveProperty(uniqueProperties[i] + "_" + (1 + j), propertyValues.get(index++)));
						}
					}
					for(int i = 4; i >= 3; i--){
						mve.getProperty(uniqueProperties[i]).setName(uniqueProperties[i] + "_1");
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (1 + j), propertyValues.get(index++)));
						}
					}
					
					// Sharing Properties
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_final");
						mve.addPropertyAfter(sharingProperties[i] + "_final", new MoveProperty(sharingProperties[i] + "_multihit", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_final");
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_final" : "_multihit"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_final");
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_final" : "_multihit"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit", propertyValues.get(index++)));
					}
					
					// Num Final Hitbox
					mve.getProperty("num_final_hitboxes").setValue(numFinalHitboxes + 1);
				}else if(numUniqueHitboxes > 1){//=================================Multhit on multiple Singles===========================================
					// Parent Hitbox
					for(int i = 1; i <= numHitboxes; i++){
						mve.getProperty("parent_hitbox_" + i).setValue(submittedHitboxes + i);
					}
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)((i + numHitboxes) - 1), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), 1));
					}
					
					// Individual Properties
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)((j + numUniqueHitboxes) - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(j + numUniqueHitboxes), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + ((int)numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					// Sharing Properties
					for(int i = 0; i < 20; i++){
						for(int j = 1; j <= numUniqueHitboxes; j++){
							mve.getProperty(sharingProperties[i] + "_" + j).setName(sharingProperties[i] + "_final_" + j);
						}
						mve.addPropertyAfter(sharingProperties[i] + "_final_" + (int)numUniqueHitboxes, new MoveProperty(sharingProperties[i] + "_multihit", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						for(int j = 1; j <= numUniqueHitboxes; j++){
							mve.getProperty(sharingProperties[i] + "_" + j).setName(sharingProperties[i] + "_final_" + j);
						}
						mve.addPropertyAfter(sharingProperties[i] + (i == 21 ? "_final_" + (int)numUniqueHitboxes : "_multihit"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						for(int j = 1; j <= numUniqueHitboxes; j++){
							mve.getProperty(sharingProperties[i] + "_" + j).setName(sharingProperties[i] + "_final_" + j);
						}
						mve.addPropertyAfter(sharingProperties[i] + (i == 23 ? "_final_" + (int)numUniqueHitboxes : "_multihit"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit", propertyValues.get(index++)));
					}
					
					// Num Final Hitboxes
					mve.getProperty("num_final_hitboxes").setValue(numUniqueHitboxes);
				}
			}else if(numMultiHitboxes == 1){
				if(numFinalHitboxes == 0){//======================================Multihit on Multihit==============================================
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + i - 1), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numUniqueHitboxes + 1));
					}
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_1", new MoveProperty(sharingProperties[i] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i]).setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes == 1){//==================================Multihit on Multihit + one Final======================================
					
					mve.getProperty(getFinalHitboxIndex(mve, 1, (int)numHitboxes)).setValue(numHitboxes + submittedHitboxes);
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + i - 1), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numUniqueHitboxes));
					}
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_1", new MoveProperty(sharingProperties[i] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes > 1){//==============================Multihit on Multihit + multiple Finals====================================
					for(int i = 1; i <= numFinalHitboxes; i++){
						mve.getProperty(getFinalHitboxIndex(mve, i, (int)numHitboxes)).setValue(numHitboxes + submittedHitboxes - numFinalHitboxes + i);
					}
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + i - 1), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numMultiHitboxes + 1));
					}
					
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_1", new MoveProperty(sharingProperties[i] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.getProperty(sharingProperties[i] + "_multihit").setName(sharingProperties[i] + "_multihit_1");
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? "1" : "2"), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_2", propertyValues.get(index++)));
					}
				}
			}else if(numMultiHitboxes > 1){
				if(numFinalHitboxes == 0){
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + (i - 1)), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numMultiHitboxes + 1));
					}
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes), new MoveProperty(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes == 1){
					mve.getProperty(getFinalHitboxIndex(mve, 1, (int)numHitboxes)).setValue(numHitboxes + submittedHitboxes);
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + (i - 1)), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numMultiHitboxes + 1));
					}
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes), new MoveProperty(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
				}else if(numFinalHitboxes > 1){
					for(int i = 1; i <= numFinalHitboxes; i++){
						mve.getProperty(getFinalHitboxIndex(mve, i, (int)numHitboxes)).setValue(numHitboxes + submittedHitboxes - numFinalHitboxes + i);
					}
					for(int i = 1; i <= submittedHitboxes; i++){
						mve.addPropertyAfter("parent_hitbox_" + (int)(numHitboxes + (i - 1)), new MoveProperty("parent_hitbox_" + (int)(numHitboxes + i), numMultiHitboxes + 1));
					}
					for(int i = 0; i < 3; i++){
						for(int j = 1; j <= submittedHitboxes; j++){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (int)(numHitboxes + j - 1), new MoveProperty(uniqueProperties[i] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					for(int j = 1; j <= submittedHitboxes; j++){
						for(int i = 4; i >= 3; i--){
							mve.addPropertyAfter(uniqueProperties[i] + "_" + (i == 4 ? (int)(numHitboxes + (j - 1)) : (int)(numHitboxes + j)), new MoveProperty(uniqueProperties[i + (i == 4 ? -1 : 1)] + "_" + (int)(numHitboxes + j), propertyValues.get(index++)));
						}
					}
					
					for(int i = 0; i < 20; i++){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes), new MoveProperty(sharingProperties[i] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 21; i >= 20; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 21 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 21 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
					for(int i = 23; i >= 22; i--){
						mve.addPropertyAfter(sharingProperties[i] + "_multihit_" + (i == 23 ? (int)(numMultiHitboxes) : (int)(numMultiHitboxes + 1)), new MoveProperty(sharingProperties[i + (i == 23 ? -1 : 1)] + "_multihit_" + (int)(numMultiHitboxes + 1), propertyValues.get(index++)));
					}
				}
			}
		}
		mve.getProperty("num_hitboxes").setValue(numHitboxes + submittedHitboxes);
		mve.getProperty("num_unique_hitboxes").setValue(numUniqueHitboxes + 1);
		
		didNotEdit = false;
		return true;
	}
	
	private static HBox addSharingProperty(int index){
		Label prompt_lbl = new Label(sharingProperties[index] + ":");
		
		TextField input_txtF = new TextField("0");
		input_txtF.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				try{
					if(newValue.equals("-"))
						newValue = "0";
					
					Float.parseFloat(newValue);
				}catch(NumberFormatException e){
					if(newValue.equals(""))
						input_txtF.setText("");
					else
						input_txtF.setText(oldValue);
				}
			}
		});
		input_txtF.setPrefWidth(50);
		
		HBox result = new HBox(5);
		result.setAlignment(Pos.BASELINE_RIGHT);
		result.getChildren().addAll(prompt_lbl, input_txtF);
		
		return result;
	}
	
	private static HBox addHitboxProperty(int numID, int index){
		
		Label prompt_lbl = new Label(uniqueProperties[index] + "_" + numID + ":");
		
		TextField input_txtF = new TextField("0");
		input_txtF.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				try{
					if(newValue.equals("-"))
						newValue = "0";
					
					Float.parseFloat(newValue);
				}catch(NumberFormatException e){
					if(newValue.equals(""))
						input_txtF.setText("");
					else
						input_txtF.setText(oldValue);
				}
			}
		});
		input_txtF.setPrefWidth(50);
		
		HBox result = new HBox(5);
		result.setAlignment(Pos.BASELINE_RIGHT);
		result.getChildren().addAll(prompt_lbl, input_txtF);
		
		return result;
	}
	
	private static int getNumMultiHitboxes(Move move, int numHitboxes){
		int offset = move.getIndex("parent_hitbox_1");
		int numMultiHitboxes = 0;
		int i = 0;
		while(i < numHitboxes - 1){
			if(move.getProperty(offset + i).getValue() == move.getProperty(offset + i + 1).getValue()){
				i++;
				boolean streakEnded = false;
				while(!streakEnded){
					if(move.getProperty(offset + i).getValue() == move.getProperty(offset + i + 1).getValue()){
						i++;
					}else{
						numMultiHitboxes++;
						i++;
						streakEnded = true;
					}
				}
			}else{
				i++;
			}
		}
		return numMultiHitboxes;
	}
	private static int getFinalHitboxIndex(Move move, int placing, int numHitboxes){
		int offset = move.getIndex("parent_hitbox_1");
		int i = 0;
		while(placing != 0){
			if(offset + i == offset + numHitboxes || move.getProperty(offset + i).getValue() != move.getProperty(offset + i + 1).getValue()){
				placing--;
				i++;
			}else{
				i++;
				boolean streakEnded = false;
				while(!streakEnded){
					if(move.getProperty(offset + i).getValue() == move.getProperty(offset + i + 1).getValue()){
						i++;
					}else{
						i++;
						streakEnded = true;
					}
				}
			}
		}
		System.out.println(move.getProperty(offset + i - 1).getName());
		return offset + i - 1;
	}
}
