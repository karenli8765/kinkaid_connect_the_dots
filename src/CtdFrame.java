import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CtdFrame extends JFrame implements ActionListener, ChangeListener
{

    private CtdViewPanel viewPanel;
    private JButton loadDotsButton, saveButton,
            openImageButton, clearDotsButton,
            clearImageButton, saveScreenButton,
            removeDotButton, insertDotButton, toggleDotButton,
            nextButton, prevButton;
    private JCheckBox linesToggle;
    private JToggleButton editButton, addButton;
    private AnglePanel angleSelector;
    private JSlider opacitySlider;

    public CtdFrame()
    {
        super("Connect the Dots");
        setSize(800, 800);

        buildGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);

    }

    private void buildGUI()
    {

        Box toolPanel = generateToolPanel();
        viewPanel = new CtdViewPanel();
        viewPanel.setParent(this);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(toolPanel, BorderLayout.NORTH);
        this.getContentPane().add(viewPanel, BorderLayout.CENTER);

    }

    private Box generateToolPanel()
    {
        Box viewToolPanel = Box.createHorizontalBox();
        viewToolPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

        viewToolPanel.add(generateFileBox());
        viewToolPanel.add(generateAppearanceBox());
        viewToolPanel.add(generateModeBox());
        viewToolPanel.add(generateDotBox());
        viewToolPanel.add(Box.createHorizontalGlue());

        return viewToolPanel;
    }

    private Box generateFileBox()
    {
        Box filePanel = Box.createVerticalBox();

        loadDotsButton = new JButton("Load Dots");
        loadDotsButton.addActionListener(this);
        filePanel.add(loadDotsButton);

        openImageButton = new JButton("Load Image");
        openImageButton.addActionListener(this);
        filePanel.add(openImageButton);

        saveButton = new JButton("Save Dots");
        saveButton.addActionListener(this);
        filePanel.add(saveButton);

        saveScreenButton = new JButton("Export");
        saveScreenButton.addActionListener(this);
        filePanel.add(saveScreenButton);

        filePanel.setBorder(BorderFactory.createTitledBorder("File Operations"));
        return filePanel;
    }

    private Box generateAppearanceBox()
    {
        Box appearancePanel = Box.createVerticalBox();

        Box clearPanel = Box.createHorizontalBox();
        clearDotsButton = new JButton("Clear Dots");
        clearDotsButton.addActionListener(this);
        clearPanel.add(clearDotsButton);

        clearImageButton = new JButton("Clear Image");
        clearImageButton.addActionListener(this);
        clearPanel.add(clearImageButton);
        clearPanel.add(Box.createHorizontalGlue());
        appearancePanel.add(clearPanel);

        Box sliderPanel = Box.createHorizontalBox();
        sliderPanel.add(new JLabel("Image Transparency"));
        opacitySlider = new JSlider(0, 255, 128);
        opacitySlider.addChangeListener(this);
        sliderPanel.add(opacitySlider);
        appearancePanel.add(sliderPanel);


        Box linesPanel = Box.createHorizontalBox();
        linesPanel.add(new JLabel("Show Lines"));
        linesToggle = new JCheckBox();
        linesToggle.setSelected(true);
        linesToggle.addActionListener(this);
        linesPanel.add(linesToggle);
        linesPanel.add(Box.createHorizontalGlue());
        appearancePanel.add(linesPanel);

        appearancePanel.add(Box.createVerticalGlue());

        appearancePanel.setBorder(BorderFactory.createTitledBorder("Appearance"));
        return appearancePanel;
    }


    private Box generateModeBox()
    {
        Box addEditPanel = Box.createVerticalBox();
        addButton = new JToggleButton("Add");
        editButton = new JToggleButton("Edit");
        ButtonGroup grp = new ButtonGroup();
        addButton.setSelected(true);
        grp.add(addButton);
        grp.add(editButton);
        addEditPanel.add(addButton);
        addEditPanel.add(editButton);
        editButton.addActionListener(this);
        addButton.addActionListener(this);
        addEditPanel.setBorder(BorderFactory.createTitledBorder("Mode"));
        addEditPanel.add(Box.createVerticalGlue());
        return addEditPanel;
    }

    private Box generateDotBox()
    {
        Box dotPanel = Box.createHorizontalBox();
        angleSelector = new AnglePanel();
        angleSelector.addActionListener(this);
        angleSelector.setMinimumSize(new Dimension(50, 50));
        angleSelector.setPreferredSize(new Dimension(100, 100));
        angleSelector.setMaximumSize(new Dimension(100, 100));
        dotPanel.add(angleSelector);

        Box dotSubPanel = Box.createVerticalBox();
        removeDotButton = new JButton("Remove Dot");
        removeDotButton.addActionListener(this);
        removeDotButton.setAlignmentX(CENTER_ALIGNMENT);
        removeDotButton.setEnabled(false);
        dotSubPanel.add(removeDotButton);

        insertDotButton = new JButton("Insert Dot");
        insertDotButton.addActionListener(this);
        insertDotButton.setAlignmentX(CENTER_ALIGNMENT);
        insertDotButton.setEnabled(false);
        dotSubPanel.add(insertDotButton);

        toggleDotButton = new JButton("o/•");
        toggleDotButton.addActionListener(this);
        toggleDotButton.setAlignmentX(CENTER_ALIGNMENT);
        toggleDotButton.setEnabled(false);
        dotSubPanel.add(toggleDotButton);

        JPanel prevNextBox = new JPanel();
        prevButton = new JButton("<");
        prevButton.addActionListener(this);
        prevNextBox.add(prevButton);
        prevButton.setEnabled(false);
        nextButton = new JButton(">");
        nextButton.addActionListener(this);
        prevNextBox.add(nextButton);
        nextButton.setEnabled(false);
        prevNextBox.setAlignmentX(CENTER_ALIGNMENT);
        dotSubPanel.add(prevNextBox);

        dotPanel.add(dotSubPanel);
        dotPanel.add(Box.createVerticalGlue());
        dotPanel.setBorder(BorderFactory.createTitledBorder("Edit Dot"));
        return dotPanel;
    }

    public void angleHasChangedInPanel(double a)
    {
        angleSelector.setAngle(a);
    }

    public void setPointIsSelected(boolean b)
    {
        removeDotButton.setEnabled(b && editButton.isSelected());
        insertDotButton.setEnabled(b && editButton.isSelected());
        toggleDotButton.setEnabled(b && editButton.isSelected());
        prevButton.setEnabled(b && editButton.isSelected());
        nextButton.setEnabled(b && editButton.isSelected());
    }

    public void actionPerformed(ActionEvent aEvt)
    {
        if (aEvt.getSource() == loadDotsButton)
            viewPanel.loadButtonPressed();
        if (aEvt.getSource() == saveButton)
            viewPanel.saveButtonPressed();
        if (aEvt.getSource() == openImageButton)
            viewPanel.doLoadImage();
        if (aEvt.getSource() == linesToggle)
            viewPanel.setShouldShowLines(linesToggle.isSelected());
        if (aEvt.getSource() == addButton)
            viewPanel.setMode(CtdViewPanel.MODE_ADD);
        if (aEvt.getSource() == editButton)
            viewPanel.setMode(CtdViewPanel.MODE_EDIT);
        if (aEvt.getSource() == clearDotsButton)
            viewPanel.clearDots();
        if (aEvt.getSource() == clearImageButton)
            viewPanel.clearImage();
        if (aEvt.getSource() == angleSelector)
            viewPanel.updateAngleFromControl(angleSelector.getAngle());
        if (aEvt.getSource() == saveScreenButton)
            viewPanel.doSaveScreen();
        if (aEvt.getSource() == removeDotButton)
            viewPanel.removePoint();
        if (aEvt.getSource() == insertDotButton)
            viewPanel.insertPoint();
        if (aEvt.getSource() == toggleDotButton)
            viewPanel.togglePoint();
        if (aEvt.getSource() == prevButton)
            viewPanel.selectPreviousDot();
        if (aEvt.getSource() == nextButton)
            viewPanel.selectNextDot();
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource() == opacitySlider)
        {
            viewPanel.setOpacity(opacitySlider.getValue());
        }
    }

}
