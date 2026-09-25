/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package itprocurementsystem;

/**
 * We use a JFrame because someone must register before they can enter MainFrame.
 * This form opens as its own window from the login screen.
 * Extending JFrame gives it a title bar and the normal window controls.
 *
 * @author alban-byamugisha
 */
public class RegisterFrame extends javax.swing.JFrame {
    // Keep department IDs while displaying only their names.
    private java.util.ArrayList<RegistrationDAO.Department> departments;

    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterFrame.class.getName());

    /**
     * Creates new form RegisterFrame
     */
    public RegisterFrame() {
        // Build the controls arranged in NetBeans Design view.
        initComponents();
        // Remove designer sample passwords before the user starts typing.
        jPasswordFieldPassword.setText("");
        jPasswordFieldConfirm.setText("");
        // These unused designer controls are hidden; they are not registration inputs.
        jTextField6.setVisible(false);
        jLabel7.setVisible(false);
        loadDepartments();
        jButtonCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) { createAccount(); }
        });
        jButtonBackToLogin.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) { backToLogin(); }
        });
        // The window's close button returns to login just like Back to Login.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) { backToLogin(); }
        });
        // Center the window after its size has been calculated.
        setLocationRelativeTo(null);
        // Closing registration should not stop the whole application.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

    // Load the department choices from MySQL and keep registration disabled on failure.
    private void loadDepartments() {
        jComboBoxDepartment.removeAllItems();
        jComboBoxDepartment.addItem("Select department");
        jButtonCreateAccount.setEnabled(false);
        try {
            departments = new RegistrationDAO().getDepartments();
            for (int i = 0; i < departments.size(); i++) {
                jComboBoxDepartment.addItem(departments.get(i).getName());
            }
            jButtonCreateAccount.setEnabled(!departments.isEmpty());
            if (departments.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "No departments are available yet.");
            }
        } catch (java.sql.SQLException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not load departments. Start MySQL, return to login and reopen Create Account.");
        }
    }

    // The form reads the controls; the DAO validates and saves the account.
    private void createAccount() {
        int index = jComboBoxDepartment.getSelectedIndex() - 1;
        int departmentId = index >= 0 && departments != null && index < departments.size()
                ? departments.get(index).getId() : 0;
        char[] password = jPasswordFieldPassword.getPassword();
        char[] confirmation = jPasswordFieldConfirm.getPassword();
        jButtonCreateAccount.setEnabled(false);
        try {
            new RegistrationDAO().register(jTextFieldFullName.getText(), jTextFieldUsername.getText(),
                    jTextFieldEmail.getText(), departmentId, new String(password), new String(confirmation));
            javax.swing.JOptionPane.showMessageDialog(this, "Account created. You can now log in.");
            backToLogin();
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (java.sql.SQLException | IllegalStateException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not confirm account creation. Check the connection and try logging in before registering again.");
        } finally {
            // Clear temporary character arrays and visible password fields after every attempt.
            java.util.Arrays.fill(password, '\0');
            java.util.Arrays.fill(confirmation, '\0');
            jPasswordFieldPassword.setText("");
            jPasswordFieldConfirm.setText("");
            jButtonCreateAccount.setEnabled(departments != null && !departments.isEmpty());
        }
    }

    // Open a fresh login window and close only this registration window.
    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabelTitle = new javax.swing.JLabel();
        jLabelFullName = new javax.swing.JLabel();
        jLabelUserName = new javax.swing.JLabel();
        jLabelEmail = new javax.swing.JLabel();
        jLabelDepartment = new javax.swing.JLabel();
        jLabelPassword = new javax.swing.JLabel();
        JLabelConfirmPassword = new javax.swing.JLabel();
        jLabelRoleInfo = new javax.swing.JLabel();
        jButtonBackToLogin = new javax.swing.JButton();
        jButtonCreateAccount = new javax.swing.JButton();
        jComboBoxDepartment = new javax.swing.JComboBox<>();
        jTextFieldFullName = new javax.swing.JTextField();
        jTextFieldUsername = new javax.swing.JTextField();
        jTextFieldEmail = new javax.swing.JTextField();
        jPasswordFieldPassword = new javax.swing.JPasswordField();
        jPasswordFieldConfirm = new javax.swing.JPasswordField();

        jLabel7.setText("jLabel7");

        jTextField6.setText("jTextField6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitle.setText("Create Account");

        jLabelFullName.setText("Full Name:");

        jLabelUserName.setText("User Name:");

        jLabelEmail.setText("Email:");

        jLabelDepartment.setText("Department:");

        jLabelPassword.setText("Password:");

        JLabelConfirmPassword.setText("Confirm Password");

        jLabelRoleInfo.setText("New accounts are registered as Requesters.");

        jButtonBackToLogin.setText("Back to Login");

        jButtonCreateAccount.setText("Create Account");

        jComboBoxDepartment.setModel(new javax.swing.DefaultComboBoxModel<String>(
            new String[] {"Select department"}
        ));
        jComboBoxDepartment.addActionListener(this::jComboBoxDepartmentActionPerformed);

        jTextFieldEmail.addActionListener(this::jTextFieldEmailActionPerformed);

        jPasswordFieldPassword.addActionListener(this::jPasswordFieldPasswordActionPerformed);

        jPasswordFieldConfirm.addActionListener(this::jPasswordFieldConfirmActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelRoleInfo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButtonBackToLogin)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonCreateAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelUserName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelFullName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDepartment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(JLabelConfirmPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxDepartment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldFullName, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .addComponent(jTextFieldUsername)
                            .addComponent(jTextFieldEmail)
                            .addComponent(jPasswordFieldConfirm)
                            .addComponent(jPasswordFieldPassword))))
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelFullName)
                    .addComponent(jTextFieldFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelUserName)
                    .addComponent(jTextFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelEmail)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelDepartment)
                    .addComponent(jComboBoxDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPassword)
                    .addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JLabelConfirmPassword)
                    .addComponent(jPasswordFieldConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelRoleInfo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonBackToLogin)
                    .addComponent(jButtonCreateAccount))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmailActionPerformed

    private void jComboBoxDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDepartmentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxDepartmentActionPerformed

    private void jPasswordFieldConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldConfirmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordFieldConfirmActionPerformed

    private void jPasswordFieldPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordFieldPasswordActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabelConfirmPassword;
    private javax.swing.JButton jButtonBackToLogin;
    private javax.swing.JButton jButtonCreateAccount;
    private javax.swing.JComboBox<String> jComboBoxDepartment;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelDepartment;
    private javax.swing.JLabel jLabelEmail;
    private javax.swing.JLabel jLabelFullName;
    private javax.swing.JLabel jLabelPassword;
    private javax.swing.JLabel jLabelRoleInfo;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUserName;
    private javax.swing.JPasswordField jPasswordFieldConfirm;
    private javax.swing.JPasswordField jPasswordFieldPassword;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldFullName;
    private javax.swing.JTextField jTextFieldUsername;
    // End of variables declaration//GEN-END:variables
}
