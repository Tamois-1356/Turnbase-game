package UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageLoader {
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
        private static final String[] SEARCH_PATHS = {
        "ProjectOOP/resources/images/", 
        "resources/images/",
    };

    public static ImageIcon loadImage(String filename) {
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        try {
            // 1. Try loading from file system
            for (String path : SEARCH_PATHS) {
                File imageFile = new File(path + filename);
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img != null) {
                        ImageIcon icon = new ImageIcon(img);
                        imageCache.put(filename, icon);
                        return icon;
                    }
                }
            }

            // 2. Try loading from classpath
            java.net.URL imgURL = ImageLoader.class.getClassLoader().getResource("images/" + filename);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                imageCache.put(filename, icon);
                return icon;
            }
            
            System.err.println("Warning: Could not find image '" + filename + "'");
            return null;
            
        } catch (IOException e) {
            System.err.println("Error loading image: " + filename);
            return null;
        }
    }
    
    public static ImageIcon loadScaledImage(String filename, int width, int height) {
        ImageIcon original = loadImage(filename);
        if (original == null) {
            return createPlaceholder(width, height, filename);
        }
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
    
    public static ImageIcon loadUnitImage(String unitName) {
        String name = unitName.toLowerCase();
        String filename = "default.png";

        // Logic to map Unit Names to your specific PNG files
        if (name.contains("killer")) {
            filename = "IS_Killer.png";
        } else if (name.contains("defender")) {
            filename = "IS_Defender.png";
        } else if (name.contains("helper") || name.contains("support")) {
            filename = "IS_Support.png";
        } else if (name.contains("alpha")) {
            filename = "Vr_Alpha.png";
        } else if (name.contains("beta")) {
            filename = "Vr_Beta.png";
        } else if (name.contains("gamma") || name.contains("gemma")) {
            // Mapping Gamma unit to Gemma image
            filename = "Vr_Gemma.png";
        } else if (name.contains("virus")) {
            // Fallback for any other virus
            filename = "Vr_Alpha.png";
        } else if (name.contains("tcell") || name.contains("t-")) {
            filename = "IS_Killer.png";
        } else if (name.contains("bcell") || name.contains("b-")) {
            filename = "IS_Support.png";
        }
        
        // Load and scale the image (approx 100x100 for good visibility)
        return loadScaledImage(filename, 80, 80);
    }
    
    public static ImageIcon loadBackground() {
        return loadImage("battle_background.png");
    }
    
    // Creates a placeholder box with the first letter of the filename if missing
    private static ImageIcon createPlaceholder(int width, int height, String filename) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(50, 50, 50, 100));
        g.fillRoundRect(0, 0, width, height, 20, 20);
        g.setColor(Color.RED);
        g.drawRoundRect(0, 0, width-1, height-1, 20, 20);
        g.drawString("?", width/2 - 4, height/2 + 4);
        g.dispose();
        return new ImageIcon(img);
    }
}