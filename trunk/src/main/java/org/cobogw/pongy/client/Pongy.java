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

import java.util.HashMap;

import org.cobogw.gwt.waveapi.gadget.client.NeedsWave;
import org.cobogw.gwt.waveapi.gadget.client.Participant;
import org.cobogw.gwt.waveapi.gadget.client.ParticipantUpdateEvent;
import org.cobogw.gwt.waveapi.gadget.client.ParticipantUpdateEventHandler;
import org.cobogw.gwt.waveapi.gadget.client.State;
import org.cobogw.gwt.waveapi.gadget.client.StateUpdateEvent;
import org.cobogw.gwt.waveapi.gadget.client.StateUpdateEventHandler;
import org.cobogw.gwt.waveapi.gadget.client.WaveFeature;
import org.cobogw.pongy.client.Bar.BAR_POSITION;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

//FIXME: vertraag bal loser bij startup, na [s] command
//FIXME: Toon nieuwe punten eerder: bij message "waitin" en niet bij start bal.

//TODO: Help tekst aanpassen

//http://test.cobogw.org/Pongy.gadget.xml
/**
 * Entry point classes define <code>onModuleLoad()</code>. TODO check for view
 * mode?
 */
  //, description = "Play Pong in Google Wave with Pongy", scaling = true, height = 400, author = "Hilbrand Bouwkamp", author_email="hs@bouwkamp.com")
@Gadget.ModulePrefs(title = "Pongy", height = 400)
public class Pongy extends Gadget<UserPreferences> implements NeedsWave {

  public static enum PLAYER {
    PLAYER_LEFT, PLAYER_RIGHT, COMPUTER;

    public boolean equals(String anObject) {
      return this.toString().equals(anObject);
    }
  };

  public static enum STATE_KEYS {
    PLAYER_LEFT_POSY, PLAYER_RIGHT_POSY, PLAYER_LEFT_READY, PLAYER_RIGHT_READY,
    LAST_WINNER, BALL_START, BALL_WAIT, PLAYER_LEFT_POINTS, PLAYER_RIGHT_POINTS, GAME_STATE;

    public boolean equals(String anObject) {
      return this.toString().equals(anObject);
    }
  };

  public static enum GAME_STATE {
    NEW, INIT, NEW_MATCH, ON, PAUZE, END;

    public boolean equals(String anObject) {
      return this.toString().equals(anObject);
    }
  }

  private static String BALL_WAIT_LEFT = "WL";
  private static String BALL_WAIT_RIGHT = "WR";

  private final static String instructionsText = "Instructions:<br><br>"
      + "[A] Move up<br>"
      + "[Z] Move down<br>"
      //+ "[P] Pause Game<br>"
      + "[S] Game starts when you both hit S key<br>"
      //+ "[N] New Game<br><br>"
      + "(Click with the mouse on the gamefield<br>if the keys don't react!)";
//  private final static String textPause = "Press [P] to resume the game";
  private final static String textInviteSomeone =
      "Invite someone to play a game with";
  private final static String textWaitingForBothPlayers =
      "Waiting for both players to press [S] to start the game.";
  private final static String texWaitingForOtherPlayer =
      "Waiting for other player to press [S]";

  private final int width = 520;
  private final int height = 400;
  private final int barwidth = 10;
  private final int ballheight = 10;

  private final Ball ball;
  private final Player playerLeft;
  private final Player playerRight;

  private Player thisPlayer;
  private Player otherPlayer;

  // private final BarComputer barComputer;
  private final Timer runner;
  private final Timer playerTimer;

  private final PongyView view;

  // Game play
  private final int gamespeed = 20;
  private final int winnertimeout = 3000;
  private PLAYER lastwinner;
  protected boolean pauze = false;
  private boolean matchStarted = false;

  // Wave interaction
  private WaveFeature wave;
  private HashMap<String, String> delta = new HashMap<String, String>();
  private Participant thisParticipant;

  /**
   * This is the entry point method.
   */
  public Pongy() {
    view = new PongyViewImpl(width, height);
    ball = new Ball(height, width, barwidth, ballheight);
    ball.setVisible(false);
    view.setBall(ball);

    playerLeft = new Player(height, barwidth, BAR_POSITION.LEFT);
    view.setBarPlayerLeft(playerLeft.getBar());
    playerRight = new Player(height, barwidth, BAR_POSITION.RIGHT);
    view.setBarPlayerRight(playerRight.getBar());

    runner = new Timer() {
      @Override
      public void run() {
        ball.move();
        if (thisPlayer != null) {
          thisPlayer.move();
        }
        detectCollision();
      }
    };
    playerTimer = new Timer() {
      private int lastTopY = -1;

      public void run() {
        if (wave != null) {
          final String gstate = getGameState(STATE_KEYS.GAME_STATE);
          final State state = wave.getState();

          if (state != null && GAME_STATE.ON.equals(gstate)) {
            // Handle this player state
            if (!thisPlayer.isKeyDown()
                && lastTopY != thisPlayer.getBar().getTopY()) {
              lastTopY = thisPlayer.getBar().getTopY();
              delta.put(thisPlayer.getPosYKey(), "" + lastTopY);
              if (!wave.isPlayback()) {
                state.submitDelta(delta);
              }
            }
            // Handle other player state
            final Integer y = state != null ? state.getInt(otherPlayer
                .getPosYKey()) : null;

            if (y != null) {
              otherPlayer.getBar().setTopY(y);
            }
          }
        }
      }
    };
  }

  public void initializeFeature(WaveFeature feature) {
    wave = feature;
  }

  protected void init(UserPreferences preferences) {
    view.addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        if (wave == null || wave.isPlayback()) {
          return;
        }
        final char keyCode = Character.toUpperCase((char) event
            .getNativeKeyCode());

//        if ('N' == keyCode && playerLeft == thisPlayer) {
//          setStateDelta(STATE_KEYS.PLAYER_LEFT_POINTS, "0");
//          setStateDelta(STATE_KEYS.PLAYER_RIGHT_POINTS, "0");
//          //submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.NEW_MATCH);
//        } else
        if ('S' == keyCode
            && GAME_STATE.NEW_MATCH.equals(getGameState(STATE_KEYS.GAME_STATE))) {
          view.showSingleLine(texWaitingForOtherPlayer);
          if (playerLeft == thisPlayer) {
            setStateDelta(STATE_KEYS.PLAYER_LEFT_READY, "1");
            setStateDelta(STATE_KEYS.BALL_START, String.valueOf(Random
                .nextInt(1000) > 500));
            submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.NEW_MATCH);
          } else {
            submitStateDelta(STATE_KEYS.PLAYER_RIGHT_READY, "1");
          }
//        } else if ('P' == keyCode) {
//        togglePauze();
        } else {
          if (thisPlayer != null) {
            thisPlayer.setKeyState(true, keyCode);
          }
        }
      }
    });
    view.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        if (wave != null && !wave.isPlayback() && thisPlayer != null) {
          thisPlayer.setKeyState(false, (char) event.getNativeKeyCode());
        }
      }
    });
    wave.addParticipantUpdateEventHandler(new ParticipantUpdateEventHandler() {
      public void onUpdate(ParticipantUpdateEvent event) {
        if (wave != null && thisParticipant == null) {
          thisParticipant = wave.getViewer();
        }
        initParticipants();
      }
    });
    wave.addStateUpdateEventHandler(new StateUpdateEventHandler() {

      public void onUpdate(StateUpdateEvent event) {
        final String gs = getGameState(STATE_KEYS.GAME_STATE, GAME_STATE.NEW);

        switch (GAME_STATE.valueOf(gs)) {
        case NEW:
          break;
        case INIT:
          view.showSingleLine(textInviteSomeone);
          break;
        case NEW_MATCH:
          if ((thisPlayer == playerLeft
              && getGameState(STATE_KEYS.PLAYER_LEFT_READY) == null)
             || (thisPlayer == playerRight
              && getGameState(STATE_KEYS.PLAYER_RIGHT_READY) == null)) {
            if ("0".equals(getGameState(STATE_KEYS.PLAYER_LEFT_POINTS, "0")) &&
                "0".equals(getGameState(STATE_KEYS.PLAYER_RIGHT_POINTS, "0"))) {
              view.showInstructions(instructionsText);
            } else {
              view.showSingleLine(textWaitingForBothPlayers);
            }
          }
          if (thisPlayer == playerLeft
              && getGameState(STATE_KEYS.PLAYER_LEFT_READY) != null
              && getGameState(STATE_KEYS.PLAYER_RIGHT_READY) != null) {
            setStateDelta(STATE_KEYS.PLAYER_LEFT_READY, (String) null);
            setStateDelta(STATE_KEYS.PLAYER_RIGHT_READY, (String) null);
            submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.ON);
          }
          break;
        case ON:
          if (!matchStarted) {
            newMatch();
          }
          // game in progress
          // handle bar position of opponent
          view.debug("ball state:" + getGameState(STATE_KEYS.BALL_WAIT, "unknown"));
          if ((playerLeft == thisPlayer && BALL_WAIT_LEFT.equals(getGameState(STATE_KEYS.BALL_WAIT, ""))) ||
              (playerRight == thisPlayer && BALL_WAIT_RIGHT.equals(getGameState(STATE_KEYS.BALL_WAIT, "")))) {
            submitStateDelta(STATE_KEYS.BALL_WAIT, "");
          }
          if ("".equals(getGameState(STATE_KEYS.BALL_WAIT, ""))) {
            ball.resumeBall();
          }
          break;
//        case PAUZE:
//           TODO implement pause
//           Game paused by on of the players, wait for both to acknowledge
//           continue
//          break;
        case END:
          matchStarted = false;
          showPoints();
          lastwinner = PLAYER.PLAYER_LEFT.equals(getGameState(STATE_KEYS.LAST_WINNER))
              ? PLAYER.PLAYER_LEFT : PLAYER.PLAYER_RIGHT;
          // Game ended, update score and send game on after timeout.
          view.debug("lastwinner:" + lastwinner);
          final boolean youWin =
              (PLAYER.PLAYER_LEFT.equals(lastwinner) && playerLeft == thisPlayer) ||
              (PLAYER.PLAYER_RIGHT.equals(lastwinner) && playerRight == thisPlayer);
          if (youWin) {
            handleMatchOver();
          }
          view.showWinner("YOU " + (youWin ? "WIN!!" : "LOSE!!"));
          if (!wave.isPlayback() && youWin) {
            new Timer() {
              @Override
              public void run() {
                submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.NEW_MATCH);
              }
            }.schedule(winnertimeout);
          }
          break;
        }
      }
    });
  }

//  private void togglePauze() {
//    pauze = !pauze;
//    if (pauze) {
//      runner.cancel();
//      playerTimer.cancel();
//      view.showSingleLine(pauzeText);
//    } else {
//      view.setLineVisible(true);
//      playerTimer.scheduleRepeating(1000);
//      runner.scheduleRepeating(gamespeed);
//    }
//  }

  /**
   * Detect if the player missed the ball, only the player itself can report is
   * has missed the ball, this to avoid collision detection.
   */
  private void detectCollision() {
    if (ball.atLeftBorder()) {
      ball.waitBall();
      // player1 (left player)
      if (playerLeft == thisPlayer) {
        if (ball.getBottomY() < playerLeft.getBar().getTopY()
            || ball.getTopY() > playerLeft.getBar().getBottomY()) {
          matchOver(PLAYER.PLAYER_RIGHT);
        } else {
          submitStateDelta(STATE_KEYS.BALL_WAIT, BALL_WAIT_RIGHT);
        }
      }
    } else if (ball.atRightBorder()) {
      ball.waitBall();
      // player2 (right player)
      if (playerRight == thisPlayer) {
        if (ball.getBottomY() < playerRight.getBar().getTopY()
            || ball.getTopY() > playerRight.getBar().getBottomY()) {
          matchOver(PLAYER.PLAYER_LEFT);
        } else {
          submitStateDelta(STATE_KEYS.BALL_WAIT, BALL_WAIT_LEFT);
        }
      }
    }
  }

  private String getGameState(STATE_KEYS key) {
    return getGameState(key, (String)null);
  }

  private String getGameState(STATE_KEYS key, GAME_STATE defaultValue) {
    return getGameState(key, defaultValue.toString());
  }

  private String getGameState(STATE_KEYS key, String defaultValue) {
    final State s = wave.getState();
    final String value = s != null ? s.get(key.toString(), null) : null;

    return value == null ? defaultValue : value;
  }

  private void handleMatchOver() {
    ball.ballOut();
    runner.cancel();
    playerTimer.cancel();
  }

  private void increasePoints(STATE_KEYS playerPoints) {
    delta.put(playerPoints.toString(), String.valueOf(
        Integer.parseInt(getGameState(playerPoints, "0")) + 1));
  }

  private void initParticipants() {
    final JsArray<Participant> participants = wave.getParticipants();
    if (participants.length() > 0) {
      if (thisPlayer == null) {
        final String name = participants.get(0).getDisplayName();
        final String url = participants.get(0).getThumbnailUrl();

        if (thisParticipant.getId().equals(wave.getHost().getId())) {
          thisPlayer = playerLeft;
          view.setPlayerNameLeft(url, name);
        } else {
          thisPlayer = playerRight;
          view.setPlayerNameRight(url, name);
        }
        thisPlayer.setPlayerName(name);
      }
    }
    if (participants.length() > 1) {
      if (otherPlayer == null) {
        final String name = participants.get(1).getDisplayName();
        final String url = participants.get(1).getThumbnailUrl();

        if (thisParticipant.getId().equals(wave.getHost().getId())) {
          otherPlayer = playerRight;
          view.setPlayerNameRight(url, name);
        } else {
          otherPlayer = playerLeft;
          view.setPlayerNameLeft(url, name);
        }
        otherPlayer.setPlayerName(name);
        showPoints();
      }
      submitStateOnlyLeftDelta(STATE_KEYS.GAME_STATE, GAME_STATE.NEW_MATCH);
    } else {
      // waiting for players...
      if (!GAME_STATE.INIT.equals(getGameState(STATE_KEYS.GAME_STATE))) {
        submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.INIT);
      }
    }
  }

  /**
   * Match is over, increase points and set winner state.
   *
   * @param whowins Player that wins
   */
  private void matchOver(PLAYER whowins) {
    handleMatchOver();
    if (PLAYER.PLAYER_LEFT.equals(whowins)) {
      increasePoints(STATE_KEYS.PLAYER_LEFT_POINTS);
      setStateDelta(STATE_KEYS.LAST_WINNER, PLAYER.PLAYER_LEFT.toString());
    } else {
      increasePoints(STATE_KEYS.PLAYER_RIGHT_POINTS);
      setStateDelta(STATE_KEYS.LAST_WINNER, PLAYER.PLAYER_RIGHT.toString());
    }
    submitStateDelta(STATE_KEYS.GAME_STATE, GAME_STATE.END);
    view.debug("matchOver: whowins=" + whowins);
  }

  private void newMatch() {
    showPoints();
    ball.setVisible(true);
    thisPlayer.start();
    final String balStart = getGameState(STATE_KEYS.BALL_START);

    ball.start(lastwinner, balStart == null ? true : Boolean.valueOf(balStart));
    runner.scheduleRepeating(gamespeed);
    if (!wave.isPlayback()) {
      playerTimer.scheduleRepeating(1000);
    }
    matchStarted = true;
  }

  private void showPoints() {
    view.showGame(getGameState(STATE_KEYS.PLAYER_LEFT_POINTS, "0"),
        getGameState(STATE_KEYS.PLAYER_RIGHT_POINTS, "0"));
  }

  private void setStateDelta(STATE_KEYS key, String value) {
    delta.put(key.toString(), value);
  }

  private void submitStateDelta(STATE_KEYS key, GAME_STATE value) {
    submitStateDelta(key, value.toString());
  }

  private void submitStateDelta(STATE_KEYS key, String value) {
    final State s = wave.getState();

    if (!wave.isPlayback() && s != null) {
      delta.put(key.toString(), value);
      s.submitDelta(delta);
      delta.clear();
    }
  }

  /**
   * This submit should only be done by one player. This is set ot the left
   * player.
   *
   * @param key
   * @param value
   */
  private void submitStateOnlyLeftDelta(STATE_KEYS key, GAME_STATE value) {
    if (!wave.isPlayback() && thisPlayer == playerLeft) {
      submitStateDelta(key, value.toString());
    }
  }
}
