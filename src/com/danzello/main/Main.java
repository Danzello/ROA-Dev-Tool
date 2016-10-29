package com.danzello.main;


import static com.danzello.main.utils.FileReadWriter.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.danzello.main.data.CharacterModel;
import com.danzello.main.data.Move;
import com.danzello.main.data.MoveProperty;
import com.danzello.main.windows.AlertWindow;
import com.danzello.main.windows.ConfirmSaveWindow;
import com.danzello.main.windows.HitboxWindow;



public class Main extends Application{

	//C:\Users\dan9z_000\AppData\Local\RivalsofAether\dev_mode\dev_ver_0.3\active
	
	private static final String version = "1.0";
	private static String directory;

	private static CharacterModel character;
	
	private static Scene scene, scene2, scene3;
	
	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// For Scene 3
		Label saveIndicator_lbl = new Label();
		saveIndicator_lbl.setPrefWidth(50);
		saveIndicator_lbl.setFont(new Font(15));
		BooleanProperty saved = new SimpleBooleanProperty();
		saved.addListener((e, oldValue, newValue) ->{
			if(newValue)
				saveIndicator_lbl.setText("Saved");
			else
				saveIndicator_lbl.setText("");
		});
		saved.setValue(true);
		//
		
		// For Scene 2
		Label filePath_lbl = new Label();
		//
		
		Region region = new Region();
		region.setPrefHeight(100);
		
		Label title_lbl = new Label("ROA Dev Tool");
		title_lbl.setFont(new Font(80));
		
		Region region2 = new Region();
		region2.setPrefHeight(100);
		
		Label guide_lbl = new Label("Please give the file path containing the 9 .ini files");
		guide_lbl.setFont(new Font(20));
		
		TextField filePath_txtF = new TextField();
		filePath_txtF.setPrefWidth(300);
		
		Button browse_btn = new Button("Browse");
		browse_btn.setOnAction(e -> {
			DirectoryChooser fc = new DirectoryChooser();
			File file = fc.showDialog(primaryStage);
			if(file.getAbsolutePath() != null)
				filePath_txtF.setText(file.getAbsolutePath());
		});
				
		HBox inputFile = new HBox(10);
		inputFile.getChildren().addAll(filePath_txtF, browse_btn);
		inputFile.setAlignment(Pos.CENTER);
		

		Button nxt_btn = new Button("Next");
		nxt_btn.setPrefSize(60, 30);
		nxt_btn.setOnAction(e -> {
			directory = filePath_txtF.getText().replace('\\' , File.separatorChar);
			if(Files.exists(Paths.get(directory)) && !directory.equals("")){
				if(!directory.equals(null)){
					List<String> missingFiles = validateDirectory(directory);
					if(!missingFiles.isEmpty()){
						String message = "You are missing files:";
						for(int i = 0; i < missingFiles.size(); i++){
							message += " " + missingFiles.get(i) + ",";
						}
						AlertWindow.display(message, primaryStage);
					}else{
						filePath_lbl.setText(directory);
						primaryStage.setScene(scene2);
						filePath_txtF.setText("");
					}
				}
			}else{
				AlertWindow.display("The file given doesnt exist", primaryStage);
			}
		});
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(region, title_lbl, region2, guide_lbl, inputFile, nxt_btn);
		layout.setAlignment(Pos.TOP_CENTER);
		
		scene = new Scene(layout, 700, 700);
		scene.getWindow();
		
//////////////////////////////////////////////////////////////////////////////////////////
		
		Region gap = new Region();
		gap.setPrefHeight(50);
		
		Region gap2 = new Region();
		gap2.setPrefHeight(75);
		
		Label instructions_lbl = new Label("Choose the character to be modified");
		instructions_lbl.setFont(new Font(30));
		VBox pane = new VBox(0);
		pane.getChildren().add(instructions_lbl);
		
		Region gap3 = new Region();
		gap3.setPrefHeight(75);
		
		
		// For Scene 3
		ObservableList<Move> myObservableList = FXCollections.observableList(new CharacterModel((byte)0).getMoves());
		ListView<Move> moveNav_lstV = new ListView<>();
		//
		
		Button zetter_btn = new Button("Zetterburn");
		zetter_btn.setPrefSize(280, 25);
		zetter_btn.setOnAction(e -> {
			loadAndAdvance(ZETTERBURN, myObservableList, moveNav_lstV, primaryStage);
		});
		Button fors_btn = new Button("Forsburn");
		fors_btn.setPrefSize(280, 25);
		fors_btn.setOnAction(e -> {
			loadAndAdvance(FORSBURN, myObservableList, moveNav_lstV, primaryStage);
		});
		Button wrastor_btn = new Button("Wrastor");
		wrastor_btn.setPrefSize(280, 25);
		wrastor_btn.setOnAction(e -> {
			loadAndAdvance(WRASTOR, myObservableList, moveNav_lstV, primaryStage);
		});
		Button maypul_btn = new Button("Maypul");
		maypul_btn.setPrefSize(280, 25);
		maypul_btn.setOnAction(e -> {
			loadAndAdvance(MAYPUL, myObservableList, moveNav_lstV, primaryStage);
		});
		Button general_btn = new Button("General");
		general_btn.setPrefSize(280, 25);
		general_btn.setOnAction(e -> {
			loadAndAdvance(GENERAL, myObservableList, moveNav_lstV, primaryStage);
		});
		Button absa_btn = new Button("Absa");
		absa_btn.setPrefSize(280, 25);
		absa_btn.setOnAction(e -> {
			loadAndAdvance(ABSA, myObservableList, moveNav_lstV, primaryStage);
		});
		Button kragg_btn = new Button("Kragg");
		kragg_btn.setPrefSize(280, 25);
		kragg_btn.setOnAction(e -> {
			loadAndAdvance(KRAGG, myObservableList, moveNav_lstV, primaryStage);
		});
		Button orcane_btn = new Button("Orcane");
		orcane_btn.setPrefSize(280, 25);
		orcane_btn.setOnAction(e -> {
			loadAndAdvance(ORCANE, myObservableList, moveNav_lstV, primaryStage);
		});
		Button etalus_btn = new Button("Etalus");
		etalus_btn.setPrefSize(280, 25);
		etalus_btn.setOnAction(e -> {
			loadAndAdvance(ETALUS, myObservableList, moveNav_lstV, primaryStage);
		});
		
		VBox charcterButtons = new VBox(5);
		//grid.setAlignment(Pos.CENTER);
		charcterButtons.getChildren().addAll(zetter_btn, fors_btn, wrastor_btn, absa_btn, maypul_btn, kragg_btn, orcane_btn, etalus_btn, general_btn);
		
		Region gap4 = new Region();
		gap4.setPrefHeight(50);
		
		Button changePath_btn = new Button("Change Path");
		changePath_btn.setOnAction(e ->{
			directory = null;
			primaryStage.setScene(scene);
		});
		
		VBox layout2 = new VBox(10);
		layout2.setFillWidth(false);
		layout2.setAlignment(Pos.TOP_CENTER);
		layout2.getChildren().addAll(gap, filePath_lbl, gap2, instructions_lbl, gap3, charcterButtons, gap4, changePath_btn);
		
		scene2 = new Scene(layout2, 700, 700);
		
///////////////////////////////////////////////////////////////////////////////////////////////
		
		
		TableView<MoveProperty> propertyEditor_tbl = new TableView<>();
		propertyEditor_tbl.setEditable(true);
		
		HBox nav = new HBox(10);
		Button back_btn = new Button("Back");
		back_btn.setPrefWidth(50);
		back_btn.setOnAction(e -> {
			if(!saved.getValue()){
				if(ConfirmSaveWindow.display(primaryStage)){
					save(directory, character);
				}
			}
			primaryStage.setScene(scene2);
			myObservableList.clear();
			propertyEditor_tbl.getItems().clear();
			moveNav_lstV.getItems().clear();
			character = null;
			saved.setValue(true);
		});
		
		Region space = new Region();
		space.setPrefWidth(10);
		
		TextField search_txtF = new TextField();
		search_txtF.setPrefWidth(200);
		search_txtF.setOnAction(e -> {
			propertyEditor_tbl.setItems(FXCollections.observableArrayList(character.getMove(moveNav_lstV.getSelectionModel().getSelectedIndex()).filter(search_txtF.getText())));
		});
		
		Button search_btn = new Button("Search");
		search_btn.setOnAction(e ->{
			propertyEditor_tbl.setItems(FXCollections.observableArrayList(character.getMove(moveNav_lstV.getSelectionModel().getSelectedIndex()).filter(search_txtF.getText())));
		});
		
		Region space2 = new Region();
		space2.setPrefWidth(100);
		
		Button save_btn = new Button("Save");
		save_btn.setPrefWidth(50);
		save_btn.setOnAction(e -> {
			save(directory, character);
			saved.setValue(true);
		});
		
		
		
		
		Button addHitBox_btn = new Button("Add Hitbox");
		addHitBox_btn.setPrefWidth(100);
		addHitBox_btn.setOnAction(e -> {
			if(moveNav_lstV.getSelectionModel().getSelectedItem().getProperty("num_hitboxes") != null){
				saved.setValue((HitboxWindow.display(moveNav_lstV.getSelectionModel().getSelectedItem(), primaryStage) ? saved.getValue() : false));
				propertyEditor_tbl.setItems(FXCollections.observableArrayList(moveNav_lstV.getSelectionModel().getSelectedItem().getProperties()));
			}
		});
        
		nav.getChildren().addAll(back_btn, addHitBox_btn, saveIndicator_lbl, space, search_txtF, search_btn, space2, save_btn);
		
        moveNav_lstV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Move>() {
            @Override
            public void changed(ObservableValue<? extends Move> observable, Move oldValue, Move newValue) {
            	//System.out.print("observable: "+ observable +"\noldValue: "+ oldValue +"\nnewValue: "+ newValue+"\n\n");
            	if(newValue != null){
            		propertyEditor_tbl.scrollTo(0);
            		propertyEditor_tbl.setItems(FXCollections.observableArrayList(newValue.getProperties()));
            	}
            }
        });
        
        moveNav_lstV.setCellFactory(new Callback<ListView<Move>, ListCell<Move>>(){
           
        	public ListCell<Move> call(ListView<Move> p) {
                ListCell<Move> cell = new ListCell<Move>(){
                	
                    @Override
                    protected void updateItem(Move t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }else{
                        	setText(null);
                        }
                    }
 
                };
                 
                return cell;
            }
        });
        
        
        
		
        
		
		
		
		TableColumn<MoveProperty, String> name_clm = new TableColumn<MoveProperty, String>("Name");
		name_clm.setCellValueFactory(new PropertyValueFactory<MoveProperty, String>("name"));
		name_clm.prefWidthProperty().bind(propertyEditor_tbl.widthProperty().multiply(0.75));
		name_clm.setResizable(false);
		name_clm.setSortable(false);
		
		TableColumn<MoveProperty, Float> value_clm = new TableColumn<>("Value");
        value_clm.setCellValueFactory(new PropertyValueFactory<MoveProperty, Float>("value"));
        value_clm.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Float>() {
			@Override
			public String toString(Float object) {
				return String.valueOf(object);
			}
			@Override
			public Float fromString(String string) {
				try{
					if(!Float.isNaN(Float.parseFloat(string)))
						return Float.parseFloat(string);
					return null;
				}catch(NumberFormatException e){
					return null;
				}
			}
		}));
		value_clm.setSortable(false);
		value_clm.setOnEditCommit(
            new EventHandler<CellEditEvent<MoveProperty, Float>>() {
                public void handle(CellEditEvent<MoveProperty, Float> t) {
                	if(t.getNewValue() != null){
	                    ((MoveProperty) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue(t.getNewValue());
	                    saved.setValue(false);
                	}else{
                		((MoveProperty) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue(t.getOldValue());
                	}
                }
            }
        );
		
		value_clm.prefWidthProperty().bind(propertyEditor_tbl.widthProperty().multiply(0.25));
		value_clm.setResizable(false);
		
		propertyEditor_tbl.getColumns().addListener(new ListChangeListener<TableColumn<MoveProperty, ?>>() {
	        public void onChanged(Change<? extends TableColumn<MoveProperty, ?>> change) {
	          change.next();
	          if(change.wasReplaced()) {
	              propertyEditor_tbl.getColumns().clear();
	              propertyEditor_tbl.getColumns().addAll(name_clm, value_clm);
	          }
	        }

	    });
		
//		propertyEditor_tbl.setRowFactory(new Callback<TableView<Property>, TableRow<Property>>(){
//
//			@Override
//			public TableRow<Property> call(TableView<Property> param) {
//				TableRow<Property> row = new TableRow<Property>(){
//					protected void updateItem(Property t, boolean bln) {
//						super.updateItem(t, bln);
//						System.out.println(getText());
//						if(t != null)
//							setText("hello");
//					}
//				};
//				return row;
//			}
//			
//		});
		
		propertyEditor_tbl.getColumns().addAll(name_clm, value_clm);
		
		BorderPane borderPane = new BorderPane();
		BorderPane.setMargin(nav, new Insets(10));
		borderPane.setLeft(moveNav_lstV);
		borderPane.setCenter(propertyEditor_tbl);
		borderPane.setTop(nav);
		
		scene3 = new Scene(borderPane, 700, 700);
//		scene3.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>(){
//			@Override
//			public void handle(WindowEvent event) {
//				if(!saved){
//					if(ConfirmSaveWindow.display(primaryStage)){
//						save(directory, character);
//					}
//				}
//			}
//		});
		
///////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event) {
				if(primaryStage.getScene() == scene3){
					if(!saved.getValue()){
						if(ConfirmSaveWindow.display(primaryStage)){
							save(directory, character);
						}
					}
				}
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("ROA Dev Tool");
		primaryStage.show();
	}

	private static List<String> validateDirectory(String path){
		
		List<String> missingFiles = new ArrayList<String>();
		
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_absa.ini")))
			missingFiles.add("custom_absa.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_etalus.ini")))
			missingFiles.add("custom_etalus.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_forsburn.ini")))
			missingFiles.add("custom_forsburn.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_general.ini")))
			missingFiles.add("custom_general.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_kragg.ini")))
			missingFiles.add("custom_kragg.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_maypul.ini")))
			missingFiles.add("custom_maypul.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_orcane.ini")))
			missingFiles.add("custom_orcane.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_wrastor.ini")))
			missingFiles.add("custom_wrastor.ini");
		if(!Files.exists(Paths.get(path + File.separatorChar + "custom_zetterburn.ini")))
			missingFiles.add("custom_zetterburn.ini");
		
		return missingFiles;
	}
	
	private static void loadAndAdvance(byte chara, ObservableList<Move> myObservableList, ListView<Move> moveNav_lstV, Stage stage){
		try{
			character = getCharacterFromFile(directory, chara);
			if(character != null){
				myObservableList.setAll(character.getMoves());
				moveNav_lstV.setItems(myObservableList);
				moveNav_lstV.getSelectionModel().select(0);
				stage.setScene(scene3);
			}else{
				AlertWindow.display("Something is syntactically incorrect with " + getFileName(chara) + ": a weird character is placed at the start of a line", stage);
			}
		}catch(ArrayIndexOutOfBoundsException e){
			AlertWindow.display("Something is syntactically incorrect with " + getFileName(chara) + ": there is a missing \"=\" somewhere", stage);
		}
	}

}
