public class BoardThread extends Thread {

    private Panel panel;
    public BoardThread(Panel panel){
        this.panel = panel;
        start();


    }

    public void run(){
        while(true){
            panel.step();
            panel.repaint();

        }
    }

}
