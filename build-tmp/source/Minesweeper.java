import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.bezier.guido.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Minesweeper extends PApplet {




//Declare and initialize NUM_ROWS and NUM_COLS = 20
private final static int NUM_ROWS = 20;
private final static int NUM_COLS = 20;
private final static int totalBombs = 50;
//int bombCount = 0;
private MSButton[][] buttons; //2d array of minesweeper buttons
private ArrayList <MSButton> bombs = new ArrayList <MSButton>(); //ArrayList of just the minesweeper buttons that are mined

public void setup ()
{
    size(400, 400);
    textAlign(CENTER,CENTER);
    
    // make the manager
    Interactive.make( this );
    //declare and initialize buttons
    buttons = new MSButton[NUM_ROWS][NUM_COLS];
    for(int r =0;r<NUM_ROWS;r++)
    {
        for(int c =0;c<NUM_COLS;c++)
        {
            buttons[r][c] = new MSButton(r,c);
        }
    }
    setBombs();
    // for(int r=0;r<NUM_ROWS;r++)
    //     for(int c=0;c<NUM_COLS;c++)
    //         System.out.println(buttons[r][c].countBombs(r,c));
}
public void setBombs()
{
    // Leave top two for debugging
    // int xBomb=0;
    // int yBomb=0;
    // bombs.add(buttons[xBomb][yBomb]);
    int xBomb = (int)((Math.random()*NUM_COLS));
    int yBomb = (int)((Math.random()*NUM_ROWS));
    for(int nBomb=0;nBomb<totalBombs;nBomb++)
    {
      if(!bombs.contains(buttons[xBomb][yBomb]))
        {
            bombs.add(buttons[xBomb][yBomb]);  
            xBomb = (int)((Math.random()*NUM_COLS));
            yBomb = (int)((Math.random()*NUM_ROWS));  
        }  
    }
}

public void draw ()
{
    background( 0 );
    if(isWon())
    {
        displayWinningMessage();
       
    }
}
public boolean isWon()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(!buttons[r][c].isMarked() && !buttons[r][c].isClicked())
            {
                return false;
            }
    return true;
}
public void displayLosingMessage()
{
    for(int r = 0; r < NUM_ROWS; r++)
        for(int c = 0; c < NUM_COLS; c++)   
            if(bombs.contains(buttons[r][c]))
                buttons[r][c].setLabel("B");
 
    String message = new String("Game Over!");
    for(int i = 0; i < message.length(); i++ )
    {
        buttons[9][i+5].clicked = true;
        if(!bombs.contains(buttons[9][i+5]))
            bombs.add(buttons[9][i+5]);
        buttons[9][i+5].setLabel(message.substring(i,i+1));
    }
}
public void displayWinningMessage()
{
    String message = new String("You Win!");
    for(int i = 0; i < message.length(); i++ )
    {
        buttons[9][i+6].clicked = true;
        if(!bombs.contains(buttons[9][i+6]))
            bombs.add(buttons[9][i+6]);
        buttons[9][i+6].setLabel(message.substring(i,i+1));
    }
}

public class MSButton
{
    private int r, c;
    private float x,y, width, height;
    private boolean clicked, marked;
    private String label;
    
    public MSButton ( int rr, int cc )
    {
        width = 400/NUM_COLS;
        height = 400/NUM_ROWS;
        r = rr;
        c = cc; 
        x = c*width;
        y = r*height;
        label = "";
        marked = clicked = false;
        Interactive.add( this ); // register it with the manager
    }
    public boolean isMarked()
    {
        return marked;
    }
    public boolean isClicked()
    {
        return clicked;
    }
    // called by manager
    
    public void mousePressed () 
    {
        clicked = true;
        if(keyPressed)
            marked = !marked;
        else if(bombs.contains(this))
            displayLosingMessage();
        else if(countBombs(r,c) >0)
            label = "" + countBombs(r,c);
        else
        {
            if(isValid(r-1,c) && !buttons[r-1][c].clicked)
              buttons[r-1][c].mousePressed();
           if(isValid(r+1,c) && !buttons[r+1][c].clicked)
              buttons[r+1][c].mousePressed();
           if(isValid(r,c-1) && !buttons[r][c-1].clicked)
              buttons[r][c-1].mousePressed();
           if(isValid(r,c+1) && !buttons[r][c+1].clicked)
              buttons[r][c+1].mousePressed();
           if(isValid(r-1,c+1) && !buttons[r-1][c+1].clicked)
              buttons[r-1][c+1].mousePressed();
           if(isValid(r+1,c+1) && !buttons[r+1][c+1].clicked)
              buttons[r+1][c+1].mousePressed();
           if(isValid(r-1,c-1) && !buttons[r-1][c-1].clicked)
              buttons[r-1][c-1].mousePressed();
           if(isValid(r+1,c-1) && !buttons[r+1][c-1].clicked)
              buttons[r+1][c-1].mousePressed();
        }


    }

    public void draw () 
    {    
        if (marked)
            fill(0);
        else if( clicked && bombs.contains(this) ) 
            fill(255,0,0);
        else if(clicked)
            fill( 200 );
        else 
            fill( 100 );

        rect(x, y, width, height);
        fill(0);
        text(label,x+width/2,y+height/2);
    }
    public void setLabel(String newLabel)
    {
        label = newLabel;
    }
    public boolean isValid(int r, int c)
    {
        //checks if it is a valid location on the grid
        return ((r >= 0 && r <NUM_ROWS) && (c >=0 && c <NUM_COLS));        
    }
    public int countBombs(int row, int col)
    {
        int bombCount = 0;
        if(isValid(row-1,col-1))//top left
            if(bombs.contains(buttons[row-1][col-1]))
                bombCount++;
        if(isValid(row-1,col))//top middle
            if(bombs.contains(buttons[row-1][col]))
                bombCount++;
        if(isValid(row-1,col+1))//top right
            if(bombs.contains(buttons[row-1][col+1]))
                bombCount++;
        if(isValid(row,col-1))//middle left
            if(bombs.contains(buttons[row][col-1]))
                bombCount++;
        if(isValid(row,col+1))//middle right
            if(bombs.contains(buttons[row][col+1]))
                bombCount++;
        if(isValid(row+1,col-1))//bottom left
            if(bombs.contains(buttons[row+1][col-1]))
                bombCount++;
        if(isValid(row+1,col))//bottom middle    
            if(bombs.contains(buttons[row+1][col]))
                bombCount++;           
        if(isValid(row+1,col+1))//bottom right
            if(bombs.contains(buttons[row+1][col+1]))
                bombCount++;            
        return bombCount;
    }
}



  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Minesweeper" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
