package hello;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class hello {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new SnakeGame());
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
        });
    }
}

class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int SCALE = 20;
    private static final int SPEED = 100; // Intervalle entre les étapes du jeu, en millisecondes.

    private int x = WIDTH / 2;
    private int y = HEIGHT / 2;
    private int dx = 0;
    private int dy = -1;
    private int score = 0;

    private boolean isPaused = false; // Variable indiquant si le jeu est en pause

    private final ArrayList<int[]> snake = new ArrayList<>();
    private int[] food;

    private final javax.swing.Timer timer;
    private final Random random = new Random();

    public SnakeGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GRAY);
        addKeyListener(this);

        snake.add(new int[]{x, y});
        generateFood();

        timer = new javax.swing.Timer(SPEED, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessinez le cadre qui représente les murs
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, WIDTH - SCALE, HEIGHT - SCALE); // Pour créer un cadre qui entoure la zone de jeu

        g.setColor(Color.YELLOW);
        for (int[] segment : snake) {
            g.fillRect(segment[0], segment[1], SCALE, SCALE);
        }
        
        g.setColor(Color.BLUE);
        g.fillRect(food[0], food[1], SCALE, SCALE);

        g.setColor(Color.GREEN);
        g.drawString("Score: " + score, 10, 20);

        if (isPaused) {
            g.setColor(Color.YELLOW);
            g.drawString("Jeu en pause", WIDTH / 2 - 50, HEIGHT / 2);
        }
    }

    private void generateFood() {
        int foodX;
        int foodY;

        do {
            foodX = random.nextInt((WIDTH - SCALE) / SCALE) * SCALE;
            foodY = random.nextInt((HEIGHT - SCALE) / SCALE) * SCALE;
        } while (isSnakePosition(foodX, foodY));

        food = new int[]{foodX, foodY};
    }

    private boolean isSnakePosition(int x, int y) {
        for (int[] segment : snake) {
            if (segment[0] == x && segment[1] == y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPaused) {
            return; // Si le jeu est en pause, ne faites rien
        }

        moveSnake();
        checkCollision();
        repaint();
    }

    private void moveSnake() {
        x += dx * SCALE;
        y += dy * SCALE;
        snake.add(0, new int[]{x, y});
        if (snake.size() > score + 1) {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollision() {
        if (x < SCALE || x >= WIDTH - SCALE || y < SCALE || y >= HEIGHT - SCALE) {
            gameOver();
        }

        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i)[0] == x && snake.get(i)[1] == y) {
                gameOver();
            }
        }

        if (x == food[0] && y == food[1]) {
            score++;
            generateFood();
        }
    }

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Votre score est " + score);
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_P) { // Touche pour mettre en pause ou reprendre
            isPaused = !isPaused; // Basculer entre pause et reprise
        } 
        else if (!isPaused) { // Ne permet pas de changer de direction pendant la pause
            if (key == KeyEvent.VK_UP && dy != 1) {
                dx = 0;
                dy = -1;
            } 
            else if (key == KeyEvent.VK_DOWN && dy != -1) {
                dx = 0;
                dy = 1;
            } 
            else if (key == KeyEvent.VK_LEFT && dx != 1) {
                dx = -1;
                dy = 0;
            } 
            else  if (key == KeyEvent.VK_RIGHT && dx != -1) {
                dx = 1;
                dy = 0;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Pas d'action nécessaire.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Pas d'action nécessaire.
    }
}
