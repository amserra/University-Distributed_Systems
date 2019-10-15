import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.apple.eawt.ApplicationListener;

public class GUI extends JFrame implements ActionListener {

    JFrame f;
    JPanel panel;
    JLabel user_label, password_label, message;
    JTextField userName_text;
    JPasswordField password_text;
    JButton submit, cancel;
    ActionListener l;

    GUI() {
        f = new JFrame("ucBusca - Login");
        f.setSize(300, 100);
        f.setLocation(300, 300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel(new GridLayout(3, 1));
        f.add(panel, BorderLayout.CENTER);

        user_label = new JLabel("User Name: ");
        userName_text = new JTextField();

        password_label = new JLabel("Password : ");
        password_text = new JPasswordField();

        // Submit
        submit = new JButton("Login");
        message = new JLabel();

        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);
        panel.add(message);
        panel.add(submit);

        submit.addActionListener(this);

        f.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Verificar se nao esta vazio, etc
        String userName = userName_text.getText();
        String password = String.valueOf(password_text.getPassword());
        if (userName.trim().equals("admin") && password.trim().equals("admin")) {
            message.setText(" Hello " + userName + "");
        } else {
            message.setText(" Invalid user.. ");
        }

    }

}