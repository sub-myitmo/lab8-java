package client.gui;

import client.Client;
import client.helpers.CommunicationControl;
import common.models.StudyGroup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Stack;

public class VisualTable extends JFrame {

    private HashMap<Integer, String> owners;
    private HashMap<AnimatedCircle, Long> circles;
    private ResourceBundle messages = ResourceBundle.getBundle("client.gui.gui", UserSettings.getInstance().getSelectedLocale());

    private Client client;
    private CommunicationControl communicationControl;
    private DefaultTableModel tableModel;
    private JTable table;

    public VisualTable(Client client, CommunicationControl communicationControl, ArrayList<Stack<StudyGroup>> listOfStacks) {
        this.client = client;
        this.communicationControl = communicationControl;
        circles = new HashMap<>();

        owners = new HashMap<>();
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{messages.getString("ownerID"), messages.getString("ownerName")}) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        // Создаем таблицу и устанавливаем модель
        table = new JTable(tableModel);

        // Создаем панель прокрутки и добавляем таблицу
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 10));

        CirclesPanel circlesPanel = new CirclesPanel(circles, listOfStacks, client, communicationControl);
        getContentPane().add(circlesPanel);

        // Размещаем панель прокрутки в правой части окна
        getContentPane().add(scrollPane, BorderLayout.WEST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);


        JButton openMainFrameButton = new JButton(messages.getString("openDataTable"));

        // Добавьте обработчик событий, который открывает окно MainFrame при нажатии на кнопку
        openMainFrameButton.addActionListener(e -> {
            MainWindow mainFrame = new MainWindow(client, communicationControl);
            mainFrame.setVisible(true);
            dispose();
        });

        // Создайте панель с FlowLayout для размещения кнопки в верхнем правом углу
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonPanel.add(openMainFrameButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(circlesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // В основной контейнер
        getContentPane().add(mainPanel);

        // таймер на 2 секунды
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData(MainWindow.getStudyGroups());
            }
        });
        timer.start();
    }

    public void updateData(Stack<StudyGroup> stackStudyGroups) {
        // Очищаем коллекции
        owners.clear();
        circles.clear();

        int countOwners = 0;
        for (StudyGroup studyGroup : stackStudyGroups) {
            if (!owners.containsValue(studyGroup.getOwner().getUsername())) {
                owners.put(countOwners, studyGroup.getOwner().getUsername());
                countOwners++;
            }
        }

        // Очищаем модель таблицы и заполняем новыми данными
        tableModel.setRowCount(0);
        for (HashMap.Entry<Integer, String> entry : owners.entrySet()) {
            Integer id = entry.getKey();
            String owner = entry.getValue();
            tableModel.addRow(new Object[]{id, owner});
        }

        for (StudyGroup studyGroup : stackStudyGroups) {
            int originalX = studyGroup.getCoordinates().getX() + 50;
            double originalY = studyGroup.getCoordinates().getY() + 50;

            long radius;
            if (studyGroup.getStudentsCount() < 50) {
                radius = 20;
            } else if (studyGroup.getStudentsCount() < 200) {
                radius = 40;
            } else {
                radius = 60;
            }

            String username = studyGroup.getOwner().getUsername();
            int colorIndex = getKeyByUsername(owners, username);
            circles.put(new AnimatedCircle(originalX, originalY, (int) radius, colorIndex, studyGroup.getName()), studyGroup.getId());
        }

        // После обновления данных в таблице и списка кругов, нужно вызвать repaint для обновления GUI
        repaint();


    }


    public static Integer getKeyByUsername(HashMap<Integer, String> map, String username) {
        for (HashMap.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
