import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class ClientGUI extends JFrame {
    private JPanel panel;
    private Image backgroundImage;

    public ClientGUI() {
        try {
            // Load the background image from a file
            backgroundImage = ImageIO.read(new File("image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
        };

        // Create a vertical BoxLayout for the panel
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        // Create a label and text field for the large number
        JTextField numberTextField = new JTextField(20); // Set the preferred width of the text field
        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        numberPanel.setOpaque(false); // Make the background transparent
        numberTextField.setPreferredSize(new Dimension(250, 30));
        numberPanel.add(numberTextField);
        numberTextField.setToolTipText("Enter Student ID");

        // Add the number panel to the main panel
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some space between the components
        panel.add(numberPanel);

        // Create labels and combo boxes for the destinations
        String[] destinations = { "Destination 1", "Destination 2", "Destination 3", "Destination 4", "Destination 5" };
        for (String destination : destinations) {
            JComboBox<String> destinationsComboBox = new JComboBox<>(destinations);
            JPanel destinationsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            destinationsPanel.setOpaque(false);
            destinationsComboBox.setPreferredSize(new Dimension(230, 40));
            destinationsPanel.add(destinationsComboBox);

            // Add the destinations panel to the main panel
            panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some space between the components
            panel.add(destinationsPanel);
        }

        // Create a button to submit the form
        JButton submitButton = new JButton("Submit");
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(new Color(255, 225, 147));
        submitButton.setBorder(new LineBorder(new Color(255, 153, 51), 2)); // set border color and thickness
        submitButton.setOpaque(true); // Make the button background opaque
        // Set the font of the button
        Font font = new Font("Arial", Font.BOLD, 16);
        submitButton.setFont(font);
        submitButton.addActionListener(e -> {
            // Validate the student ID field
            if (numberTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a student ID", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Collect the selected destinations
                List<String> selectedDestinations = new ArrayList<>();
                for (Component component : panel.getComponents()) {
                    if (component instanceof JPanel) {
                        Component[] components = ((JPanel) component).getComponents();
                        if (components.length == 1 && components[0] instanceof JComboBox) {
                            JComboBox<String> comboBox = (JComboBox<String>) components[0];
                            String selectedDestination = (String) comboBox.getSelectedItem();
                            if (selectedDestination != null) {
                                selectedDestinations.add(selectedDestination);
                            }
                        }
                    }
                }
        
                // Store the form data in a dictionary
                String studentID = numberTextField.getText();
                Map<String, List<String>> preferences = new HashMap<>();
                preferences.put(studentID, selectedDestinations);
        
                // Send the form data to the server
                try {
                    Socket socket = new Socket("localhost", 1024);
                    OutputStream outputStream = socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(preferences);
                    objectOutputStream.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Failed to send data to server", "Error", JOptionPane.ERROR_MESSAGE);
                }
        
                // Show a confirmation message
                JOptionPane.showMessageDialog(panel, "Form submitted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        

        // panel.add(Box.createRigidArea(new Dimension(0, 200))); // Add some space
        // between the components
        submitButton.setPreferredSize(new Dimension(200, 50));
        Border buttonPadding = BorderFactory.createEmptyBorder(0, 20, 0, 20);
        submitButton.setBorder(buttonPadding);
        panel.add(submitButton);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setSize(550, 700);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
