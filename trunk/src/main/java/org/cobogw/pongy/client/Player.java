package org.cobogw.pongy.client;

import org.cobogw.pongy.client.Bar.BAR_POSITION;
import org.cobogw.pongy.client.Pongy.STATE_KEYS;

public class Player {
  private final Bar bar;
  private String playerName;
  private boolean keyDown;
  private char activeKey;
  private final String posYKey;

  public Player(int barHeight, int barWidth, BAR_POSITION barPosition) {
    bar = new Bar(barHeight, barWidth, barPosition);
    keyDown = false;
    posYKey = (barPosition == BAR_POSITION.LEFT ? STATE_KEYS.PLAYER_LEFT_POSY
        : STATE_KEYS.PLAYER_RIGHT_POSY).toString();
  }

  public Bar getBar() {
    return bar;
  }

  public String getPlayerName() {
    return playerName;
  }

  public String getPosYKey() {
    return posYKey;
  }

  public boolean isKeyDown() {
    return keyDown;
  }
 
  public void move() {
    int direction = 0;
    if (keyDown) {
      if ('A' == activeKey) {
        direction = -1;
      } else if ('Z' == activeKey) {
        direction = 1;
      }
      bar.move(direction);
    }
  }

  public void setKeyState(boolean down, char keyCode) {
    this.keyDown = down;
    activeKey = keyCode;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public void start() {
    keyDown = false;
    bar.start();
  }
}
