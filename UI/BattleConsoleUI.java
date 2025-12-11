package UI;

import AbstractClass.Unit;
import Skills.Skill;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;

public class BattleConsoleUI extends JFrame {
    private final TeamPanel teamAPanel;
    private final TeamPanel teamBPanel;
    private final HeaderPanel headerPanel;
    private final UserInputHandler inputHandler;
    private final VisualEffectManager effectManager;
    
    private List<Unit> teamA;
    private List<Unit> teamB;
    
    public enum TargetType { ENEMY, ALLY, ANY }
    public enum ActionType { BASIC_ATTACK, USE_SKILL }
    
    public static class ActionChoice {
        public final ActionType type;
        public final Skill skill;
        public ActionChoice(ActionType type, Skill skill) {
            this.type = type;
            this.skill = skill;
        }
    }
    
    public BattleConsoleUI() {
        this.inputHandler = new UserInputHandler();
        this.effectManager = new VisualEffectManager();
        this.headerPanel = new HeaderPanel();
        this.teamAPanel = new TeamPanel("=== IMMUNE TEAM ===", new Color(50, 150, 80), true, inputHandler);
        this.teamBPanel = new TeamPanel("=== VIRUS TEAM ===", new Color(180, 50, 50), false, inputHandler);
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("IMMUNE WAR!!!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // --- BACKGROUND UPDATE START ---
        // Custom panel with background image painting logic
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load background using ImageLoader
                ImageIcon bgIcon = ImageLoader.loadBackground();
                if (bgIcon != null) {
                    g.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback color
                    g.setColor(new Color(20, 20, 30));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(true);
        
        // Battle arena (transparent so background shows through)
        JPanel battleArena = new JPanel(new GridLayout(1, 2, 40, 0));
        battleArena.setOpaque(false); // Make transparent
        battleArena.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        battleArena.add(teamAPanel);
        battleArena.add(teamBPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(battleArena, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    public void updateTurn(int turn) {
        SwingUtilities.invokeLater(() -> headerPanel.setTurn(turn));
    }
    
    public void setInstruction(String text) {
        SwingUtilities.invokeLater(() -> headerPanel.setInstruction(text));
    }
    
    public void updateTeams(List<Unit> teamA, List<Unit> teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
        SwingUtilities.invokeLater(() -> {
            teamAPanel.updateUnits(teamA);
            teamBPanel.updateUnits(teamB);
        });
    }
    
    public CompletableFuture<ActionChoice> requestAction(Unit unit) {
        return inputHandler.requestAction(unit, teamAPanel, this);
    }
    
    public CompletableFuture<Unit> requestTarget(List<Unit> validTargets, TargetType type) {
        List<TeamPanel> panels = new ArrayList<>();
        if (type == TargetType.ENEMY || type == TargetType.ANY) panels.add(teamBPanel);
        if (type == TargetType.ALLY || type == TargetType.ANY) panels.add(teamAPanel);
        
        String targetTypeStr = type == TargetType.ENEMY ? "enemy" : type == TargetType.ALLY ? "ally" : "unit";
        setInstruction(">> Select a " + targetTypeStr + " target");
        return inputHandler.requestTarget(validTargets, type, panels);
    }
    
    public void showDamage(Unit target, int damage) {
        SwingUtilities.invokeLater(() -> {
            UnitCard card = findCardForUnit(target);
            if (card != null) effectManager.showDamage(card, damage);
        });
    }
    
    public void showHealing(Unit target, int healing) {
        SwingUtilities.invokeLater(() -> {
            UnitCard card = findCardForUnit(target);
            if (card != null) effectManager.showHealing(card, healing);
        });
    }
    
    public void showShield(Unit target, int shield) {
        SwingUtilities.invokeLater(() -> {
            UnitCard card = findCardForUnit(target);
            if (card != null) effectManager.showShield(card, shield);
        });
    }
    
    private UnitCard findCardForUnit(Unit unit) {
        UnitCard card = teamAPanel.findCardForUnit(unit);
        if (card != null) return card;
        return teamBPanel.findCardForUnit(unit);
    }
    
    public void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}