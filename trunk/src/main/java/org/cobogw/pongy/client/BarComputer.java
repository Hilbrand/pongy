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

import com.google.gwt.user.client.Random;

public class BarComputer extends Bar {
  private int reactonx = 0;
  private int margin = 0;
  private final int width;
  private final int maxmargin = 30;
  private final int randupper = 1000;
  private final int maxspeed = 3;
  private final int reactrand = 100;
  public BarComputer(int height, int width, int barwidth) {
    super(height, barwidth, BAR_POSITION.RIGHT);
    this.width = width;
    getElement().getStyle().setPropertyPx(CSS.A.RIGHT, 0);
    reactonx = width/2;
  }

  public void move(Ball ball) {
    final int bty = ball.getTopY();
    final int bby = ball.getBottomY();
    final int ct = getTopY();
    final int cb = getBottomY();
    if (ball.getX() < reactonx || ball.getDirX() < 0) {
      return;
    }
    final boolean rand = Random.nextInt(randupper) > reactrand  ? true : false;
    final float r2 = maxspeed * (Random.nextInt(randupper)/(float)randupper);

    if (ct+margin > bty && rand) {
      super.move(-1 * r2);
    } else if (cb+margin < bby && rand) {
      super.move(1 * r2);
    }
  }

  public void start (int pComputer, int pYou) {
    super.start();
    int total = pComputer + pYou+1;
    reactonx = width/3 +
      (int)((width/3)* 1 * ((float)pComputer / (total)));
    margin = maxmargin - (pYou*maxmargin)/total;
  }
}

