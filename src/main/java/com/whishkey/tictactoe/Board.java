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
        Arrays.fill(y, -1);
      }
    }
    return arr;
  }
  
  public int[][][] copy() {
    return board;
  }
  
  public ArrayList<Integer> arrList() {
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
  
  public void fromArr(ArrayList<Integer> L) {
    int index = 0;
    for (int i: L) {
      board[index / 9][(index / 3) % 3][index % 3] = i;
      index += 1;
    }
  }
  
}
