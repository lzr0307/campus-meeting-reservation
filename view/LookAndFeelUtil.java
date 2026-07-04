package coursePractice.meetingMIS.view;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class LookAndFeelUtil {
    private LookAndFeelUtil() {
    }

    public static void apply() {
        setFlatLafProperties();
        if (!tryFlatLaf()) {
            tryNimbusOrSystem();
        }
        setVisualDefaults();
        setDefaultFont();
    }

    private static void setFlatLafProperties() {
        System.setProperty("flatlaf.useWindowDecorations", "false");
        System.setProperty("flatlaf.menuBarEmbedded", "false");
        System.setProperty("flatlaf.animation", "true");
    }

    private static boolean tryFlatLaf() {
        String[] flatLafClasses = {
                "com.formdev.flatlaf.FlatIntelliJLaf",
                "com.formdev.flatlaf.FlatLightLaf"
        };
        for (String className : flatLafClasses) {
            try {
                UIManager.setLookAndFeel(className);
                return true;
            } catch (Exception ignored) {

            }
        }
        return false;
    }

    private static void tryNimbusOrSystem() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            
        }
    }

    private static void setDefaultFont() {
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 14);
        UIManager.getLookAndFeelDefaults().keySet().forEach(key -> {
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        });
    }

    private static void setVisualDefaults() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.selectionBackground", new Color(0xDCEBFF));
        UIManager.put("Table.selectionForeground", new Color(0x111827));
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("TabbedPane.showTabSeparators", true);
    }
}
