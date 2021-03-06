package com.whishkey.tictactoe;

/*
 TODO : 
 etc.
 */

// Note: "x" state is 0, "o" state is 1, blank state is -1
// Note: UI is based on a 3 x 3 grid of buttons
// Note: Tab size is set to 2 spaces

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
//import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Button;
import android.os.Build;
import android.os.Bundle;
import com.whishkey.tictactoe.R;

public class MainActivity extends Activity implements View.OnClickListener {
	
	private final int[] idArr = { R.id.button0, R.id.button1, R.id.button2, R.id.button3,
			R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, };
	private TextView turn_text, win_text, z_text;
	private final Board b = new Board();
	private int x, y, z;
	private int turn;
	private boolean winFlag;
	private AlertDialog.Builder winDialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// no annoying app title/activity bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			z = 0;
			turn = 0;
			winFlag = false;
		}

		turn_text = (TextView) findViewById(R.id.turn_txt);
		z_text = (TextView) findViewById(R.id.z_txt);
		win_text = (TextView) findViewById(R.id.win_txt);
		
		// set tags for each grid button
		for (int i = 0; i < idArr.length; i++) {
			View v = (View) findViewById(idArr[i]);
			v.setTag(i);
		}

		initBut(new int[] { R.id.button_up, R.id.button_down, R.id.button_reset });

		initBut(idArr);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			winDialogBuilder = new AlertDialog.Builder(this,
					android.R.style.Theme_Material_Dialog_Alert);
		} else {
			winDialogBuilder = new AlertDialog.Builder(this);
		}
		
		winDialogBuilder.setPositiveButton("OK",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		turn = savedInstanceState.getInt("STATE_TURN");
		winFlag = savedInstanceState.getBoolean("STATE_WIN");
		z = savedInstanceState.getInt("STATE_Z");
		b.fromArr(savedInstanceState.getIntegerArrayList("STATE_BOARD"));

		updateDisplay(b, idArr, z);

		// Set win text
		if (winFlag) {
			win_text.setText(stateStr(turn % 2) + " wins!");
		} else {
			win_text.setText(" ");
		}

		turn_text.setText("Turn " + Integer.toString(turn + 1) + ": "
				+ stateStr(turn % 2));

		z_text.setText(Integer.toString(z + 1));
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putInt("STATE_TURN", turn);
		savedInstanceState.putBoolean("STATE_WIN", winFlag);
		savedInstanceState.putInt("STATE_Z", z);
		savedInstanceState.putIntegerArrayList("STATE_BOARD", b.toArr());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// if possible, increase z focus, update display
		case R.id.button_up:
			if (z < 3 - 1) {
				z += 1;
				updateDisplay(b, idArr, z);
				z_text.setText(Integer.toString(z + 1));
			}
			break;

		// if possible, decrease z focus, update display
		case R.id.button_down:
			if (z > 1 - 1) {
				z -= 1;
				updateDisplay(b, idArr, z);
				z_text.setText(Integer.toString(z + 1));
			}
			break;

		// reset game, update display
		case R.id.button_reset:
			turn = 0;
			winFlag = false;
			b.reset();
			updateDisplay(b, idArr, z);
			win_text.setText(" ");
			turn_text.setText("Turn " + Integer.toString(turn + 1) + ": "
					+ stateStr(turn % 2));
			break;

		// Grid button: if not set: set state, update display,
		// check if play wins game, increment turn. Else, ignore.
		default:
			x = tagInt(v) % 3;
			y = tagInt(v) / 3;

			caseButtonGrid(v);
		}
	}

	/**
	 * Get integer tag from a view
	 * 
	 * @param View
	 * @return Integer tag
	 */
	int tagInt(View v) {
		return (int) v.getTag();
	}

	/**
	 * Return "x" or "o" based on int state
	 * 
	 * @param state
	 * @return "x" or "o"
	 */
    String stateStr(int state) {
		switch (state) {
		case 0:
			return "\u274c"; // "x"
		case 1:
			return "\u26aa"; // "o"
		default:
			return " ";
		}
	}

	void caseButtonGrid(View v) {
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
	
				if (winBoard(b, x, y, z)) { // check if play wins game
					win_text.setText(stateStr(turn % 2) + " wins!");
	
					winDialogBuilder
						.setMessage(stateStr(turn % 2) + " wins!")
						.show();
					
					winFlag = true;
				} else {
					turn += 1;
					turn_text.setText("Turn " + Integer.toString(turn + 1) + ": "
							+ stateStr(turn % 2));
				}
		}
	}

	/**
	 * Updates text on each button in id array
	 * 
	 * @param brd
	 * @param id_arr
	 * @param z
	 */
    void updateDisplay(Board brd, int[] id_arr, int z) {
		for (int id : id_arr) {
			Button but = (Button) findViewById(id);
			int x = tagInt(but) % 3;
			int y = tagInt(but) / 3;
			but.setText(stateStr(brd.getState(x, y, z)));
		}
	}

	/**
	 * To initialize buttons easily
	 * 
	 * @param id_arr
	 */
    void initBut(int[] id_arr) {
		// DisplayMetrics metrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//
		// int width;
		// if ((metrics.widthPixels - 2 * 20) / 4 < 40) {
		// width = 40;
		// } else {
		// width = (metrics.widthPixels - 2 * 20) / 4;
		// }

		for (int id : id_arr) {
			Button but = (Button) findViewById(id);
			// but.setLayoutParams(new ViewGroup.LayoutParams(width, width));
			but.setOnClickListener(this);
		}
	}

	// i hate it b/c it's hard-coded to 3x3x3
	/**
	 * Check if a player has won based on last play
	 * 
	 * @param b
	 *          Board in
	 * @param x
	 *          X-pos of last play
	 * @param y
	 *          Y-pos of last play
	 * @param z
	 *          Z-pos of last play
	 * @return True if they won, else false
	 */
    boolean winBoard(Board b, int x, int y, int z) {
		if (winGrid(b.getGrid(x))) { // i fucked the coordinate order
			return true; // checks xy board
		}

		// checks xz
		int[][] grid = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				grid[i][j] = b.getState(i, y, j);
			}
		}
		if (winGrid(grid)) {
			return true;
		}

		// checks yz
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				grid[i][j] = b.getState(i, j, z);
			}
		}
		if (winGrid(grid)) {
			return true;
		}

		// checks diagonal
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				grid[i][j] = b.getState(i, j, j);
			}
		}
		if (winGrid(grid)) {
			return true;
		}

		// checks opposite diagonal
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				grid[i][j] = b.getState(i, 2 - j, j);
			}
		}
        return winGrid(grid);

    }

	// did two separate, more flexible algorithms before settling
	// to this inelegant one

	// could possibly replace with one that checks with some
	// python all([]) function implemented to java
	/**
	 * Checks if the 2d grid is in a win state
	 * 
	 * @param grid
	 *          To check
	 * @return True, if a player won, else false
	 */
    boolean winGrid(int[][] grid) {

		// check horizontals
		for (int i = 0; i < 3; i++) {
			if (grid[i][0] == -1) {
				continue;
			}
			if (grid[i][1] == grid[i][0] && grid[i][2] == grid[i][0]) {
				return true; // method does not tell which player won --
			} // will have to be implied
		}

		// check verticals
		for (int i = 0; i < 3; i++) {
			if (grid[0][i] == -1) {
				continue;
			}
			if (grid[1][i] == grid[0][i] && grid[2][i] == grid[0][i]) {
				return true;
			}
		}

		// check diagonal
		if (grid[0][0] == grid[1][1] && grid[2][2] == grid[1][1] && grid[1][1] != -1) {
			return true;
		}
		// check opposite diagonal
        return grid[0][2] == grid[1][1] && grid[2][0] == grid[1][1] && grid[1][1] != -1;

    }

}
