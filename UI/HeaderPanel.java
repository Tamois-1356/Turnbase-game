package UI;

import javax.swing.*;
import java.awt.*;

/**
 * HeaderPanel - Displays turn counter and instructions
 * Single Responsibility: Header information display
 */
public class HeaderPanel extends JPanel {
    private final JLabel turnLabel;
    private final JLabel instructionLabel;
    
    public HeaderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 45));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        turnLabel = new JLabel("TURN 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 32));
        turnLabel.setForeground(new Color(255, 215, 0));
        
        instructionLabel = new JLabel("Battle Starting...", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        instructionLabel.setForeground(new Color(150, 200, 255));
        
        add(turnLabel, BorderLayout.NORTH);
        add(instructionLabel, BorderLayout.CENTER);
    }
    
    public void setTurn(int turn) {
        turnLabel.setText("TURN " + turn);
    }
    
    public void setInstruction(String text) {
        instructionLabel.setText(text);
    }
}