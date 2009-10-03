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

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * View implementation.
 */
public class PongyViewImpl extends Composite implements PongyView {
  private final Image playerLeftWidget;
  private final Label pointsPlayerLeftWidget;
  private final Image playerRightWidget;
  private final Label pointsPlayerRightWidget;
  private final HTML line;
  private final HTML instructions;
  private final HTML singleLine;
  private final HTML copy;
  private final Label winner;
  private final int width;
  private final FocusPanel fop;
  private final FlowPanel fp;

  public PongyViewImpl(int width, int height) {
    this.width = width;
    playerLeftWidget = new Image();
    playerLeftWidget.setVisible(false);
    playerRightWidget = new Image();
    playerRightWidget.setVisible(false);
    pointsPlayerLeftWidget = new Label();
    pointsPlayerRightWidget = new Label();
    line = new HTML("&nbsp;");
    copy = new HTML();
    winner = new Label();
    fop = new FocusPanel();
    fop.setWidth(width + "px");
    fop.setHeight(height + "px");
    fp = new FlowPanel();
    fop.add(fp);
    fp.getElement().getStyle().setProperty(CSS.A.POSITION,
        CSS.V.POSITION.RELATIVE);

    instructions = new HTML();
    instructions.setStyleName("instruct");
    fp.add(instructions);
    //copy.setStyleName("copy");
    //fp.add(copy);
    fp.add(playerLeftWidget);
    fp.add(playerRightWidget);
    setStylePlayerName(playerLeftWidget, CSS.A.RIGHT);
    setStylePlayerName(playerRightWidget, CSS.A.LEFT);
    fp.add(pointsPlayerLeftWidget);
    fp.add(pointsPlayerRightWidget);
    setStyleScore(pointsPlayerLeftWidget, CSS.A.RIGHT);
    setStyleScore(pointsPlayerRightWidget, CSS.A.LEFT);

    fp.add(line);
    line.setStyleName("middleline");
    line.getElement().getStyle().setPropertyPx(CSS.A.LEFT, width / 2);
    line.setHeight(height - 20 + "px");
    line.setVisible(false);

    fp.add(winner);
    winner.setStyleName("winner");
    winner.setVisible(false);
    winner.getElement().getStyle().setPropertyPx(CSS.A.TOP, width / 2 - 100);

    singleLine = new HTML();
    fp.add(singleLine);
    singleLine.setStyleName("singleLine");
    CSS.setProperty(fop, CSS.A.BACKGROUND_COLOR, Color.BLACK);
    RootPanel.get().add(fop);
  }

  public void debug(String text) {
//    if (text != null && !"".equals(text)) {
//      RootPanel.get().add(new InlineHTML(text +", "));
//    }
  }

  public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
    return fop.addKeyDownHandler(handler);
  }

  public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
    return fop.addKeyUpHandler(handler);
  }

  public void setBall(Ball ball) {
    fp.add(ball);
  }

  public void setBarPlayerLeft(Bar bar) {
    fp.add(bar);
  }

  public void setBarPlayerRight(Bar bar) {
    fp.add(bar);
  }

  public void setPlayerNameLeft(String imgUrl, String name) {
    playerLeftWidget.setVisible(true);
    playerLeftWidget.setUrl(imgUrl);
    playerLeftWidget.setTitle(name);
  };

  public void setPlayerNameRight(String imgUrl, String name) {
    playerRightWidget.setVisible(true);
    playerRightWidget.setUrl(imgUrl);
    playerRightWidget.setTitle(name);
  }

  public void setLineVisible(boolean visible) {
    instructions.setVisible(false);
    singleLine.setVisible(false);
    winner.setVisible(false);
    line.setVisible(visible);
  }

  public void showGame(String pointsPlayerLeft, String pointsPlayerRight) {
    setLineVisible(true);
    pointsPlayerLeftWidget.setText(pointsPlayerLeft);
    pointsPlayerRightWidget.setText(pointsPlayerRight);
  }

  public void showInstructions(String text) {
    setLineVisible(false);
    instructions.setHTML(text);
    instructions.setVisible(true);
  }

  public void showSingleLine(String text) {
    setLineVisible(false);
    singleLine.setHTML(text);
    singleLine.setVisible(true);
  }

  public void showWinner(String text) {
    setLineVisible(false);
    winner.setText(text);
    winner.setVisible(true);
  }

  private void setStylePlayerName(Image score, String ori) {
    score.setStyleName("playerName");
    score.getElement().getStyle().setPropertyPx(ori, width / 2 + 100);
  }

  private void setStyleScore(Label score, String ori) {
    score.setStyleName("score");
    score.getElement().getStyle().setPropertyPx(ori, width / 2 + 50);
  }
}
