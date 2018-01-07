package game.logIn;

import game.mainFrame.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogInPanel extends JPanel {

    private static final Dimension TEXT_FIELD_SIZE = new Dimension(100, 20);
    private JLabel errorMessage;
    private JTextField username;
    private JTextField password;
    private JLabel signUp;
    private JButton signInBtn;

    public LogInPanel(GameFrame gameFrame){
        setLayout(new GridBagLayout());

        initializePanelComponents();
        setUpPanelComponents();

        signInBtn.addActionListener(e -> {
            if(username.getText().isEmpty()){
                errorMessage.setText("Please write your username");
                return;
            }
            if(password.getText().isEmpty()){
                errorMessage.setText("Please write your password");
                return;
            }
            gameFrame.loginRequest(username.getText(), password.getText());
        });

        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameFrame.setHeader("Sign up");
                gameFrame.signUp();
            }
        });
    }

    /**
     * Initializes all components that is used in the panel.
     */
    private void initializePanelComponents(){
        errorMessage = new JLabel("", JLabel.CENTER);
        errorMessage.setPreferredSize(new Dimension(200, 20));
        errorMessage.setForeground(Color.RED);

        username = new JTextField();
        username.setPreferredSize(TEXT_FIELD_SIZE);

        password = new JTextField();
        password.setPreferredSize(TEXT_FIELD_SIZE);

        signInBtn = new JButton("Sign in");
        signUp = new JLabel("Sign up");
    }

    /**
     * Adds all components to panel with custom constraints
     */
    private void setUpPanelComponents(){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("Username"), constraints);
        constraints.insets = new Insets(5, 0, 0 ,0);
        constraints.gridy++;
        add(username, constraints);
        constraints.gridy++;
        add(new JLabel("Password"), constraints);
        constraints.gridy++;
        add(password, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(10, 0, 0 ,0);
        add(signInBtn, constraints);
        constraints.gridy++;
        add(errorMessage, constraints);
        constraints.insets = new Insets(50, 0, 0 ,0);
        constraints.gridy++;
        add(signUp, constraints);
    }

    /**
     * Show an error message to the user.
     * @param message error message
     */
    public void setErrorMessage(String message){
        errorMessage.setText(message);
    }
}
