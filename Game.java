import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Timer;
import java.io.*;
import java.util.TimerTask;


public class Game extends JFrame {
    private int cols;
    private int rows;
    private int nBombs;
    private int bombsNotFlaggedRemaining;

    private Cell[][] cells;
    private boolean[][] dpTable;

    private JPanel nord = new JPanel();
    private JPanel sud = new JPanel();

    private JButton setup = new JButton();

    private boolean firstClick;
    private int cellsClicked;

    private JTextField nFlags = new JTextField(5);
    private JTextField crono = new JTextField(5);

    private TimerTask timerTask;
    private int secondsPassed;
    private Timer timer;

    public Game(int rows, int cols, int nBombs) {
        super("Minesweeper");
        this.rows = rows;
        this.cols = cols;
        this.nBombs = nBombs;
        this.bombsNotFlaggedRemaining = nBombs;

        this.sud.setLayout(new GridLayout(this.rows,this.cols));
        sud.setPreferredSize(new Dimension(Math.abs(35*this.rows*9/7),Math.abs(35*this.cols*6/7)));
        this.nord.setLayout(new FlowLayout());
        nord.setPreferredSize(new Dimension(Math.abs(35*this.rows/7),Math.abs(35*this.cols/7)));

        this.cells = createCellMatrixAndFillSouthJPanel();
        fillNorthJPanel();

        this.getContentPane().add(nord,BorderLayout.NORTH);
        this.getContentPane().add(sud,BorderLayout.SOUTH);

        this.timerTask = createTimerTask();

        this.setSize(40*this.rows,40*this.cols);
        //this.pack();
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class ButtonClickedEvent extends MouseAdapter {
        @Override public void mousePressed(MouseEvent e){
            int i=0;
            int j=0;
            JButton thatButton = (JButton) e.getSource();
            for(int x = 0; x<rows; x++){
                for(int y = 0; y<cols; y++){
                    if(cells[x][y].getButton() == thatButton){
                        i = x;
                        j = y;
                    }
                }
            }
            if(SwingUtilities.isLeftMouseButton(e)){
                if (!firstClick){
                    firstClick = true;
                    generateBombs(i,j);
                    calculateEnemies();
                    timer = new Timer();
                    timer.scheduleAtFixedRate(timerTask,1000,1000);
                }

                if(cells[i][j].isBomb() && !cells[i][j].isFlagged()){
                    gameOver();
                }else if(!cells[i][j].isFlagged()){
                    recursiveTurnVisible(i,j);
                }
            }
            else if (SwingUtilities.isRightMouseButton(e)){
                if(cells[i][j].isFlagged()){
                    bombsNotFlaggedRemaining++;
                    cells[i][j].markAsNotFlagged();
                }else if(bombsNotFlaggedRemaining>0 && !cells[i][j].isVisible()){
                    cells[i][j].markAsFlagged();
                    bombsNotFlaggedRemaining--;
                }
                nFlags.setText(""+bombsNotFlaggedRemaining);
            }
        }
    }

    class ResetListenter implements ActionListener {
        @Override public void actionPerformed(ActionEvent e){
            resetGame();
        }
    }

    public void resetGame(){
        timer.cancel();
        timerTask = createTimerTask();
        //Get the components in the panel
        Component[] componentList = sud.getComponents();
        //Loop through the components
        for(Component c : componentList){
        //Remove it
        sud.remove(c);
        }
        this.cells = createCellMatrixAndFillSouthJPanel();
        sud.revalidate();
        sud.repaint();
    }

    public void generateBombs(int x,int y){
        for(int i = 0; i<this.nBombs; i++){
            Random r = new Random();
            int tempI = r.nextInt(rows);
            int tempJ = r.nextInt(cols);
            while(cells[tempI][tempJ].isBomb() || vectorContainedIn3x3(x,y,tempI,tempJ)){
                tempI = r.nextInt(rows);
                tempJ = r.nextInt(cols);
            }
            cells[tempI][tempJ].becomeBomb();
        }
    }

    public boolean vectorContainedIn3x3(int x, int y, int randI, int randJ){
        for(int i = -1; i<=1; i++){
            for(int j= -1; j<=1; j++){
                if(randI+i == x && randJ+j == y) return true;
            }
        }
        return false;
    }

    public void calculateEnemies(){
        int count;
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                count = 0;
                for(int x = -1; x<=1; x++){
                    for(int y =-1; y<=1; y++){
                        if(!cells[i][j].isBomb()){
                            try{
                                if(cells[i+x][j+y].isBomb()) count++;
                            }catch(Exception ex){}
                        }
                    }
                }
                if(!cells[i][j].isBomb())
                    cells[i][j].setNumberText(count);
                count = 0;
            }
        }
    }

    public void recursiveTurnVisible(int i, int j){
        Queue<Cell> q = new LinkedListQueue<>();
        q.enqueue(cells[i][j]);
        while(!q.isEmpty()){
            Cell lastCell = q.top();
            if(true){
                if(lastCell.isBomb() || lastCell.isVisible()) q.dequeue();
                else{
                    lastCell.turnVisible();
                    this.cellsClicked++;
                    if (cellsClicked == (rows*cols)-nBombs){
                        win();
                    }
                    q.dequeue();
                    if(lastCell.getEnemies() == 0){
                        for(int x=-1; x<=1; x++){
                            for(int y = -1; y<=1; y++){
                                int cellX = lastCell.getX();
                                int cellY = lastCell.getY();
                                if(cellX+x<rows && cellY+y<cols && cellX+x>=0 && cellY+y >= 0 &&        !cells[cellX+x][cellY+y].isVisible()){
                                    q.enqueue(cells[cellX+x][cellY+y]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void gameOver(){
        this.timer.cancel();
        Icon icon = new ImageIcon("dead35.png");
        this.setup.setIcon(icon);
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                if(!cells[i][j].isBomb()){
                    if(cells[i][j].isFlagged()){
                        cells[i][j].markAsFlagged();
                        cells[i][j].turnVisible();
                        cells[i][j].getButton().setIcon(null);
                        cells[i][j].getButton().setText("X");
                        cells[i][j].getButton().setForeground(Color.RED);
                    }
                }
                else{
                    cells[i][j].turnVisible();
                }
            }
        }

        int option = JOptionPane.showOptionDialog(null,"You lost!\nDo you want to play again?","Game Over",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,null,null);
        if(option == JOptionPane.NO_OPTION){
            System.exit(1);
        }else{
            resetGame();
        }
    }

    public void win(){
        this.timer.cancel();
        Icon icon = new ImageIcon("win35.png");
        this.setup.setIcon(icon);
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                if(cells[i][j].isBomb()){
                    cells[i][j].turnVisible();
                }
            }
        }
        int option = JOptionPane.showOptionDialog(null,"You won!\nDo you want to play again?","Game Won",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,null,null);
        if(option == JOptionPane.NO_OPTION){
            System.exit(1);
        }else{
            resetGame();
        }
    }

    public Cell[][] createCellMatrixAndFillSouthJPanel(){
        Cell[][] matrix = new Cell[this.rows][this.cols];
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<cols; j++){
                matrix[i][j] = new Cell((this.rows+this.cols)/2,i,j);
                this.sud.add(matrix[i][j].getButton());
                matrix[i][j].getButton().addMouseListener(new ButtonClickedEvent());
            }
        }

        this.firstClick = false;
        this.secondsPassed = 0;
        this.cellsClicked = 0;
        this.bombsNotFlaggedRemaining = this.nBombs;

        this.crono.setText("0");
        this.nFlags.setText(""+this.bombsNotFlaggedRemaining);

        this.dpTable = new boolean[rows][cols];

        Icon icon = new ImageIcon("smile35.png");
        setup.setIcon(icon);

        return matrix;
    }

    public void fillNorthJPanel(){

        nFlags.setHorizontalAlignment(JTextField.CENTER);
        nFlags.setForeground(Color.RED);
        nFlags.setBackground(Color.BLACK);
        nFlags.setFont(new Font("Monospaced",Font.ITALIC,25));
        nFlags.setEditable(false);

        crono.setHorizontalAlignment(JTextField.CENTER);
        crono.setForeground(Color.RED);
        crono.setBackground(Color.BLACK);
        crono.setFont(new Font("Monospaced",Font.ITALIC,25));
        crono.setEditable(false);

        setup.setOpaque(true);
        setup.setSize(40,40);

        setup.setSize(40,40);
        setup.addActionListener( new ResetListenter());

        nord.add(nFlags);
        nord.add(setup);
        nord.add(crono);

    }

    public TimerTask createTimerTask(){
        return new TimerTask(){
            @Override public void run(){
                secondsPassed++;
                crono.setText(""+secondsPassed);
            }
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override public void run(){
                Game g = new Game(10,10,10);
            }
        });
    }
}
