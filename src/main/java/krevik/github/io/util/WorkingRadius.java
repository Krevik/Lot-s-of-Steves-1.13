package krevik.github.io.util;

public class WorkingRadius {
    private int xRadius;
    private int yRadius;
    private int zRadius;

    public WorkingRadius(int x,int y,int z){
        xRadius = x;
        yRadius = y;
        zRadius = z;
    }

    public int getXRadius(){
        return xRadius;
    }

    public int getYRadius() {
        return yRadius;
    }

    public int getZRadius() {
        return zRadius;
    }
}
