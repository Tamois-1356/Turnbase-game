package UI;

import AbstractClass.Unit;
import Skills.Skill;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;

public class UserInputHandler {
    private CompletableFuture<BattleConsoleUI.ActionChoice> actionFuture;
    private CompletableFuture<Unit> targetFuture;
    private Unit currentActingUnit;
    private boolean waitingForTarget = false;
    private BattleConsoleUI.TargetType targetType;
    private List<TeamPanel> targetPanels;
    
    public CompletableFuture<BattleConsoleUI.ActionChoice> requestAction(Unit unit, TeamPanel teamPanel, JFrame parent) {
        currentActingUnit = unit;
        actionFuture = new CompletableFuture<>();
        waitingForTarget = false;
        
        teamPanel.highlightUnit(unit);
        
        return actionFuture;
    }
    
    public CompletableFuture<Unit> requestTarget(List<Unit> validTargets, BattleConsoleUI.TargetType type, List<TeamPanel> panels) {
        targetFuture = new CompletableFuture<>();
        waitingForTarget = true;
        targetType = type;
        targetPanels = panels;
        
        // Reset highlights
        for (TeamPanel panel : panels) {
            panel.resetHighlights();
        }
        
        return targetFuture;
    }
    
    public void handleUnitClick(Unit unit, boolean isPlayerTeam) {
        if (waitingForTarget) {
            handleTargetSelection(unit, isPlayerTeam);
        } else if (isPlayerTeam && actionFuture != null && !actionFuture.isDone() && unit == currentActingUnit) {
            showActionMenu(unit);
        }
    }
    
    private void handleTargetSelection(Unit unit, boolean isPlayerTeam) {
        boolean isValidTarget = false;
        
        if (targetType == BattleConsoleUI.TargetType.ENEMY && !isPlayerTeam) {
            isValidTarget = true;
        } else if (targetType == BattleConsoleUI.TargetType.ALLY && isPlayerTeam) {
            isValidTarget = true;
        } else if (targetType == BattleConsoleUI.TargetType.ANY) {
            isValidTarget = true;
        }
        
        if (isValidTarget && targetFuture != null && !targetFuture.isDone()) {
            targetFuture.complete(unit);
            waitingForTarget = false;
            
            // Reset highlights
            if (targetPanels != null) {
                for (TeamPanel panel : targetPanels) {
                    panel.resetHighlights();
                }
            }
        }
    }
    
    private void showActionMenu(Unit unit) {
        // Find parent window
        Container container = SwingUtilities.getAncestorOfClass(JFrame.class, null);
        JFrame parentFrame = container instanceof JFrame ? (JFrame) container : null;
        
        ActionMenuDialog dialog = new ActionMenuDialog(parentFrame, unit, this);
        dialog.setVisible(true);
    }
    
    public void completeAction(BattleConsoleUI.ActionChoice choice) {
        if (actionFuture != null && !actionFuture.isDone()) {
            actionFuture.complete(choice);
        }
    }
    
    private static class ActionMenuDialog extends JDialog {
        public ActionMenuDialog(JFrame parent, Unit unit, UserInputHandler handler) {
            super(parent, unit.getName() + " - Actions", true);
            setSize(350, 450);
            setLocationRelativeTo(parent);
            setUndecorated(true);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(new Color(40, 40, 55));
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            
            JLabel header = new JLabel("Choose Action");
            header.setFont(new Font("Arial", Font.BOLD, 20));
            header.setForeground(new Color(255, 215, 0));
            header.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(header);
            panel.add(Box.createVerticalStrut(20));
            
            // Basic Attack
            JButton attackBtn = createActionButton("Basic Attack", new Color(200, 100, 100));
            attackBtn.addActionListener(e -> {
                handler.completeAction(new BattleConsoleUI.ActionChoice(BattleConsoleUI.ActionType.BASIC_ATTACK, null));
                dispose();
            });
            panel.add(attackBtn);
            panel.add(Box.createVerticalStrut(10));
            
            // Skills
            List<Skill> skills = unit.getSkills();
            for (Skill skill : skills) {
                if (skill instanceof Skills.PassiveSkill) continue;
                
                var btnText = skill.getName() + (skill.isReady() ? "" : " (CD: " + skill.getCooldownLeft() + ")");
                JButton skillBtn = createActionButton(btnText, skill.isReady() ? new Color(100, 150, 255) : new Color(80, 80, 90));
                skillBtn.setEnabled(skill.isReady());
                
                final Skill currentSkill = skill;
                skillBtn.addActionListener(e -> {
                    handler.completeAction(new BattleConsoleUI.ActionChoice(BattleConsoleUI.ActionType.USE_SKILL, currentSkill));
                    dispose();
                });
                
                panel.add(skillBtn);
                panel.add(Box.createVerticalStrut(8));
            }
            
            add(panel);
        }
        
        private static JButton createActionButton(String text, Color bgColor) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 15));
            btn.setForeground(Color.WHITE);
            btn.setBackground(bgColor);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(280, 45));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn.isEnabled()) btn.setBackground(bgColor.brighter());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(bgColor);
                }
            });
            
            return btn;
        }
    }
}