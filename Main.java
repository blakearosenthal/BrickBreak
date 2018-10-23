import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame gFrame = new JFrame();
        Game game = new Game();
        gFrame.setBounds(460,25,1000,1000);
        gFrame.setTitle("Brick Break");
        gFrame.setResizable(false);
        gFrame.setVisible(true);
        gFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gFrame.add(game);
    }
}