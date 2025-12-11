package UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class VisualEffectManager {
    
    public void showDamage(UnitCard card, int damage) {
        showFloatingText(card, "-" + damage, new Color(255, 100, 100));
        card.flashColor(new Color(255, 0, 0, 100));
    }
    
    public void showHealing(UnitCard card, int healing) {
        showFloatingText(card, "+" + healing, new Color(100, 255, 100));
        card.flashColor(new Color(0, 255, 0, 100));
    }
    
    public void showShield(UnitCard card, int shield) {
        showFloatingText(card, "(_) +" + shield, new Color(100, 200, 255));
        card.flashColor(new Color(0, 150, 255, 100));
    }
    
    private void showFloatingText(UnitCard card, String text, Color color) {
        JLabel floatingLabel = new JLabel(text);
        floatingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        floatingLabel.setForeground(color);
        
        JWindow window = new JWindow();
        window.add(floatingLabel);
        window.pack();
        
        Point cardLocation = card.getLocationOnScreen();
        window.setLocation(
            cardLocation.x + card.getWidth() / 2 - window.getWidth() / 2,
            cardLocation.y + 20
        );
        window.setAlwaysOnTop(true);
        window.setVisible(true);
        
        animateFloatingText(window, floatingLabel, color);
        card.updateDisplay();
    }
    
    private void animateFloatingText(JWindow window, JLabel label, Color originalColor) {
        Timer timer = new Timer(50, null);
        timer.addActionListener(new ActionListener() {
            int count = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                window.setLocation(window.getX(), window.getY() - 3);
                float alpha = 1.0f - (count / 20.0f);
                int alphaValue = (int) (alpha * 255);
                
                if (alphaValue >= 0) {
                    label.setForeground(new Color(
                        originalColor.getRed(),
                        originalColor.getGreen(),
                        originalColor.getBlue(),
                        alphaValue
                    ));
                }
                
                count++;
                if (count > 20) {
                    timer.stop();
                    window.dispose();
                }
            }
        });
        timer.start();
    }
}