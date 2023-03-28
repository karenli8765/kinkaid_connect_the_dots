import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class AnglePanel extends AbstractButton implements MouseListener, MouseMotionListener
{
    private double myAngle;
    public AnglePanel()
    {
        super();
        myAngle = Math.PI/4;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public double getAngle()
    {
        return myAngle;
    }

    public void setAngle(double myAngle)
    {
        this.myAngle = myAngle;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.black);
        int diam = Math.min(getWidth(),getHeight());
        g.drawRect(0,0,getWidth(),getHeight());
        g.drawLine(0,getHeight()/2,getWidth(),getHeight()/2);
        g.drawLine(getWidth()/2,0,getWidth()/2,getHeight());
        g.drawOval((int)(getWidth()/2-diam*0.375),
                (int)(getHeight()/2-diam*0.375),
                (int)(diam*0.75),
                (int)(diam*0.75));
        int x = (int)(getWidth()/2+diam*0.375*Math.cos(myAngle));
        int y = (int)(getHeight()/2+diam*0.375*Math.sin(myAngle));
        g.fillOval(x-3,y-3,6,6);
    }


    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        setAngle(Math.atan2((e.getY()-getHeight()/2),(e.getX()-getWidth()/2)));
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_FIRST,"Angle Changed"));
        repaint();
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        setAngle(Math.atan2((e.getY()-getHeight()/2),(e.getX()-getWidth()/2)));
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_FIRST,"Angle Changed"));
        repaint();
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e)
    {

    }

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  {@code MOUSE_DRAGGED} events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&amp;Drop implementations,
     * {@code MOUSE_DRAGGED} events may not be delivered during a native
     * Drag&amp;Drop operation.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        setAngle(Math.atan2((e.getY()-getHeight()/2),(e.getX()-getWidth()/2)));
        fireActionPerformed(new ActionEvent(this,ActionEvent.ACTION_FIRST,"Angle Changed"));
        repaint();
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {

    }
}
