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

import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;

/**
 * Interface of the View (MVP) part of the Pongy game.  
 */
public interface PongyView extends HasKeyDownHandlers, HasKeyUpHandlers {

  /**
   * Places the ball on the view.
   * 
   * @param ball Ball
   */
  void setBall(Ball ball);

  /**
   * Places the left player bar on the view.
   *
   * @param bar Bar of the left player
   */
  void setBarPlayerLeft(Bar bar);
  
  /**
   * Places the right player bar on the view.
   *
   * @param bar Bar of the right player
   */
  void setBarPlayerRight(Bar bar);

  /**
   * Shows the vertical dotted line in the middle of the game field. Any other
   * information shown, i.e. instructions, single line, winner are always hidden
   * when this method is called.
   *
   * @param visible If true shows the dotted line, otherwise it's hidden
   */
  void setLineVisible(boolean visible);

  /**
   * Sets the left player name and image.
   *
   * @param imgUrl Image of left player
   * @param name name of left player
   */
  void setPlayerNameLeft(String imgUrl, String name);

  /**
   * Sets the right player name and image.
   *
   * @param imgUrl Image of right player
   * @param name name of right player
   */
  void setPlayerNameRight(String imgUrl, String name);

  /**
   * Shows the game field including the player points.
   *
   * @param pointsPlayerLeft Points player left
   * @param pointsPlayerRight Points player right
   */
  void showGame(String pointsPlayerLeft, String pointsPlayerRight);

  /**
   * Shows the game instructions on the playing field.
   * 
   * @param text Game instructions
   */
  void showInstructions(String text);
  
  /**
   * Shows a single line of text on the playing field.
   *
   * @param text Text to show
   */
  void showSingleLine(String text);

  /**
   * Shows the winner or loser text on the game field.
   *
   * @param text Text if player has won or lost
   */
  void showWinner(String text);
}
