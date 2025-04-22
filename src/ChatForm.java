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
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        JLabel text = new JLabel("<html><div style='width:200px; word-wrap: break-word;'>" + message + "</div></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setForeground(Color.BLACK);

        JLabel senderLabel = new JLabel("<html><strong>" + sender + "</strong> <small>(" + timestamp + ")</small></html>");
        senderLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        senderLabel.setForeground(new Color(100, 100, 100));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBackground(sender.equals(userName) ? new Color(204, 229, 255) : new Color(220, 220, 220));
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12));
        bubble.add(senderLabel, BorderLayout.NORTH);
        bubble.add(text, BorderLayout.CENTER);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(bubble);
        wrapper.add(Box.createVerticalStrut(8));

        chatPanel.add(wrapper);
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}