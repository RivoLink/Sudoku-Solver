package mg.rivolink.app.sudokusolver.core.sudoku.bfb;

import mg.rivolink.app.sudokusolver.core.sudoku.SudokuSolver;

/*
 * Brute-Force Backtracking
 *
 *
 */

public class SolverBFB implements SudokuSolver{

	private int grid[][];

	private boolean solving(int row,int col){
		int nextRow=0;
		int nextCol=0;

		if(col==8){
			nextRow=row+1;
			nextCol=0;
		}
		else{
			nextRow=row;
			nextCol=col+1;
		}

		if(row==9)
			return true;

		if(grid[row][col]!=0){
			return solving(nextRow,nextCol);
		}
		else{
			for(int val=1;val<10;val++){
				if(!isCorrect(val,row,col))
					continue;

				grid[row][col]=val;

				if(solving(nextRow,nextCol))
					return true;
			}

			grid[row][col]=0;
			return false;
		}
	}

	private boolean isCorrect(int val,int row,int col){
		return !onRow(val,row) && !onCol(val,col) && !onBox(val,row,col);
	}

	private boolean onCol(int val,int col){
		for(int row=0;row<9;row++){
			if(grid[row][col]==val)
				return true;
		}
		return false;
	}

	private boolean onRow(int val,int row){
		for(int col=0;col<9;col++){
			if(grid[row][col]==val)
				return true;
		}
		return false;
	}

	private boolean onBox(int val,int row,int col){
		int ptUp=3*(row/3);
		int ptLeft=3*(col/3);
		for(int r=ptUp;r<ptUp+3;r++){
			for(int c=ptLeft;c<ptLeft+3;c++){
				if(grid[r][c]==val)
					return true;
			}
		}
		return false;
	}

	@Override
	public void init(int[][] grid){
		this.grid=grid;
	}

	@Override
	public boolean solve(){
		return solving(0,0);
	}

	@Override
	public void free(boolean[][] mask){
		if(grid==null || mask==null)
			return;
		
		for(int r=0;r<grid.length;r++){
			for(int c=0;c<grid[r].length;c++){
				grid[r][c]=0;
				mask[r][c]=false;
			}
		}
	}

}


