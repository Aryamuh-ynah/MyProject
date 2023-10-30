
/*
 * @author Humayra
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Map {
    public int mp[][];
    public int brickWidth;
    public int brickHeight;


    public Map(int row, int col) {
        mp = new int[row][col];
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                mp[r][c] = 1;
            }
        }

        brickWidth = 540 / col;
        brickHeight = 200 / row;
    }

    // This draws the bricks
    public void draw(Graphics2D g) {
        
        int r= mp.length;
        for (int i = 0; i < mp.length; i++) {
            for (int j = 0; j < mp[0].length; j++) {
                if(i<r/2){
                if (mp[i][j] > 0) {
                    g.setColor(new Color(0X7f00ff)); 
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
            else{
                if (mp[i][j] > 0) {
                    g.setColor(new Color(0Xff33ff)); 
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
            }
        }
    }

    
    public void setBrickValue(int value, int row, int col) {
        mp[row][col] = value;
    }
}

class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 0;
    private int lives = 3;
    private int level = 1; 

    private Timer timer;
    private int delay = 8;

    private int pdl = 310;
    private int ballposX = 320;
    private int ballposY = 350;
    private int ballXdir = -1;
    private int ballYdir = -3;

    private Map map;
    private String playerName;
    private boolean gameover = false;

    public GamePlay(String name, int selectedLevel) {
        playerName = name;
        level = selectedLevel;

        switch (level) { 
            case 1: // Easy level
                map = new Map(4, 7);
                totalBricks = 28;
                break;
            case 2: // Medium level
                map = new Map(6, 7);
                totalBricks = 42;
                break;
            case 3: // Hard level
                
                ballYdir = -4;
                map = new Map(8, 8);
                totalBricks = 64;
                break;
        }

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();

    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background color
        g.setColor(Color.CYAN);
        g.fillRect(1, 1, 692, 592);

        map.draw((Graphics2D) g);

        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        g.setColor(new Color(0x0000ff));
        g.fillRect(pdl, 550, 100, 12);

        g.setColor(Color.RED); // Ball color
        g.fillOval(ballposX, ballposY, 20, 20);

        g.setColor(Color.black);
        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Score: " + score, 520, 30);

        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Lives: " + lives, 20, 30);

        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
			gameover=true;
            g.setColor(new Color(0XFF6464));
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("You Won, Score: " + score, 190, 300);

            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Player: " + playerName, 230, 350);
            g.drawString("Score: " + score, 230, 400);
            g.drawString("Press Enter to Restart.", 190, 450);
		
        }

        if (ballposY > 570 ) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            if(lives>0){
                lives--;
            }
            if (lives <= 0) {
                g.setColor(Color.BLACK);
                showGameOverMessage(g);
            } else {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -3;
                pdl = 310;
            }
        }

        g.dispose();
    }
	private void showGameOverMessage(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("MV Boli", Font.BOLD, 30));
        g.drawString("Game Over!", 190, 300);

        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Player: " + playerName, 230, 350);
        g.drawString("Score: " + score, 230, 400);
        g.drawString("Press Enter to Restart", 190, 450);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        timer.start();
        if (play) {
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(pdl, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }
            for (int i = 0; i < map.mp.length; i++) {
                for (int j = 0; j < map.mp[0].length; j++) {
                    if (map.mp[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 3;

                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width)
                                ballXdir = -ballXdir;
                            else {
                                ballYdir = -ballYdir;
                            }
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent a) {

    }
    @Override
    public void keyPressed(KeyEvent a) {
        if (a.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (pdl >= 600) {
                pdl = 600;
            } else {
                play = true;
                pdl += 40;
            }
        }
        if (a.getKeyCode() == KeyEvent.VK_LEFT) {
            if (pdl < 10) {
                pdl = 10;
            } else {
                play = true;
                pdl -= 40;
            }
        }

        if (a.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -3;
                score = 0;
                totalBricks = 28;
                lives = 3;
                map = new Map(4, 7);
                repaint();
            }
        }

        if (a.getKeyCode() == KeyEvent.VK_1) {
            if (!play) {
                level = 1;
                restartGame(level);
            }
        } else if (a.getKeyCode() == KeyEvent.VK_2) {
            if (!play) {
                level = 2;
                restartGame(level);
            }
        } else if (a.getKeyCode() == KeyEvent.VK_3) {
            if (!play) {
                level = 3;

                restartGame(level);
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent a) {

    }

    private void restartGame(int l) {
        play = true;
        ballposX = 320;
        ballposY = 350;
        ballXdir = -1;
        ballYdir = -3;
        score = 0;
        lives = 3;

        switch (l) {
            case 1: // Easy level
                map = new Map(4, 7);
                totalBricks = 28;
                break;
            case 2: // Medium level
                map = new Map(6, 7);
                totalBricks = 42;
                break;
            case 3: // Hard level
                map = new Map(8, 8);
                totalBricks = 64;
                break;
        }

        repaint();
    }
}

class Main {
    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Enter your name:");

        if (name != null && !name.isEmpty()) {
            int selectedLevel = JOptionPane.showOptionDialog(null, "Select the level:", "Level Selection",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Easy", "Medium", "Hard"}, "Easy");

            if (selectedLevel != JOptionPane.CLOSED_OPTION) {
                SwingUtilities.invokeLater(() -> createAndShowGameFrame(name, selectedLevel + 1));
            } else {
                JOptionPane.showMessageDialog(null, "Invalid level selection. Game cannot start.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid player name. Game cannot start.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createAndShowGameFrame(String name, int selectedLevel) {
        JFrame frame = new JFrame();
        GamePlay gamePlay = new GamePlay(name, selectedLevel);
        frame.setBounds(20, 10, 700, 600);
        frame.setTitle("Break The BriX");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePlay);

        frame.setVisible(true);
    }
}