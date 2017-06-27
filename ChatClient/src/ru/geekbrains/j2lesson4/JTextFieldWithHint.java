package ru.geekbrains.j2lesson4;

import javax.swing.*;
import java.awt.*;

/**
 * Created by amifanick on 15.06.2017.
 */
public class JTextFieldWithHint extends JTextField {
    private String hint;
    private static Font hintFont = new Font("Arial", Font.PLAIN, 14);

    public JTextFieldWithHint(String hint) {
        this.hint = hint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(getText().isEmpty()){
            g.setFont(hintFont);
            g.setColor(Color.gray);
            g.drawString(hint,7,18);
        }
    }
}
