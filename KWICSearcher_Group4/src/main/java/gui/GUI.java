package gui;

import org.example.CorpusBuilder;
import xml.OutputToXml;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class GUI extends JFrame {
    private JComboBox numLeftNeighbours, numRightNeighbours;
    private JRadioButton caseSensitive, caseInsensitive;
    private JTextField link, search;
    private JTextArea resultArea;
    private JButton searchButton, submitButton, saveToXML;
    private final CorpusBuilder builder = new CorpusBuilder();
    private final OutputToXml xml = new OutputToXml();
    private boolean caseS;
    private int left, right, count;
    private String result;
    private JLabel lblResults;
    private boolean isUrlSet = false;

    public GUI() {
        setSize(1000, 800);
        setTitle("Quick Searcher");

        addWindowListener(new MyWindowListener());

        BorderLayout mainLayout = new BorderLayout();
        getContentPane().setLayout(mainLayout);

        setUpNorth();
        setUpCenter();
        setUpWest();

    }

    private void setUpNorth() {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        JPanel linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.X_AXIS));
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        northPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        link = new JTextField(40);
        link.setPreferredSize(new Dimension(200, 20));
        submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));
        submitButton.addActionListener(new submitButtonListener());

        JLabel urlLabel = new JLabel("URL/File: ");
        urlLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        JLabel wordLabel = new JLabel("Target word: ");
        wordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        search = new JTextField(40);
        search.setPreferredSize(new Dimension(50, 20));
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.addActionListener(new searchButtonListener());

        linkPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        linkPanel.add(urlLabel);
        linkPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        linkPanel.add(link);
        linkPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        linkPanel.add(submitButton);
        linkPanel.setBackground(new Color(135, 206, 250));

        searchPanel.add(wordLabel);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(search);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchButton);
        searchPanel.setBackground(new Color(135, 206, 250));

        northPanel.add(Box.createVerticalStrut(20));
        northPanel.add(linkPanel);
        northPanel.add(Box.createVerticalStrut(20));
        northPanel.add(searchPanel);

        northPanel.setBackground(new Color(135, 206, 250));
        // add the outermost panel to the main layout
        getContentPane().add(northPanel, BorderLayout.NORTH);
    }

    private void setUpCenter() {
        resultArea = new JTextArea();
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        lblResults = new JLabel("0 Result found");
        lblResults.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        lblResults.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(lblResults, BorderLayout.SOUTH);

    }

    private void setUpWest() {
        JPanel westPanel = new JPanel();
        BoxLayout westLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        westPanel.setLayout(westLayout);
        Dimension textFieldDim = new Dimension(50, 20);
        westPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        ButtonGroup colorGroup = new ButtonGroup();
        caseSensitive = new JRadioButton("Case Sensitive");
        caseSensitive.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        caseSensitive.setBackground(new Color(255, 165, 0));
        caseSensitive.addActionListener(new cSListener());
        caseInsensitive = new JRadioButton("Case Insensitive");
        caseInsensitive.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        caseInsensitive.setBackground(new Color(255, 165, 0));
        caseInsensitive.addActionListener(new cIListener());
        colorGroup.add(caseSensitive);
        colorGroup.add(caseInsensitive);
        caseSensitive.setSelected(true);
        caseS = true;


        JLabel numLeft = new JLabel("Number of Left Neighbours:");
        numLeft.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        numLeftNeighbours = new JComboBox(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        numLeftNeighbours.setSelectedIndex(0);
        numLeftNeighbours.setAlignmentX(Component.LEFT_ALIGNMENT);
        numLeftNeighbours.setPreferredSize(textFieldDim);
        numLeftNeighbours.setMaximumSize(textFieldDim);

        JLabel numRight = new JLabel("Number of Right Neighbours:");
        numRight.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        numRightNeighbours = new JComboBox(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        numRightNeighbours.setSelectedIndex(0);
        numRightNeighbours.setAlignmentX(Component.LEFT_ALIGNMENT);
        numRightNeighbours.setPreferredSize(textFieldDim);
        numRightNeighbours.setMaximumSize(textFieldDim);

        saveToXML = new JButton("Save To XML");
        saveToXML.setMaximumSize(new Dimension(500, 500));
        saveToXML.addActionListener(new saveToXmlListener());

        westPanel.add(Box.createVerticalGlue());
        westPanel.add(caseSensitive);
        westPanel.add(Box.createVerticalStrut(5));
        westPanel.add(caseInsensitive);
        westPanel.add(Box.createVerticalStrut(25));
        westPanel.add(numLeft);
        westPanel.add(Box.createVerticalStrut(5));
        westPanel.add(numLeftNeighbours);
        westPanel.add(Box.createVerticalStrut(5));
        westPanel.add(numRight);
        westPanel.add(Box.createVerticalStrut(5));
        westPanel.add(numRightNeighbours);
        westPanel.add(Box.createVerticalStrut(25));
        westPanel.add(saveToXML);
        westPanel.add(Box.createVerticalGlue());

        westPanel.setBackground(new Color(255, 165, 0));

        getContentPane().add(westPanel, BorderLayout.WEST);
    }

    /** The following three methods is from the user MockerTim from
     * the link <a href="https://stackoverflow.com/questions/6530105/highlighting-text-in-java">...</a>
     */
    // Creates highlights around all occurrences of pattern in textComp
    public void highlight(JTextComponent textComp, String pattern) {
        // First remove all old highlights
        removeHighlights(textComp);

        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());

            int pos = 0;
            // Search for pattern
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                pos += pattern.length();
            }

        } catch (BadLocationException e) {
        }
    }

    // Removes only our private highlights
    public void removeHighlights(JTextComponent textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (int i = 0; i < hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    // An instance of the private subclass of the default highlight painter
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);

    // A private subclass of the default highlight painter
    class MyHighlightPainter
            extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) { super(color); }
    }

    private class cSListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == caseSensitive) {
                if (caseSensitive.isSelected()) {
                    caseS = true;
                } else {
                    caseS = false;
                }
            }
        }
    }

    private class cIListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == caseInsensitive) {
                if (caseInsensitive.isSelected()) {
                    caseS = false;
                }else {
                    caseS = true;
                }
            }
        }
    }


    private class searchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == searchButton) {
                if (!isUrlSet) {
                    JOptionPane.showMessageDialog(getContentPane(), "Please enter a URL first.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    builder.resetCount();

                    String word = search.getText();
                    left = (Integer) numLeftNeighbours.getSelectedItem();
                    right = (Integer) numRightNeighbours.getSelectedItem();
                    result = builder.search(word, left, right, caseS);
                    resultArea.setText(result);

                    highlight(resultArea, " " + word + " " );

                    count = builder.countResult();
                    if (count< 1) {
                        lblResults.setText(0 +" Result found");
                    } else if (count == 1) {
                        lblResults.setText(1 + " Result  found");
                    }else if (count > 1) {
                        lblResults.setText(count + " Results  found");}

                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(getContentPane(), "Error: " + e1.getMessage());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid input.");
                }
            }
        }
    }

    private class submitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == submitButton) {
                try {
                    String urlText = link.getText();
                    String wikiUrl = "https://en.wikipedia.org/wiki";
                    if (urlText.startsWith(wikiUrl)) {
                        builder.setUrl(urlText);
                        isUrlSet = true;
                        JOptionPane.showMessageDialog(getContentPane(), "URL set successfully.");
                    }
                    else if (!urlText.isEmpty()) {
                        Scanner inputStream = new Scanner(new File(urlText));
                        StringBuilder text = new StringBuilder();
                        while (inputStream.hasNext()) { text.append(inputStream.next()); }
                        builder.setText(text.toString());
                    }
                    else {
                        JOptionPane.showMessageDialog(getContentPane(), "Please enter a url for Wikipedia english page or a valid file name!");
                    }
                } catch (IllegalArgumentException e1) {
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid URL!");
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid File Name!");
                }
            }
        }
    }

    private class saveToXmlListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == saveToXML) {
                try {
                    if (result != null && !result.isEmpty()) {
                        String outFilename = JOptionPane.showInputDialog(getContentPane(), "Please input a path where you want to save your XML file:");
                        if (outFilename != null && !outFilename.isEmpty()) {
                            xml.createXmlWithInput(result, outFilename, builder.countResult());
                            JOptionPane.showMessageDialog(getContentPane(), "Saved XML to file successfully.");
                        } else {
                            JOptionPane.showMessageDialog(getContentPane(), "Please enter a valid filename", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(getContentPane(), "No results to save.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException e1) {
                    JOptionPane.showMessageDialog(getContentPane(), "Invalid input.");
                }
            }
        }
    }


    private static class MyWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setVisible(true);
    }
}