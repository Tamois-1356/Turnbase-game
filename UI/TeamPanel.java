package UI;

import AbstractClass.Unit;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class TeamPanel extends JPanel {
    private final String title;
    private final Color themeColor;
    private final boolean isPlayerTeam;
    private final UserInputHandler inputHandler;
    private final List<UnitCard> unitCards = new ArrayList<>();
    
    public TeamPanel(String title, Color themeColor, boolean isPlayerTeam, UserInputHandler inputHandler) {
        this.title = title;
        this.themeColor = themeColor;
        this.isPlayerTeam = isPlayerTeam;
        this.inputHandler = inputHandler;
        
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(35, 35, 50));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeColor, 4, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(themeColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(titleLabel);
        add(Box.createVerticalStrut(25));
    }
    
    public void updateUnits(List<Unit> units) {
        // Keep title
        Component titleComponent = getComponent(0);
        removeAll();
        add(titleComponent);
        add(Box.createVerticalStrut(25));
        
        unitCards.clear();
        
        for (Unit unit : units) {
            UnitCard card = new UnitCard(unit, isPlayerTeam, inputHandler);
            unitCards.add(card);
            add(card);
            add(Box.createVerticalStrut(15));
        }
        
        revalidate();
        repaint();
    }
    
    public UnitCard findCardForUnit(Unit unit) {
        for (UnitCard card : unitCards) {
            if (card.getUnit() == unit) {
                return card;
            }
        }
        return null;
    }
    
    public void resetHighlights() {
        for (UnitCard card : unitCards) {
            card.setHighlighted(false);
        }
    }
    
    public void highlightUnit(Unit unit) {
        for (UnitCard card : unitCards) {
            if (card.getUnit() == unit) {
                card.setHighlighted(true);
            }
        }
    }
}