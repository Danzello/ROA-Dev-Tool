package com.danzello.main.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmSaveWindow {
	
	private static boolean answer;
	
	public static boolean display(Stage owner){
		
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(owner);
		
		Label prompt_lbl = new Label("You didn't save changes! Would you like to save them?");
		
		Button accept_btn = new Button("Save");
		accept_btn.setPrefSize(100, 25);
		accept_btn.setOnAction(e -> {
			answer = true;
			window.close();
		});
		Button decline_btn = new Button("Don't Save");
		decline_btn.setPrefSize(100, 25);
		decline_btn.setOnAction(e -> {
			answer = false;
			window.close();
		});
		
		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(accept_btn, decline_btn);
		
		VBox layout = new VBox(30);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(prompt_lbl, buttons);
		
		Scene scene = new Scene(layout, 350, 100);
		
		window.setResizable(false);
		window.setScene(scene);
		window.setTitle("Unsaved Changes");
		window.showAndWait();
		
		return answer;
	}
	
}
