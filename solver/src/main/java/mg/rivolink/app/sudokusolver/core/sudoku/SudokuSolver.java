package mg.rivolink.app.sudokusolver.core.sudoku;

public interface SudokuSolver{

	public void init(int[][] grid);

	public boolean solve();

	public void free(boolean[][] mask);

}

