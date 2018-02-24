package com.whishkey.tictactoe;

import java.util.Arrays;
import java.util.ArrayList;

// TODO : make parcelable

public class Board {
  private int[][][] board;
  
  Board() {
    board = Fill(new int[3][3][3], -1);
  }
  
  int[][] getGrid(int x) {
    return board[x];
  }
  
  public int getState(int x, int y, int z) {
    return board[x][y][z];
  }
  
  public void setState(int x, int y, int z, int state) {
    board[x][y][z] = state;
  }
  
  public void reset() {
    board = Fill(board, -1);
  }
  
  private int[][][] Fill(int[][][] arr, int n) {
    for (int[][] z: arr) {
      for (int[] y: z) {
        Arrays.fill(y, n);
      }
    }
    return arr;
  }
  
  /**
   * Place board states into an arraylist
   * @return L : Arraylist encoded from the board
   */
  public ArrayList<Integer> toArr() {
    ArrayList<Integer> L = new ArrayList<Integer>();
    for (int[][] z: board) {
      for (int[] y: z) {
        for (int x: y) {
          L.add(x);
        }
      }
    }
    return L;
  }
  
  /**
   * Reconstructs board from arraylist made from toArr
   * @param L : Arraylist to decode into the board
   */
  public void fromArr(ArrayList<Integer> L) {
    for (int i = 0; i < L.size(); i++) {
      board[(i / 3) / 3][(i / 3) % 3][i % 3] = L.get(i);
    }
  }
  
}
