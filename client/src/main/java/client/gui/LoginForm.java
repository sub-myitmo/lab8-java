package client.gui;

import client.Client;
import client.StartClient;
import client.helpers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginForm extends JFrame {
    Client client;
    StartClient startClient;

    ResourceBundle messages;

    JLabel titleLabel;
    JLabel usernameLabel;
    JLabel passwordLabel;
    JCheckBox accountExistsCheckBox;
    JButton loginButton;

    public LoginForm(CommunicationControl communicationControl) {
        // Задаем язык по умолчанию (английский)
        setLanguage(new Locale("en", "IE"));

        setTitle(messages.getString("authorization"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLayout(new BorderLayout());

        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup languageGroup = new ButtonGroup();

        JRadioButton englishButton = new JRadioButton("English");
        JRadioButton russianButton = new JRadioButton("Русский");
        JRadioButton deutschButton = new JRadioButton("Deutsch");
        JRadioButton swedishButton = new JRadioButton("Svenska");

        languageGroup.add(englishButton);
        languageGroup.add(russianButton);
        languageGroup.add(deutschButton);
        languageGroup.add(swedishButton);

        englishButton.setSelected(true);

        englishButton.addActionListener(e -> setLanguage(new Locale("en", "IE")));
        russianButton.addActionListener(e -> setLanguage(new Locale("ru", "RU")));
        deutschButton.addActionListener(e -> setLanguage(new Locale("de", "DE")));
        swedishButton.addActionListener(e -> setLanguage(new Locale("sv", "SE")));

        languagePanel.add(englishButton);
        languagePanel.add(russianButton);
        languagePanel.add(deutschButton);
        languagePanel.add(swedishButton);

        getContentPane().add(languagePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        usernameLabel = new JLabel(messages.getString("username"));
        inputPanel.add(usernameLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 30));
        inputPanel.add(usernameField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        passwordLabel = new JLabel(messages.getString("password"));
        inputPanel.add(passwordLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 30));
        inputPanel.add(passwordField, gridBagConstraints);

        getContentPane().add(inputPanel, BorderLayout.CENTER);

        accountExistsCheckBox = new JCheckBox(messages.getString("accountExists"));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        inputPanel.add(accountExistsCheckBox, gridBagConstraints);
        gridBagConstraints.gridwidth = 3;

        JPanel buttonPanel = new JPanel(new FlowLayout());

        loginButton = new JButton(messages.getString("login"));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());

                if (!usernameLabel.getText().trim().isEmpty() && !password.isEmpty() && client.processAuthentication(accountExistsCheckBox.isSelected(), usernameField.getText(), Hasher.hash(String.valueOf(passwordField.getPassword())))) {
                    MainWindow mainFrame = new MainWindow(client, communicationControl);
                    mainFrame.setVisible(true);
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Ошибка при входе! Либо повреждены данные, либо сервер на данный момент не доступен. Повторите попытку позже!");
                }

            }
        });
        buttonPanel.add(loginButton);

        // Добавление панели кнопок на основную форму
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setRunClient(StartClient app) {
        this.startClient = app;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void setLanguage(Locale locale) {
        UserSettings.getInstance().setSelectedLocale(locale);
        messages = ResourceBundle.getBundle("client.gui.gui", locale);

        if (titleLabel != null) {
            titleLabel.setText(messages.getString("authorization"));
        }
        if (usernameLabel != null) {
            usernameLabel.setText(messages.getString("username"));
        }
        if (passwordLabel != null) {
            passwordLabel.setText(messages.getString("password"));
        }
        if (accountExistsCheckBox != null) {
            accountExistsCheckBox.setText(messages.getString("accountExists"));
        }
        if (loginButton != null) {
            loginButton.setText(messages.getString("login"));
        }
    }
}
