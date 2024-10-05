package client.gui;

import client.Client;
import client.helpers.CommunicationControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class SettingsForm extends JFrame {
    ResourceBundle messages;

    JButton saveButton;

    public SettingsForm(Client client, CommunicationControl communicationControl){
        setLanguage(new Locale("en", "IE"));
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
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

        saveButton = new JButton(messages.getString("save"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow mainFrame = new MainWindow(client, communicationControl);
                mainFrame.setVisible(true);
                dispose();
            }
        });
        getContentPane().add(saveButton, BorderLayout.SOUTH);
    }

    private void setLanguage(Locale locale) {
        UserSettings.getInstance().setSelectedLocale(locale);
        messages = ResourceBundle.getBundle("client.gui.gui", locale);

        if (saveButton != null) {
            saveButton.setText(messages.getString("save"));
        }
    }
}

