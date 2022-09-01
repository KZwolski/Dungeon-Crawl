package com.codecool.dungeoncrawl.game.controller;

import com.codecool.dungeoncrawl.game.map.GameMap;
import com.codecool.dungeoncrawl.game.creatures.Monster;
import com.codecool.dungeoncrawl.game.creatures.Player;
import com.codecool.dungeoncrawl.game.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import static com.codecool.dungeoncrawl.game.controller.GameController.monstersMoving;
import static com.codecool.dungeoncrawl.game.music.MusicPlayer.stopSounds;

public class FightController {

    @FXML
    private ComboBox<?> boxSpells;

    @FXML
    private Button buttonAbility;

    @FXML
    private Button buttonAttack;

    @FXML
    private Button buttonBlock;

    @FXML
    private Label monsterDamage;

    @FXML
    private Label monsterHealth;

    @FXML
    private Label monsterLvl;

    @FXML
    private Label monsterAbility;

    @FXML
    private Label monsterName;

    @FXML
    private TextArea output;

    @FXML
    private Label playerAbility;

    @FXML
    private Label playerDamage;

    @FXML
    private Label playerHealth;

    @FXML
    private Label playerLvl;

    @FXML
    private Label playerMana;

    @FXML
    private Label playerName;

    public static Player player;
    public static Monster monster;
    public static boolean isFightAvailable = false;

    public void updateStats() {
        updatePlayerStats();
        updateMonsterStats();
    }

    private void updateMonsterStats() {
        monsterName.setText(monster.getTileName());
        monsterHealth.setText("HP: " + monster.getHealth());
        monsterDamage.setText("DAMAGE: " + monster.getDamage());
        monsterLvl.setText("EXP:" + monster.getExp());
        monsterAbility.setText("ABILITY: " + monster.getAbilityPower());
    }

    private void updatePlayerStats() {
        playerName.setText(NameController.userName);
        playerHealth.setText("HP: " + player.getHealth());
        playerAbility.setText("ABILITY: " + player.getAbilityPower());
        playerLvl.setText("LVL: " + player.getExp());
        playerDamage.setText("DAMAGE: " + player.getDamage());
        playerMana.setText("MANA: " + player.getMana());
    }

    @FXML
    void initialize() {
        stopSounds();
        stopAllMonstersMoving();

        initBattleButtons();

        updateStats();
    }

    private void initBattleButtons() {
        buttonAttack.setOnAction(e -> makeMove(Action.ATTACK, player, monster));
        buttonBlock.setOnAction(e -> makeMove(Action.BLOCK, player, monster));
        if (player.getMana() >= 40){
            buttonAbility.setOnAction(e -> makeMove(Action.ABILITY, player, monster));
        } else {
            buttonAbility.setOnAction(e -> output.appendText("You dont have enough mana \n"));
        }
    }

    private static void stopAllMonstersMoving() {
        monstersMoving.stop();
    }

    private void makeMove(Action userAction, Player player, Monster monster) {
        Action monsterAction = makeMonsterMove();
        Action.ActionResult result = userAction.checkAgainst(monsterAction);
        fightTurn(userAction, player, monster, monsterAction, result);
        checkBattleResult(player, monster);
        updateStats();
    }

    private void fightTurn(Action userAction, Player player, Monster monster, Action monsterAction, Action.ActionResult result) {
        if (result == Action.ActionResult.DRAW) {
            output.appendText("DRAW\n");
        } else if (result == Action.ActionResult.WIN) {
            dealDamageToMonster(userAction, player, monster);
        } else { // LOSE
            dealDamageToPlayer(player, monster, monsterAction);
        }
        regenerateMana(player);
    }

    private static void regenerateMana(Player player) {
        if (player.getMana() < 100){
            player.setMana(player.getMana() + 10);
        }
    }

    private void dealDamageToPlayer(Player player, Monster monster, Action monsterAction) {
        int dmg = monster.calcDamage(monsterAction);
        player.setHealth(player.getHealth() - dmg);
        output.appendText(String.format(("%s deals %s to %s \n"), monster.getTileName(), dmg, NameController.userName));
    }

    private void dealDamageToMonster(Action userAction, Player player, Monster monster) {
        int dmg = player.calcDamage(userAction);
        if (userAction == Action.ABILITY) {
            player.setMana(player.getMana() - 40);
        }
        monster.setHealth(monster.getHealth() - dmg);
        output.appendText(String.format(("%s deals %s to %s \n"), NameController.userName, dmg, monster.getTileName()));
    }

    private void checkBattleResult(Player player, Monster monster) {
        if (monster.getHealth() <= 0) {
            playerWin(monster);
        } else if (player.getHealth() <= 0){
            monsterWin();
        }
    }

    private void playerWin(Monster monster) {
        player.setExp(player.getExp() + monster.getExp());
        monster.getCreature().getCell().setCreature(null);
        GameMap.removeMonster(monster);
        ViewController.setGameView();
    }

    private void monsterWin() {
        ViewController.setEndView();
    }

    private Action makeMonsterMove() {
        return Action.values()[(int) (Utils.RANDOM.nextInt(0, Action.values().length))];
    }

}

