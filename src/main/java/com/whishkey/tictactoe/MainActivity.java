package com.whishkey.tictactoe;

/*
  To do:
  etc.
*/

// Note: "x" state is 0, "o" state is 1, blank state is -1
// Note: UI is based on a 3 x 3 grid of buttons

import java.util.ArrayList;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;
import com.whishkey.tictactoe.R;

public class MainActivity extends Activity implements
View.OnClickListener {
  Button b0, b1, b2, b3, b4, b5, b6, b7, b8;
  int[] idArr = {R.id.button0, R.id.button1, R.id.button2,
                 R.id.button3, R.id.button4, R.id.button5, 
                 R.id.button6, R.id.button7, R.id.button8, };
  Button bU, bD, bR;
  
  TextView turn_text, win_text, z_text;
  Board b = new Board();
  int x, y, z, turn;
  boolean winFlag;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    this.requestWindowFeature(Window.FEATURE_NO_TITLE); // no annoying app title bar
    
    setContentView(R.layout.activity_main);
    
    if (savedInstanceState == null) {
      z = 0;
      turn = 0;
      winFlag = false;
    }
     
    turn_text = (TextView) findViewById(R.id.turn_txt);
    z_text = (TextView) findViewById(R.id.z_txt);
    win_text = (TextView) findViewById(R.id.win_txt);
    
    initBut(new int[] {R.id.button_up, R.id.button_down, R.id.button_reset});
    
    initBut(idArr);
    
  }

  
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can restore the view hierarchy
    super.onRestoreInstanceState(savedInstanceState);

    // Restore state members from saved instance
    turn = savedInstanceState.getInt("STATE_TURN");
    winFlag = ((savedInstanceState.getInt("STATE_WIN")) != 0);
    z = savedInstanceState.getInt("STATE_Z");
    b.fromArr(savedInstanceState.getIntegerArrayList("STATE_BOARD"));
    
    updateDisp(b, idArr, z);
    
    if (winFlag) {
      win_text.setText(stateStr(turn  % 2) + " wins!");
    } else {
      win_text.setText(" ");
    }
    
    turn_text.setText("Turn " + Integer.toString(turn + 1) + ": " +
                      stateStr(turn % 2));
    
    z_text.setText(Integer.toString(z + 1));
  }
  
  @Override
  protected void onSaveInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
    
    // Save the user's current game state
    savedInstanceState.putInt("STATE_TURN", turn);
    savedInstanceState.putInt("STATE_WIN", ((winFlag) ? 1 : 0));
    savedInstanceState.putInt("STATE_Z", z);
    savedInstanceState.putIntegerArrayList("STATE_BOARD", b.arrList());
  }
  //
    
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
              
      case R.id.button_up: // if possible, increase z focus, update display
        if (z < 3 - 1) {
          z += 1;
          updateDisp(b, idArr, z);
          z_text.setText(Integer.toString(z + 1));
        }
        break;
    
      case R.id.button_down: // if possible, decrease z focus, update display
        if (z > 0) {
          z -= 1;
          updateDisp(b, idArr, z);
          z_text.setText(Integer.toString(z + 1));
        }
        break;
    
      case R.id.button_reset: // reset game, update display
        turn = 0;
        winFlag = false;
        b.reset();
        updateDisp(b, idArr, z);
        win_text.setText(" ");
        turn_text.setText("Turn " + Integer.toString(turn + 1) + ": " +
                          stateStr(turn % 2));
        break;
      
      default:
        x = tagInt(v) % 3;
        y = tagInt(v) / 3;
        
        switch (b.getState(x, y, z)) {
          case 0: // if button has existing "x" or "o" state,
          case 1:
            break; // ignore
          default:
            if (winFlag) {
              break;
            }
            
            b.setState(x, y, z, turn % 2); // otherwise, set state, progress
            
            Button B = (Button) v;
            B.setText(stateStr(b.getState(x, y, z)));
            
            if (winState(b, x, y, z)) { // check if play wins game
              win_text.setText(stateStr(turn  % 2) + " wins!");
              winFlag = true;
            } else {
              turn += 1;
              turn_text.setText("Turn " + Integer.toString(turn + 1) + ": " +
                                stateStr(turn % 2));
            }
        }
    }
  }
  
  public int tagInt(View v) { // get int from a view (buttons have int tags)
    return Integer.parseInt(v.getTag().toString());
  }
  
  public String stateStr(int state) { // return "x" or "o" based on state
    switch (state) {
      case 0:
        return "\u274c"; // "x"
      case 1:
        return "\u26aa"; // "o"
      default:
        return " ";
    }
  }
  
  public void updateDisp(Board brd, int[] id_arr, int z) { // updates text on each button in id array
    for (int id: id_arr) {
      Button but = (Button) findViewById(id);
      int x = tagInt(but) % 3;
      int y = tagInt(but) / 3;
      but.setText(stateStr(brd.getState(x, y, z)));
    }
  }
  
  public void initBut(int[] id_arr) { // to create buttons easily
    for (int id: id_arr) {
      Button but = (Button) findViewById(id);
      but.setOnClickListener(this);
    }
  }
  
  // i hate it b/c it's hard-coded to 3x3x3
  public boolean winState(Board b, int x, int y, int z) {
    if (checkGrid(b.getGrid(x))) { // i fucked the coordinate order
      return true;
    }
    
    
    int[][] grid = new int[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid[i][j] = b.getState(i, y, j);
      }
    }
    if (checkGrid(grid)) {
      return true;
    }
    
    grid = new int[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid[i][j] = b.getState(i, j, z);
      }
    }
    if (checkGrid(grid)) {
      return true;
    }
    
    
    grid = new int[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid[i][j] = b.getState(i, j, j);
      }
    }
    if (checkGrid(grid)) {
      return true;
    }
    
    grid = new int[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid[i][j] = b.getState(i, 2 - j, j);
      }
    }
    if (checkGrid(grid)) {
      return true;
    }
    
    return false;
  }
  
  // did two separate, more flexible algorithms before settling
  // to this inelegant one
  
  // could possibly replace with one that checks with some
  // python all([]) function implemented to java
  public boolean checkGrid(int[][] grid) { // check for win in 2d
    for (int i = 0; i < 3; i++) {
      if (grid[i][1] == grid[i][0] &&
          grid[i][2] == grid[i][0]) {
        if (grid[i][0] == -1) {
          return false;
        }
        return true; // method does not tell which player won --
      }              // will have to be implied
    }
    
    for (int i = 0; i < 3; i++) {
      if (grid[1][i] == grid[0][i] &&
          grid[2][i] == grid[0][i]) {
        if (grid[0][i] == -1) {
          return false;
        }
        return true;
      }
    }
    
    if (grid[0][0] == grid[1][1] &&
        grid[2][2] == grid[1][1]) {
      if (grid[1][1] == -1) {
          return false;
        }
      return true;
    }
    if (grid[0][2] == grid[1][1] &&
        grid[2][0] == grid[1][1]) {
      if (grid[1][1] == -1) {
          return false;
      }
      return true;
    }
    
    return false;
  }

}
