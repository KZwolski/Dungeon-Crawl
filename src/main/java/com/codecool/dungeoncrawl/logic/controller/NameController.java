package com.codecool.dungeoncrawl.logic.controller;

import com.codecool.dungeoncrawl.Main;
import com.codecool.dungeoncrawl.Tiles;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.codecool.dungeoncrawl.Main.*;
import static com.codecool.dungeoncrawl.logic.music.MusicPlayer.playSound;
import static com.codecool.dungeoncrawl.logic.music.MusicPlayer.stepSound;

public class NameController {
    @FXML
    private TextField tfName;

    public TextField getTfName() {
        return tfName;
    }

    public static String userName;

    public static String getUserName() {
        return userName;
    }

    public static boolean startGame;

    GameController gc;

    GraphicsContext context;

    @FXML
    void startGame(KeyEvent event) throws IOException {
        userName = tfName.getText();
        if (event.getCode().equals(KeyCode.ENTER)) {
            startGame = true;
            bossLevel = MapLoader.loadMap(true);
            for (int i = 0; i < LEVELS_AMOUNT; i++) {
                levels[i] = MapLoader.loadMap(false);
            }
            map = levels[level - 1];
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 650);
            Stage stage = (Stage) tfName.getScene().getWindow();
//            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Dungeon Crawl");
            stage.setScene(scene);

            scene.setOnKeyPressed(this::onKeyPressed);

            gc = fxmlLoader.getController();
//    static GameController gc = new GameController();
            context = gc.getCanvas().getGraphicsContext2D();
//            Stage stageToClose = (Stage) tfName.getScene().getWindow();
//            stageToClose.close(); // nic nie zamykamy, działamy w jednym oknie

            refresh();

        }

    }

    private void onKeyPressed(KeyEvent keyEvent) {
        //wywolanie okna game over
//        if (FightController.isGameOver) {
//            gameOver();
//        }
        playSound(stepSound, (float) 0.1);
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
//            case R:
//                getFight();
            default:
                break;
        }
//        if (FightController.isFightAvailable) {
//            startFight();
//        }
    }

    public void refresh() {
//        boolean playerOnItem = false;
//        checkTile();
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int x = -SCREEN_SIZE; x < SCREEN_SIZE; x++) {
            for (int y = -SCREEN_SIZE; y < SCREEN_SIZE; y++) {
                try {
                    Cell cell = map.getCell(map.getPlayer().getX() + x - (SCREEN_SIZE / 2), map.getPlayer().getY() + y - (SCREEN_SIZE / 2));
                    if (cell.getActor() != null) {
                        Tiles.drawTile(context, cell.getActor(), x, y);
//                        if (cell.getItem() != null && cell.getActor() instanceof Player) {
//                            playerOnItem = true;
//                        }
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
//        if (playerOnItem) {
//            showButton(pickUpItemBtn);
//        } else {
//            hideButton(pickUpItemBtn);
//        }
//
//        gc.getHealthLabel().setText("" + map.getPlayer().getHealth());
//        animation.play();
    }

}