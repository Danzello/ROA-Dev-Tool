package com.danzello.main.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertWindow {

	public static void display(String message, Stage owner){
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(owner);
		
		Text message_lbl = new Text(message);
		message_lbl.setWrappingWidth(325);
		message_lbl.setTextAlignment(TextAlignment.CENTER);
		
		Button ok_btn = new Button("OK");
		ok_btn.setPrefWidth(60);
		ok_btn.setOnAction(e -> {
			window.close();
		});
		
		VBox layout = new VBox(30);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().setAll(message_lbl, ok_btn);
		
		Scene scene = new Scene(layout, 350, 100);
		
		window.setResizable(false);
		window.setScene(scene);
		window.setTitle("Alert!");
		window.showAndWait();
	}
	
}
