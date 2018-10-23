import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Game extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    private int screen = -1;
    private boolean play = false;
    private int score = 0;
    private int bricks = 24;
    private Timer timer;
    private int paddleX = 450;
    private int ballPosX = 490;
    private int ballPosY = 700;
    private int ballXdir = -1;
    private int ballYdir = -2;
    private int rows = 4;
    private int cols = 6;
    private Image paddle;
    private Image background;
    private Image settings;
    private Image menu;
    private File paddleBounce;
    private File brickBounce;
    private File click;
    private File music;
    private File lose;
    private File doot;
    private int loseCount = 0;
    private int winCount = 0;
    private File pop;
    private boolean looping;
    private MapGenerator map;

    Game() {
        map = new MapGenerator(rows, cols);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(6, this);
        menu = createImage("Images/menu.png");
        paddle = createImage("Images/paddle.png");
        background = createImage("Images/space.png");
        settings = createImage("Images/Settings.png");
        paddleBounce = new File("Sounds/paddleBounce.wav");
        brickBounce = new File("Sounds/brickBounce.wav");
        click = new File("Sounds/click.wav");
        lose = new File("Sounds/lose.wav");
        doot = new File("Sounds/doot.wav");
        pop = new File("Sounds/pop.wav");
        music = new File("Sounds/music.wav");
        timer.start();
    }

    public void paint(Graphics g) {
        // Main Menu
        if (screen == -1) {
            // Background
            g.setColor(new Color(32,39,41));
            g.fillRect(0, 0, 1000, 1000);

            // Menu
            g.drawImage(menu, 0, 0, null);
        }
        // Settings
        else if (screen == 0) {
            // Settings
            g.drawImage(settings, 0, 0, null);

        }
        // Game
        else {
            // Background
            g.drawImage(background, 0, 0, null);

            // Map
            map.draw((Graphics2D) g);

            // Border
            g.setColor(Color.gray);
            g.fillRect(0, 0, 3, 992);
            g.fillRect(0, 0, 992, 3);
            g.fillRect(992, 0, 3, 992);

            // Score
            g.setColor(Color.white);
            g.setFont(new Font("comic sans ms", Font.BOLD, 35));
            g.drawString("" + score, 900, 40);

            // Paddle
            g.drawImage(paddle, paddleX, 850, null);

            // Ball
            g.setColor(Color.white);
            g.fillOval(ballPosX, ballPosY, 20, 20);

            if (bricks <= 0) {
                play = false;
                winCount++;
                ballXdir = 0;
                ballYdir = 0;
                g.setColor(new Color(255,241,118));
                g.setFont(new Font("serif", Font.BOLD, 40));
                g.drawString("You Win!", 300, 400);

                g.setFont(new Font("serif", Font.BOLD, 40));
                g.drawString("Press Space to Restart", 340, 450);
            }

            if (ballPosY > 935) {
                play = false;
                loseCount++;
                ballXdir = 0;
                ballYdir = 0;
                g.setColor(new Color(255,82,82));
                g.setFont(new Font("serif", Font.BOLD, 40));
                g.drawString("Game Over!", 400, 500);

                g.setFont(new Font("serif", Font.BOLD, 40));
                g.drawString("Press Space to Restart", 320, 550);
            }
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {

            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(paddleX, 850, 100, 15))) {
                ballYdir = -ballYdir;
                playSound(paddleBounce, false, 0.0);
            }

            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            bricks--;
                            score += 100;

                            playSound(brickBounce, false, 0.0);

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballPosX += ballXdir;
            ballPosY += ballYdir;
            if (ballPosX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballPosY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballPosX > 970) {
                ballXdir = -ballXdir;
            }
        }
        if (winCount == 20) {
            playSound(doot,false,5);
        }
        if (loseCount == 20) {
            playSound(lose,false,6);
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (screen == 1) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!play) {
                    play = true;
                    winCount = 0;
                    loseCount = 0;
                    restart();
                    System.out.println("SPACE");
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                play = false;
                screen = -1;
                repaint();
                System.out.println("ESCAPE");
            }
        }
    }

    private void restart() {
        ballPosX = 490;
        ballPosY = 700;
        ballXdir = -1;
        ballYdir = -2;
        paddleX = 310;
        score = 0;
        bricks = cols * rows;
        map = new MapGenerator(rows, cols);

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mousePosX = e.getX();
        int mousePosY = e.getY();
        if (screen == -1) {
            if (mousePosX >= 400 && mousePosX <= 600 && mousePosY >= 600 && mousePosY <= 700) {
                screen = 1;
                System.out.println("PLAY");
                if (!looping) {
                    playSound(music, true, -10.0);
                }
            }
            if (mousePosX >= 900 && mousePosX <= 950 && mousePosY >= 850 && mousePosY <= 900) {
                screen = 0;
                playSound(click, false, -6.0);
                System.out.println("SETTINGS");
            }
        }
        if (screen == 0) {
            if (mousePosX >= 900 && mousePosX <= 950 && mousePosY >= 50 && mousePosY <= 100) {
                screen = -1;
                playSound(click, false, -6.0);
                System.out.println("BACK");
            }
            if (mousePosY >= 230 && mousePosY <= 330) {
                if (mousePosX >= 100 && mousePosX <= 250) {
                    MapGenerator.brickColor = Color.WHITE;
                    playSound(pop, false, -2.5);
                    System.out.println("WHITE");
                }
                if (mousePosX >= 320 && mousePosX <= 470) {
                    MapGenerator.brickColor = new Color(255,82,82);
                    playSound(pop, false, -2.5);
                    System.out.println("RED");
                }
                if (mousePosX >= 540 && mousePosX <= 690) {
                    MapGenerator.brickColor = new Color(13,71,161);
                    playSound(pop, false, -2.5);
                    System.out.println("BLUE");
                }
                if (mousePosX >= 760 && mousePosX <= 910) {
                    MapGenerator.brickColor = new Color(99,210,151);
                    playSound(pop, false, -2.5);
                    System.out.println("GREEN");
                }
            }
            if (mousePosY >= 480 && mousePosY <= 580) {
                if (mousePosX >= 100 && mousePosX <= 250) {
                    rows = 2;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Rows: 2");
                }
                if (mousePosX >= 320 && mousePosX <= 470) {
                    rows = 3;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Rows: 3");
                }
                if (mousePosX >= 540 && mousePosX <= 690) {
                    rows = 4;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Rows: 4");
                }
                if (mousePosX >= 760 && mousePosX <= 910) {
                    rows = 5;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Rows: 5");
                }
            }
            if (mousePosY >= 730 && mousePosY <= 880) {
                if (mousePosX >= 100 && mousePosX <= 250) {
                    cols = 4;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Cols: 4");
                }
                if (mousePosX >= 320 && mousePosX <= 470) {
                    cols = 5;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Cols: 5");
                }
                if (mousePosX >= 540 && mousePosX <= 690) {
                    cols = 6;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Cols: 6");
                }
                if (mousePosX >= 760 && mousePosX <= 910) {
                    cols = 7;
                    restart();
                    playSound(pop, false, -2.5);
                    System.out.println("Cols: 7");
                }
            }
        }
        if (screen == 1) {
            if (!play) {
                play = true;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        if (screen == -1) {
            repaint();
        }
        if (play) {
            int mousePosX = e.getX();
            if (mousePosX >= 950) {
                paddleX = 897;
            } else if (mousePosX <= 50) {
                paddleX = 3;
            } else {
                paddleX = mousePosX - 50;
            }
        }
    }

    private void playSound(File Sound, boolean loop, double volume) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Sound));
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((float) volume);
            clip.start();
            if (loop) {
                clip.loop(100000);
                looping = true;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private Image createImage(String filePath) {
        try {
            File pathToFile = new File(filePath);
            return ImageIO.read(pathToFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}