/*
 * Copyright 2009 Hilbrand Bouwkamp, hs@bouwkamp.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cobogw.pongy.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.Color;
import org.cobogw.pongy.client.Pongy.PLAYER;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

public class Ball extends HTML {
  private int posx = 100;
  private int posy = 100;
  private final int size;
  private float speed = 3;
  private final float startimeelapse = 300;
  private float timeelapse = startimeelapse;
  private int dirx = 1;
  private int diry = 1;
  private final int wheight;
  private final int wwidth;
  private final int bwidth;
  private boolean wait;

  public Ball(int wheight, int wwidth, int bwidth, int ballheight) {
    super("&nbsp;");
    this.wheight = wheight;
    this.wwidth = wwidth;
    this.bwidth = bwidth;
    size = ballheight;
    setWidth(size + "px");
    setHeight(size + "px");
    getElement().getStyle().setProperty(CSS.A.BACKGROUND_COLOR, "#00FF00");
    getElement().getStyle()
        .setProperty(CSS.A.POSITION, CSS.V.POSITION.ABSOLUTE);
    getElement().getStyle().setPropertyPx(CSS.A.FONT_SIZE, 5);
  }

  /**
   * 
   * @param lastWinner
   * @param direction
   *          if true direction y is 1, else -1
   */
  public void start(PLAYER lastWinner, boolean direction) {
    final boolean lw = PLAYER.PLAYER_LEFT.equals(lastWinner);
    posx =  lw ? (bwidth+1) : wwidth - (bwidth+1);
    posy = wheight / 2;
    dirx = lw ? 1 : -1;
    diry = direction ? 1 : -1;
    timeelapse = startimeelapse;
    speed = calcSpeed(timeelapse);
    wait = false;
  }

  public void ballOut() {
    new Timer() {
      private int loop = 20;
      private boolean alter = false;

      @Override
      public void run() {
        if (0 == loop) {
          getElement().getStyle()
              .setProperty(CSS.A.BACKGROUND_COLOR, "#00FF00");
          cancel();
        } else {
          String color = alter ? "#00FF00" : Color.TRANSPARENT;
          getElement().getStyle().setProperty(CSS.A.BACKGROUND_COLOR, color);
        }
        alter = !alter;
        --loop;
      }
    }.scheduleRepeating(100);
  }

  public void move() {
    if (posx <= 0) {
      if (wait) {
        // wait for state change
        return;
      }
      dirx = -dirx;
    } else if (posx >= wwidth - bwidth) {
      if (wait) {
        // wait for state change
        return;
      }
      dirx = -dirx;
    }
    posx = Math.max(Math.min(posx + (int) (speed * dirx), wwidth - bwidth), 0);
    if (wheight - size < posy) {
      posy = wheight - size;
      diry = -diry;
    } else if (0 > posy) {
      posy = 0;
      diry = -diry;
    } else {
      posy = posy + (int) (speed * diry);
    }
    CSS.setPropertyPx(getElement(), CSS.A.LEFT, posx);
    CSS.setPropertyPx(getElement(), CSS.A.TOP, posy);
    speed = calcSpeed(timeelapse);
    timeelapse += 3;
  }

  public boolean atLeftBorder() {
    return posx <= 0 && dirx < 0;
  }
  
  public boolean atRightBorder() {
    return posx >= wwidth - bwidth && dirx > 0;
  }

  public int getDirX() {
    return dirx;
  }

  public int getX() {
    return posx;
  }

  public int getBottomY() {
    return posy + size;
  }

  public int getTopY() {
    return posy;
  }

  /**
   * If the ball was in a possible end-of-game state the ball is stalled until
   * or the game is started again (in case one of the players lost) or if it was
   * determined the player got the ball and the resumBall method was called.
   */
  public void resumeBall() {
    wait = false;
  }

  public void waitBall() {
    wait = true;
  }

  private float calcSpeed(float timeElapse) {
    return (float) Math.sqrt(Math.sqrt(timeelapse));
  }
}
