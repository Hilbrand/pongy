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

import com.google.gwt.user.client.ui.HTML;

public class Bar extends HTML {
  public static enum BAR_POSITION { LEFT, RIGHT };
  
  private final int barheight = 70;
  protected int posy = 0;
  protected int speed = 20;
  private final int wheight;

  /**
   * 
   * @param wheight
   * @param barwidth
   * @param position position 
   */
  public Bar(int wheight, int barwidth, BAR_POSITION barPosition) {
    super("&nbsp;");
    this.wheight = wheight;
    setWidth(barwidth + "px");
    setHeight(barheight + "px");
    getElement().getStyle().setProperty(CSS.A.BACKGROUND_COLOR, "#00FF00");
    getElement().getStyle()
        .setProperty(CSS.A.POSITION, CSS.V.POSITION.ABSOLUTE);
    getElement().getStyle().setPropertyPx(
        BAR_POSITION.LEFT.equals(barPosition) ? CSS.A.LEFT : CSS.A.RIGHT, 0);
    start();
  }

  public void start() {
    posy = (wheight - barheight) / 2;
    move(0);
  }

  public int getTopY() {
    return posy;
  }

  public int getBottomY() {
    return posy + barheight;
  }

  public void setTopY(int topY) {
    posy = topY;
    getElement().getStyle().setPropertyPx(CSS.A.TOP, posy);
  }

  public void move(float direction) {
    posy = (int) (posy + direction * speed);
    checkborders();
    setTopY(posy);
  }

  private void checkborders() {
    if (wheight - barheight < posy) {
      posy = wheight - barheight;
      move(0);
    }
    if (0 > posy) {
      posy = 0;
      move(0);
    }
  }
}
