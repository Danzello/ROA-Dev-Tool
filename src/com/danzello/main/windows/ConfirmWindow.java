package com.danzello.main.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow {
	
	private static int answer;
	
	public static int display(String message, Stage owner){
		
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(owner);
		
		Text prompt_lbl = new Text(message);
		prompt_lbl.setWrappingWidth(325);
		prompt_lbl.setTextAlignment(TextAlignment.CENTER);
		
		Button accept_btn = new Button("Yes");
		accept_btn.setPrefSize(100, 25);
		accept_btn.setOnAction(e -> {
			answer = 1;
			window.close();
		});
		Button decline_btn = new Button("No");
		decline_btn.setPrefSize(100, 25);
		decline_btn.setOnAction(e -> {
			answer = 2;
			window.close();
		});
		
		window.setOnCloseRequest(e -> {
			answer = 0;
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
		window.setTitle("Hold On!");
		window.showAndWait();
		
		return answer;
	}
	
}
