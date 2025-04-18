import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatForm extends JFrame {
    private JPanel chatPanel;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JLabel titleLabel;  // Label for the "Server Chat" title
    private String userName;  // Store the user's name

    public ChatForm(String userName) {
        this.userName = userName;
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

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(Color.WHITE);
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
            addMessageBubble(userName, message);
            inputField.setText("");
        }
    }

    private void addMessageBubble(String sender, String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        JLabel text = new JLabel("<html><div style='width:200px; word-wrap: break-word;'>" + message + "</div></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        text.setForeground(Color.BLACK);

        int lineCount = (int) Math.ceil((double) message.length() / 30);  // Rough estimate for line breaks
        int bubbleHeight = 60 + lineCount * 20;  // Increase height for each additional line of text

        JLabel senderAndTimestamp = new JLabel("<html><div style='width:200px;'><strong>" + sender + " - " + timestamp + "</strong></div></html>");
        senderAndTimestamp.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        senderAndTimestamp.setForeground(new Color(100, 100, 100));

        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBackground(new Color(220, 220, 220));
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12)); // Padding inside the bubble
        bubble.add(text, BorderLayout.CENTER);
        bubble.setOpaque(true);

        bubble.setPreferredSize(new Dimension(240, bubbleHeight));  // Adjust height to accommodate multiple lines
        bubble.setMaximumSize(new Dimension(240, bubbleHeight));   // Prevent resizing beyond this size

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS)); // Stack message text and sender info vertically
        wrapper.setBackground(Color.WHITE);
        wrapper.add(bubble);
        wrapper.add(senderAndTimestamp); // Add sender's name and timestamp below the message bubble

        chatPanel.add(wrapper);

        chatPanel.add(Box.createVerticalStrut(4));  // Adjust this value for desired spacing between messages

        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum()); // Scrolls to the bottom
        });
    }





    public static void main(String[] args) {
        // Pass the user's name to the constructor
        SwingUtilities.invokeLater(() -> new ChatForm("User"));
    }
}
