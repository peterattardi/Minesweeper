import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;


public class Cell {
    private JButton me;
    private int l;
    private boolean AmIBomb;
    private int myEnemies;
    private boolean AmIVisible;
    private boolean AmIFlagged;
    private int myX;
    private int myY;

    public Cell(int w,int myX, int myY){
        this.myX = myX;
        this.myY = myY;
        this.me = new JButton();
        this.AmIBomb = false;
        this.AmIFlagged = false;
        this.me.setSize(w,w);
        this.me.setOpaque(true);
        this.me.setBorderPainted(true);
        this.me.setBackground(Color.WHITE);
        this.me.setBorder(new LineBorder(new Color(175,175,175)));


    }

    public final JButton getButton(){
        return this.me;
    }

    public final boolean isBomb(){
        return this.AmIBomb;
    }

    public void becomeBomb(){
        this.AmIBomb = true;
    }

    public void markAsFlagged(){
        this.me.setBorderPainted(true);
        this.me.setBorder(new LineBorder(new Color(175,175,175)));
        Icon i = new ImageIcon("flag15.png");
        this.me.setIcon(i);
        this.AmIFlagged = true;

    }

    public void markAsNotFlagged(){
        this.me.setIcon(null);
        this.AmIFlagged = false;
    }

    public boolean isFlagged(){
        return this.AmIFlagged;
    }

    public void turnVisible(){
        this.AmIVisible = true;
        this.me.setBackground(new Color(224,224,224));
        if(this.AmIBomb){
            Icon i = new ImageIcon("bomb15.png");
            this.me.setIcon(i);
        }else{
            if (this.myEnemies != 0) this.me.setText(""+this.myEnemies);
            switch (this.myEnemies) {
                case 0 : break;
                case 1 : this.me.setForeground(Color.BLUE); break;
                case 2 : this.me.setForeground(Color.GREEN); break;
                case 3 : this.me.setForeground(Color.RED); break;
                case 4 : this.me.setForeground(new Color(100,0,255)); break;
                case 5 : this.me.setForeground(new Color(200,0,200)); break;
                case 6 : this.me.setForeground(new Color(150,0,0)); break;
                default : break;
            }
        }
    }

    public boolean isVisible(){
        return this.AmIVisible;
    }

    public final int getEnemies(){
        return this.myEnemies;
    }

    public void setNumberText(int count){
        this.myEnemies = count;
    }

    public final int getX(){
        return this.myX;
    }

    public final int getY(){
        return this.myY;
    }

}
