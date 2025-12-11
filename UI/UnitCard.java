package UI;

import AbstractClass.Unit;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class UnitCard extends JPanel {
    private final Unit unit;
    private final boolean isPlayerTeam;
    private final UserInputHandler inputHandler;
    private boolean highlighted = false;
    
    private Timer flashTimer;
    private Color flashColor;
    private float flashAlpha = 0f;
    
    private JLabel avatarLabel;
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JLabel hpLabel;
    private JLabel statsLabel;
    
    public UnitCard(Unit unit, boolean isPlayerTeam, UserInputHandler inputHandler) {
        this.unit = unit;
        this.isPlayerTeam = isPlayerTeam;
        this.inputHandler = inputHandler;
        
        initializeCard();
    }
    
    private void initializeCard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false); // Requirement: Remove black background
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Minimal padding
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        
        // --- IMAGE SETUP ---
        ImageIcon icon = ImageLoader.loadUnitImage(unit.getName());
        avatarLabel = new JLabel(icon, SwingConstants.CENTER);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Requirement: User interacts on the image
        avatarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Initial border for the image (invisible padding to prevent jump when highlighted)
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        
        // --- INFO SETUP ---
        nameLabel = new JLabel(unit.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Shadow effect for better readability on background
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0)); 
        
        // HP Panel
        JPanel hpPanel = createHpPanel();
        
        // Stats
        statsLabel = new JLabel(String.format("ATK: %d  |  DEF: %d", unit.getAtk(), unit.getDef()));
        statsLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Bold for visibility
        statsLabel.setForeground(new Color(220, 220, 240));
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- LAYOUT: Information first, Image last ---
        add(nameLabel);
        add(Box.createVerticalStrut(5));
        add(statsLabel);
        add(Box.createVerticalStrut(8));
        add(hpPanel);
        add(Box.createVerticalStrut(15));
        add(avatarLabel); // Requirement: Relocate image downward
        
        if (!unit.isAlive()) {
            JLabel deadLabel = new JLabel("DEFEATED");
            deadLabel.setFont(new Font("Arial", Font.BOLD, 14));
            deadLabel.setForeground(new Color(255, 80, 80));
            deadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(Box.createVerticalStrut(5));
            add(deadLabel);
            avatarLabel.setEnabled(false);
            avatarLabel.setCursor(Cursor.getDefaultCursor());
        }
        
        // --- INTERACTION ---
        // Add listener specifically to the avatar/image
        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (unit.isAlive()) {
                    inputHandler.handleUnitClick(unit, isPlayerTeam);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (unit.isAlive()) {
                    // Highlight ONLY the image
                    avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!highlighted) {
                    // Reset to invisible border
                    avatarLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                } else {
                    // Revert to selected highlight if active
                    avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 3));
                }
            }
        });
    }
    
    private JPanel createHpPanel() {
        JPanel hpPanel = new JPanel();
        hpPanel.setLayout(new BoxLayout(hpPanel, BoxLayout.Y_AXIS));
        hpPanel.setOpaque(false);
        hpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        hpLabel = new JLabel("HP: " + unit.getHp() + "/" + unit.getMaxHp());
        hpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        hpLabel.setForeground(new Color(150, 255, 150));
        hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        hpBar = new JProgressBar(0, unit.getMaxHp()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (unit.getShield() > 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int shieldWidth = (int) ((unit.getShield() / (double) unit.getMaxHp()) * getWidth());
                    g2d.setColor(new Color(200, 200, 255, 150));
                    g2d.fillRect(0, 0, Math.min(shieldWidth, getWidth()), getHeight());
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 11));
                    String shieldText = "+" + unit.getShield();
                    g2d.drawString(shieldText, 5, getHeight() - 5);
                }
            }
        };
        hpBar.setValue(unit.getHp());
        hpBar.setStringPainted(false);
        hpBar.setForeground(new Color(100, 200, 100));
        hpBar.setBackground(new Color(60, 60, 70));
        hpBar.setMaximumSize(new Dimension(180, 20)); 
        hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        hpPanel.add(hpLabel);
        hpPanel.add(Box.createVerticalStrut(3));
        hpPanel.add(hpBar);
        return hpPanel;
    }
    
    public Unit getUnit() { return unit; }
    
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlighted) {
            // Highlight the image
            avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 0), 3));
        } else {
            // Reset to padding
            avatarLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        }
    }
    
    public void updateDisplay() {
        hpBar.setValue(Math.max(0, unit.getHp()));
        hpLabel.setText("HP: " + unit.getHp() + "/" + unit.getMaxHp());
        statsLabel.setText(String.format("ATK: %d  |  DEF: %d", unit.getAtk(), unit.getDef()));
        repaint();
    }
    
    public void flashColor(Color color) {
        flashColor = color;
        flashAlpha = 1.0f;
        if (flashTimer != null) flashTimer.stop();
        flashTimer = new Timer(50, (ActionEvent e) -> {
            flashAlpha -= 0.1f;
            if (flashAlpha <= 0) {
                flashTimer.stop();
                flashColor = null;
            }
            repaint();
        });
        flashTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Only paint flash effect, skip background
        if (flashColor != null && flashAlpha > 0) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), (int)(flashAlpha * 255)));
            // Flash over the whole area to signal damage/heal clearly
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        }
    }
}