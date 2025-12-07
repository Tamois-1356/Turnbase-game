package UI;

import AbstractClass.Unit;
import Skills.PassiveSkill;
import Skills.Skill;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import javax.swing.border.Border;

public class BattleUI extends JFrame {
    
    private JPanel teamAPanel;
    private JPanel teamBPanel;
    private JTextArea battleLog;
    private JLabel turnLabel;
    private JPanel actionPanel;
    private JPanel skillButtonsPanel;
    private JLabel instructionLabel;
    
    private List<Unit> teamA;
    private List<Unit> teamB;
    private final List<UnitCard> teamACards = new ArrayList<>();
    private final List<UnitCard> teamBCards = new ArrayList<>();
    
    private CompletableFuture<Integer> actionChoice;
    private CompletableFuture<Unit> targetChoice;
    private Unit currentActingUnit;
    private Skill selectedSkill;
    private boolean waitingForTarget = false;
    
    public BattleUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("IMMUNE VS VIRUS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(43, 43, 43));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: Turn counter and instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(26, 26, 26));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        turnLabel = new JLabel("TURN 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnLabel.setForeground(Color.WHITE);
        
        instructionLabel = new JLabel("Battle in progress...", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(255, 215, 0));
        
        topPanel.add(turnLabel, BorderLayout.NORTH);
        topPanel.add(instructionLabel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center: Battle field
        JPanel battleField = new JPanel(new GridLayout(1, 2, 30, 0));
        battleField.setBackground(new Color(43, 43, 43));
        battleField.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        teamAPanel = createTeamPanel("IMMUNE TEAM (PLAYER)", new Color(46, 125, 50));
        teamBPanel = createTeamPanel("VIRUS TEAM (ENEMY)", new Color(198, 40, 40));
        
        battleField.add(teamAPanel);
        battleField.add(teamBPanel);
        mainPanel.add(battleField, BorderLayout.CENTER);
        
        // Bottom: Action panel and battle log
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(43, 43, 43));
        
        // Action panel (for skills selection)
        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(new Color(58, 58, 58));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel actionLabel = new JLabel("Choose Action:");
        actionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionLabel.setForeground(Color.WHITE);
        actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        skillButtonsPanel = new JPanel();
        skillButtonsPanel.setLayout(new BoxLayout(skillButtonsPanel, BoxLayout.Y_AXIS));
        skillButtonsPanel.setBackground(new Color(58, 58, 58));
        skillButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        actionPanel.add(actionLabel);
        actionPanel.add(Box.createVerticalStrut(10));
        actionPanel.add(skillButtonsPanel);
        actionPanel.setVisible(false);
        
        bottomPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Battle log
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(new Color(43, 43, 43));
        
        JLabel logLabel = new JLabel("Battle Log:");
        logLabel.setFont(new Font("Arial", Font.BOLD, 14));
        logLabel.setForeground(Color.WHITE);
        
        battleLog = new JTextArea(8, 50);
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(26, 26, 26));
        battleLog.setForeground(new Color(0, 255, 0));
        battleLog.setFont(new Font("Courier New", Font.PLAIN, 12));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        bottomPanel.add(logPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createTeamPanel(String title, Color borderColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(58, 58, 58));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(borderColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        return panel;
    }
    
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void updateTurn(int turn) {
        SwingUtilities.invokeLater(() -> turnLabel.setText("TURN " + turn));
    }
    
    public void setInstruction(String text) {
        SwingUtilities.invokeLater(() -> instructionLabel.setText(text));
    }
    
    public void updateTeams(List<Unit> teamA, List<Unit> teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
        
        SwingUtilities.invokeLater(() -> {
            updateTeamDisplay(teamAPanel, teamA, teamACards, true);
            updateTeamDisplay(teamBPanel, teamB, teamBCards, false);
        });
    }
    
    private void updateTeamDisplay(JPanel teamPanel, List<Unit> team, List<UnitCard> cards, boolean isPlayerTeam) {
        Component title = teamPanel.getComponent(0);
        teamPanel.removeAll();
        teamPanel.add(title);
        teamPanel.add(Box.createVerticalStrut(15));
        
        cards.clear();
        
        for (Unit unit : team) {
            UnitCard unitCard = new UnitCard(unit, isPlayerTeam);
            cards.add(unitCard);
            teamPanel.add(unitCard);
            teamPanel.add(Box.createVerticalStrut(10));
        }
        
        teamPanel.revalidate();
        teamPanel.repaint();
    }

    public List<Unit> getTeamB() {return teamB;}
    public void setTeamB(List<Unit> teamB) {this.teamB = teamB;}
    public List<Unit> getTeamA() {return teamA;}
    public void setTeamA(List<Unit> teamA) {this.teamA = teamA;}
    
    // Inner class for clickable unit cards
    private class UnitCard extends JPanel {
        private final Unit unit;
        private final boolean isPlayerTeam;
        private Border normalBorder;
        private Border hoverBorder;
        private final Border selectedBorder;
        
        public UnitCard(Unit unit, boolean isPlayerTeam) {
            this.unit = unit;
            this.isPlayerTeam = isPlayerTeam;
            
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(unit.isAlive() ? new Color(74, 74, 74) : new Color(42, 42, 42));
            
            normalBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(unit.isAlive() ? new Color(102, 102, 102) : new Color(51, 51, 51), 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            );
            
            hoverBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            );
            
            selectedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 255, 0), 4),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            );
            
            setBorder(normalBorder);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
            
            // Character icon/avatar
            JLabel avatarLabel = new JLabel(getCharacterIcon(unit), SwingConstants.CENTER);
            avatarLabel.setFont(new Font("Arial", Font.BOLD, 48));
            avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel(unit.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setForeground(unit.isAlive() ? Color.WHITE : Color.GRAY);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel hpLabel = new JLabel("HP: " + unit.getHp() + "/" + unit.getMaxHp());
            hpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            hpLabel.setForeground(Color.LIGHT_GRAY);
            hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JProgressBar hpBar = new JProgressBar(0, unit.getMaxHp());
            hpBar.setValue(unit.getHp());
            hpBar.setStringPainted(false);
            hpBar.setForeground(new Color(76, 175, 80));
            hpBar.setBackground(new Color(100, 100, 100));
            hpBar.setMaximumSize(new Dimension(200, 20));
            hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            statsPanel.setBackground(getBackground());
            statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel atkLabel = new JLabel("ATK: " + unit.getAtk());
            atkLabel.setForeground(new Color(255, 107, 107));
            atkLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            JLabel defLabel = new JLabel("DEF: " + unit.getDef());
            defLabel.setForeground(new Color(77, 171, 247));
            defLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            statsPanel.add(atkLabel);
            statsPanel.add(defLabel);
            
            add(avatarLabel);
            add(Box.createVerticalStrut(5));
            add(nameLabel);
            add(Box.createVerticalStrut(5));
            add(hpLabel);
            add(Box.createVerticalStrut(5));
            add(hpBar);
            add(Box.createVerticalStrut(8));
            add(statsPanel);
            
            if (!unit.isAlive()) {
                JLabel deadLabel = new JLabel("DEFEATED");
                deadLabel.setForeground(new Color(198, 40, 40));
                deadLabel.setFont(new Font("Arial", Font.BOLD, 12));
                deadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(Box.createVerticalStrut(5));
                add(deadLabel);
            }
            
            // Add click listeners
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (unit.isAlive() && (waitingForTarget || (isPlayerTeam && actionChoice != null && !actionChoice.isDone()))) {
                        setBorder(hoverBorder);
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (currentActingUnit != unit) {
                        setBorder(normalBorder);
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleUnitClick();
                }
            });
        }
        
        private String getCharacterIcon(Unit unit) {
            String name = unit.getName().toLowerCase();
            if (name.contains("tcell") || name.contains("t-")) {
                return "[T]"; // T-Cell icon
            } else if (name.contains("bcell") || name.contains("b-")) {
                return "[B]"; // B-Cell icon
            } else if (name.contains("virus")) {
                return "[V]"; // Virus icon
            }
            return "[U]"; // Default icon
        }
        
        private void handleUnitClick() {
            if (!unit.isAlive()) return;
            
            // If waiting for target selection
            if (waitingForTarget && !isPlayerTeam) {
                if (targetChoice != null && !targetChoice.isDone()) {
                    targetChoice.complete(unit);
                    waitingForTarget = false;
                    resetAllBorders();
                }
            }
            // If waiting for action selection (player's own unit)
            else if (isPlayerTeam && actionChoice != null && !actionChoice.isDone() && unit == currentActingUnit) {
                // Show skills popup
                showSkillsPopup();
            }
        }
        
        public void highlight() {
            setBorder(selectedBorder);
        }
        
        public void resetBorder() {
            setBorder(normalBorder);
        }
    }
    
    private void resetAllBorders() {
        for (UnitCard card : teamACards) {
            card.resetBorder();
        }
        for (UnitCard card : teamBCards) {
            card.resetBorder();
        }
    }
    
    private void showSkillsPopup() {
        if (currentActingUnit == null) return;
        
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this, currentActingUnit.getName() + " - Choose Action", true);
            dialog.setSize(350, 400);
            dialog.setLocationRelativeTo(this);
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(58, 58, 58));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel headerLabel = new JLabel("Select an action:");
            headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(headerLabel);
            contentPanel.add(Box.createVerticalStrut(15));
            
            // Basic Attack button
            JButton basicAttackBtn = new JButton("[ATK] Basic Attack");
            basicAttackBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            basicAttackBtn.setMaximumSize(new Dimension(250, 45));
            styleButton(basicAttackBtn, new Color(255, 107, 107));
            basicAttackBtn.addActionListener(e -> {
                selectedSkill = null;
                actionChoice.complete(0);
                dialog.dispose();
            });
            contentPanel.add(basicAttackBtn);
            contentPanel.add(Box.createVerticalStrut(10));
            
            // Skill buttons
            List<Skill> skills = currentActingUnit.getSkills();
            int skillIndex = 0;
            for (Skill skill : skills) {
                if (skill instanceof PassiveSkill) continue;
                
                final int index = skillIndex;
                JButton skillBtn = new JButton("[SKILL] " + skill.getName() + (skill.isReady() ? "" : " (Cooldown)"));
                skillBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                skillBtn.setMaximumSize(new Dimension(250, 45));
                skillBtn.setEnabled(skill.isReady());
                
                Color color = skill.isReady() ? new Color(77, 171, 247) : new Color(102, 102, 102);
                styleButton(skillBtn, color);
                
                final Skill currentSkill = skill;
                skillBtn.addActionListener(e -> {
                    selectedSkill = currentSkill;
                    actionChoice.complete(index + 1);
                    dialog.dispose();
                });
                
                contentPanel.add(skillBtn);
                contentPanel.add(Box.createVerticalStrut(8));
                skillIndex++;
            }
            
            dialog.add(contentPanel);
            dialog.setVisible(true);
        });
    }
    
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            battleLog.append(message + "\n");
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        });
    }
    
    public CompletableFuture<Integer> chooseAction(Unit unit) {
        currentActingUnit = unit;
        actionChoice = new CompletableFuture<>();
        
        SwingUtilities.invokeLater(() -> {
            setInstruction(">> Click on " + unit.getName() + " to choose an action");
            
            // Highlight the acting unit
            for (UnitCard card : teamACards) {
                if (card.unit == unit) {
                    card.highlight();
                }
            }
        });
        
        return actionChoice;
    }
    
    public CompletableFuture<Unit> chooseTarget(List<Unit> enemies) {
        targetChoice = new CompletableFuture<>();
        waitingForTarget = true;
        
        SwingUtilities.invokeLater(() -> {
            setInstruction("[TARGET] Click on an enemy to attack!");
            resetAllBorders();
        });
        
        return targetChoice;
    }
}