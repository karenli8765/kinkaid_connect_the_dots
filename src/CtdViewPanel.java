import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class CtdViewPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
    // constants
    private static final int DOT_DIAM = 6; // the size of the dots
    private static final int DOT_RADIUS = DOT_DIAM/2;
    private static final int DOT_FONT_SIZE = 9; // the fontsize of the numbers
    private static final double NUMBER_RADIUS = 12; // the distance from the dot's center to the number's center.
    private static final int SELECTION_RADIUS_SQUARED_THRESHOLD = 900; // the square of the minimum distance a dot the user must click to select one.
    // constants - these two are the options we allow for "mode"
    public static final int MODE_EDIT = 0;
    public static final int MODE_ADD = 1;


    // member variables
    private ArrayList<CtdPoint> myDots;
    private Image backgroundImage;
    private int opacity; // the opacity of the white box over the background image.
    private Font dotFont;
    private boolean shouldShowLines;
    private int mode; // either MODE_EDIT or MODE_ADD
    private CtdPoint tempDot; // a dot in the process of being added - null if there isn't one.
    private int selectedIndex; // the position on the list of the currently selected dot, or -1 if none is selected.
    private double defaultAngle; // the angle between the dot and number for all new dots.
    private CtdFrame myParent; // the Frame holding this panel.
    private File lastFile; // the last location you accessed a file since the program ran.

    public CtdViewPanel()
    {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        myDots = new ArrayList<CtdPoint>();
        dotFont = new Font("Arial",Font.PLAIN,DOT_FONT_SIZE);
        shouldShowLines = true;
        mode = MODE_ADD;
        backgroundImage = null;
        opacity = 128;
        defaultAngle = Math.PI/4;
        selectedIndex = -1;
        lastFile = null;
    }

    public void setParent(CtdFrame p)
    {
        myParent = p;
    }

    @Override
    public String toString()
    {
        String output = "";
        // TODO: #2 - add each point's toString() result to output. Put a line break "\n" after each point.
        for (int i = 0; i < myDots.size(); i++)
        {
            output += myDots.get(i).toString();
        }
        return output;
    }

    /**
     * set the variable "selectedIndex" to the given value.
     * Request focus so that this panel can respond to the
     * keyboard. Also let the parent window know that there
     * has been a change, so that it can respond.
     * @param index - the index that is selected, or -1 if
     *              no dots are selected.
     */
    public void setSelectedIndex(int index)
    {
        selectedIndex = index;
        if (selectedIndex >= 0)
            requestFocus();
        if (myParent == null)
            return;
        if (selectedIndex >= 0)
        {
            myParent.angleHasChangedInPanel(myDots.get(selectedIndex).getAngle());
            myParent.setPointIsSelected(true);
        }
        else
        {
            myParent.setPointIsSelected(false);
        }

    }

    /**
     * decrements "selectedIndex" and repaints. You should probably put some safeties on this to avoid crashes.
     * */
    public void selectPreviousDot()
    {
        //TODO #? - you write this. Hint: make use of setSelectedIndex().
        if (selectedIndex > 0)
        {
            setSelectedIndex(selectedIndex - 1);
        }
        repaint();
    }

    /**
     * increments "selectedIndex" and repaints. You should probably put some safeties on this to avoid crashes.
     */
    public void selectNextDot()
    {
        //TODO #? - you write this. Hint: make use of setSelectedIndex().
        if (selectedIndex < myDots.size() - 1)
        {
            setSelectedIndex(selectedIndex + 1);
        }
        repaint();
    }
// ----------------------------------  Graphics functions

    /**
     * Draw the screen, including the background image (if any),
     * the dots and the lines (if enabled).
     * @param g - the Graphics object.
     */
    public void paintComponent(Graphics g)
    {
        if (backgroundImage != null)
        {
            // if we have a background image, draw it.
            g.drawImage(backgroundImage, 0, 0, null);
            // cover it with a translucent white box to fade it.
            g.setColor(new Color(255, 255, 255, opacity));
            g.fillRect(0,0,getWidth(),getHeight());
        }
        else
        {
            // otherwise, clear the screen.
            super.paintComponent(g);
        }
        drawDots(g);
        if (shouldShowLines)
            drawLines(g);
    }

    /**
     * draw all the dots in myDots onscreen.
     * @param g - the Graphics object.
     */
    public void drawDots(Graphics g)
    {
        if (MODE_EDIT == mode && -1 != selectedIndex && myDots.size()>selectedIndex)
        {
            g.setColor(Color.green);
            // TODO - #? draw a highlight circle around the dot at "selectedIndex" in myDots.
            int highlightXValue = myDots.get(selectedIndex).getX() - 1;
            int highlightYValue = myDots.get(selectedIndex).getY() - 1;
            if (highlightXValue < 0)
            {
                highlightXValue = highlightXValue * -1;
            }
            g.fillOval(highlightXValue, highlightYValue, 7, 7);
        }

        g.setColor(Color.black);
        g.setFont(dotFont);
        // TODO #3 - loop through all the dots in myDots. For each
        //  dot...
        //      if this is the first dot or if its X is negative, draw a hollow dot of DOT_RADIUS, centered on its (-X,Y).
        //      if its X is non-negative, draw a solid dot of DOT_RADIUS, centered on its (X,Y).
        //      Also, for each dot, drawString for (i+1) so that it is NUMBER_RADIUS away from the (±X,Y),
        //         at this dot's radius. Here's (partial) code for this part:
        for (int i = 0; i < myDots.size(); i++)
        {
            CtdPoint dot = myDots.get(i);
            double angle = dot.getAngle();
            if (dot.getX() >= 0)
            {
                g.drawString("" + (i + 1),
                        (int) (dot.getX() + NUMBER_RADIUS * Math.cos(angle) - g.getFontMetrics().stringWidth("" + (i + 1)) / 2),
                        (int) (dot.getY() + NUMBER_RADIUS * Math.sin(angle) + DOT_FONT_SIZE / 2));
            }
            else
            {
                g.drawString("" + (i + 1),
                        (int) (-dot.getX() + NUMBER_RADIUS * Math.cos(angle) - g.getFontMetrics().stringWidth("" + (i + 1)) / 2),
                        (int) (dot.getY() + NUMBER_RADIUS * Math.sin(angle) + DOT_FONT_SIZE / 2));
            }
            if (i == 0)
            {
                g.drawOval(dot.getX(), dot.getY(), 5, 5);
            }
            else if (dot.getX() < 0)
            {
                g.drawOval(-1 * dot.getX(), dot.getY(), 5, 5);
            }
            else
            {
                g.fillOval(dot.getX(), dot.getY(), 5, 5);
            }
        }


        if (tempDot != null)
        {
            // TODO - #3.5 The "tempDot" is the dot that is in the process of being added.
            //  (A non-null tempDot means the user is in "MODE_ADD" and dragging the dot around.)
            //  Draw this one, too, following the rules for to do #3.
            g.fillOval(tempDot.getX(), tempDot.getY(), 5, 5);

        }
    }

    /**
     * draws all the lines between dots, as well as the "latest dot" if we are in add mode - color codes if there are
     * intersections.
     * @param g - the Graphics object
     */
    public void drawLines(Graphics g)
    {
        // if there are no dots, then bail out now.
        if (myDots.isEmpty())
            return;

        // get a list of which lines intersect (conflict at index refers to line from myDots' index -> index + 1)
        ArrayList<Boolean> conflicts = getConflicts();

        // ---------------------------
        // TODO - #4: Loop through all the dots in myDots, except the last one.
        //    Consider you are on dot at location A, and we also are interested in the next dot, at location B = A+1.
        //    For each dotA -> dotB, if dotB's X value is non-negative, you will draw a line from dotA -> dotB.
        //    The color of this line is determined by the "conflicts" list generated in the previous line.
        //    If the value of "conflicts" at A is true, then the line from dotA -> dotB should be red; otherwise draw
        //         it in black.
        for (int i = 0; i < myDots.size() - 1; i++)
        {
            CtdPoint dotStart = myDots.get(i);
            CtdPoint dotEnd = myDots.get(i + 1);
            g.setColor(Color.black);
            if (conflicts.get(i) == true)
            {
                g.setColor(Color.RED);
            }
            if (dotEnd.getX() >= 0)
            {
                if (dotStart.getX() < 0)
                {
                    g.drawLine(-dotStart.getX(), dotStart.getY(), dotEnd.getX(), dotEnd.getY());
                    continue;
                }
                g.drawLine(dotStart.getX(), dotStart.getY(), dotEnd.getX(), dotEnd.getY());
            }
        }


        // ---------------------------
        if (MODE_ADD == mode && tempDot != null && tempDot.getX()>=0)
        {
            g.setColor(Color.BLACK);
            if (isTempDotAConflict(tempDot))
            {
                g.setColor(Color.RED);
            }
            //--------------------------------
            // TODO - #4.5 draw the line from the last dot in the myDots list to tempDot.
            CtdPoint lastDot = myDots.get(myDots.size()-1);
            if (lastDot.getX() < 0)
            {
                g.drawLine(-lastDot.getX(), lastDot.getY(), tempDot.getX(), tempDot.getY());
            }
            else
            {
                g.drawLine(lastDot.getX(), lastDot.getY(), tempDot.getX(), tempDot.getY());
            }


            //--------------------------------
        }
    }
//-----------------------------------  End Graphics functions
//-----------------------------------  Respond to GUI controls

    /**
     * read the given text file and convert the information into an
     * the myDots list of dots.
     * @param filename - the path to the file.
     */
    public void loadDots(String filename)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            myDots.clear();
            String latestLine;
            while ((latestLine = reader.readLine()) != null)
            {
                String[] parts = latestLine.split("\t");
                CtdPoint pt;
                if (parts.length == 3)
                    pt = new CtdPoint(Integer.parseInt(parts[0]),
                                           Integer.parseInt(parts[1]),
                                            Double.parseDouble(parts[2]));
                else throw new IOException("Wrong number of numbers in this line: "+latestLine);
                //TODO #1: You've just created a new dot, called "pt." Time
                // to put it in the myDots list. (Hint: This is one line.)
                myDots.add(pt);
            }
            reader.close();
        }
        catch (FileNotFoundException fnfExp)
        {
            System.out.println("Could not find file.");
            fnfExp.printStackTrace();
        }
        catch (IOException ioExp)
        {
            System.out.println("Error reading from the file");
            ioExp.printStackTrace();
        }
        repaint();
    }

    /**
     * The user has just indicated that they want to load in a text
     * file of dots - show a file dialog and then (if they say "ok")
     * call loadDots with that file path.
     */
    public void loadButtonPressed()
    {
        JFileChooser chooser = new JFileChooser();
        if (lastFile!=null)
            chooser.setSelectedFile(lastFile);
        int result = chooser.showOpenDialog(this);
        if (JFileChooser.APPROVE_OPTION == result)
        {
            lastFile = chooser.getSelectedFile();
            String filename = lastFile.getPath();
            loadDots(filename);
        }

    }

    /**
     * save a text file containing the information about all the
     * dots that this program can read.
     * @param filename - the path to this file.
     */
    public void doSave(String filename)
    {
        try
        {
            PrintWriter fout = new PrintWriter(filename);
            //TODO - #5: for each dot in myDots, print its x, y and angle,
            // separated by tabs ("\t"). Instead of using "System.out," however,
            // use "fout" instead.
            for (int i = 0; i < myDots.size(); i++)
            {
                fout.println(myDots.get(i).getX() + "\t" + myDots.get(i).getY() + "\t" + myDots.get(i).getAngle());
            }
            fout.close();
        }
        catch (IOException ioExp)
        {
            System.out.println("Problem saving file.");
            ioExp.printStackTrace();
        }
    }

    /**
     * give the user the option to select a location to save an
     * image file that matches the current display.
     */
    public void doSaveScreen()
    {
        JFileChooser chooser = new JFileChooser();
        if (lastFile != null)
            chooser.setSelectedFile(lastFile);
        chooser.setDialogTitle("Export");
        String[] extensions = {"jpg","gif","png"};
        chooser.setFileFilter(new FileNameExtensionFilter("images",extensions));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            lastFile = chooser.getSelectedFile();
            String filename = lastFile.getPath();
            BufferedImage exportImage = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
            Graphics2D gExport = exportImage.createGraphics();
            // tell it to draw things well, or you get mushy fonts and square dots.
            gExport.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gExport.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            paintComponent(gExport);
            gExport.dispose();
            int i = filename.lastIndexOf('.');
            if (i<=0)
            {
                try
                {
                    ImageIO.write(exportImage, "png", new File(filename + ".png"));
                }catch(IOException ioExp)
                {
                    System.out.println("Problem writing file.");
                    ioExp.printStackTrace();
                }
            }
            else
            {
                String extension = filename.substring(i+1);
                try
                {
                    ImageIO.write(exportImage, extension, new File(filename));
                }catch(IOException ioExp)
                {
                    System.out.println("Problem writing file.");
                    ioExp.printStackTrace();
                }
            }
            Graphics2D g = (Graphics2D)getGraphics();
            g.drawImage(exportImage, null, 0,0);

        }
    }

    /**
     * show a file dialog to let the user display an image on
     * the screen.
     */
    public void doLoadImage()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select an Image");
        if (lastFile!= null)
            chooser.setSelectedFile(lastFile);
        String[] extensions = {"jpg","gif","jpeg","png"};
        chooser.setFileFilter(new FileNameExtensionFilter("images",extensions));
        int result = chooser.showOpenDialog(this);
        if (JFileChooser.APPROVE_OPTION == result)
        {
            lastFile = chooser.getSelectedFile();
            String fileName = lastFile.getPath();
            backgroundImage = new ImageIcon(fileName).getImage();
        }
        repaint();
    }

    /**
     * The user has pressed the button to save the dots.
     * Ask the user where to save and (if he/she says "save")
     * call doSave() with the selected file location.
     */
    public void saveButtonPressed()
    {
        JFileChooser chooser = new JFileChooser();
        if (lastFile != null)
            chooser.setSelectedFile(lastFile);
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            lastFile = chooser.getSelectedFile();
            String filename = lastFile.getPath();
            doSave(filename);
        }
    }

    public void setShouldShowLines(boolean shouldShowLines)
    {
        this.shouldShowLines = shouldShowLines;
        repaint();
    }

    /**
     * update the current mode
     * @param mode either MODE_ADD or MODE_EDIT.
     */
    public void setMode(int mode)
    {
        if (MODE_ADD == mode && myParent != null)
        {
            myParent.angleHasChangedInPanel(defaultAngle);
            myParent.setPointIsSelected(false);
        }
        if (MODE_EDIT == mode && MODE_ADD == this.mode)
        {
            selectedIndex = -1;
        }
        this.mode = mode;
    }

    public void setOpacity(int opacity)
    {
        this.opacity = opacity;
        repaint();
    }

    /**
     * remove all dots from the list. (Note: after an unfortunate accident,
     * I have added a dialog to ask the user for confirmation.)
     */
    public void clearDots()
    {
        int result = JOptionPane.showConfirmDialog(this,"Are you sure?");
        if (JOptionPane.YES_OPTION == result)
        {
            // TODO - #? - allow the user to remove all the dots. (Hint: 1 line.)
            myDots.clear();
            repaint();
        }
    }

    /**
     * discard the loaded image
     */
    public void clearImage()
    {
        backgroundImage = null;
        repaint();
    }

    /**
     * if a dot is selected in MODE_EDIT, make update its angle to the given angle;
     * if we are in MODE_ADD, update the defaultAngle.
     * @param a - an angle value for this dot.
     */
    public void updateAngleFromControl(double a)
    {
        if (MODE_EDIT == mode && selectedIndex>=0)
        {
            // TODO - #? - update the selected dot's angle to a. (Hint: 1 line)
            myDots.get(selectedIndex).setAngle(a);
            // ----------
            repaint();
        }
        if (MODE_ADD == mode)
        {
            defaultAngle = a;
        }
        requestFocus();
    }

    /**
     * if we are in MODE_EDIT and a point is selected, remove it
     *    from the list of dots;
     * if we are in MODE_ADD, remove the last dot.
     */
    public void removePoint()
    {
        if (MODE_EDIT == mode && selectedIndex>=0)
        {
            // TODO - #? get rid of the selected dot.
            myDots.remove(selectedIndex);
            setSelectedIndex(-1);
            repaint();
        }
        if (MODE_ADD == mode && myDots.size() > 0)
        {
            // TODO - #? get rid of the last dot.
            myDots.remove(myDots.size() - 1);
            repaint();
        }
    }

    /**
     * If we are in MODE_EDIT and a point is selected, add
     * another point immediately after the selected one.
     */
    public void insertPoint()
    {
        if (MODE_EDIT == mode && selectedIndex >= 0)
        {
            CtdPoint pt;
            // TODO: #? - if the selectedIndex refers to the last dot,
            //              make "pt" become a new dot near it.
            //            otherwise, make "pt" be a new dot halfway between
            //              this dot and the next one.
            //            then insert pt into the list of dots after the selected dot.
            if (selectedIndex == myDots.size() - 1 || myDots.get(selectedIndex + 1).getX() < 0) {
                CtdPoint oldPt = myDots.get(selectedIndex);
                if (myDots.get(selectedIndex).getX() >= 0) {
                    pt = new CtdPoint(oldPt.getX() + 10, oldPt.getY() + 10, 10);
                } else {
                    pt = new CtdPoint((-1 * oldPt.getX()) + 10, oldPt.getY() + 10, 10);
                }
                if (selectedIndex == myDots.size() - 1)
                {
                    myDots.add(pt);
                }
                else
                {
                    myDots.add(selectedIndex + 1, pt);
                }
            }
            else
            {
                CtdPoint dotBefore = myDots.get(selectedIndex);
                CtdPoint dotAfter = myDots.get(selectedIndex + 1);
                int newPtX = (dotBefore.getX() + dotAfter.getX())/2;
                if (myDots.get(selectedIndex).getX() < 0)
                {
                    newPtX = ((-1 * dotBefore.getX()) + dotAfter.getX())/2;
                }
                int newPtY = (dotBefore.getY() + dotAfter.getY())/2;
                CtdPoint newPtToAdd = new CtdPoint(newPtX, newPtY, dotBefore.getAngle());
                myDots.add(selectedIndex + 1, newPtToAdd);
            }

            //-----------------
            setSelectedIndex(selectedIndex+1);
            requestFocus();
            repaint();

        }
    }

    /**
     * changes the dot at selectedIndex between hollow and solid.
     */
    public void togglePoint()
    {
        if (MODE_ADD == mode || selectedIndex<0)
            return;
        // TODO - #? flip the sign of the x value of the dot at selectedIndex on the list.
        int newXValue = -1 * myDots.get(selectedIndex).getX();
        myDots.get(selectedIndex).setX(newXValue);

        // ---------------------
        repaint();
        requestFocus();
    }


// ----------------------------------- End GUI responses
// ----------------------------------- Calculations about points, segments

    /**
     * Cycles through all N dots in myDots and generates a list representing whether the line
     * starting at index has a conflict with another line in the list.
     * @return - a list of N-1 booleans.
     */
    public ArrayList<Boolean> getConflicts()
    {
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        for (int i = 0; i<myDots.size()-1; i++)
            result.add(false);

        for (int i = 0; i<myDots.size()-1; i++)
        {
            if (myDots.get(i+1).getX()<0)
                continue; // this is a jump  - no line.
            for (int j = i+1; j<myDots.size()-1;j++)
            {
                // detect whether the line segment starting at i interferes with the line segment starting at j.
                if (myDots.get(j+1).getX()<0)
                    continue; // this is a jump  - no line
                if (j == i+1)
                {
                    // this means j is the endpoint after i. We need to see whether it has doubled back on itself.
                    if (pointOnLineSegment(myDots.get(i),myDots.get(j),myDots.get(j+1)))
                    {
                        result.set(i,true);
                        result.set(j,true);
                    }
                }
                else
                {
                    if (segmentsOverlap(myDots.get(i),myDots.get(i+1),myDots.get(j),myDots.get(j+1)))
                    {
                        result.set(i,true);
                        result.set(j,true);
                    }
                }

            }

        }
        return result;
    }


    /**
     * determines whether the line from the last dot in myDots to tempdot would conflict with any of the internal
     * lines in myDots.
     * @param tempDot - the dot under consideration.
     * @return - whether there is a conflict.
     */
    public boolean isTempDotAConflict(CtdPoint tempDot)
    {
        if (tempDot.getX() < 0) // if this is a jump, then don't worry!
            return false;

        CtdPoint p3 = myDots.get(myDots.size()-1); // last dot
        for (int i=0; i<myDots.size()-1; i++)
        {
            CtdPoint p1 = myDots.get(i);
            CtdPoint p2 = myDots.get(i+1);
            if (p2.getX()<0) // if this is a "jump," don't worry.
                continue;
            if (p2 == p3)
            {
                if (pointOnLineSegment(p1, p3, tempDot))
                {
                    return true;
                }
            }
            else
            if (segmentsOverlap(p1,p2,p3,tempDot))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * determines whether the line point -> p1 intersects p1 -> p2 anywhere other than at p1.
     * @param point
     * @param p1
     * @param p2
     * @return
     */
    public boolean pointOnLineSegment(CtdPoint point, CtdPoint p1, CtdPoint p2)
    {
        int a = Math.abs(point.getX());
        int b = point.getY();
        int c = Math.abs(p1.getX());
        int d = p1.getY();
        int e = Math.abs(p2.getX());
        int f = p2.getY();

        // if p1 == p2, then return whether point = p1.
        if (c==e && d == f)
            return (a==c && b==d);

        // if p1 -> p2 is vertical, return whether point has same x value and is on same side of p1 as p2 is.
        if (c == e)
            return (a == c) && ((1.0*b-d)/(f-d) >= 0);

        // if p1 -> p2 is horizontal, return whether point has same y value and is on same side of p1 as p2 is.
        if (d == f)
            return (b == d) && ((1.0*a-c)/(e-c) >= 0);


        // otherwise, we are at a diagonal.
        // find how many multiples of the line segment a is from c and b is from d.
        double sx = (1.0*a-c)/(e-c);
        double sy = (1.0*b-d)/(f-d);

        // are we collinear within 0.1 (rounding error)?
        // if so, let's check whether sx >=0.
        return (Math.abs(sx-sy)<0.1)&&(sx>=0);
    }


    /**
     * determines whether line segment p1-p2 intersects
     * with line segment p3-p4.
     * @param p1 start of first segment
     * @param p2 end of first segment
     * @param p3 start of second segment
     * @param p4 end of second segment
     * @return  whether line segments p1-p2 and p3-p4 intersect.
     */
    public boolean segmentsOverlap(CtdPoint p1, CtdPoint p2, CtdPoint p3, CtdPoint p4)
    {
        int a = Math.abs(p1.getX());
        int b = p1.getY();
        int c = Math.abs(p2.getX());
        int d = p2.getY();
        int e = Math.abs(p3.getX());
        int f = p3.getY();
        int g = Math.abs(p4.getX());
        int h = p4.getY();
        // Consider the boxes with opposite corners p1,p2 and p3,p4.
        //   If these boxes don't overlap, then the line segments can't
        //   overlap. This is faster & easier to determine.
        if ((Math.min(a,c)>Math.max(e,g)) ||
                (Math.max(a,c)<Math.min(e,g))||
                (Math.min(b,d)>Math.max(f,h))||
                (Math.max(b,d)<Math.min(f,h)))
            return false;

        int denominator = (d-b)*(g-e)-(c-a)*(h-f);
        if (denominator == 0)
        {
            // either slopes match, or one line is horizontal. and
            // the other is vertical. (denominator = 0-0)
            if ((d-b)*(g-e) == 0)
            {
                // lines are both either horizontal or vertical, so if they
                // passed the box test, they cross.
                return true;
            }
            // so, they're parallel. Now let's see whether
            //  they are collinear.
            // note: if (d-b) or (c-a) were zero, then previous "if" would
            //    have returned true.
            double sx = (1.0*e-a)/(c-a);
            double sy = (1.0*f-b)/(d-b);
            // are we collinear within 0.001 (rounding error)?
            // since we're within the same box, this determines whether we touch.
            return (Math.abs(sx-sy)<0.001);
        }
        else // denominator is non-zero, so lines aren't parallel.
        {
            // consider where lines cross. "S" is fraction of
            //  way from p1 to p2 where this crossing is.
            //  "T" is fraction of way from p3 to p4 where they
            //   cross.
            double s = (1.0*(g-e)*(f-b)-(h-f)*(e-a))/denominator;
            double t = (1.0*(c-a)*(f-b)-(d-b)*(e-a))/denominator;
            // for line segments to intersect, both s and t must
            // fall between 0 and 1.
            return (s>=0) && (s<=1) && (t>=0) && (t<=1);
        }

    }

    /**
     * finds the index of the closest dot to the given (x,y), or
     * -1 if none are closer than √SELECTION_RADIUS_SQUARED_THRESHOLD.
     * @param x - x coordinate of point of interest
     * @param y - y coordinate of point of interest
     * @return the index of the closest point, or -1, if none are close.
     */
    public int getClosestPointTo(int x, int y)
    {
        int index = -1;
        double closest_d2 = SELECTION_RADIUS_SQUARED_THRESHOLD;
        for (int i = 0; i<myDots.size(); i++)
        {
            double d = Math.pow(Math.abs(myDots.get(i).getX())-x,2)+
                    Math.pow(myDots.get(i).getY()-y,2);
            if (d<closest_d2)
            {
                closest_d2 = d;
                index = i;
            }

        }
        return index;
    }
// -------------------------------- end calculations section

// -------------------------------- MouseListener and MouseMotionListener
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
        if (MODE_ADD == mode)
        {
            if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0)
                tempDot = new CtdPoint(e.getX(), e.getY(), defaultAngle);
            else
                tempDot = new CtdPoint(-e.getX(), e.getY(), defaultAngle);
        }
        else
        {
            setSelectedIndex(getClosestPointTo(e.getX(),e.getY()));

        }
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
        if (mode == MODE_ADD)
        {
            if ((e.getModifiersEx()& MouseEvent.SHIFT_DOWN_MASK) ==0)
                myDots.add(new CtdPoint(e.getX(), e.getY(), defaultAngle));
            else
                myDots.add(new CtdPoint(-e.getX(), e.getY(), defaultAngle));
            repaint();
        }
        tempDot = null;
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
        if (MODE_ADD == mode)
        {
            if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0)
                tempDot = new CtdPoint(e.getX(), e.getY(), defaultAngle);
            else
                tempDot = new CtdPoint(-e.getX(), e.getY(), defaultAngle);
            repaint();
        }
        if (MODE_EDIT == mode)
        {
            if (selectedIndex != -1)
            {
                if (myDots.get(selectedIndex).getX()<0)
                    myDots.get(selectedIndex).setX(-1*e.getX());
                else
                    myDots.get(selectedIndex).setX(e.getX());
                myDots.get(selectedIndex).setY(e.getY());
                repaint();
            }

        }
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
    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (MODE_ADD == mode || selectedIndex <0)
            return;
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            // TODO #? - move the selected dot up one pixel.
            int newYValue = -1 + myDots.get(selectedIndex).getY();
            myDots.get(selectedIndex).setY(newYValue);
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            // TODO #? - move the selected dot down one pixel.
            int newYValue = 1 + myDots.get(selectedIndex).getY();
            myDots.get(selectedIndex).setY(newYValue);
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            // TODO #? - move the selected dot left one pixel.
            int newXValue = myDots.get(selectedIndex).getX();
            if (newXValue >= 0)
            {
                newXValue = newXValue - 1;
            }
            else
            {
                newXValue = newXValue + 1;
            }
            myDots.get(selectedIndex).setX(newXValue);
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            // TODO #? - move the selected dot right one pixel.
            int newXValue = myDots.get(selectedIndex).getX();
            if (newXValue >= 0)
            {
                newXValue = newXValue + 1;
            }
            else
            {
                newXValue = newXValue - 1;
            }
            myDots.get(selectedIndex).setX(newXValue);
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_PERIOD)
        {
            selectNextDot();
        }
        if (e.getKeyCode() == KeyEvent.VK_COMMA)
        {
            selectPreviousDot();
        }
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
