package client.gui;

import client.Client;
import common.actions.Request;
import common.models.StudyGroup;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import client.helpers.CommunicationControl;

class CirclesPanel extends JPanel {
    private HashMap<AnimatedCircle, Long> circles;
    private ArrayList<Stack<StudyGroup>> listOfStacks;
    private final Color[] colors = {Color.RED, Color.LIGHT_GRAY, Color.GREEN, Color.YELLOW};

    public CirclesPanel(HashMap<AnimatedCircle, Long> circles, ArrayList<Stack<StudyGroup>> listOfStacks, Client client, CommunicationControl communicationControl) {
        this.circles = circles;
        this.listOfStacks = listOfStacks;

        Timer timer = new Timer(70, e -> {
            for (AnimatedCircle circle : circles.keySet()) {
                if (circle.growing) {
                    circle.radius += 2;
                    if (circle.radius >= 70) {
                        circle.growing = false;
                    }
                } else {
                    circle.radius -= 2;
                    if (circle.radius <= 50) {
                        circle.growing = true;
                    }
                }
            }
            repaint();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                for (AnimatedCircle circle : circles.keySet()){
                    int x = e.getX();
                    int y = e.getY();
                    int distance = (int) Math.sqrt(Math.pow(x - circle.x, 2) + Math.pow(y - circle.y, 2));

                    if (distance <= circle.radius) {
                        // Действие при клике на круг
                        StudyGroup studyGroup = findStudyGroupByID(circles.get(circle));
                        if (studyGroup != null) {
                            EditStudyGroup editStudyGroup = new EditStudyGroup(communicationControl);
                            editStudyGroup.setInfo(studyGroup);
                            if (client.getCurrentUser().equals(studyGroup.getOwner())){
                                JButton saveButton;
                                saveButton = editStudyGroup.getSaveButton();
                                saveButton.addActionListener(e1 -> {
                                    try {
                                        client.sendRequest(new Request("update_by_id", String.valueOf(studyGroup.getId()), editStudyGroup.update(), client.getCurrentUser()));
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                });
                            } else{
                                editStudyGroup.setNonEditable();
                            }

                        }
                        break;
                    }
                }
            }
        });

        MouseAdapter ma = new MouseAdapter(){

            private Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                origin = new Point(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, CirclesPanel.this);
                    if (viewPort != null) {
                        int deltaX = origin.x - e.getX();
                        int deltaY = origin.y - e.getY();

                        Rectangle view = viewPort.getViewRect();
                        view.x += deltaX;
                        view.y += deltaY;

                        CirclesPanel.this.scrollRectToVisible(view);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                origin = null;
            }
        };

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
    }

    private StudyGroup findStudyGroupByID(long id) {
        for (Stack<StudyGroup> stackOfGroup : listOfStacks){
            for (StudyGroup studyGroup : stackOfGroup) {
                if (studyGroup.getId() == id) {
                    return studyGroup;
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (AnimatedCircle circle : circles.keySet()) {
            g2d.setColor(colors[circle.colorIndex]);
            g2d.fillOval(circle.x - circle.radius, (int) circle.y - circle.radius, 2 * circle.radius, 2 * circle.radius);
            g2d.setColor(Color.BLACK);


            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int textWidth = metrics.stringWidth(circles.get(circle) + " " + circle.strStudyGroup + " " + circle.colorIndex);
            int textHeight = metrics.getHeight();
            g.setFont(g.getFont());
            g.drawString(circles.get(circle) + " " + circle.strStudyGroup + " " + circle.colorIndex, circle.x - textWidth / 2, (int) circle.y - textHeight / 2 + metrics.getAscent());
        }
    }

}
