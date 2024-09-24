import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    //Altura e Largura da janela
    int boardWidth = 360;
    int boardHeight = 640;

    // Imagens
    Image bottompipeImg;
    Image toppipeImg;
    Image birdImg;
    Image backgroundImg;

    // bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    // logica do jogo
    Bird bird;
    int velocitX = -4;
    int velocitY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();


    Timer gameloop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        
        setFocusable(true);
        addKeyListener(this);

        // Carregando imagens
        backgroundImg = new ImageIcon(getClass().getResource("./images/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./images/flappybird.png")).getImage();
        toppipeImg = new ImageIcon(getClass().getResource("./images/toppipe.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("./images/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameloop = new Timer(1000/60, this);
        gameloop.start();
    }

    public void placePipes(){

        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        
        Pipe topPipe = new Pipe(toppipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottompipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);

            
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: "+ String.valueOf((int)score), 10, 35);
        }else {
            g.drawString(String.valueOf((int)score), 10, 35);
        }
    }

    public void move(){
        //bird
        velocitY += gravity;
        bird.y += velocitY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocitX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; // 2 pipes
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }
    }

public boolean collision(Bird b, Pipe p){
    return b.x < p.x + p.width && // canto esquerdo superior de b não encosta em p
           b.x + b.width > p.x && // canto direito superior de b passou de p
           b.y < p.y + p.height && // canto esquerdo superior de b não encosta no canto esquerdo inferior de p
           b.y + b.height > p.y; // canto esquerdo inferior de b passou do canto esquerdo superior de p 
}

    @Override
    public void actionPerformed(ActionEvent arg0) {
        
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameloop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocitY = -9;
        }
        if (gameOver) {
            bird.y = birdY;
            velocitY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameloop.start();
            placePipesTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}