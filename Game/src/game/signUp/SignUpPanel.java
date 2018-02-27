package game.signUp;

import game.mainFrame.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignUpPanel extends JPanel {

    private static final Dimension TEXT_FIELD_SIZE = new Dimension(100, 20);
    private JTextField username;
    private JTextField password;
    private JButton signUpBtn;
    private JLabel back;
    private JLabel errorMessage;
    private JLabel invalidInfo;

    public SignUpPanel(GameFrame gameFrame){
        setLayout(new GridBagLayout());
        initializePanelComponents();
        setUpPanelComponents();
        signUpBtn.addActionListener(e -> {
            if(username.getText().isEmpty()){
                errorMessage.setText("You have to choose a username");
                return;
            }
            if(password.getText().length() < 6){
                errorMessage.setText("The password must be longer than 6 characters");
                return;
            }
            gameFrame.signUpRequest(username.getText(), password.getText());
        });

        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearFields();
                gameFrame.loginMode();
            }
        });
    }

    public void clearFields(){
        username.setText("");
        password.setText("");
        errorMessage.setText("");
    }

    /**
     * Show an error message to the user.
     * @param message error message
     */
    public void setErrorMessage(String message){
        errorMessage.setText(message);
    }

    /**
     * Initializes all components that is used in the panel.
     */
    private void initializePanelComponents(){
        username = new JTextField();
        username.setPreferredSize(TEXT_FIELD_SIZE);

        password = new JTextField();
        password.setPreferredSize(TEXT_FIELD_SIZE);

        signUpBtn = new JButton("Sign up");
        back = new JLabel("Back");

        errorMessage = new JLabel("", JLabel.CENTER);
        errorMessage.setPreferredSize(new Dimension(300,20));
        errorMessage.setForeground(Color.RED);

        invalidInfo = new JLabel();
        invalidInfo.setForeground(Color.RED);
    }

    /**
     * Adds all components to panel with custom constraints
     */
    private void setUpPanelComponents(){

        GridBagConstraints constraints = new GridBagConstraints();
        Insets labelInsets = new Insets(5, 0, 0 ,0);
        Insets newInformationInsets = new Insets(20, 0, 0 ,0);

        constraints.insets = labelInsets;
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("Select username"), constraints);

        constraints.gridy++;
        add(username, constraints);

        constraints.insets = newInformationInsets;
        constraints.gridy++;
        add(new JLabel("Select Password"), constraints);

        constraints.insets = labelInsets;
        constraints.gridy++;
        add(password, constraints);

        constraints.insets = newInformationInsets;
        constraints.gridy++;
        add(signUpBtn, constraints);

        constraints.gridy++;
        add(errorMessage, constraints);

        constraints.gridy += 10;
        add(back, constraints);
    }

}
