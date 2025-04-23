package gui;

import client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
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

        titleLabel = new JLabel("Server Chat - " + userName);
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setPreferredSize(new Dimension(300, 40));
        inputField.setBackground(new Color(240, 240, 240));
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 102, 204));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        ImageIcon sendIcon = new ImageIcon("C:\\STUFF\\Sophomore\\CS202\\java-chat-app\\sendLogo.png");
        Image img = sendIcon.getImage();  // Transform the image
        Image newImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        sendButton.setIcon(new ImageIcon(newImg));

        JButton attachButton = new JButton("📎");
        attachButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        attachButton.setBackground(Color.WHITE);
        attachButton.setFocusPainted(false);
        attachButton.setPreferredSize(new Dimension(50, 40));
        attachButton.addActionListener(e -> showAttachDialog());

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(attachButton, BorderLayout.WEST);
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

    private void addImageBubble(String sender, ImageIcon imageIcon) {
        String timestamp = new SimpleDateFormat("HH:mm").format(new Date());

        Image img = imageIcon.getImage();
        Image smallImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon smallImageIcon = new ImageIcon(smallImg);

        JLabel imageLabel = new JLabel(smallImageIcon);
        imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageLabel.setToolTipText("Click to view full image");

        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JDialog dialog = new JDialog(ChatForm.this, "Image Preview", true);
                dialog.setLayout(new BorderLayout());

                ImageIcon fullImageIcon = new ImageIcon(imageIcon.getImage());
                JLabel fullImageLabel = new JLabel(fullImageIcon);
                fullImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

                int imageWidth = fullImageIcon.getIconWidth();
                int imageHeight = fullImageIcon.getIconHeight();

                int maxWidth = 800;
                int maxHeight = 600;

                double scale = 1.0;

                if (imageWidth > maxWidth || imageHeight > maxHeight) {
                    scale = Math.min((double) maxWidth / imageWidth, (double) maxHeight / imageHeight);
                }


                imageWidth = (int) (imageWidth * scale);
                imageHeight = (int) (imageHeight * scale);

                dialog.setSize(imageWidth + 20, imageHeight + 20);
                dialog.add(new JScrollPane(fullImageLabel), BorderLayout.CENTER);
                dialog.setLocationRelativeTo(ChatForm.this);
                dialog.setVisible(true);
            }
        });

        JLabel senderAndTimestamp = new JLabel("<html><div style='width:200px;'><strong>" + sender + " - " + timestamp + "</strong></div></html>");
        senderAndTimestamp.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        senderAndTimestamp.setForeground(new Color(100, 100, 100));


        JPanel bubble = new JPanel();
        bubble.setBackground(new Color(220, 220, 220));
        bubble.setOpaque(false);
        bubble.add(imageLabel);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(bubble);
        wrapper.add(senderAndTimestamp);

        chatPanel.add(wrapper);
        chatPanel.add(Box.createVerticalStrut(4));

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    private void showAttachDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
            addImageBubble(userName, imageIcon);
        }
    }

}