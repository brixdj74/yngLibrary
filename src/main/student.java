/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author brixd
 */
public class student extends javax.swing.JFrame {

    ImageIcon logo = new ImageIcon(getClass().getResource("/img/logo.png"));

    PreparedStatement pst,pst1,pst2 = null;
    ResultSet rs,rsx,rsy = null;
    int i,q,d;
    String BOOKID;
    public String librarian;
    
    static final String USERNAME = "root";
    static final String PASSWORD = "";
    static final String DATACONN = "jdbc:mysql://localhost/setlibrary";
    com.mysql.jdbc.Connection SQLCONN = null;
    
    public student() {
        initComponents();
        SQLCONN = (com.mysql.jdbc.Connection) con();
        updateMode();
        setResizable(false);
        setTitle("Student Information");
        setIconImage(logo.getImage()); 
        studentTable();studentSort();
    }
    
    public final Connection con(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            SQLCONN = (com.mysql.jdbc.Connection) DriverManager.getConnection(DATACONN,USERNAME,PASSWORD);
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null,e);
        }
        return SQLCONN;
        
    }

    public void studentSort(){
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); // create a renderer
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // set its alignment
        t.setDefaultRenderer(Object.class, centerRenderer);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // set the auto resize mode to all columns
        t.setFillsViewportHeight(true); // set the table to fill the viewport height
        
        try{
            c.removeAllItems();
            pst = SQLCONN.prepareStatement("select distinct cy from student order by cy asc");
            rs=pst.executeQuery();
            
            while(rs.next()){
                c.addItem(rs.getString("cy"));
            }
            
        }catch(SQLException e){
            
        }
    }
    
    public void studentTable(){
        try{
        
        pst = SQLCONN.prepareStatement("select * from student order by cy asc");
        rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) t.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        String fn = rs.getString("fn");
                        String mn = rs.getString("mn");
                        String ln = rs.getString("ln");
                        String full = fn+" "+mn+" "+ln;
                        cd.add(rs.getString("studentId"));
                        cd.add(full);
                        cd.add(rs.getString("no"));
                        cd.add(rs.getString("em"));
                        cd.add(rs.getString("bd"));
                        cd.add(rs.getString("cy"));
                    }
                    bt.addRow(cd);
                }    
        }
        
        catch(SQLException e){
            System.out.println(e);
        }
    }
    
    public void searchStudent(){
        try{
        String fs = s.getText();
        pst = SQLCONN.prepareStatement("select * from student where ln like ? OR fn like ? order by cy asc");
        pst.setString(1,fs+"%");
        pst.setString(2,fs+"%");
        
        rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) t.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        String fn = rs.getString("fn");
                        String mn = rs.getString("mn");
                        String ln = rs.getString("ln");
                        String full = fn+" "+mn+" "+ln;
                        cd.add(rs.getString("studentId"));
                        cd.add(full);
                        cd.add(rs.getString("no"));
                        cd.add(rs.getString("em"));
                        cd.add(rs.getString("bd"));
                        cd.add(rs.getString("cy"));
                    }
                    bt.addRow(cd);
                }    
        }
        
        catch(Exception e){
            
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        card = new javax.swing.JPanel();
        a1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        t = new javax.swing.JTable();
        s = new javax.swing.JTextField();
        c = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        a2 = new javax.swing.JPanel();
        submit = new javax.swing.JButton();
        cy = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        add = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        bd = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        fn = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        id = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        mn = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        no = new javax.swing.JTextField();
        em = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ln = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        sf = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(780, 470));
        setSize(new java.awt.Dimension(780, 470));
        getContentPane().setLayout(null);

        jLabel2.setText("Students");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 10, 220, 40);

        jButton1.setText("Student List");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(500, 20, 120, 23);

        card.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        card.setLayout(new java.awt.CardLayout());

        a1.setLayout(null);

        t.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Student Name", "Contact Number", "Email", "Birth Date", "Course & Year"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        t.setRowHeight(30);
        jScrollPane1.setViewportView(t);
        if (t.getColumnModel().getColumnCount() > 0) {
            t.getColumnModel().getColumn(0).setMinWidth(0);
            t.getColumnModel().getColumn(0).setPreferredWidth(0);
            t.getColumnModel().getColumn(0).setMaxWidth(0);
            t.getColumnModel().getColumn(2).setMinWidth(110);
            t.getColumnModel().getColumn(2).setPreferredWidth(110);
            t.getColumnModel().getColumn(2).setMaxWidth(110);
            t.getColumnModel().getColumn(3).setMinWidth(200);
            t.getColumnModel().getColumn(3).setPreferredWidth(200);
            t.getColumnModel().getColumn(3).setMaxWidth(200);
            t.getColumnModel().getColumn(4).setMinWidth(100);
            t.getColumnModel().getColumn(4).setPreferredWidth(100);
            t.getColumnModel().getColumn(4).setMaxWidth(100);
            t.getColumnModel().getColumn(5).setMinWidth(120);
            t.getColumnModel().getColumn(5).setPreferredWidth(120);
            t.getColumnModel().getColumn(5).setMaxWidth(120);
        }

        a1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 40, 720, 320);

        s.setText("Search");
        s.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sFocusLost(evt);
            }
        });
        s.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sActionPerformed(evt);
            }
        });
        a1.add(s);
        s.setBounds(10, 10, 120, 22);

        c.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cActionPerformed(evt);
            }
        });
        a1.add(c);
        c.setBounds(600, 10, 130, 22);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Sort by Course & Year");
        a1.add(jLabel12);
        jLabel12.setBounds(460, 10, 130, 20);

        card.add(a1, "card2");

        a2.setLayout(null);

        submit.setText("Save");
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });
        a2.add(submit);
        submit.setBounds(450, 310, 72, 23);
        a2.add(cy);
        cy.setBounds(10, 270, 140, 22);

        jLabel11.setText("Coures & Year");
        a2.add(jLabel11);
        jLabel11.setBounds(10, 250, 140, 20);
        a2.add(add);
        add.setBounds(10, 210, 520, 22);

        jLabel9.setText("Address");
        a2.add(jLabel9);
        jLabel9.setBounds(10, 190, 290, 20);

        bd.setDateFormatString("yyyy-MM-dd");
        a2.add(bd);
        bd.setBounds(10, 150, 140, 22);

        jLabel7.setText("Birth Date");
        a2.add(jLabel7);
        jLabel7.setBounds(10, 130, 140, 20);
        a2.add(fn);
        fn.setBounds(10, 90, 140, 22);

        jLabel3.setText("First Name");
        a2.add(jLabel3);
        jLabel3.setBounds(10, 70, 140, 20);
        a2.add(id);
        id.setBounds(10, 40, 290, 22);

        jLabel1.setText("Student Identification Number");
        a2.add(jLabel1);
        jLabel1.setBounds(10, 20, 290, 20);
        a2.add(jSeparator1);
        jSeparator1.setBounds(10, 10, 520, 10);
        a2.add(mn);
        mn.setBounds(160, 90, 140, 22);

        jLabel4.setText("Middle Name");
        a2.add(jLabel4);
        jLabel4.setBounds(160, 70, 140, 20);

        jLabel8.setText("Contact Number");
        a2.add(jLabel8);
        jLabel8.setBounds(160, 130, 140, 20);
        a2.add(no);
        no.setBounds(160, 150, 140, 22);
        a2.add(em);
        em.setBounds(310, 150, 140, 22);

        jLabel10.setText("Email");
        a2.add(jLabel10);
        jLabel10.setBounds(310, 130, 140, 20);

        jLabel5.setText("Last Name");
        a2.add(jLabel5);
        jLabel5.setBounds(310, 70, 140, 20);
        a2.add(ln);
        ln.setBounds(310, 90, 140, 22);

        jLabel6.setText("Suffix");
        a2.add(jLabel6);
        jLabel6.setBounds(460, 70, 70, 20);
        a2.add(sf);
        sf.setBounds(460, 90, 70, 22);

        card.add(a2, "card3");

        getContentPane().add(card);
        card.setBounds(10, 50, 740, 370);

        jButton2.setText("Add Student");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(630, 20, 120, 23);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dn = sdf.format(bd.getDate());
        
        try{
            pst =SQLCONN.prepareStatement("insert into student (`studentId`,`fn`,`mn`,`ln`,`sf`,`add`,`no`,`em`,bd,cy) values (?,?,?,?,?,?,?,?,?,?)");
                 pst.setString(1,id.getText());
                 pst.setString(2,fn.getText());
                 pst.setString(3,mn.getText());
                 pst.setString(4,ln.getText());
                 pst.setString(5,sf.getText());
                 pst.setString(6,add.getText());
                 pst.setString(7,no.getText());
                 pst.setString(8,em.getText());
                 pst.setString(9,dn);
                 pst.setString(10,cy.getText());
                 
                 if(JOptionPane.showConfirmDialog(null, "Confirm Add Book","Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();studentTable();
                    a2.setVisible(false);a1.setVisible(true);
                    JOptionPane.showMessageDialog(null,"Student Added.","Success",JOptionPane.INFORMATION_MESSAGE);
                 }        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_submitActionPerformed

    private void sFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sFocusGained
        if(s.getText().equals("Search")){
            s.setText(null);
        }
    }//GEN-LAST:event_sFocusGained

    private void sFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sFocusLost
        if(s.getText().isBlank()){
            s.setText("Search");
        }
    }//GEN-LAST:event_sFocusLost

    private void sActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sActionPerformed
        if(s.getText().equals("Search") || s.getText().isBlank())
        {
            studentTable();
        }
        else{
             searchStudent();   
                }
    }//GEN-LAST:event_sActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        a1.setVisible(true);
        a2.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        a1.setVisible(false);
        a2.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void cActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cActionPerformed
        try{
            pst = SQLCONN.prepareStatement("select * from student where cy = ?");
            pst.setString(1,c.getSelectedItem().toString());
            
            rs=pst.executeQuery();
            ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) t.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        String fn = rs.getString("fn");
                        String mn = rs.getString("mn");
                        String ln = rs.getString("ln");
                        String full = fn+" "+mn+" "+ln;
                        cd.add(rs.getString("studentId"));
                        cd.add(full);
                        cd.add(rs.getString("no"));
                        cd.add(rs.getString("em"));
                        cd.add(rs.getString("bd"));
                        cd.add(rs.getString("cy"));
                    }
                    bt.addRow(cd);
                }    
            
        }catch(Exception e){
            
        }
    }//GEN-LAST:event_cActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(student.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(student.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(student.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(student.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new student().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel a1;
    private javax.swing.JPanel a2;
    public javax.swing.JTextField add;
    private com.toedter.calendar.JDateChooser bd;
    private javax.swing.JComboBox<String> c;
    private javax.swing.JPanel card;
    public javax.swing.JTextField cy;
    public javax.swing.JTextField em;
    public javax.swing.JTextField fn;
    public javax.swing.JTextField id;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JTextField ln;
    public javax.swing.JTextField mn;
    public javax.swing.JTextField no;
    private javax.swing.JTextField s;
    public javax.swing.JTextField sf;
    private javax.swing.JButton submit;
    private javax.swing.JTable t;
    // End of variables declaration//GEN-END:variables
private boolean isDarkMode = false;
    
     private void updateMode() {
        if (isDarkMode) {
            FlatDarkLaf.install();
        } else {
            FlatLightLaf.install();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }
}
