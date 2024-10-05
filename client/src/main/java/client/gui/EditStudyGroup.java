package client.gui;

import client.helpers.CommunicationControl;
import common.actions.Console;
import common.actions.GroupMask;
import common.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditStudyGroup extends JFrame {
    private ArrayList<JTextField> fields = new ArrayList<>();
    private ArrayList<JComboBox> boxes = new ArrayList<>();
    private ArrayList<JLabel> labels = new ArrayList<>();
    private JButton saveButton;
    private CommunicationControl communicationControl;
    private ResourceBundle messages = ResourceBundle.getBundle("client.gui.gui", UserSettings.getInstance().getSelectedLocale());


    public EditStudyGroup(CommunicationControl communicationControl) {
        this.communicationControl = communicationControl;
        String[] fieldNames = {"ID", messages.getString("name"), messages.getString("coordX"), messages.getString("coordY"),
                messages.getString("creationDate"), messages.getString("studentsCount"), messages.getString("expelledStudents"),
                messages.getString("transferredStudents"), messages.getString("semesterEnum"), messages.getString("namePerson"),
                messages.getString("birthday"), messages.getString("weight"), messages.getString("locX"), messages.getString("locY"),
                messages.getString("locZ")};

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(16, 2));

        for (int i = 0; i < fieldNames.length; i++) {
            JLabel label = new JLabel(fieldNames[i] + ":");
            JTextField field = new JTextField(10);
            if (i == 0 || i == 4) {
                field.setEditable(false);
            }
            if (i == 8) {
                labels.add(i, label);
                String[] commands = Semester.getNames();
                JComboBox<String> commandList = new JComboBox<>(commands);
                boxes.add(0, commandList);
                panel.add(label);
                panel.add(commandList);
            } else {
                if (i>8){fields.add(i-1, field);}
                else {fields.add(i, field);}

                labels.add(i, label);
                addLabelAndField(panel, label, field);
            }
        }

        saveButton = new JButton(messages.getString("save"));
        panel.add(saveButton);
        add(panel);


        // Other JFrame setup code
        setTitle("Study group");
        setVisible(true);
        setSize(1000, 600);
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Центрируем окно

    }

    private void addLabelAndField(JPanel panel, JLabel label, JTextField field) {
        panel.add(label);
        panel.add(field);
    }

    public void setInfo(StudyGroup studyGroup) {
        fields.get(0).setText(String.valueOf(studyGroup.getId()));
        fields.get(1).setText(studyGroup.getName());
        fields.get(2).setText(String.valueOf(studyGroup.getCoordinates().getX()));

        fields.get(3).setText(String.valueOf(studyGroup.getCoordinates().getY()));

        fields.get(4).setText(String.valueOf(studyGroup.getCreationDate()));

        fields.get(5).setText(String.valueOf(studyGroup.getStudentsCount()));

        fields.get(6).setText(String.valueOf(studyGroup.getExpelledStudents()));
        fields.get(7).setText(String.valueOf(studyGroup.getTransferredStudents()));
        boxes.get(0).setSelectedItem(String.valueOf(studyGroup.getSemesterEnum()));
        //fields.get(8).setText(String.valueOf(studyGroup.getSemesterEnum()));

        fields.get(8).setText(String.valueOf(studyGroup.getGroupAdmin().getName()));

        fields.get(9).setText(String.valueOf(studyGroup.getGroupAdmin().getBirthday()).substring(0, 10));
        fields.get(10).setText(String.valueOf(studyGroup.getGroupAdmin().getWeight()));

        fields.get(11).setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getX()));
        fields.get(12).setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getY()));
        fields.get(13).setText(String.valueOf(studyGroup.getGroupAdmin().getLocation().getZ()));

    }

    public void setNonEditable() {
        boxes.get(0).setEditable(false);
        fields.get(1).setEditable(false);
        fields.get(2).setEditable(false);
        fields.get(3).setEditable(false);
        fields.get(4).setEditable(false);
        fields.get(5).setEditable(false);
        fields.get(6).setEditable(false);
        fields.get(7).setEditable(false);
        fields.get(8).setEditable(false);
        fields.get(9).setEditable(false);
        fields.get(10).setEditable(false);
        fields.get(11).setEditable(false);
        fields.get(12).setEditable(false);
        fields.get(13).setEditable(false);
    }

    public void setEditable() {
        boxes.get(0).setEditable(true);
        fields.get(1).setEditable(true);
        fields.get(2).setEditable(true);
        fields.get(3).setEditable(true);
        fields.get(5).setEditable(true);
        fields.get(6).setEditable(true);
        fields.get(7).setEditable(true);
        fields.get(8).setEditable(true);
        fields.get(9).setEditable(true);
        fields.get(10).setEditable(true);
        fields.get(11).setEditable(true);
        fields.get(12).setEditable(true);
        fields.get(13).setEditable(true);
    }

    public GroupMask update() {
        String newName = communicationControl.setName(fields.get(1).getText());
        Coordinates coordinates = communicationControl.setCoordinates(fields.get(2).getText(), fields.get(3).getText());


        Long newStudentsCount = communicationControl.setStudentCount(fields.get(5).getText());
        Long newExpelledStudents = communicationControl.setExpelledStudents(fields.get(6).getText());
        Integer newTransferredStudents = communicationControl.setTransferredStudents(fields.get(7).getText());


        Semester newSemester = communicationControl.chooseSemester((String) boxes.get(0).getSelectedItem());

        Location location = communicationControl.setLocation(fields.get(11).getText(), fields.get(12).getText(), fields.get(13).getText());

        Person person = communicationControl.setPerson(fields.get(8).getText(),
                fields.get(9).getText(), fields.get(10).getText(), location);

        GroupMask mask = new GroupMask(newName, coordinates,
                newStudentsCount, newExpelledStudents, newTransferredStudents, newSemester, person);

        return mask;
    }

    public JButton getSaveButton() {
        return saveButton;
    }


}
