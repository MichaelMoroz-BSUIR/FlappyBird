package sample;

public class Bounds {
    public int x, y, width, height;

    public boolean intersects(int x0, int y0, int width0, int height0) {
        if (width < 0 || height < 0 || width0 < 0 || height0 < 0) {
            return false;
        }
        return (x0 + width0) >= x &&
                (y0 + height0) >= y &&
                x0 <= (x + width) &&
                y0 <= (y + height);
    }

    public boolean intersects(Bounds bounds) {
        return bounds != null &&
                intersects(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
