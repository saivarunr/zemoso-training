import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;



public class Sudoku{
	static int puzzle[][];
	static int zeros=0;
	static int previous=0;
	static int sum=0;
	public static void main(String args[]) throws IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		puzzle=new int[9][9];
		
		for(int k=0;k<50;k++){
				String x_grid_title=reader.readLine(); //reading grid title 
				for(int i=0;i<9;i++){
					String temp=reader.readLine();		//reading row
					
					char[] character=temp.toCharArray();
					for(int j=0;j<9;j++){
						puzzle[i][j]=Character.getNumericValue(character[j]);	//reading columns
						if(puzzle[i][j]==0)										//checking for not filled positions
							++zeros;
					}
				}
				previous=zeros;
				int X=0,Y=0;
				while(zeros!=0){							//Following cross checking method to fill values optimally
					if(Y==9){
						X+=3;
						Y=0;
					}
					if(X==9){
						X=0;
						if(previous==zeros){
							resetPuzzle();
							break;//No updation in the board, then exit
						}
						previous=zeros;//Update the current status
					}
					//For each grid get the possible not existing numbers
					LinkedList<Integer> integers=getNotExisitingNumbers(X,Y);
					//Take the possible values and TRY to find a place
					for(Integer integer:integers){
						resetPuzzle();
						placeFinder(X,Y,integer);
					}
					Y+=3;
				}
				
				/* If cross-checking method fails to fill all the empty values
				 * then switch to backtracking method to fill the values based on trial and error method
				 * */
				if(zeros!=0){		
					//Now start backtracking
					solveByBacktracking(0,0);
				}
				sum+=puzzle[0][0]*100+puzzle[0][1]*10+puzzle[0][2];
		}
		System.out.println(sum);
	}
	
	private static boolean solveByBacktracking(int x,int y) {
		if(y==9){
			x++;
			y=0;
		}
		if(x==9)
			return true;//Sudoku is solved all rows are parsed
		if(puzzle[x][y]!=0)//if present position is filled then goto next position
			return solveByBacktracking(x, y+1);
		for(int i=1;i<10;i++){
			if(!isPossible(x,y,i)) //Check if a particular value from 1 to 9 fits or not
				continue;         //If it doesn't goto next value
			puzzle[x][y]=i; 	
			if(solveByBacktracking(x, y+1)) //Solve by advancing
				return true; 				//if position is satisfied return true
			else
				puzzle[x][y]=0;			//if not reset the value to zero and backtrack
		}
		return false;
	}

	private static boolean isPossible(int x, int y, int n) {
		for(int i=0;i<9;i++){
			if(puzzle[x][i]==n|puzzle[i][y]==n) //if value already exists either in row or column return false
				return false;
		}
		int newX=(x/3)*3;				//Get starting position of row in a box
		int newY=(y/3)*3;				//Get starting position of column in a box
		for(int i=newX;i<newX+3;i++)	
			for(int j=newY;j<newY+3;j++)
				if(puzzle[i][j]==n)		//If value consists in the box itself return false
					return false;
		return true;					//If not any of above return true, saying value is available
	}

	
	//Find a suitable place for a value give by LinkedList integers
	private static void placeFinder(int x, int y, Integer integer) {
		//Horizontal check
		for(int i=x;i<x+3;i++){
			for(int j=0;j<9;j++){
				if(puzzle[i][j]==integer){
					for(int k=y;k<y+3;k++){
						if(puzzle[i][k]==0)
							puzzle[i][k]=-1;
					}
				}
			}
		}
		//Vertical check
		for(int i=0;i<9;i++){
			for(int j=y;j<y+3;j++){
				if(puzzle[i][j]==integer){
					for(int k=x;k<x+3;k++){
						if(puzzle[k][j]==0)
							puzzle[k][j]=-1;
					}
				}
			}
		}
		int count=0;
		//Box check
		int tempX = 0,tempY = 0;
		for(int i=x;i<x+3;i++){
			for(int j=y;j<y+3;j++){
				if(puzzle[i][j]==0){
					count++;
					tempX=i;
					tempY=j;
				}
			}
		}
		//If there's only one empty spot fill it with number
		if(count==1){
			puzzle[tempX][tempY]=integer;
			--zeros;
		}
	}
	
	/*While using cross-checking method, crossed values are filled with -1
	 * for next iteration board is to be updated 
	 * */
	static void resetPuzzle() {
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(puzzle[i][j]==-1)
				puzzle[i][j]=0;
		}
	
	
	
	/*The method which takes Starting X and Y co-ordinates of a cell and then returns a list of values
	* which can replace zeros
	*/
	private static LinkedList<Integer> getNotExisitingNumbers(int x, int y) {
		LinkedList<Integer> integers=new LinkedList<Integer>();
		//First add all the values from 1 to 9
		for(int i=1;i<10;i++){
			integers.add(i);
		}
		//Remove those values which are present in the cell
		for(int i=x;i<x+3;i++){
			for(int j=y;j<y+3;j++){
				//If the value is not zero and if and only if linkedlist contains that value remove it
				if(puzzle[i][j]!=0 && integers.contains(puzzle[i][j])){
					integers.removeFirstOccurrence(puzzle[i][j]);
				}
			}
		}
		
		return integers;
	}
}