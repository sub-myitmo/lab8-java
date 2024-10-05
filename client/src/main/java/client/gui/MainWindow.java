package client.gui;

import client.Client;
import client.StartClient;
import client.helpers.CommunicationControl;
import common.actions.Console;
import common.actions.Request;
import common.actions.Response;
import common.models.StudyGroup;
import client.gui.sort.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MainWindow extends JFrame {
    private final Map<Integer, StudyGroup> rowToObjectMap = new HashMap<>();
    private static JTable table;
    private ArrayList<Stack<StudyGroup>> listOfStackStudyGroup;
    public static int rowCounter = 0;
    public String selectedCommand;
    JButton saveButton;
    boolean actionBool = true;
    private static Stack<StudyGroup> takeArray;
    private ResourceBundle messages = ResourceBundle.getBundle("client.gui.gui", UserSettings.getInstance().getSelectedLocale());


    public MainWindow(Client client, CommunicationControl communicationControl) {
        setTitle(messages.getString("mainWindow"));
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);


        StudyGroupSortingPanel studyGroupSortingPanel = new StudyGroupSortingPanel();

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.setBackground(Color.WHITE);
        panel1.setBorder(BorderFactory.createTitledBorder(""));

        JLabel userLabel = new JLabel("USER: " + client.getCurrentUser().getUsername());
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        userLabel.setBorder(border);

        JButton goToVisualisationTableButton = new JButton(messages.getString("visualizeObject"));
        goToVisualisationTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VisualTable visualTable = new VisualTable(client, communicationControl, listOfStackStudyGroup);
                visualTable.setVisible(true);
                dispose();
            }
        });
        JButton logOut = new JButton(messages.getString("logOut"));
        logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] arrayStart = new String[]{String.valueOf(StartClient.port)};
                new StartClient().main(arrayStart);
                dispose();
            }
        });
        JButton settings = new JButton(messages.getString("settings"));
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsForm(client, communicationControl);
                dispose();
            }
        });

        panel1.add(userLabel, BorderLayout.WEST);
        panel1.add(logOut, BorderLayout.SOUTH);
        panel1.add(settings, BorderLayout.SOUTH);
        panel1.add(goToVisualisationTableButton, BorderLayout.SOUTH);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel2.setBackground(Color.LIGHT_GRAY);
        panel2.setBorder(BorderFactory.createTitledBorder("Study groups table"));
        String[] columnNames = {"id", messages.getString("name"), messages.getString("coordX"), messages.getString("coordY"),
                messages.getString("creationDate"), messages.getString("studentsCount"), messages.getString("expelledStudents"),
                messages.getString("transferredStudents"), messages.getString("semesterEnum"), messages.getString("namePerson"),
                messages.getString("birthday"), messages.getString("weight"), messages.getString("locX"), messages.getString("locY"),
                messages.getString("locZ"), "editable"};
        //System.out.println(columnNames.length);
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (column == this.getColumnCount() - 1) { // Последняя ячейка в строке
                    StudyGroup studyGroup = rowToObjectMap.get(row);
                    if (studyGroup.getOwner().equals(client.getCurrentUser())) {
                        comp.setBackground(Color.GREEN);
                    } else {
                        comp.setBackground(Color.RED);
                    }
                } else {
                    comp.setBackground(Color.WHITE);
                }

                return comp;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                actionBool = false;
                if (e.getClickCount() == 1) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    if (row != -1) {
                        EditStudyGroup editStudyGroup = new EditStudyGroup(communicationControl);
                        // код для перехода на другой
                        StudyGroup studyGroup = rowToObjectMap.get(row);
                        editStudyGroup.setInfo(studyGroup);
                        if (!studyGroup.getOwner().equals(client.getCurrentUser())) {
                            editStudyGroup.setNonEditable();
                        } else {
                            editStudyGroup.setEditable();
                        }
                        saveButton = editStudyGroup.getSaveButton();

                        saveButton.addActionListener(e1 -> {
                            try {
                                System.out.println(studyGroup.getId());
                                client.sendRequest(new Request("update_by_id", String.valueOf(studyGroup.getId()), editStudyGroup.update(), client.getCurrentUser()));
                                Response res = client.receiveResponse();
                                System.out.println(res.getResponse() + " " + res.getIsGoodResponse());
                                editStudyGroup.dispose();
                            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        actionBool = true;
                    } else {
                        System.out.println();
                    }
                }
            }
        });

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(() -> {
            if (actionBool) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        this.listOfStackStudyGroup = new ArrayList<>();

                        // Обновление данных в таблице
                        client.sendRequest(new Request("sendNewStack", "", client.getCurrentUser()));
                        Response response = client.receiveResponse();
                        if (response != null) {
                            try {
                                int count = (Integer) response.getResponseObject();
                                for (int i = 0; i < count; i++) {
                                    Response tempResponse = client.receiveResponse();
                                    if (tempResponse != null) {
                                        listOfStackStudyGroup.add((Stack<StudyGroup>) tempResponse.getResponseObject());
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        clearData(tableModel);
                        // Очистка модели данных таблицы

                        rowCounter = 0;
                        Stack<StudyGroup> newJoinedArray = new Stack<>();

                        for (Stack<StudyGroup> list : listOfStackStudyGroup) {
                            newJoinedArray.addAll(list);
                        }
                        setStudyGroups(newJoinedArray);

                        StudyGroupUtils.parameters = studyGroupSortingPanel.getParamaters();
                        newJoinedArray = StudyGroupUtils.sortAndFilterStudyGroups(newJoinedArray);
                        for (StudyGroup studyGroup : newJoinedArray) {
                            Date date = studyGroup.getCreationDate();
                            DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, messages.getLocale());
                            String text = format.format(date);
                            Object[] rowData = {String.valueOf(studyGroup.getId()), String.valueOf(studyGroup.getName()), String.valueOf(studyGroup.getCoordinates().getX()),
                                    String.valueOf(studyGroup.getCoordinates().getY()), text, String.valueOf(studyGroup.getStudentsCount()),
                                    String.valueOf(studyGroup.getExpelledStudents()), String.valueOf(studyGroup.getTransferredStudents()), String.valueOf(studyGroup.getSemesterEnum()),
                                    String.valueOf(studyGroup.getGroupAdmin().getName()), String.valueOf(studyGroup.getGroupAdmin().getBirthday()),
                                    String.valueOf(studyGroup.getGroupAdmin().getWeight()), String.valueOf(studyGroup.getGroupAdmin().getLocation().getX()),
                                    String.valueOf(studyGroup.getGroupAdmin().getLocation().getY()), String.valueOf(studyGroup.getGroupAdmin().getLocation().getZ())};

                            rowToObjectMap.put(tableModel.getRowCount(), studyGroup);
                            tableModel.addRow(rowData);
                        }

                        // Уведомление таблицы об изменении данных
                        tableModel.fireTableDataChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }, 0, 2, TimeUnit.SECONDS);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 300));
        scrollPane.setBackground(java.awt.Color.MAGENTA);
        panel2.add(scrollPane, BorderLayout.WEST);


        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel3.setBackground(java.awt.Color.WHITE);
        panel3.setBorder(BorderFactory.createTitledBorder(""));
        String[] commands = {"", "add", "clear", "info", "shuffle", "reorder", "print_field_ascending_students_count", "remove_by_id", "update_by_id", "execute_script", "print_ascending"};

        // Создаем выпадающий список и добавляем в панель
        JComboBox<String> commandList = new JComboBox<>(commands);

        panel3.add(commandList, BorderLayout.CENTER);
        JButton commandButton = new JButton(messages.getString("start"));

        commandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (selectedCommand) {
                    case "":
                        break;
                    case "add":
                        actionBool = false;

                        EditStudyGroup addStudyGroup = new EditStudyGroup(communicationControl);
                        saveButton = addStudyGroup.getSaveButton();
                        saveButton.addActionListener(e1 -> {
                            try {
                                client.sendRequest(new Request("add", "", addStudyGroup.update(), client.getCurrentUser()));
                                client.receiveResponse();
                            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            } finally {
                                addStudyGroup.dispose();
                            }

                        });
                        actionBool = true;
                        break;
                    case "clear":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("clear", "", null, client.getCurrentUser()));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        Response response4;
                        try {
                            response4 = client.receiveResponse();
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (response4 == null) {
                            JOptionPane.showMessageDialog(null, "Данные утеряны или сервер недоступен, повторите попытку позже!");
                        } else {

                            JOptionPane.showMessageDialog(null, response4.getResponse());
                        }
                        actionBool = true;
                        break;
                    case "info":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("info", "", null, client.getCurrentUser()));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                        Response response1;
                        try {
                            //do {
                            response1 = client.receiveResponse();


                            //} while (Objects.equals(response1, null));
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (response1 == null) {
                            JOptionPane.showMessageDialog(null, "Данные утеряны или сервер недоступен, повторите попытку позже!");
                        } else {
                            System.out.println(response1.getResponse() + " " + response1.getIsGoodResponse());
                            System.out.println(response1.getResponse());
                            JOptionPane.showMessageDialog(null, response1.getResponse());
                        }
                        actionBool = false;
                        break;
                    case "print_field_ascending_students_count":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("print_field_ascending_students_count", "", null, client.getCurrentUser()));
                            Response response = client.receiveResponse();
                            if (response == null) {
                                JOptionPane.showMessageDialog(null, "Данные утеряны или сервер недоступен, повторите попытку позже!");
                            } else {
                                System.out.println(response.getResponse());
                                JOptionPane.showMessageDialog(null, response.getResponse());
                            }
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        actionBool = true;
                        break;
                    case "print_ascending":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("print_ascending", "", null, client.getCurrentUser()));
                            Response response = client.receiveResponse();
                            System.out.println(response.getResponse());
                            //JOptionPane.showMessageDialog(null, response.getResponse());
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        actionBool = true;
                        break;
                    case "shuffle":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("shuffle", "", null, client.getCurrentUser()));
                            Response response = client.receiveResponse();
                            if (response == null) {
                                JOptionPane.showMessageDialog(null, "Данные утеряны или сервер недоступен, повторите попытку позже!");
                            } else {
                                System.out.println(response.getResponse());
                                JOptionPane.showMessageDialog(null, response.getResponse());
                            }
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        actionBool = true;
                        break;
                    case "reorder":
                        actionBool = false;
                        try {
                            client.sendRequest(new Request("reorder", "", null, client.getCurrentUser()));
                            Response response = client.receiveResponse();
                            if (response == null) {
                                JOptionPane.showMessageDialog(null, "Данные утеряны или сервер недоступен, повторите попытку позже!");
                            } else {
                                System.out.println(response.getResponse());
                                JOptionPane.showMessageDialog(null, response.getResponse());
                            }
                        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        actionBool = true;
                        break;
                    case "remove_by_id":
                        actionBool = false;
                        String inputRemId = JOptionPane.showInputDialog(null, messages.getString("insertArgument"));
                        try {
                            int id = Integer.parseInt(inputRemId);
                            StudyGroup myStudyGroup = null;
                            for (Stack<StudyGroup> stackOfGroup : listOfStackStudyGroup) {
                                for (StudyGroup studyGroup : stackOfGroup) {
                                    if (studyGroup.getId() == id) {
                                        myStudyGroup = studyGroup;
                                    }
                                }
                            }
                            if (inputRemId.isEmpty() || (myStudyGroup == null)) {
                                JOptionPane.showMessageDialog(null, "Группы с таким id не существует!");
                            } else {

                                try {
                                    client.sendRequest(new Request("remove_by_id", inputRemId, null, client.getCurrentUser()));
                                    Response response = client.receiveResponse();
                                    System.out.println(response.getResponse());
                                    JOptionPane.showMessageDialog(null, response.getResponse());
                                } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Ошибка при вводе id");
                        }
                        actionBool = true;
                        break;

                    case "update_by_id":
                        actionBool = false;
                        String inputID = JOptionPane.showInputDialog(null, messages.getString("insertArgument"));
                        try {
                            int id = Integer.parseInt(inputID);
                            StudyGroup myStudyGroup = null;
                            for (Stack<StudyGroup> stackOfGroup : listOfStackStudyGroup) {
                                for (StudyGroup studyGroup : stackOfGroup) {
                                    if (studyGroup.getId() == id) {
                                        myStudyGroup = studyGroup;
                                    }
                                }
                            }
                            if (inputID.isEmpty() || (myStudyGroup == null)) {
                                JOptionPane.showMessageDialog(null, "Группы с таким id не существует!");
                            } else {
                                EditStudyGroup update_by_id = new EditStudyGroup(communicationControl);
                                saveButton = update_by_id.getSaveButton();
                                update_by_id.setInfo(myStudyGroup);

                                saveButton.addActionListener(e1 -> {
                                    Response resp;
                                    try {

                                        client.sendRequest(new Request("update_by_id", inputID, update_by_id.update(), client.getCurrentUser()));
                                        resp = client.receiveResponse();
                                    } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    JOptionPane.showMessageDialog(null, resp.getResponse());

                                });
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Ошибка при вводе id");
                        }
                        actionBool = true;
                        break;

                    case "execute_script":
                        actionBool = false;
                        Thread myThread = new Thread(() -> {
                            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                            JPanel panel = new JPanel();
                            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                            panel.add(Box.createVerticalGlue());

                            final JLabel label = new JLabel("Выбранный файл");
                            label.setAlignmentX(CENTER_ALIGNMENT);
                            panel.add(label);

                            JFileChooser fileopen = new JFileChooser();
                            int ret = fileopen.showDialog(null, "Открыть файл");
                            if (ret == JFileChooser.APPROVE_OPTION) {
                                File file = fileopen.getSelectedFile();
                                JOptionPane.showMessageDialog(null, "передан в исполнение");
                                System.out.println(client.processScriptToServer(file));
                            }


                        });
                        myThread.start();
                        actionBool = true;
                        break;
                    default:
                        System.out.println("Дичь какая то получилась!!!");
                        break;
                }
            }
        });


        commandList.addActionListener(e -> {
            // Получаем выбранную команду
            selectedCommand = (String) commandList.getSelectedItem();

            // Вызываем метод, передавая выбранную команду в качестве аргумента
            System.out.println(selectedCommand);
        });

        panel3.add(commandButton, BorderLayout.SOUTH);

        //добавление панелей
        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel lowPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.add(panel1, BorderLayout.NORTH);

        contentPane.add(panel2, BorderLayout.CENTER);

        lowPane.add(panel3, BorderLayout.NORTH);
        lowPane.add(studyGroupSortingPanel, BorderLayout.WEST);
        contentPane.add(lowPane, BorderLayout.SOUTH);
        add(contentPane);

        pack();
    }

    public void clearData(DefaultTableModel model) {
        model.setRowCount(0);

    }

    public static Stack<StudyGroup> getStudyGroups() {
        return takeArray;
    }

    public static void setStudyGroups(Stack<StudyGroup> studyGroups) {
        takeArray = studyGroups;
    }
}
