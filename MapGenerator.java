import java.awt.*;

class MapGenerator {
    int map[][];
    int brickWidth;
    int brickHeight;
    static Color brickColor = new Color(255,82,82);

    MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }

        brickWidth = 840/col;
        brickHeight = 350/row;
    }

    void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
               if (map[i][j] > 0) {
                   g.setColor(brickColor);
                   g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                   g.setStroke(new BasicStroke(3));
                   g.setColor(new Color(32,39,41));
                   g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
               }
            }
        }
    }

    void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}