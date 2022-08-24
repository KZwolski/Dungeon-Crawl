package com.codecool.dungeoncrawl;


import com.codecool.dungeoncrawl.logic.*;
import com.codecool.dungeoncrawl.logic.Items.Item;
import com.codecool.dungeoncrawl.logic.actors.Monster;
import com.codecool.dungeoncrawl.logic.controller.GameController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

import java.io.IOException;

public class Main extends Application {

    private final int SCREEN_SIZE = 20;
    private final int LEVELS_AMOUNT = 3;
    private GameMap[] levels = new GameMap[3];
    private int level = 1;
    private int eqNumber = 0;
    GameMap map;
    Canvas canvas = new Canvas(
            SCREEN_SIZE * Tiles.TILE_WIDTH,
            SCREEN_SIZE * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Button pickUpItemBtn = new Button("Pick up");

    GameController gc;


    public Main() throws IOException {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int i = 0; i < LEVELS_AMOUNT; i++) {
            levels[i] = MapLoader.loadMap();
        }
        map = levels[level - 1];

        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));




        TableView tableView = new TableView<>();
        TableColumn<Item, String> column1 = new TableColumn<>("Item");
        TableColumn<Item, String> column2 = new TableColumn<>("Description");
        TableColumn<Item, Integer> column3 = new TableColumn<>("Value");
        column1.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        column2.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        column3.setCellValueFactory(new PropertyValueFactory<>("itemValue"));
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.setStyle("-fx-background-color: grey");


        ui.add(new Label("Health: "), 0, 0);
        ui.add(healthLabel, 1, 0);
        pickUpItemBtn.setFocusTraversable(false);
        ui.add(pickUpItemBtn,1,1);
        pickUpItemBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.getPlayer().pickUpItem();
//            tableView.getItems().add(map.getPlayer().getCell().getItem()); i dont know how to fucking optimalize that
            List<Item> playerInventory = map.getPlayer().getInventory();
            tableView.getItems().add(playerInventory.get(eqNumber));
            eqNumber++;
            refresh();
        });
        pickUpItemBtn.setStyle("-fx-background-color: grey");
        tableView.setPlaceholder(new Label("No items found yet"));


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        gc = fxmlLoader.getController();
        context = gc.getCanvas().getGraphicsContext2D();



        BorderPane borderPane = new BorderPane();

        gc.getBorderpane().setCenter(gc.getCanvas());
        gc.getBorderpane().setRight(ui);
        gc.getBorderpane().setLeft(tableView);
//        borderPane.setCenter(canvas);
//        borderPane.setRight(ui);
//        borderPane.setLeft(tableView);


        gc.getBorderpane().setRight(ui);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> incrementLabel()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        primaryStage.setScene(scene);
        refresh();
        scene.setOnKeyPressed(this::onKeyPressed);

        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    private void incrementLabel() {
        List<Monster> monsters = map.getMonsters();

        for(int i =0; i<monsters.size(); i++){
            monsters.get(i).monsterMovement(map);
        }
        refresh();
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case W:
            case UP:
                map.getPlayer().move(0, -1);
                refresh();
                break;
            case S:
            case DOWN:
                map.getPlayer().move(0, 1);
                refresh();
                break;
            case A:
            case LEFT:
                map.getPlayer().move(-1, 0);
                refresh();
                break;
            case D:
            case RIGHT:
                map.getPlayer().move(1, 0);
                refresh();
                break;
            case R:
                gc.getFight();
        }
    }

    private void refresh() {
        checkTile();
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int x = -SCREEN_SIZE; x < SCREEN_SIZE; x++) {
            for (int y = -SCREEN_SIZE; y < SCREEN_SIZE; y++) {
                try {
                    Cell cell = map.getCell(map.getPlayer().getX() + x - (SCREEN_SIZE / 2), map.getPlayer().getY() + y - (SCREEN_SIZE / 2));
                    if (cell.getActor() != null) {
                        Tiles.drawTile(context, cell.getActor(), x, y);
                    } else if (cell.getItem() != null) {
                        Tiles.drawTile(context, cell.getItem(), x, y);
                    } else {
                        Tiles.drawTile(context, cell, x, y);
                    }
                } catch (Exception e) {
                    Tiles.drawTile(context, new Cell(map, x, y, CellType.EMPTY), x, y);
                }
            }
        }
        healthLabel.setText("" + map.getPlayer().getHealth());
    }

    private void checkTile() {
        if (map.getPlayer().getCell().getType().equals(CellType.STAIRS)) {
            level++;
            map = levels[level - 1];
        }
    }
}
