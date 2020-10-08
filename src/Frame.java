import java.awt.*;
import javax.swing.*;
public class Frame extends JFrame{

    public Frame(){
        setSize(800,600);
        setTitle("Stringify");
        setLocation(2500,200);
        setDefaultCloseOperation(3);

        Panel p = new Panel(0,0,1400,600);
        p.setVisible(true);
        add(p);

        setVisible(true);
    }

}
