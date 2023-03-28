import java.util.Objects;

public class CtdPoint
{
    int x, y;
    double angle;

    public CtdPoint()
    {
        this(0,0,Math.PI/4);
    }

    public CtdPoint(int x, int y)
    {
        this(x,y,Math.PI/4);
    }

    public CtdPoint(int x, int y, double angle)
    {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public double getAngle()
    {
        return angle;
    }

    public void setAngle(double angle)
    {
        this.angle = angle;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CtdPoint ctdPoint = (CtdPoint) o;
        return x == ctdPoint.x &&
                y == ctdPoint.y;
    }

    @Override
    public String toString()
    {
        return "CtdPoint{" +
                "(" + x +
                ", " + y +
                ") @"+String.format("%3.2f",angle)+"radians }";
    }
}
