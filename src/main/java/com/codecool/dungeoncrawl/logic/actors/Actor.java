package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.Drawable;
import com.codecool.dungeoncrawl.logic.GameMap;

public abstract class Actor implements Drawable {
    private Cell cell;
    private int health = 10;

    private int exp;

    private int damage;

    public Actor(Cell cell) {
        this.cell = cell;
        this.cell.setActor(this);
    }

    public Actor getActor() {
        return this.cell.getActor();
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);
        if (checkIfMovePossible(dx, dy)){
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }

    public int getHealth() {
        return health;
    }

    public abstract boolean checkIfMovePossible(int x, int y);

    public Cell getCell() {
        return cell;
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
