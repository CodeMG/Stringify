import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Panel extends JPanel{

    private long counter = 0;

    private long cooldown = 100;

    Board board;
    BufferedImage img;
    boolean[][] tmp;
    int errorAcceptance = 0;
    public Panel(int x,int y,int width,int height){
        setBackground(Color.WHITE);
        setBounds(x,y,width,height);

        img = ImageLoader.loadImageGrayscale("./image/cat.jpg");
        board = new Board(200,500,img.getWidth(),img.getHeight());
        BoardThread thread = new BoardThread(this);
        //for(int i = 0; i < 500;i++){
        //    board.addString(i,(i+200)%500);
        //}
        /*board.addString(img,0,250);
        double error = board.getErrorChange(img,0,250,true);
        tmp = board.getPixelsHitByLine(img,0,250);
        System.out.println(error);
        */
        //testStep();
    }

    public void save(){
        FileStorer.save(board.strings);
    }

    public void testStep(){
        for(int i = 0; i < 1;i++){
            int start = (int)(Math.random()*500);
            int end = (int)(Math.random()*100);
            tmp = board.getPixelsHitByLine3(img,start,end);
            board.addString(img,start,end);
        }
    }

    public void step(){
        //Add strings
        int x = 0;
        int y = 0;
        double bestErrorRate = 0;
        for(int i = 0; i < 1;i++){
            int start = (int)(Math.random()*500);
            int end = (int)(Math.random()*500);
            if(start!= end){
                double error = board.getErrorChange(img,start,end,true);
                if(error < errorAcceptance){
                    //System.out.println("Error: " + error);
                    if(bestErrorRate > error){
                        x = start;
                        y = end;
                        bestErrorRate = error;

                    }
                    board.addString(img,start,end);
                }
            }
        }
        //board.addString(img,x,y);
        for(int i = 0; i < 1;i++){
            int amount = board.strings.size();
            int start = (int) (Math.random() * amount);
            amount = board.strings.get(start).size();
            if(amount != 0) {
                int end = board.strings.get(start).get((int) (Math.random() * amount));
                if (start != end) {
                    double error = board.getErrorChange(img, start, end, false);
                    if (error < errorAcceptance) {
                        //System.out.println("Error removing: " + error);
                        board.removeString(img, start, end);
                    }
                }
            }
        }
        if(errorAcceptance < 0) {
            //errorAcceptance++;
        }
        counter++;
        if(counter >= cooldown){
            save();
            counter = 0;
        }

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //g.drawImage(img,(int) ((getWidth() / 2) - (img.getWidth() / 2) )+400,(int) ((getHeight() / 2) - (img.getHeight() / 2)),this);
        board.draw(g,getWidth()/2,getHeight()/2);

        /*
        g.setColor(Color.RED);
        for(int i = 0; i < tmp.length;i++){
            for(int j = 0; j < tmp[i].length;j++){
                if(tmp[i][j]) {
                    g.fillRect((int) ((getWidth() / 2) - (tmp.length / 2) + i), (int) ((getHeight() / 2) - (tmp[i].length / 2) + j), 2, 2);
                }
            }
        }
*/

    }

}
