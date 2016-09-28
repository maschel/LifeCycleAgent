package nl.hu.msda.device;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class BookSellerGui extends JFrame {
    private BookSellerAgent myAgent;

    private JTextField titleField;
    private JTextField priceField;
    private JButton addButton;

    public JPanel getPanel() {
        return panel;
    }

    private JPanel panel;

    public BookSellerGui(BookSellerAgent a) {
        myAgent = a;

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String title = titleField.getText().trim();
                    String price = priceField.getText().trim();
                    myAgent.updateCatalogue(title, Integer.parseInt(price));
                    titleField.setText("");
                    priceField.setText("");
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(BookSellerGui.this, "Invalid values. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
