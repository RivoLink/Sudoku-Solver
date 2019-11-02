/* 
 * Java program for Sudoku generator
 * Source Code from: 
 * https://www.geeksforgeeks.org/program-sudoku-generator/&sa=U&ved=2ahUKEwjfr7qK2LvlAhXZPsAKHST3B_kQFjAAegQIAhAB&usg=AOvVaw2JEM-EycFJEa7Qlji2qdyO
 *
 */

package mg.rivolink.app.sudokusolver.core.sudoku;

import java.lang.Math; 

public class SudokuGenerator{

	private int SRN; // square root of N 
    private int N=9; // number of columns/rows
    private int K=DEFAULT_K; // number of filled cells
	private int[][] soluce;
	private int[][] sudoku;

	public static final int DEFAULT_K=30;

	// Constructor
	public SudokuGenerator(){
		this(9);
	}

    // Constructor 
    public SudokuGenerator(int N){ 
        this.N=N; 

        // Compute square root of N 
        Double SRNd=Math.sqrt(N); 
        SRN=SRNd.intValue(); 
    }

	public int getK(){
		return K;
	}

	public int getN(){
		return N;
	}

	// Get the matrix of generate game
	public int[][] getSudoku(){
		if(sudoku==null)
			generate(DEFAULT_K);

		return sudoku;
	}

	// Get the soluce of generate game
	public int[][] getSoluce(){
		if(soluce==null)
			soluce=new int[N][N];

		return soluce;
	}

	// Generate sudoku
	public int[][] generate(int K){
		this.K=K;
		this.soluce=new int[N][N];
		this.sudoku=new int[N][N];

		fillValues();

		return sudoku;
	}

    // Sudoku Generator 
    public void fillValues(){ 
        // Fill the diagonal of SRN x SRN matrices 
        fillDiagonal(); 

        // Fill remaining blocks 
        fillRemaining(0,SRN); 

        // Add Randomly K digits to make game 
        addKDigits(); 
    } 

    // Fill the diagonal SRN number of SRN x SRN matrices 
    void fillDiagonal(){ 
        for(int i=0;i<N;i=i+SRN){
			// for diagonal box, start coordinates->i==j 
            fillBox(i,i); 
		}
    } 

    // Returns false if given 3 x 3 block contains num. 
    boolean unUsedInBox(int rowStart,int colStart,int num){ 
        for(int i=0;i<SRN;i++){
            for(int j=0;j<SRN;j++){
                if(soluce[rowStart+i][colStart+j]==num) 
                    return false; 
			}
		}
        return true; 
    } 

    // Fill a 3 x 3 matrix. 
    void fillBox(int row,int col){ 
        int num; 
        for(int i=0;i<SRN;i++){ 
            for(int j=0;j<SRN;j++){ 
                do{ 
                    num=randomGenerator(N); 
                } 
                while(!unUsedInBox(row,col,num)); 

                soluce[row+i][col+j]=num; 
            } 
        } 
    } 

    // Random generator 
    int randomGenerator(int num){ 
        return (int)Math.floor((Math.random()*num+1)); 
    } 

    // Check if safe to put in cell 
    boolean CheckIfSafe(int i,int j,int num){ 
        return (unUsedInRow(i,num)&& 
		unUsedInCol(j,num)&& 
		unUsedInBox(i-i%SRN,j-j%SRN,num)); 
    } 

    // check in the row for existence 
    boolean unUsedInRow(int i,int num){ 
        for(int j=0;j<N;j++) 
			if(soluce[i][j]==num) 
                return false; 
        return true; 
    } 

    // check in the row for existence 
    boolean unUsedInCol(int j,int num){ 
        for(int i=0;i<N;i++) 
            if(soluce[i][j]==num) 
                return false; 
        return true; 
    } 

    // A recursive function to fill remaining  
    // matrix 
    boolean fillRemaining(int i,int j){ 
        //  System.out.println(i+" "+j); 
        if(j>=N&&i<N-1){ 
            i=i+1; 
            j=0; 
        } 
        if(i>=N&&j>=N) 
            return true; 

        if(i<SRN){ 
            if(j<SRN) 
                j=SRN; 
        } 
        else if(i<N-SRN){ 
            if(j==(i/SRN)*SRN)
                j=j+SRN; 
        } 
        else{ 
            if(j==N-SRN){ 
                i=i+1; 
                j=0; 
                if(i>=N) 
                    return true; 
            } 
        } 

        for(int num=1;num<=N;num++){ 
            if(CheckIfSafe(i,j,num)){ 
                soluce[i][j]=num; 
                if(fillRemaining(i,j+1)) 
                    return true; 

                soluce[i][j]=0; 
            } 
        } 
        return false; 
    } 

    // Add the K no. of digits to 
    // complete game
    public void addKDigits(){ 
        int count=K; 
        while(count!=0){ 
            int cellId=randomGenerator(N*N); 

            // System.out.println(cellId); 
            // extract coordinates i  and j 
            int i=(cellId/N); 
            int j=cellId%N;

            if(i>N-1) 
                i=N-1; 

            // System.out.println(i+" "+j); 
            if(sudoku[i][j]==0){ 
                count--; 
                sudoku[i][j]=soluce[i][j]; 
            } 
        } 
    }

}


