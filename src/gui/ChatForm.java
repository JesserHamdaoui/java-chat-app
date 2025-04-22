package gui;

import client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatForm extends JFrame implements Client.MessageListener {
    private JPanel chatPanel;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    private final Client client;
    private final String userName;

    public ChatForm(Client client, String userName) {
        this.client = client;
        this.userName = userName;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chat Interface");
        setSize(450, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        titleLabel = new JLabel("server.Server Chat - " + userName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 102, 204));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            inputField.setText("");
        }
    }

    @Override
    public void onMessageReceived(String fullMessage) {
        String[] parts = fullMessage.split(": ", 2);
        String sender = parts[0];
        String content = parts.length > 1 ? parts[1] : "";
        addMessageBubble(sender, content);
    }

    private void addMessageBubble(String sender, String message) {
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
        boolean isCurrentUser = sender.equals(userName);

        // Bubble styling
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        bubble.setBackground(isCurrentUser ? new Color(0, 102, 204) : new Color(220, 220, 220));

        // Message text
        JLabel textLabel = new JLabel("<html><div style='width: 200px; color: " +
                (isCurrentUser ? "white" : "black") + ";'>" + message + "</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Sender and timestamp
        JLabel metaLabel = new JLabel(
                (isCurrentUser ? "You" : sender) + " • " + timestamp
        );
        metaLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        metaLabel.setForeground(isCurrentUser ? new Color(200, 200, 255) : new Color(100, 100, 100));

        bubble.add(metaLabel);
        bubble.add(Box.createVerticalStrut(2));
        bubble.add(textLabel);

        // Align bubble to right/left
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(bubble, isCurrentUser ? BorderLayout.EAST : BorderLayout.WEST);

        chatPanel.add(wrapper);
        chatPanel.revalidate();
        chatPanel.repaint();

        // Auto-scroll
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

}