package info.simplyapps.game.hedgehog.rendering.objects;

public class Obstacle extends info.simplyapps.gameengine.rendering.objects.Obstacle {

    public Obstacle(int type, float life, int gReference, int points) {
        super(type, life, gReference, points);
    }

    // movement time
    public long lastMove;

    // the obstacle position on the game map
    public int x;
    public int y;

}
