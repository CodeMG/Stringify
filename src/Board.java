import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Board {
    private int radius;
    private int amountOfNeedles;
    private int colorstep = 15;
    private int width;
    private int height;

    public Map<Integer, ArrayList<Integer>> strings;
    private double[][] currentState;

    public Board(int radius, int amountOfNeedles,int width,int height){
        //this.radius = width < height? width/2-1 : height/2-1;
        this.radius = width/2;
        this.amountOfNeedles = amountOfNeedles;
        strings = new HashMap<>();
        for(int i = 0; i < amountOfNeedles;i++){
            strings.put(i,new ArrayList<Integer>());
        }
        currentState = new double[width][height];
        for(int i = 0; i < currentState.length;i++){
            for(int j = 0; j < currentState[i].length;j++){
                currentState[i][j] = 255.0;
            }
        }

    }

    public void addString(BufferedImage image,int start, int end){
        strings.get(start).add(end);
        strings.get(end).add(start);

        boolean[][] contains = getPixelsHitByLine(image,start,end);
        for(int i = 0; i  < contains.length;i++){
            for(int j = 0; j < contains[i].length;j++){
                if(contains[i][j]) {
                    currentState[i][j] -= colorstep;
                }
            }
        }
    }

    public void removeString(BufferedImage image,int start, int end){
        if(!strings.get(start).contains((Object)end)){
            return;
        }
        strings.get(start).remove((Object)end);
        strings.get(end).remove((Object)start);


        boolean[][] contains = getPixelsHitByLine(image,start,end);
        for(int i = 0; i  < contains.length;i++){
            for(int j = 0; j < contains[i].length;j++){
                if(contains[i][j]) {
                    currentState[i][j] += colorstep;
                }
            }
        }
    }

    public void draw(Graphics g,int x,int y){
        int newX = x - radius;
        int newY = y - radius;
        g.setColor(Color.BLACK);
        g.drawOval(newX,newY,radius*2,radius*2);


         g.setColor(Color.BLACK);
        double angle = 2*Math.PI/amountOfNeedles;
        double xPos = 0;
        double yPos = -radius;
        for(int i = 0; i < amountOfNeedles;i++){
            g.fillOval((int)Math.round(xPos + x),(int)Math.round(yPos + y),3,3);
            double newXPos = xPos * Math.cos(angle) - yPos * Math.sin(angle);
            double newYPos = xPos * Math.sin(angle) + yPos * Math.cos(angle);
            xPos = newXPos;
            yPos = newYPos;
        }


        g.setColor(new Color(0,0,0,(int)colorstep));
        for(int i = 0; i < strings.size();i++){
            ArrayList<Integer> list = strings.get(i);
            double startXPos = 0 * Math.cos(angle*i) - (-radius) * Math.sin(angle*i);
            double startYPos = 0 * Math.sin(angle*i) + (-radius) * Math.cos(angle*i);
            for (int j = 0; j < list.size();j++){
                int targetIndex = list.get(j);
                double targetXPos = 0 * Math.cos(angle*targetIndex) - (-radius) * Math.sin(angle*targetIndex);
                double targetYPos = 0 * Math.sin(angle*targetIndex) + (-radius) * Math.cos(angle*targetIndex);
                g.drawLine((int)Math.round(startXPos)+x,(int)Math.round(startYPos)+y,(int)Math.round(targetXPos)+x,(int)Math.round(targetYPos)+y);
            }
        }

        /*
        for(int i = 0; i < currentState.length;i++){
            for(int j = 0; j < currentState[i].length;j++){
                g.setColor(new Color((int)currentState[i][j],(int)currentState[i][j],(int)currentState[i][j]));
                g.drawRect(i,j,1,1);
            }
        }
        */
    }

    public double getErrorChange(BufferedImage image,int start, int end, boolean adding){
        boolean[][] contains = getPixelsHitByLine(image,start,end);

        //Now go through all the pixels and calculate the error change

        double total = 0.0;
        double counter = 0.0;
        for(int i = 0; i < contains.length;i++){
            for(int j = 0; j < contains[1].length;j++){
                if(contains[i][j]){
                    counter = counter + 1;
                    double imageState = new Color(image.getRGB(i,j)).getRed();
                    //double multipl = (1-Math.sin(imageState * Math.PI/255) + 1)/2;
                    double multipl = 1.0;
                    if(adding){
                        double newError = imageState - (currentState[i][j]-colorstep);
                        double currentError = imageState - currentState[i][j];

                        total += multipl*(Math.abs(newError) - Math.abs(currentError));
                    }
                    else {
                        double newError = imageState - (currentState[i][j]+colorstep);
                        double currentError = imageState - currentState[i][j];
                        total += multipl*(Math.abs(newError) - Math.abs(currentError));
                    }
                }
            }
        }
        //total/=counter;
        return total;
    }

    public boolean[][] getPixelsHitByLine3(BufferedImage image,int start, int end){
        //position the image accordingly
        int imageMidX = image.getWidth()/2;
        int imageMidY = image.getHeight()/2;

        int[] startCoordinates = getPinCoordinates(start);
        int[] endCoordinates = getPinCoordinates(end);
        startCoordinates[0] += imageMidX;
        startCoordinates[1] += imageMidY;
        endCoordinates[0] += imageMidX;
        endCoordinates[1] += imageMidY;
        boolean[][] contains = new boolean[image.getWidth()][image.getHeight()];


        //for each pixel check if on the line
        for(int i = 0; i < image.getWidth();i++){
            for(int j = 0; j < image.getHeight();j++){
                int p3x = i;
                int p3y = j;
                double u = ((p3x - startCoordinates[0])*(endCoordinates[0] - startCoordinates[0]) + (p3y - startCoordinates[1])*(endCoordinates[1] - startCoordinates[1])) / (Math.pow(endCoordinates[0] - startCoordinates[0],2) + Math.pow(endCoordinates[1] - startCoordinates[1],2));
                int newX = (int)((double)startCoordinates[0] + (u  * ((double) (endCoordinates[0] - startCoordinates[0]))));
                int newY = (int)((double)startCoordinates[1] + (u  * ((double) (endCoordinates[1] - startCoordinates[1]))));
                double distance = Math.sqrt(Math.pow(newX - p3x,2) + Math.pow(newY - p3y,2));
                if(distance <= 1){
                    contains[i][j] = true;
                }
            }
        }

        return contains;
    }


    public boolean[][] getPixelsHitByLine2(BufferedImage image,int start, int end){
        //position the image accordingly
        int imageMidX = image.getWidth()/2;
        int imageMidY = image.getHeight()/2;

        int[] startCoordinates = getPinCoordinates(start);
        int[] endCoordinates = getPinCoordinates(end);
        startCoordinates[0] += imageMidX;
        startCoordinates[1] += imageMidY;
        endCoordinates[0] += imageMidX;
        endCoordinates[1] += imageMidY;
        boolean[][] contains = new boolean[image.getWidth()][image.getHeight()];


        //for each pixel check if on the line
        for(int i = 0; i < image.getWidth();i++){
            for(int j = 0; j < image.getHeight();j++){
                int p3x = i;
                int p3y = j;
                double m = (double)(endCoordinates[1] - startCoordinates[1]) / (double)(endCoordinates[0]-startCoordinates[0]);
                double b = startCoordinates[1] - m*startCoordinates[0];
                double distance = Math.abs(m*p3x+b-p3y);
                if(distance <= 1){
                    contains[i][j] = true;
                }
            }
        }

        return contains;
    }

    public boolean[][] getPixelsHitByLine(BufferedImage image,int start, int end){
        //position the image accordingly
        int imageMidX = image.getWidth()/2;
        int imageMidY = image.getHeight()/2;

        int[] startCoordinates = getPinCoordinates(start);
        int[] endCoordinates = getPinCoordinates(end);
        startCoordinates[0] += imageMidX;
        startCoordinates[1] += imageMidY;
        endCoordinates[0] += imageMidX;
        endCoordinates[1] += imageMidY;
        boolean[][] contains = new boolean[image.getWidth()][image.getHeight()];

        int x1 = startCoordinates[0];
        int x2 = endCoordinates[0];

        int y1 = startCoordinates[1];
        int y2 = endCoordinates[1];


        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;

        if (dx >= dy) {
            while (true) {
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
                if(x>0 && x < contains.length && y > 0 && y < contains[x].length) {
                    contains[x][y] = true;
                }
            }
        } else {
            while (true) {
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
                if(x>0 && x < contains.length && y > 0 && y < contains[x].length) {
                    contains[x][y] = true;
                }
            }
        }


        return contains;
    }

    private int[] getPinCoordinates(int i){
        double angle = 2*Math.PI/amountOfNeedles;
        double targetXPos = 0 * Math.cos(angle*i) - (-radius) * Math.sin(angle*i);
        double targetYPos = 0 * Math.sin(angle*i) + (-radius) * Math.cos(angle*i);

        int[] ret = new int[2];
        ret[0] = (int)targetXPos;
        ret[1] = (int)targetYPos;
        return ret;
    }

}
