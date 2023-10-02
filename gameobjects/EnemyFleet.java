package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public EnemyFleet() {
        createShips();
    }

    private void createShips() {
        ships = new ArrayList<>();
        for (int y = 0; y < ROWS_COUNT; y++) {
            for (int x = 0; x < COLUMNS_COUNT; x++) {
                ships.add(new EnemyShip(x * STEP, y * STEP + 12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    public void draw(Game game) {
        ships.forEach(ship -> ship.draw(game));
    }

    public void move() {
        if (ships.isEmpty())
            return;

        boolean isChange = false;

        if (direction == Direction.LEFT && getLeftBorder() < 0) {
            direction = Direction.RIGHT;
            isChange = true;
        }

        if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
            direction = Direction.LEFT;
            isChange = true;
        }

        double speed = getSpeed();

        if (isChange)
            ships.forEach(ship -> ship.move(Direction.DOWN, speed));
        else
            ships.forEach(ship -> ship.move(direction, speed));
    }

    private double getLeftBorder() {
        return ships.stream()
                .map(ship -> ship.x)
                .min(Double::compareTo).get();
    }

    private double getRightBorder() {
        return ships.stream()
                .map(ship -> ship.x + ship.width)
                .max(Double::compareTo).get();
    }

    private double getSpeed() {
        return Math.min(2.0, 3.0 / ships.size());
    }

    public Bullet fire(Game game) {
        if (ships.isEmpty())
            return null;

        int randomNumber = game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY);
        if (randomNumber > 0)
            return null;

        int randomShip = game.getRandomNumber(ships.size());
        return ships.get(randomShip).fire();
    }

    public int verifyHit(List<Bullet> bullets) {
        if (bullets.isEmpty())
            return 0;

        int sum = 0;
        for (Bullet bullet : bullets) {
            for (EnemyShip enemyShip : ships) {
                boolean isHit = enemyShip.isCollision(bullet);
                if (isHit && enemyShip.isAlive && bullet.isAlive) {
                    enemyShip.kill();
                    bullet.kill();
                    sum += enemyShip.score;
                }
            }
        }
        return sum;
    }

    public void deleteHiddenShips() {
        ships.removeIf(enemyShip -> !enemyShip.isVisible());
    }

    public double getBottomBorder() {
        if (ships.isEmpty())
            return 0.;
        
        return ships.stream()
                .map(enemyShip -> enemyShip.y + enemyShip.height)
                .max(Double::compare).get();
    }

    public int getShipsCount() {
        return ships.size();
    }
}
