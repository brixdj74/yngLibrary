package main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.mysql.jdbc.Connection;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author brixd
 */
public class home extends javax.swing.JFrame {

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
    
    public home() {
        initComponents();
        SQLCONN = (com.mysql.jdbc.Connection) con();
        forCat();checkDue();
        updateMode();
        setResizable(false);
        setTitle("YNG Library System");
        setIconImage(logo.getImage()); 
        addKeyListener(new ModeKeyListener());
        forStart();genBookId();genBorrowId();
        time();
        
        //FF - TABLE
        forBook();
        forBorrow();
        forReturn();
    }
    
    public void checkDue(){
        Date dt= new Date();
        SimpleDateFormat dn = new SimpleDateFormat("yyyy-MM-dd");
        String db = dn.format(dt);
        
        try{
            pst = SQLCONN.prepareStatement("update borrow set borrowStatus = ? where dateDue < ? and borrowStatus = ?");
            pst.setString(1,"Expired");
            pst.setString(2,db);
            pst.setString(3,"Active");
            pst.executeUpdate();
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    
    private void forStart(){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); // create a renderer
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // set its alignment
        borrowTable.setDefaultRenderer(Object.class, centerRenderer);
        bookList.setDefaultRenderer(Object.class, centerRenderer);
        bookTable.setDefaultRenderer(Object.class, centerRenderer);
        returnTable.setDefaultRenderer(Object.class, centerRenderer);
        
        bookFunction.setVisible(false);
        //checkId.setVisible(false);
        bookLib.setVisible(false);
        bbQty.setVisible(false);
        bbActive.setVisible(false);
        returnQty.setVisible(false);
        bookId1.setVisible(false);
        activeB.setVisible(false);
        reportStat.setVisible(false);
        showDate.setVisible(false);
        Date currentDate = new Date();
        sbyDate.setDate(currentDate);
        rbyDate.setDate(currentDate);
    }
    
    private void time(){
            Timer t;
            Calendar cl=Calendar.getInstance();

            t= new Timer(0, (ActionEvent e) -> {
                Date dt= new Date();
                SimpleDateFormat dateBorrow=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateReturn = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateNow = new SimpleDateFormat("yyyy-MM-dd");
                String db = dateBorrow.format(dt);
                
                cl.setTime(dt);
                cl.add(Calendar.DAY_OF_YEAR, 3);
                String dr = dateReturn.format(cl.getTime());
        
                borrowDate.setText(db);
                retDate.setText(db);
                returnDate.setText(dr+" 16:00:00");
                
                    });
     t.start();   
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
    
    public void forCat(){
        sortCat.removeAllItems();
        try{
            pst = SQLCONN.prepareStatement("select distinct bookCategory from book order by bookCategory asc");
            rs=pst.executeQuery();
            sortCat.addItem("Book Category");
            while(rs.next()){
                sortCat.addItem(rs.getString("bookCategory"));
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    private void forBook(){
        try{
                //inner join `courses` ON `regform`.`get_course` = `courses`.`course_id` WHERE `regform`.`email`='$username' && `regform`.`reg_id`='$password'";
                pst =SQLCONN.prepareStatement("select * from book order by bookCategory ASC");
                
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel rt = (DefaultTableModel) bookTable.getModel();
                DefaultTableModel bl = (DefaultTableModel) bookList.getModel();
                rt.setRowCount(0);
                bl.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("bookId"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("bookSeries"));
                        cd.add(rs.getString("bookAuthor"));
                        cd.add(rs.getString("bookCategory"));
                        cd.add(rs.getInt("bookShelf"));
                        cd.add(rs.getInt("bookQty"));
                        cd.add(rs.getInt("activeBorrow"));
                    }
                    rt.addRow(cd);
                    bl.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    private void forBorrow(){
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowStatus = ? order by bookCategory ASC");
                pst.setString(1,"Active"); 
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateDue"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    private void forReturn(){
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowStatus=? order by bookCategory ASC");
                pst.setString(1,"Returned");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) returnTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateReturned"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    
    private void genBookId(){
        
        try{
            SQLCONN = (com.mysql.jdbc.Connection) DriverManager.getConnection(DATACONN,USERNAME,PASSWORD);
            pst = (com.mysql.jdbc.PreparedStatement) SQLCONN.prepareStatement("select * from book ORDER BY bookId DESC LIMIT 1");
            rs = pst.executeQuery();
            if(rs.next()){
                String rnno = rs.getString("bookId");
                int co = rnno.length();
                String txt = rnno.substring(0,2);
                String num = rnno.substring(2,co);
                int n = Integer.parseInt(num);
                n++;
                String snum = Integer.toString(n);
                String ftxt = txt+snum;
                BOOKID = ftxt;
                checkId.setText(BOOKID);
            }else{
                BOOKID = "SB1000";
                checkId.setText(BOOKID);
            }
        }catch(NumberFormatException | SQLException e){
            System.out.println(e);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navBar = new javax.swing.JPanel();
        outBtn = new javax.swing.JButton();
        borrowBtn = new javax.swing.JButton();
        returnBtn = new javax.swing.JButton();
        invBtn = new javax.swing.JButton();
        reportBtn = new javax.swing.JButton();
        borrowedBtn = new javax.swing.JButton();
        dueBtn = new javax.swing.JButton();
        setBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        body = new javax.swing.JPanel();
        pnl1 = new javax.swing.JPanel();
        tl1 = new javax.swing.JPanel();
        tlLbl1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        getId = new javax.swing.JTextField();
        findStudent = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        bookList = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        searchBorrow = new javax.swing.JTextField();
        findBorrow = new javax.swing.JButton();
        selectBook = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        studentEmail = new javax.swing.JTextField();
        studentName = new javax.swing.JTextField();
        studentPhone = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        bbName = new javax.swing.JTextField();
        bbAuth = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        sbEmail = new javax.swing.JTextField();
        sbName = new javax.swing.JTextField();
        sbPhone = new javax.swing.JTextField();
        bookBorrow = new javax.swing.JButton();
        bookId = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        returnDate = new javax.swing.JTextField();
        borrowDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        borrowerId = new javax.swing.JLabel();
        bbQty = new com.toedter.components.JSpinField();
        bbActive = new com.toedter.components.JSpinField();
        pnl2 = new javax.swing.JPanel();
        tl2 = new javax.swing.JPanel();
        tlLbl2 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        getBorrow = new javax.swing.JTextField();
        getBorrowBTN = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        returnbookName = new javax.swing.JTextField();
        returnbookAuthor = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        returnName = new javax.swing.JTextField();
        returnEmail = new javax.swing.JTextField();
        bookReturn = new javax.swing.JButton();
        bookId1 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        retDate = new javax.swing.JTextField();
        borDate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        brwDate1 = new javax.swing.JLabel();
        returnQty = new com.toedter.components.JSpinField();
        activeB = new com.toedter.components.JSpinField();
        pnl3 = new javax.swing.JPanel();
        tl3 = new javax.swing.JPanel();
        tlLbl3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        bookSearch = new javax.swing.JTextField();
        bookBtn = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        sortCat = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        bookBtn1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        delBook = new javax.swing.JButton();
        addBook = new javax.swing.JButton();
        editBook = new javax.swing.JButton();
        bookPane = new javax.swing.JScrollPane();
        bookTable = new javax.swing.JTable();
        bookFunction = new javax.swing.JPanel();
        ref = new javax.swing.JButton();
        bookName = new javax.swing.JTextField();
        bookAuth = new javax.swing.JTextField();
        labelTitle = new javax.swing.JLabel();
        forBName = new javax.swing.JLabel();
        forBAuth = new javax.swing.JLabel();
        forBCat = new javax.swing.JLabel();
        bookEdit = new javax.swing.JButton();
        bookAdd = new javax.swing.JButton();
        bookCat = new javax.swing.JComboBox<>();
        checkId = new javax.swing.JLabel();
        forBCode = new javax.swing.JLabel();
        bookCode = new javax.swing.JTextField();
        forBSrs = new javax.swing.JLabel();
        bookSrs = new javax.swing.JTextField();
        forBQty = new javax.swing.JLabel();
        bookQty = new com.toedter.components.JSpinField();
        bookShelf = new com.toedter.components.JSpinField();
        bookLib = new javax.swing.JTextField();
        forBShe = new javax.swing.JLabel();
        refBook = new javax.swing.JButton();
        pnl4 = new javax.swing.JPanel();
        tl4 = new javax.swing.JPanel();
        tlLbl4 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        reportPrint = new javax.swing.JButton();
        reportTo = new com.toedter.calendar.JDateChooser();
        reportFrom = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        reportType = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        reportStat = new javax.swing.JLabel();
        showDate = new javax.swing.JLabel();
        pnl5 = new javax.swing.JPanel();
        tl5 = new javax.swing.JPanel();
        tlLbl5 = new javax.swing.JLabel();
        borrowPane = new javax.swing.JScrollPane();
        borrowTable = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        sbyName = new javax.swing.JTextField();
        fbyName = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        sbyDate = new com.toedter.calendar.JDateChooser();
        fbyDate = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        sbyBook = new javax.swing.JTextField();
        fbyBook = new javax.swing.JButton();
        refBorrow = new javax.swing.JButton();
        ddr = new javax.swing.JButton();
        er = new javax.swing.JButton();
        ddr1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        pnl6 = new javax.swing.JPanel();
        tl6 = new javax.swing.JPanel();
        tlLbl6 = new javax.swing.JLabel();
        borrowPane1 = new javax.swing.JScrollPane();
        returnTable = new javax.swing.JTable();
        jPanel19 = new javax.swing.JPanel();
        rbyName = new javax.swing.JTextField();
        fbyName1 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        rbyDate = new com.toedter.calendar.JDateChooser();
        fbyDate1 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        rbyBook = new javax.swing.JTextField();
        fbyBook1 = new javax.swing.JButton();
        refReturn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        setSize(new java.awt.Dimension(1000, 600));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        navBar.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        navBar.setPreferredSize(new java.awt.Dimension(1000, 40));
        navBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navBarMouseClicked(evt);
            }
        });
        navBar.setLayout(null);

        outBtn.setText("Log Out");
        outBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outBtnActionPerformed(evt);
            }
        });
        navBar.add(outBtn);
        outBtn.setBounds(910, 10, 80, 20);

        borrowBtn.setText("Borrow");
        borrowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrowBtnActionPerformed(evt);
            }
        });
        navBar.add(borrowBtn);
        borrowBtn.setBounds(10, 10, 80, 20);

        returnBtn.setText("Return");
        returnBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnBtnActionPerformed(evt);
            }
        });
        navBar.add(returnBtn);
        returnBtn.setBounds(100, 10, 80, 20);

        invBtn.setText("Books");
        invBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invBtnActionPerformed(evt);
            }
        });
        navBar.add(invBtn);
        invBtn.setBounds(190, 10, 80, 20);

        reportBtn.setText("Reports");
        reportBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportBtnActionPerformed(evt);
            }
        });
        navBar.add(reportBtn);
        reportBtn.setBounds(280, 10, 80, 20);

        borrowedBtn.setText("Borrowed");
        borrowedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrowedBtnActionPerformed(evt);
            }
        });
        navBar.add(borrowedBtn);
        borrowedBtn.setBounds(370, 10, 100, 23);

        dueBtn.setText("Returned");
        dueBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dueBtnActionPerformed(evt);
            }
        });
        navBar.add(dueBtn);
        dueBtn.setBounds(480, 10, 80, 23);

        setBtn.setText("Librarian");
        setBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setBtnActionPerformed(evt);
            }
        });
        navBar.add(setBtn);
        setBtn.setBounds(810, 10, 80, 20);

        addBtn.setText("Student");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });
        navBar.add(addBtn);
        addBtn.setBounds(720, 10, 80, 20);

        getContentPane().add(navBar, java.awt.BorderLayout.PAGE_START);

        body.setBackground(new java.awt.Color(250, 250, 250));
        body.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bodyMouseClicked(evt);
            }
        });
        body.setLayout(new java.awt.CardLayout());

        pnl1.setLayout(null);

        tl1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl1.setLayout(new java.awt.BorderLayout());

        tlLbl1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        tlLbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl1.setText("BORROW BOOK");
        tl1.add(tlLbl1, java.awt.BorderLayout.CENTER);

        pnl1.add(tl1);
        tl1.setBounds(10, 10, 200, 40);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setLayout(null);

        getId.setText("Student Number");
        getId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                getIdFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                getIdFocusLost(evt);
            }
        });
        getId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getIdActionPerformed(evt);
            }
        });
        jPanel1.add(getId);
        getId.setBounds(10, 10, 200, 20);

        findStudent.setText("Search");
        findStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findStudentActionPerformed(evt);
            }
        });
        jPanel1.add(findStudent);
        findStudent.setBounds(220, 10, 72, 23);

        pnl1.add(jPanel1);
        jPanel1.setBounds(10, 60, 310, 40);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        bookList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Book", "Series", "Author", "Category", "Shelf", "Qty", "Borrowed"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bookList.setRowHeight(30);
        bookList.setShowGrid(true);
        jScrollPane1.setViewportView(bookList);
        if (bookList.getColumnModel().getColumnCount() > 0) {
            bookList.getColumnModel().getColumn(0).setMinWidth(0);
            bookList.getColumnModel().getColumn(0).setPreferredWidth(0);
            bookList.getColumnModel().getColumn(0).setMaxWidth(0);
            bookList.getColumnModel().getColumn(2).setMinWidth(60);
            bookList.getColumnModel().getColumn(2).setPreferredWidth(60);
            bookList.getColumnModel().getColumn(2).setMaxWidth(60);
            bookList.getColumnModel().getColumn(5).setMinWidth(60);
            bookList.getColumnModel().getColumn(5).setMaxWidth(60);
            bookList.getColumnModel().getColumn(6).setMinWidth(30);
            bookList.getColumnModel().getColumn(6).setMaxWidth(30);
            bookList.getColumnModel().getColumn(7).setMinWidth(60);
            bookList.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pnl1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 160, 960, 160);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setLayout(null);

        searchBorrow.setText("Search Book");
        searchBorrow.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchBorrowFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchBorrowFocusLost(evt);
            }
        });
        searchBorrow.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchBorrowKeyReleased(evt);
            }
        });
        jPanel2.add(searchBorrow);
        searchBorrow.setBounds(10, 10, 200, 20);

        findBorrow.setText("Find");
        findBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findBorrowActionPerformed(evt);
            }
        });
        jPanel2.add(findBorrow);
        findBorrow.setBounds(220, 10, 72, 23);

        pnl1.add(jPanel2);
        jPanel2.setBounds(10, 110, 310, 40);

        selectBook.setText("Select Book");
        selectBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBookActionPerformed(evt);
            }
        });
        pnl1.add(selectBook);
        selectBook.setBounds(870, 120, 100, 23);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel4.setLayout(null);

        studentEmail.setEditable(false);
        studentEmail.setText("Email Address");
        jPanel4.add(studentEmail);
        studentEmail.setBounds(430, 10, 200, 20);

        studentName.setEditable(false);
        studentName.setText("Student Name");
        jPanel4.add(studentName);
        studentName.setBounds(10, 10, 200, 20);

        studentPhone.setEditable(false);
        studentPhone.setText("Contact Number");
        jPanel4.add(studentPhone);
        studentPhone.setBounds(220, 10, 200, 20);

        pnl1.add(jPanel4);
        jPanel4.setBounds(330, 60, 640, 40);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel5.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Borrow Book Summary");
        jPanel5.add(jLabel2);
        jLabel2.setBounds(10, 0, 200, 30);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel7.setLayout(null);

        bbName.setEditable(false);
        bbName.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        bbName.setText("Book Name");
        jPanel7.add(bbName);
        bbName.setBounds(10, 10, 410, 20);

        bbAuth.setEditable(false);
        bbAuth.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        bbAuth.setText("Book Author");
        jPanel7.add(bbAuth);
        bbAuth.setBounds(430, 10, 200, 20);

        jPanel5.add(jPanel7);
        jPanel7.setBounds(20, 80, 640, 40);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel6.setLayout(null);

        sbEmail.setEditable(false);
        sbEmail.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        sbEmail.setText("Email Address");
        jPanel6.add(sbEmail);
        sbEmail.setBounds(430, 10, 200, 20);

        sbName.setEditable(false);
        sbName.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        sbName.setText("Student Name");
        jPanel6.add(sbName);
        sbName.setBounds(10, 10, 200, 20);

        sbPhone.setEditable(false);
        sbPhone.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        sbPhone.setText("Contact Number");
        jPanel6.add(sbPhone);
        sbPhone.setBounds(220, 10, 200, 20);

        jPanel5.add(jPanel6);
        jPanel6.setBounds(20, 30, 640, 40);

        bookBorrow.setText("Borrow");
        bookBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookBorrowActionPerformed(evt);
            }
        });
        jPanel5.add(bookBorrow);
        bookBorrow.setBounds(870, 130, 72, 23);
        jPanel5.add(bookId);
        bookId.setBounds(20, 120, 210, 20);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));
        jPanel10.setLayout(null);

        returnDate.setEditable(false);
        returnDate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        returnDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        returnDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel10.add(returnDate);
        returnDate.setBounds(110, 50, 140, 18);

        borrowDate.setEditable(false);
        borrowDate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        borrowDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        borrowDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel10.add(borrowDate);
        borrowDate.setBounds(110, 20, 140, 18);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Return Date:");
        jPanel10.add(jLabel3);
        jLabel3.setBounds(20, 50, 80, 20);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Borrow Date:");
        jPanel10.add(jLabel4);
        jLabel4.setBounds(20, 20, 80, 20);

        jPanel5.add(jPanel10);
        jPanel10.setBounds(680, 30, 260, 90);

        borrowerId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        borrowerId.setText("Borrow Id");
        jPanel5.add(borrowerId);
        borrowerId.setBounds(677, 10, 260, 16);
        jPanel5.add(bbQty);
        bbQty.setBounds(244, 130, 190, 22);
        jPanel5.add(bbActive);
        bbActive.setBounds(244, 130, 190, 22);

        pnl1.add(jPanel5);
        jPanel5.setBounds(10, 330, 960, 170);

        body.add(pnl1, "card2");

        pnl2.setLayout(null);

        tl2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl2.setLayout(new java.awt.BorderLayout());

        tlLbl2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        tlLbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl2.setText("RETURN BOOK");
        tl2.add(tlLbl2, java.awt.BorderLayout.CENTER);

        pnl2.add(tl2);
        tl2.setBounds(10, 10, 200, 40);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel13.setLayout(null);

        getBorrow.setText("Search Borrow ID");
        getBorrow.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                getBorrowFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                getBorrowFocusLost(evt);
            }
        });
        getBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getBorrowActionPerformed(evt);
            }
        });
        jPanel13.add(getBorrow);
        getBorrow.setBounds(10, 10, 200, 20);

        getBorrowBTN.setText("Search");
        getBorrowBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getBorrowBTNActionPerformed(evt);
            }
        });
        jPanel13.add(getBorrowBTN);
        getBorrowBTN.setBounds(220, 10, 72, 23);

        pnl2.add(jPanel13);
        jPanel13.setBounds(10, 60, 310, 40);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel15.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Return Book Summary");
        jPanel15.add(jLabel6);
        jLabel6.setBounds(10, 0, 200, 30);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel16.setLayout(null);

        returnbookName.setEditable(false);
        returnbookName.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        returnbookName.setText("Book Name");
        jPanel16.add(returnbookName);
        returnbookName.setBounds(10, 10, 410, 20);

        returnbookAuthor.setEditable(false);
        returnbookAuthor.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        returnbookAuthor.setText("Book Author");
        jPanel16.add(returnbookAuthor);
        returnbookAuthor.setBounds(430, 10, 200, 20);

        jPanel15.add(jPanel16);
        jPanel16.setBounds(20, 80, 640, 40);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel17.setLayout(null);

        returnName.setEditable(false);
        returnName.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        returnName.setText("Student Name");
        jPanel17.add(returnName);
        returnName.setBounds(10, 10, 200, 20);

        returnEmail.setEditable(false);
        returnEmail.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        returnEmail.setText("Student Email");
        jPanel17.add(returnEmail);
        returnEmail.setBounds(220, 10, 200, 20);

        jPanel15.add(jPanel17);
        jPanel17.setBounds(20, 30, 640, 40);

        bookReturn.setText("Return Book");
        bookReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookReturnActionPerformed(evt);
            }
        });
        jPanel15.add(bookReturn);
        bookReturn.setBounds(822, 130, 120, 23);
        jPanel15.add(bookId1);
        bookId1.setBounds(20, 120, 210, 20);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Return Date"));
        jPanel18.setLayout(null);

        retDate.setEditable(false);
        retDate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        retDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        retDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel18.add(retDate);
        retDate.setBounds(110, 50, 140, 18);

        borDate.setEditable(false);
        borDate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        borDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        borDate.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel18.add(borDate);
        borDate.setBounds(110, 20, 140, 18);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Return Date:");
        jPanel18.add(jLabel7);
        jLabel7.setBounds(20, 50, 80, 20);

        brwDate1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        brwDate1.setText("Borrow Date:");
        jPanel18.add(brwDate1);
        brwDate1.setBounds(20, 20, 80, 20);

        jPanel15.add(jPanel18);
        jPanel18.setBounds(680, 30, 260, 90);
        jPanel15.add(returnQty);
        returnQty.setBounds(30, 130, 190, 22);
        jPanel15.add(activeB);
        activeB.setBounds(244, 130, 190, 22);

        pnl2.add(jPanel15);
        jPanel15.setBounds(10, 120, 960, 170);

        body.add(pnl2, "card2");

        pnl3.setLayout(null);

        tl3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl3.setLayout(new java.awt.BorderLayout());

        tlLbl3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tlLbl3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl3.setText("BOOKS");
        tl3.add(tlLbl3, java.awt.BorderLayout.CENTER);

        pnl3.add(tl3);
        tl3.setBounds(10, 10, 200, 40);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setLayout(null);

        bookSearch.setText("Search");
        bookSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bookSearchFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                bookSearchFocusLost(evt);
            }
        });
        bookSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                bookSearchKeyReleased(evt);
            }
        });
        jPanel3.add(bookSearch);
        bookSearch.setBounds(10, 10, 150, 20);

        bookBtn.setText("Search");
        bookBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookBtnActionPerformed(evt);
            }
        });
        jPanel3.add(bookBtn);
        bookBtn.setBounds(170, 10, 72, 20);

        pnl3.add(jPanel3);
        jPanel3.setBounds(10, 60, 250, 40);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel8.setLayout(null);

        sortCat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category", "CT Research", "TM Research", "CRIM Research", "OJT Research", "General Education", "Information Technology", "English", "Mathematics", "Science", "Philippine History" }));
        sortCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortCatActionPerformed(evt);
            }
        });
        jPanel8.add(sortCat);
        sortCat.setBounds(60, 10, 160, 20);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Sort by");
        jPanel8.add(jLabel1);
        jLabel1.setBounds(10, 10, 40, 20);

        bookBtn1.setText("Sort");
        bookBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookBtn1ActionPerformed(evt);
            }
        });
        jPanel8.add(bookBtn1);
        bookBtn1.setBounds(230, 10, 72, 20);

        pnl3.add(jPanel8);
        jPanel8.setBounds(270, 60, 310, 40);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel9.setLayout(null);

        delBook.setText("Delete");
        delBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delBookActionPerformed(evt);
            }
        });
        jPanel9.add(delBook);
        delBook.setBounds(170, 10, 70, 20);

        addBook.setText("Add");
        addBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookActionPerformed(evt);
            }
        });
        jPanel9.add(addBook);
        addBook.setBounds(10, 10, 70, 20);

        editBook.setText("Edit");
        editBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBookActionPerformed(evt);
            }
        });
        jPanel9.add(editBook);
        editBook.setBounds(90, 10, 70, 20);

        pnl3.add(jPanel9);
        jPanel9.setBounds(590, 60, 250, 40);

        bookTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Book", "Series", "Author", "Category", "Shelf", "Qty", "Borrowed"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bookTable.setRowHeight(30);
        bookTable.setShowVerticalLines(true);
        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookTableMouseClicked(evt);
            }
        });
        bookPane.setViewportView(bookTable);
        if (bookTable.getColumnModel().getColumnCount() > 0) {
            bookTable.getColumnModel().getColumn(0).setMinWidth(0);
            bookTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            bookTable.getColumnModel().getColumn(0).setMaxWidth(0);
            bookTable.getColumnModel().getColumn(2).setMinWidth(60);
            bookTable.getColumnModel().getColumn(2).setPreferredWidth(60);
            bookTable.getColumnModel().getColumn(2).setMaxWidth(60);
            bookTable.getColumnModel().getColumn(3).setMinWidth(200);
            bookTable.getColumnModel().getColumn(3).setMaxWidth(200);
            bookTable.getColumnModel().getColumn(4).setMinWidth(10);
            bookTable.getColumnModel().getColumn(4).setPreferredWidth(200);
            bookTable.getColumnModel().getColumn(4).setMaxWidth(200);
            bookTable.getColumnModel().getColumn(5).setMinWidth(60);
            bookTable.getColumnModel().getColumn(5).setMaxWidth(60);
            bookTable.getColumnModel().getColumn(6).setMinWidth(30);
            bookTable.getColumnModel().getColumn(6).setPreferredWidth(30);
            bookTable.getColumnModel().getColumn(6).setMaxWidth(30);
            bookTable.getColumnModel().getColumn(7).setMinWidth(60);
            bookTable.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pnl3.add(bookPane);
        bookPane.setBounds(10, 110, 950, 400);

        bookFunction.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bookFunction.setLayout(null);

        ref.setText("....");
        ref.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refActionPerformed(evt);
            }
        });
        bookFunction.add(ref);
        ref.setBounds(275, 10, 30, 23);
        bookFunction.add(bookName);
        bookName.setBounds(30, 60, 240, 30);
        bookFunction.add(bookAuth);
        bookAuth.setBounds(30, 110, 240, 30);

        labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTitle.setText("Set Library Book");
        bookFunction.add(labelTitle);
        labelTitle.setBounds(0, 0, 310, 40);

        forBName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBName.setText("Book Name");
        bookFunction.add(forBName);
        forBName.setBounds(30, 40, 240, 20);

        forBAuth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBAuth.setText("Book Author");
        bookFunction.add(forBAuth);
        forBAuth.setBounds(30, 90, 240, 20);

        forBCat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBCat.setText("Book Category");
        bookFunction.add(forBCat);
        forBCat.setBounds(30, 140, 240, 20);

        bookEdit.setText("Edit");
        bookEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookEditActionPerformed(evt);
            }
        });
        bookFunction.add(bookEdit);
        bookEdit.setBounds(120, 360, 70, 23);

        bookAdd.setText("Save");
        bookAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookAddActionPerformed(evt);
            }
        });
        bookFunction.add(bookAdd);
        bookAdd.setBounds(200, 360, 70, 23);

        bookCat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category", "CT Research", "TM Research", "CRIM Research", "OJT Research", "General Education", "Information Technology", "English", "Mathematics", "Science", "Philippine History" }));
        bookFunction.add(bookCat);
        bookCat.setBounds(30, 160, 240, 30);

        checkId.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        bookFunction.add(checkId);
        checkId.setBounds(30, 10, 60, 20);

        forBCode.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBCode.setText("bookCode");
        bookFunction.add(forBCode);
        forBCode.setBounds(30, 190, 240, 20);
        bookFunction.add(bookCode);
        bookCode.setBounds(30, 210, 240, 30);

        forBSrs.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBSrs.setText("Book Series");
        bookFunction.add(forBSrs);
        forBSrs.setBounds(30, 240, 240, 20);
        bookFunction.add(bookSrs);
        bookSrs.setBounds(30, 260, 240, 30);

        forBQty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBQty.setText("Quantity");
        bookFunction.add(forBQty);
        forBQty.setBounds(30, 290, 120, 20);

        bookQty.setMaximum(100);
        bookQty.setMinimum(0);
        bookQty.setValue(1);
        bookFunction.add(bookQty);
        bookQty.setBounds(30, 310, 110, 30);

        bookShelf.setMaximum(100);
        bookShelf.setMinimum(1);
        bookShelf.setValue(1);
        bookFunction.add(bookShelf);
        bookShelf.setBounds(160, 310, 110, 30);

        bookLib.setEditable(false);
        bookFunction.add(bookLib);
        bookLib.setBounds(30, 360, 240, 30);

        forBShe.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        forBShe.setText("Book Shelf");
        bookFunction.add(forBShe);
        forBShe.setBounds(160, 290, 110, 20);

        pnl3.add(bookFunction);
        bookFunction.setBounds(650, 110, 310, 400);

        refBook.setText("Refresh");
        refBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refBookActionPerformed(evt);
            }
        });
        pnl3.add(refBook);
        refBook.setBounds(850, 70, 100, 23);

        body.add(pnl3, "card2");

        pnl4.setLayout(null);

        tl4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl4.setLayout(new java.awt.BorderLayout());

        tlLbl4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tlLbl4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl4.setText("REPORTS");
        tl4.add(tlLbl4, java.awt.BorderLayout.CENTER);

        pnl4.add(tl4);
        tl4.setBounds(10, 10, 200, 40);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Generate Report Section"));
        jPanel20.setLayout(null);

        reportPrint.setText("Print");
        reportPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportPrintActionPerformed(evt);
            }
        });
        jPanel20.add(reportPrint);
        reportPrint.setBounds(190, 150, 75, 23);

        reportTo.setDateFormatString("yyyy-MM-dd");
        jPanel20.add(reportTo);
        reportTo.setBounds(160, 90, 100, 22);

        reportFrom.setDateFormatString("yyyy-MM-dd");
        jPanel20.add(reportFrom);
        reportFrom.setBounds(20, 90, 100, 22);

        jLabel8.setText("TO");
        jPanel20.add(jLabel8);
        jLabel8.setBounds(130, 90, 20, 20);

        reportType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Report", "Borrow Book Report", "Return Book Report", "Due Exceed Book Report", "Summary Report" }));
        reportType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportTypeActionPerformed(evt);
            }
        });
        jPanel20.add(reportType);
        reportType.setBounds(20, 40, 240, 22);
        jPanel20.add(jSeparator1);
        jSeparator1.setBounds(20, 80, 240, 3);
        jPanel20.add(jSeparator2);
        jSeparator2.setBounds(20, 130, 240, 3);
        jPanel20.add(reportStat);
        reportStat.setBounds(20, 150, 80, 20);

        pnl4.add(jPanel20);
        jPanel20.setBounds(10, 70, 280, 190);

        showDate.setText("0");
        pnl4.add(showDate);
        showDate.setBounds(20, 270, 520, 30);

        body.add(pnl4, "card2");

        pnl5.setLayout(null);

        tl5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl5.setLayout(new java.awt.BorderLayout());

        tlLbl5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tlLbl5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl5.setText("BORROWED BOOK/S");
        tl5.add(tlLbl5, java.awt.BorderLayout.CENTER);

        pnl5.add(tl5);
        tl5.setBounds(10, 10, 200, 40);

        borrowTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Borrower ID", "Borrower Name", "Book", "Borrow Date", "Due Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        borrowTable.setRowHeight(30);
        borrowTable.setShowVerticalLines(true);
        borrowTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                borrowTableMouseClicked(evt);
            }
        });
        borrowPane.setViewportView(borrowTable);
        if (borrowTable.getColumnModel().getColumnCount() > 0) {
            borrowTable.getColumnModel().getColumn(0).setMinWidth(80);
            borrowTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            borrowTable.getColumnModel().getColumn(0).setMaxWidth(80);
            borrowTable.getColumnModel().getColumn(1).setMinWidth(100);
            borrowTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            borrowTable.getColumnModel().getColumn(1).setMaxWidth(100);
            borrowTable.getColumnModel().getColumn(2).setMinWidth(150);
            borrowTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            borrowTable.getColumnModel().getColumn(2).setMaxWidth(150);
            borrowTable.getColumnModel().getColumn(4).setMinWidth(200);
            borrowTable.getColumnModel().getColumn(4).setMaxWidth(200);
            borrowTable.getColumnModel().getColumn(5).setMinWidth(200);
            borrowTable.getColumnModel().getColumn(5).setMaxWidth(200);
        }

        pnl5.add(borrowPane);
        borrowPane.setBounds(10, 110, 950, 400);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel11.setLayout(null);

        sbyName.setText("Search by Name");
        sbyName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sbyNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sbyNameFocusLost(evt);
            }
        });
        jPanel11.add(sbyName);
        sbyName.setBounds(10, 10, 150, 20);

        fbyName.setText("Search");
        fbyName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyNameActionPerformed(evt);
            }
        });
        jPanel11.add(fbyName);
        fbyName.setBounds(170, 10, 72, 20);

        pnl5.add(jPanel11);
        jPanel11.setBounds(10, 60, 250, 40);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel12.setLayout(null);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Sort Borrow Date");
        jPanel12.add(jLabel5);
        jLabel5.setBounds(10, 10, 90, 20);

        sbyDate.setDateFormatString("yyyy-MM-dd");
        jPanel12.add(sbyDate);
        sbyDate.setBounds(110, 10, 100, 20);

        fbyDate.setText("Search");
        fbyDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyDateActionPerformed(evt);
            }
        });
        jPanel12.add(fbyDate);
        fbyDate.setBounds(220, 10, 80, 20);

        pnl5.add(jPanel12);
        jPanel12.setBounds(530, 60, 310, 40);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel14.setLayout(null);

        sbyBook.setText("Search by Book");
        sbyBook.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sbyBookFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                sbyBookFocusLost(evt);
            }
        });
        sbyBook.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sbyBookKeyReleased(evt);
            }
        });
        jPanel14.add(sbyBook);
        sbyBook.setBounds(10, 10, 150, 20);

        fbyBook.setText("Search");
        fbyBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyBookActionPerformed(evt);
            }
        });
        jPanel14.add(fbyBook);
        fbyBook.setBounds(170, 10, 72, 20);

        pnl5.add(jPanel14);
        jPanel14.setBounds(270, 60, 250, 40);

        refBorrow.setText("Refresh");
        refBorrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refBorrowActionPerformed(evt);
            }
        });
        pnl5.add(refBorrow);
        refBorrow.setBounds(850, 70, 100, 23);

        ddr.setText("Due Date Reminder");
        ddr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddrActionPerformed(evt);
            }
        });
        pnl5.add(ddr);
        ddr.setBounds(220, 30, 150, 23);

        er.setText("Email Reminder");
        er.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                erActionPerformed(evt);
            }
        });
        pnl5.add(er);
        er.setBounds(380, 30, 120, 23);

        ddr1.setText("View Due Date Exceeded");
        ddr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddr1ActionPerformed(evt);
            }
        });
        pnl5.add(ddr1);
        ddr1.setBounds(770, 30, 180, 23);

        jLabel10.setForeground(java.awt.Color.gray);
        jLabel10.setText("*Click (Due Date Reminder) first before confirm (Email Reminder)");
        pnl5.add(jLabel10);
        jLabel10.setBounds(220, 10, 360, 20);

        body.add(pnl5, "card2");

        pnl6.setLayout(null);

        tl6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        tl6.setLayout(new java.awt.BorderLayout());

        tlLbl6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tlLbl6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tlLbl6.setText("RETURNED BOOK/S");
        tl6.add(tlLbl6, java.awt.BorderLayout.CENTER);

        pnl6.add(tl6);
        tl6.setBounds(10, 10, 200, 40);

        returnTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "#", "Borrower ID", "Borrower Name", "Book", "Borrow Date", "Return Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        returnTable.setRowHeight(30);
        returnTable.setShowVerticalLines(true);
        returnTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnTableMouseClicked(evt);
            }
        });
        borrowPane1.setViewportView(returnTable);
        if (returnTable.getColumnModel().getColumnCount() > 0) {
            returnTable.getColumnModel().getColumn(0).setMinWidth(80);
            returnTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            returnTable.getColumnModel().getColumn(0).setMaxWidth(80);
            returnTable.getColumnModel().getColumn(1).setMinWidth(100);
            returnTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            returnTable.getColumnModel().getColumn(1).setMaxWidth(100);
            returnTable.getColumnModel().getColumn(2).setMinWidth(150);
            returnTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            returnTable.getColumnModel().getColumn(2).setMaxWidth(150);
            returnTable.getColumnModel().getColumn(4).setMinWidth(200);
            returnTable.getColumnModel().getColumn(4).setMaxWidth(200);
            returnTable.getColumnModel().getColumn(5).setMinWidth(200);
            returnTable.getColumnModel().getColumn(5).setMaxWidth(200);
        }

        pnl6.add(borrowPane1);
        borrowPane1.setBounds(10, 110, 950, 400);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel19.setLayout(null);

        rbyName.setText("Search by Name");
        rbyName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rbyNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                rbyNameFocusLost(evt);
            }
        });
        rbyName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbyNameActionPerformed(evt);
            }
        });
        jPanel19.add(rbyName);
        rbyName.setBounds(10, 10, 150, 20);

        fbyName1.setText("Search");
        fbyName1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyName1ActionPerformed(evt);
            }
        });
        jPanel19.add(fbyName1);
        fbyName1.setBounds(170, 10, 72, 20);

        pnl6.add(jPanel19);
        jPanel19.setBounds(10, 60, 250, 40);

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel21.setLayout(null);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Sort by Date");
        jPanel21.add(jLabel9);
        jLabel9.setBounds(10, 10, 70, 20);

        rbyDate.setDateFormatString("yyyy-MM-dd");
        jPanel21.add(rbyDate);
        rbyDate.setBounds(90, 10, 120, 20);

        fbyDate1.setText("Search");
        fbyDate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyDate1ActionPerformed(evt);
            }
        });
        jPanel21.add(fbyDate1);
        fbyDate1.setBounds(220, 10, 72, 20);

        pnl6.add(jPanel21);
        jPanel21.setBounds(530, 60, 310, 40);

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel22.setLayout(null);

        rbyBook.setText("Search by Book");
        rbyBook.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rbyBookFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                rbyBookFocusLost(evt);
            }
        });
        rbyBook.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rbyBookKeyReleased(evt);
            }
        });
        jPanel22.add(rbyBook);
        rbyBook.setBounds(10, 10, 150, 20);

        fbyBook1.setText("Search");
        fbyBook1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fbyBook1ActionPerformed(evt);
            }
        });
        jPanel22.add(fbyBook1);
        fbyBook1.setBounds(170, 10, 72, 20);

        pnl6.add(jPanel22);
        jPanel22.setBounds(270, 60, 250, 40);

        refReturn.setText("Refresh");
        refReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refReturnActionPerformed(evt);
            }
        });
        pnl6.add(refReturn);
        refReturn.setBounds(850, 70, 100, 23);

        body.add(pnl6, "card2");

        getContentPane().add(body, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        super.requestFocus();
    }//GEN-LAST:event_formMouseClicked

    private void navBarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_navBarMouseClicked
        super.requestFocus();
    }//GEN-LAST:event_navBarMouseClicked

    private void bodyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bodyMouseClicked
        super.requestFocus();
    }//GEN-LAST:event_bodyMouseClicked

    private void outBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outBtnActionPerformed
        
        if(JOptionPane.showConfirmDialog(null,"Continue to Log Out?","Log Out",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
        dispose();
        new index().setVisible(true);
        }
    }//GEN-LAST:event_outBtnActionPerformed

    private void borrowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowBtnActionPerformed
        pnl1.setVisible(true);
        pnl2.setVisible(false);
        pnl3.setVisible(false);
        pnl4.setVisible(false);
        pnl5.setVisible(false);
        pnl6.setVisible(false);
    }//GEN-LAST:event_borrowBtnActionPerformed

    private void returnBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnBtnActionPerformed
        pnl1.setVisible(false);
        pnl2.setVisible(true);
        pnl3.setVisible(false);
        pnl4.setVisible(false);
        pnl5.setVisible(false);
        pnl6.setVisible(false);
    }//GEN-LAST:event_returnBtnActionPerformed

    private void invBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invBtnActionPerformed
        pnl1.setVisible(false);
        pnl2.setVisible(false);
        pnl3.setVisible(true);
        pnl4.setVisible(false);
        pnl5.setVisible(false);
        pnl6.setVisible(false);
    }//GEN-LAST:event_invBtnActionPerformed

    private void reportBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportBtnActionPerformed
        pnl1.setVisible(false);
        pnl2.setVisible(false);
        pnl3.setVisible(false);
        pnl4.setVisible(true);
        pnl5.setVisible(false);
        pnl6.setVisible(false);
    }//GEN-LAST:event_reportBtnActionPerformed

    private void borrowedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowedBtnActionPerformed
        pnl1.setVisible(false);
        pnl2.setVisible(false);
        pnl3.setVisible(false);
        pnl4.setVisible(false);
        pnl5.setVisible(true);
        pnl6.setVisible(false);
    }//GEN-LAST:event_borrowedBtnActionPerformed

    private void dueBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dueBtnActionPerformed
        pnl1.setVisible(false);
        pnl2.setVisible(false);
        pnl3.setVisible(false);
        pnl4.setVisible(false);
        pnl5.setVisible(false);
        pnl6.setVisible(true);
    }//GEN-LAST:event_dueBtnActionPerformed
    
    private void addBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBookActionPerformed
        checkPanel();
        bookEdit.setEnabled(false);
        bookAdd.setEnabled(true);
        genBookId();
    }//GEN-LAST:event_addBookActionPerformed

    private void bookAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookAddActionPerformed
        String getCat = bookCat.getSelectedItem().toString();
        String getBook = bookName.getText();
        String getAuth = bookAuth.getText();
        String getKeep = bookLib.getText();
        if(getCat.equals("Select Category")){
            JOptionPane.showMessageDialog(null,"Select Category","Error",JOptionPane.ERROR_MESSAGE);
        }else
        if(getBook.isBlank()){
            JOptionPane.showMessageDialog(null,"Fill up Book Name.","Error",JOptionPane.ERROR_MESSAGE);
        }else
        if(getAuth.isBlank()){
            JOptionPane.showMessageDialog(null,"Fill up Book Author.","Error",JOptionPane.ERROR_MESSAGE);
        }
        else
        if(getKeep.isBlank()){
            JOptionPane.showMessageDialog(null,"Book Keeper is blank.\nPlease.\nTry to Log In again.","Error",JOptionPane.ERROR_MESSAGE);
        }
        else{
            forAddBook();
        }
    }//GEN-LAST:event_bookAddActionPerformed

    private void editBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBookActionPerformed
        checkPanel();
        bookEdit.setEnabled(true);
        bookAdd.setEnabled(false);
        
        DefaultTableModel dtm = (DefaultTableModel)bookTable.getModel();
        int si = bookTable.getSelectedRow();
        
        try{
            checkId.setText(dtm.getValueAt(si,0).toString());
            BOOKID= dtm.getValueAt(si,0).toString();

            pst =SQLCONN.prepareStatement("select * from book where bookId = ?");
            pst.setString(1,BOOKID);
                
            rs = pst.executeQuery();
            
            if(rs.next()){
                    bookName.setText(rs.getString("bookName"));
                    bookAuth.setText(rs.getString("bookAuthor"));
                    bookCat.setSelectedItem(rs.getString("bookCategory"));
                    bookCode.setText(rs.getString("bookCode"));
                    bookSrs.setText(rs.getString("bookSeries"));
                    bookQty.setValue(rs.getInt("bookQty"));
                    bookShelf.setValue(rs.getInt("bookShelf"));
                }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"No Selected Book.\n"+e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editBookActionPerformed

    private void bookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookTableMouseClicked
        if(bookEdit.isEnabled()){
            DefaultTableModel dtm = (DefaultTableModel)bookTable.getModel();
            int si = bookTable.getSelectedRow();
        
            try{
                checkId.setText(dtm.getValueAt(si,0).toString());
                BOOKID= dtm.getValueAt(si,0).toString();

                pst =SQLCONN.prepareStatement("select * from book where bookId = ?");
                pst.setString(1,BOOKID);
            
                rs = pst.executeQuery();

                if(rs.next()){
                    bookName.setText(rs.getString("bookName"));
                    bookAuth.setText(rs.getString("bookAuthor"));
                    bookCat.setSelectedItem(rs.getString("bookCategory"));
                    bookCode.setText(rs.getString("bookCode"));
                    bookSrs.setText(rs.getString("bookSeries"));
                    bookQty.setValue(rs.getInt("bookQty"));
                    bookShelf.setValue(rs.getInt("bookShelf"));
                }
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"No Selected Book.\n"+e,"Error",JOptionPane.ERROR_MESSAGE);
            }
        }  
    }//GEN-LAST:event_bookTableMouseClicked

    private void delBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBookActionPerformed
        DefaultTableModel dtm = (DefaultTableModel)bookTable.getModel();
        int si = bookTable.getSelectedRow();
        
        try{
            checkId.setText(dtm.getValueAt(si,0).toString());
            BOOKID= dtm.getValueAt(si,0).toString();
            
            pst =SQLCONN.prepareStatement("delete from book where bookId=?");
            pst.setString(1,dtm.getValueAt(si,0).toString());
            
            if(JOptionPane.showConfirmDialog(null, "Confirm Delete Book\n"+dtm.getValueAt(si,1),"Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();forBook();setBookNull();genBookId();
                    JOptionPane.showMessageDialog(null,"Book Deleted.","Success",JOptionPane.INFORMATION_MESSAGE);
                 }
            
        }catch(HeadlessException | SQLException e){
            JOptionPane.showMessageDialog(null,"No Selected Book.\n"+e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_delBookActionPerformed

    private void bookEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookEditActionPerformed
        forEditBook();
    }//GEN-LAST:event_bookEditActionPerformed

    private void refActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refActionPerformed
        genBookId();
        setBookNull();
    }//GEN-LAST:event_refActionPerformed

    private void findStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findStudentActionPerformed
        try{
                 pst =SQLCONN.prepareStatement("select * from student where studentId = ?");
                 pst.setString(1,getId.getText());
                 rs= pst.executeQuery();
                 
                 if(rs.next()){
                     studentName.setText(rs.getString("fn")+" "+rs.getString("ln")+" "+rs.getString("sf"));
                     studentPhone.setText(rs.getString("no"));
                     studentEmail.setText(rs.getString("em"));
                     sbName.setText(rs.getString("fn")+" "+rs.getString("ln")+" "+rs.getString("sf"));
                     sbPhone.setText(rs.getString("no"));
                     sbEmail.setText(rs.getString("em"));
                 }else{
                     JOptionPane.showMessageDialog(null,"Student Not Found.");
                 }
                
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e);forBook();genBookId();
            }
        
        limit();
    }//GEN-LAST:event_findStudentActionPerformed

    private void selectBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBookActionPerformed
        DefaultTableModel dtm = (DefaultTableModel)bookList.getModel();
        int si = bookList.getSelectedRow();
        
        try{
            pst =SQLCONN.prepareStatement("select * from book where bookId = ?");
            pst.setString(1,dtm.getValueAt(si,0).toString());
            
            rs = pst.executeQuery();
            
            if(rs.next()){
                
                if(rs.getInt("bookQty")<1){
                    JOptionPane.showMessageDialog(null,"Insufficient Book at the Library.","Status",JOptionPane.INFORMATION_MESSAGE);
                }else{
                    bookId.setText(rs.getString("bookId"));
                    bbName.setText(rs.getString("bookName"));
                    bbAuth.setText(rs.getString("bookAuthor"));
                    bbQty.setValue(rs.getInt("bookQty"));
                    bbActive.setValue(rs.getInt("activeBorrow"));
                }
            }
            
        }catch(SQLException | HeadlessException e){
            JOptionPane.showMessageDialog(null,"No Selected Book.\n"+e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_selectBookActionPerformed

    private void bookSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bookSearchKeyReleased
        String This = bookSearch.getText();
        if(This == null){
            forBook();
        }else if (This.equals("Search")){
           forBook();  
        }else{
          searchBook();  
        }
    }//GEN-LAST:event_bookSearchKeyReleased

    private void bookSearchFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bookSearchFocusGained
        if(bookSearch.getText().equals("Search")){
            bookSearch.setText(null);
        }
    }//GEN-LAST:event_bookSearchFocusGained

    private void bookSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bookSearchFocusLost
        if(bookSearch.getText().isBlank()){
            bookSearch.setText("Search");
        }
    }//GEN-LAST:event_bookSearchFocusLost

    private void bookBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookBtnActionPerformed
        String This = bookSearch.getText();
        if(This == null){
            forBook();
        }else if (This.equals("Search")){
           forBook();  
        }else{
          searchBook();  
        }
    }//GEN-LAST:event_bookBtnActionPerformed

    private void searchBorrowKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchBorrowKeyReleased
        String This = searchBorrow.getText();
        if(This == null){
            forBook();
        }else if (This.equals("Search Book")){
           forBook();  
        }else{
          searchBookTab();
        }
    }//GEN-LAST:event_searchBorrowKeyReleased

    private void findBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findBorrowActionPerformed
        
        String This = searchBorrow.getText();
        if(This == null){
            forBook();
        }else if (This.equals("Search Book")){
           forBook();  
        }else{
          searchBookTab();
        }
    }//GEN-LAST:event_findBorrowActionPerformed

    private void searchBorrowFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchBorrowFocusGained
        if(searchBorrow.getText().equals("Search Book")){
            searchBorrow.setText(null);
        }
    }//GEN-LAST:event_searchBorrowFocusGained

    private void searchBorrowFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchBorrowFocusLost
        if(searchBorrow.getText().isBlank()){
            searchBorrow.setText("Search Book");
        }
    }//GEN-LAST:event_searchBorrowFocusLost
    
    int bStat,rStat;
    private void genBorrowId(){
        try{
            Date dt= new Date();
            SimpleDateFormat year=new SimpleDateFormat("yyyy");
            String getYear = year.format(dt);
            
            SQLCONN = (com.mysql.jdbc.Connection) DriverManager.getConnection(DATACONN,USERNAME,PASSWORD);
            pst = (com.mysql.jdbc.PreparedStatement) SQLCONN.prepareStatement("select * from borrow ORDER BY borrowId DESC LIMIT 1");
            rs = pst.executeQuery();
            
            if(rs.next()){
                String rnno = rs.getString("borrowId");
                int co = rnno.length();
                String txt = rnno.substring(0,5);
                String num = rnno.substring(5,co);
                int n = Integer.parseInt(num);
                n++;
                String snum = Integer.toString(n);
                String ftxt = txt+snum;
                borrowerId.setText(ftxt);
            }else{
                borrowerId.setText(getYear+"-10000");
            }
        }catch(NumberFormatException | SQLException e){
            System.out.println(e);
        }
        
    }
    private void borrowNull(){
        getId.setText("Student Number");
        studentName.setText("Student Name");
        studentPhone.setText("Contact Number");
        studentEmail.setText("Email Address");
        searchBorrow.setText("Search Book");
        sbName.setText("Student Name");
        sbPhone.setText("Contact Number");
        sbEmail.setText("Email Address");
        bbName.setText("Book Name");
        bbAuth.setText("Book Author");
        bookId.setText(null);
        bbQty.setValue(0);
        bbActive.setValue(0);
        
        
    }
    private void addBorrow(){
        try{
            pst = SQLCONN.prepareStatement("insert into borrow (`borrowId`,`getStudent`,`getBook`,`dateBorrowed`,`dateDue`,`borrowStatus`,`borrowerName`,`borrowerEmail`) values (?,?,?,?,?,?,?,?)");
            pst.setString(1,borrowerId.getText());
            pst.setString(2,getId.getText());
            pst.setString(3,bookId.getText());
            pst.setString(4,borrowDate.getText());
            pst.setString(5,returnDate.getText());
            pst.setString(6,"Active");
            pst.setString(7,studentName.getText());
            pst.setString(8,studentEmail.getText());
            
            if(JOptionPane.showConfirmDialog(null, "Confirm Borrow Book","Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();forBook();
                    bStat=1;
                 }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateBook(){
        
        int getQty = bbQty.getValue();
        int getAct = bbActive.getValue();
        try{
            pst = SQLCONN.prepareStatement("update book set bookQty = ? , activeBorrow = ? where bookId = ?");
            pst.setInt(1,getQty-1);
            pst.setInt(2,getAct+1);
            pst.setString(3,bookId.getText());
            pst.executeUpdate();
            bStat = 2;
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void failBook(){
        
        try{
            pst = SQLCONN.prepareStatement("delete from borrow where borrowId = ?");
            pst.setString(1,borrowerId.getText());
            pst.executeUpdate();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void emailBorrow(){
        String from,to,host,sub,content;
        from = "Librarysystem2023.2024@gmail.com";
        to = sbEmail.getText();
        host = "localhost";
        sub= "YNG Library Book Borrow Summary";
        
       content = "<html>"
            + "<head>"
            + "<style>"
            + "    body {"
            + "        font-family: Arial, sans-serif;"
            + "        margin: 20px;"
            + "    }"
            + "    h2 {"
            + "        color: #008CBA;"
            + "    }"
            + "    p {"
            + "        font-size: 16px;"
            + "    }"
            + "    strong {"
            + "        font-weight: bold;"
            + "    }"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<p>Hi " + sbName.getText() + "! You have successfully borrowed a book at YNG Library.</p>"
            + "<h2>Book Borrow Summary</h2>"
            + "<p><strong>Borrow ID:</strong> " + borrowerId.getText() + "</p>"
            + "<p><strong>Book Borrower:</strong> " + sbName.getText() + "</p>"
            + "<p><strong>Book Borrowed:</strong> " + bbName.getText() + "</p>"
            + "<p><strong>Important Reminder:</strong></p>"
            + "<p>The book was borrowed on YNG Library dated " + borrowDate.getText() + ", the Library allows the student to borrow the book for 3 days.</p>"
            + "<p>After those 3 days, the book must be returned to YNG Library at " + returnDate.getText() + ".</p>"
            + "<p>Failure to return the book prior to the due date will cause the student to face disciplinary action.</p>"
            + "</body>"
            + "</html>";
        
                    Properties p = new Properties();
                    p.put("mail.smtp.auth","true");
                    p.put("mail.smtp.starttls.enable","true");
                    p.put("mail.smtp.host","smtp.gmail.com");
                    p.put("mail.smtp.port","587");
                    Session s = Session.getDefaultInstance(p,new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication("Librarysystem2023.2024@gmail.com","dxbtzcjufseqpyes");
                    }    
                    }); 
                    try{
                        MimeMessage m = new MimeMessage(s);
                        m.setFrom(from);
                        m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                        m.setSubject(sub);
                        m.setContent(content, "text/html; charset=utf-8");
                        Transport.send(m);
                        
                    } catch(MessagingException e){
                        JOptionPane.showMessageDialog(null,"No Internet Found."
                                + "\n Mail didn't sent.","Mail Status",JOptionPane.INFORMATION_MESSAGE);
                    }
    }
    
    private void returnEmail(){
        String from,to,host,sub,content;
        from = "Librarysystem2023.2024@gmail.com";
        to = returnEmail.getText();
        host = "localhost";
        sub= "YNG Library Return Book Summary";
        
        content = "<html>"
            + "<head>"
            + "<style>"
            + "    body {"
            + "        font-family: Arial, sans-serif;"
            + "        margin: 20px;"
            + "    }"
            + "    h2 {"
            + "        color: #008CBA;"
            + "    }"
            + "    p {"
            + "        font-size: 16px;"
            + "    }"
            + "    strong {"
            + "        font-weight: bold;"
            + "    }"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<p>Hi " + returnName.getText() + "! You have successfully return the book at YNG Library.</p>"
            + "<h2>Book Return Summary</h2>"
            + "<p><strong>Borrow ID:</strong> " + getBorrow.getText() + "</p>"
            + "<p><strong>Book Borrower:</strong> " + returnName.getText() + "</p>"
            + "<p><strong>Book Borrowed:</strong> " + returnbookName.getText() + "</p>"
                
            + "<p><strong>Additional Information:</strong></p>"
            + "<p>The book was borrowed on YNG Library dated " + borDate.getText() + ", and the book is returned by the borrower at YNG dated "+ retDate.getText() +".</p>"
            + "</body>"
            + "</html>";
        
                    Properties p = new Properties();
                    p.put("mail.smtp.auth","true");
                    p.put("mail.smtp.starttls.enable","true");
                    p.put("mail.smtp.host","smtp.gmail.com");
                    p.put("mail.smtp.port","587");
                    Session s = Session.getDefaultInstance(p,new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication("Librarysystem2023.2024@gmail.com","dxbtzcjufseqpyes");
                    }    
                    }); 
                    try{
                        MimeMessage m = new MimeMessage(s);
                        m.setFrom(from);
                        m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                        m.setSubject(sub);
                        m.setContent(content, "text/html; charset=utf-8");
                        Transport.send(m);
                        
                    } catch(MessagingException e){
                        JOptionPane.showMessageDialog(null,"No Internet Found."
                                + "\n Mail didn't sent.","Mail Status",JOptionPane.INFORMATION_MESSAGE);
                    }
    }
    private void SBYNAME(){
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowerName like ? && borrowStatus = ?");
                pst.setString(1,"%"+sbyName.getText()+"%");
                pst.setString(2,"Active");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateDue"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    private void SBYBOOK(){
        try{
                    pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where bookName like ? && borrowStatus = ?");
                    pst.setString(1,"%"+sbyBook.getText()+"%");
                    pst.setString(2,"Active");
                    rs = pst.executeQuery();
                    ResultSetMetaData st = rs.getMetaData();
                    q = st.getColumnCount();
                    DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                    bt.setRowCount(0);

                    while(rs.next()){
                        Vector cd = new Vector();
                        for(i=1;i<=q;i++){
                            cd.add(rs.getString("borrowId"));
                            cd.add(rs.getString("getStudent"));
                            cd.add(rs.getString("borrowerName"));
                            cd.add(rs.getString("bookName"));
                            cd.add(rs.getString("dateBorrowed"));
                            cd.add(rs.getString("dateDue"));
                        }
                        bt.addRow(cd);
                    }    
        }
                catch(SQLException e){
                 JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
                }    
    }
    private void RBYNAME(){
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowerName like ? && borrowStatus = ?");
                pst.setString(1,"%"+rbyName.getText()+"%");
                pst.setString(2,"Returned");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) returnTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateReturned"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    private void RBYBOOK(){
        try{
                    pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where bookName like ? && borrowStatus = ?");
                    pst.setString(1,"%"+rbyBook.getText()+"%");
                    pst.setString(2,"Returned");
                    rs = pst.executeQuery();
                    ResultSetMetaData st = rs.getMetaData();
                    q = st.getColumnCount();
                    DefaultTableModel bt = (DefaultTableModel) returnTable.getModel();
                    bt.setRowCount(0);

                    while(rs.next()){
                        Vector cd = new Vector();
                        for(i=1;i<=q;i++){
                            cd.add(rs.getString("borrowId"));
                            cd.add(rs.getString("getStudent"));
                            cd.add(rs.getString("borrowerName"));
                            cd.add(rs.getString("bookName"));
                            cd.add(rs.getString("dateBorrowed"));
                            cd.add(rs.getString("dateReturned"));
                        }
                        bt.addRow(cd);
                    }    
        }
                catch(SQLException e){
                 JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
                }    
    }
    int limiter=0;
    private void limit(){
        try{
            pst = SQLCONN.prepareStatement("select count(borrowId) from borrow where borrowerName = ? and borrowStatus = ?");
            pst.setString(1,sbName.getText());
            pst.setString(2,"Active");
            rs=pst.executeQuery();
            
            if(rs.next()){
                limiter = rs.getInt("count(borrowId)");
                System.out.println(rs.getString("count(borrowId)"));
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    private void bookBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookBorrowActionPerformed
       limit(); 
        
       if(limiter>=2){
           JOptionPane.showMessageDialog(null,"Student is not allowed to borrow more than 2 books.");
       }else{
        
        if(sbName.getText().equals("Student Name")){
            JOptionPane.showMessageDialog(null,"No Selected Student","Error",JOptionPane.INFORMATION_MESSAGE);
        }else if(bbName.getText().equals("Book Name")){
            JOptionPane.showMessageDialog(null,"No Selected Book.","Error",JOptionPane.INFORMATION_MESSAGE);
        }
        
        else{
        addBorrow();
        if(bStat==1){
          updateBook();emailBorrow();  
        }
        if(bStat==2){
            JOptionPane.showMessageDialog(null,"Book Borrowed.","Success",JOptionPane.INFORMATION_MESSAGE);
            borrowNull();forBook();genBorrowId();
        }else{
            failBook();forBook();
            JOptionPane.showMessageDialog(null,"Unable to Process Borrow Book..","Status",JOptionPane.INFORMATION_MESSAGE);
        }
        }
       }
    }//GEN-LAST:event_bookBorrowActionPerformed
    
    
    private void getIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_getIdFocusGained
        if(getId.getText().equals("Student Number")){
            getId.setText(null);
        }
    }//GEN-LAST:event_getIdFocusGained

    private void getIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_getIdFocusLost
        if(getId.getText().isBlank()){
            getId.setText("Student Number");
        }
    }//GEN-LAST:event_getIdFocusLost

    private void getIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getIdActionPerformed
        try{
                 pst =SQLCONN.prepareStatement("select * from student where studentId = ?");
                 pst.setString(1,getId.getText());
                 rs= pst.executeQuery();
                 
                 if(rs.next()){
                     studentName.setText(rs.getString("first_name")+" "+rs.getString("last_name")+" "+rs.getString("suffix"));
                     studentPhone.setText(rs.getString("phone"));
                     studentEmail.setText(rs.getString("email"));
                     sbName.setText(rs.getString("first_name")+" "+rs.getString("last_name")+" "+rs.getString("suffix"));
                     sbPhone.setText(rs.getString("phone"));
                     sbEmail.setText(rs.getString("email"));
                 }else{
                     JOptionPane.showMessageDialog(null,"Student Not Found.");
                 }
                
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e);forBook();genBookId();
            }
    }//GEN-LAST:event_getIdActionPerformed

    private void borrowTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrowTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_borrowTableMouseClicked

    private void sbyNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sbyNameFocusGained
        if(sbyName.getText().equals("Search by Name")){
            sbyName.setText(null);
        }
    }//GEN-LAST:event_sbyNameFocusGained

    private void sbyNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sbyNameFocusLost
        if(sbyName.getText().isBlank()){
            sbyName.setText("Search by Name");
        }
    }//GEN-LAST:event_sbyNameFocusLost

    private void fbyNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyNameActionPerformed
        if(sbyName.getText().equals("Search by Name") || sbyName.getText().isBlank())
        {
         forBorrow();   
        }
        else{
                SBYNAME();
                }
    }//GEN-LAST:event_fbyNameActionPerformed

    private void sbyBookFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sbyBookFocusGained
        if(sbyBook.getText().equals("Search by Book")){
            sbyBook.setText(null);
        }
    }//GEN-LAST:event_sbyBookFocusGained

    private void sbyBookFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sbyBookFocusLost
        if(sbyBook.getText().isBlank()){
            sbyBook.setText("Search by Book");
        }
    }//GEN-LAST:event_sbyBookFocusLost

    private void sbyBookKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sbyBookKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_sbyBookKeyReleased
    
    private void fbyBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyBookActionPerformed
        if(sbyBook.getText().equals("Search by Book") || sbyBook.getText().isBlank())
        {
         forBorrow();   
        }
        else{
                SBYBOOK();
                }
    }//GEN-LAST:event_fbyBookActionPerformed

    private void fbyDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyDateActionPerformed
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dn = sdf.format(sbyDate.getDate());
        
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where dateBorrowed like ? and borrowStatus = ?");
                pst.setString(1,dn+"%");
                pst.setString(2,"Active");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateDue"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }//GEN-LAST:event_fbyDateActionPerformed

    private void getBorrowFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_getBorrowFocusGained
        if(getBorrow.getText().equals("Search Borrow ID")){
            getBorrow.setText(null);
        }
    }//GEN-LAST:event_getBorrowFocusGained

    private void getBorrowFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_getBorrowFocusLost
        if(getBorrow.getText().isBlank()){
            getBorrow.setText("Search Borrow ID");
        }
    }//GEN-LAST:event_getBorrowFocusLost

    private void getBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getBorrowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_getBorrowActionPerformed

    private void getBorrowBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getBorrowBTNActionPerformed
        try{
                 pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowId = ? and borrowStatus != ?");
                 pst.setString(1,getBorrow.getText());
                 pst.setString(2,"Returned");
                 rs= pst.executeQuery();
                 
                 if(rs.next()){
                     String hiho = rs.getString("borrowStatus");
                     
                     if(hiho.equals("Expired")){
                         JOptionPane.showMessageDialog(null, """
                                                             This borrow exceeded the due date,
                                                             the student is subjected to disciplinary action.""");
                     }
                     
                     returnName.setText(rs.getString("borrowerName"));
                     returnEmail.setText(rs.getString("borrowerEmail"));
                     returnbookName.setText(rs.getString("bookName"));
                     returnbookAuthor.setText(rs.getString("bookAuthor"));
                     borDate.setText(rs.getString("dateBorrowed"));
                     int bq = rs.getInt("bookQty");
                     int aq = rs.getInt("ActiveBorrow");
                     int upbq = bq+1;
                     int upaq = aq-1;
                     bookId1.setText(rs.getString("bookId"));
                     returnQty.setValue(upbq);
                     activeB.setValue(upaq);
                     System.out.println("Update Qty of bookQty from "+bq +" to "+upbq);
                     System.out.println("Update Qty of ActiveBorrow from "+aq+" to "+upaq);
                     
                 }else{
                     JOptionPane.showMessageDialog(null,"Borrow Boook is already returned,\neither not found.");
                 }
                
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e);forBook();genBookId();
            }
    }//GEN-LAST:event_getBorrowBTNActionPerformed

    private void bookReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookReturnActionPerformed
        if(returnName.getText().equals("Student Name")){
            JOptionPane.showMessageDialog(null,"No Selected Student","Error",JOptionPane.INFORMATION_MESSAGE);
        }else if(returnbookName.getText().equals("Book Name")){
            JOptionPane.showMessageDialog(null,"No Selected Book.","Error",JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            
            updateBorrow();
            if(rStat==1){
                updateQty();
                nullReturn();forBook();forReturn();
            }else{
                JOptionPane.showMessageDialog(null,"Unable to Return Book.");
            }
            
        }
    }//GEN-LAST:event_bookReturnActionPerformed

    private void reportPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportPrintActionPerformed
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String from1 = sdf.format(reportFrom.getDate());
        String to1 = sdf.format(reportTo.getDate());
        String from = from1+" 00:00:00";
        String to = to1+" 23:59:59";
        
        showDate.setText(from +" - "+to);
        
        if(reportStat.getText().equals("Null")){
            JOptionPane.showMessageDialog(null,"Please select Report Type.");
        }else if(reportStat.getText().equals("Active Borrowed Book")){
            
            //START 1
                try {
                    pst = SQLCONN.prepareStatement("select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Active' and dateBorrowed between ? and ?");
                    pst.setString(1, from);
                    pst.setString(2,to);
                    rs = pst.executeQuery();


                    if (rs.next()) {
                            HashMap<String,Object> hm = new HashMap<String,Object>();
                            hm.put("dateFrom", from);
                            hm.put("dateTo", to);
                            hm.put("repType",reportStat.getText());

                            InputStream is = home.class.getResourceAsStream("/set_library/summaryReport.jrxml");
                            JasperDesign jd = JRXmlLoader.load(is);

                            String sql= "select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Active' and dateBorrowed between '"+from+"' and '"+to+"'";
                            System.out.println(sql);
                            JRDesignQuery newQuery = new JRDesignQuery();
                            newQuery.setText(sql);
                            jd.setQuery(newQuery);

                            JasperReport jr = JasperCompileManager.compileReport(jd);
                            JasperPrint jp = JasperFillManager.fillReport(jr, hm, SQLCONN);
                            JasperViewer.viewReport(jp, false);

                    } else {
                        JOptionPane.showMessageDialog(null, "Error");  
                    }

                } catch (JRException | SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                    System.out.println(e);
                }
            //END 1
            
        }else if(reportStat.getText().equals("Returned Book")){
            //START 2
            try {
                pst = SQLCONN.prepareStatement("select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Returned' and dateReturned between ? and ?");
                pst.setString(1, from);
                pst.setString(2,to);
                rs = pst.executeQuery();


                if (rs.next()) {
                        HashMap<String,Object> hm = new HashMap<String,Object>();
                        hm.put("dateFrom", from);
                        hm.put("dateTo", to);
                        hm.put("repType",reportStat.getText());

                        InputStream is = home.class.getResourceAsStream("/main/summaryReport.jrxml");
                        JasperDesign jd = JRXmlLoader.load(is);

                        String sql= "select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Returned' and dateReturned between '"+from+"' and '"+to+"'";
                        System.out.println(sql);
                        JRDesignQuery newQuery = new JRDesignQuery();
                        newQuery.setText(sql);
                        jd.setQuery(newQuery);

                        JasperReport jr = JasperCompileManager.compileReport(jd);
                        JasperPrint jp = JasperFillManager.fillReport(jr, hm, SQLCONN);
                        JasperViewer.viewReport(jp, false);

                } else {
                    JOptionPane.showMessageDialog(null, "Error");  
                }

            } catch (JRException | SQLException e) {
                JOptionPane.showMessageDialog(null,e);
                System.out.println(e);
            }
            
            //END 2
        }else if(reportStat.getText().equals("Summary Report")){
            //START 3
                try {
                    pst = SQLCONN.prepareStatement("select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where dateBorrowed between ? and ?");
                    pst.setString(1, from);
                    pst.setString(2,to);
                    rs = pst.executeQuery();


                    if (rs.next()) {
                            HashMap<String,Object> hm = new HashMap<String,Object>();
                            hm.put("dateFrom", from);
                            hm.put("dateTo", to);
                            hm.put("repType",reportStat.getText());

                            InputStream is = home.class.getResourceAsStream("/main/summaryReport.jrxml");
                            JasperDesign jd = JRXmlLoader.load(is);

                            String sql= "select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where dateBorrowed between '"+from+"' and '"+to+"'";
                            System.out.println(sql);
                            JRDesignQuery newQuery = new JRDesignQuery();
                            newQuery.setText(sql);
                            jd.setQuery(newQuery);

                            JasperReport jr = JasperCompileManager.compileReport(jd);
                            JasperPrint jp = JasperFillManager.fillReport(jr, hm, SQLCONN);
                            JasperViewer.viewReport(jp, false);

                    } else {
                        JOptionPane.showMessageDialog(null, "Error");  
                    }

                } catch (JRException | SQLException e) {
                    JOptionPane.showMessageDialog(null,e);
                    System.out.println(e);
                }
            
            //END 3
        }else if(reportStat.getText().equals("Due Exceed Book Report")){
            //START 4
            try {
                pst = SQLCONN.prepareStatement("select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Expired' and dateBorrowed between ? and ?");
                pst.setString(1, from);
                pst.setString(2,to);
                rs = pst.executeQuery();


                if (rs.next()) {
                        HashMap<String,Object> hm = new HashMap<String,Object>();
                        hm.put("dateFrom", from);
                        hm.put("dateTo", to);
                        hm.put("repType",reportStat.getText());

                        InputStream is = home.class.getResourceAsStream("/main/summaryReport.jrxml");
                        JasperDesign jd = JRXmlLoader.load(is);

                        String sql= "select * from borrow inner join student on borrow.getStudent = student.studentId inner join book on borrow.getBook = book.bookId where borrowStatus = 'Expired' and dateBorrowed between '"+from+"' and '"+to+"'";
                        System.out.println(sql);
                        JRDesignQuery newQuery = new JRDesignQuery();
                        newQuery.setText(sql);
                        jd.setQuery(newQuery);

                        JasperReport jr = JasperCompileManager.compileReport(jd);
                        JasperPrint jp = JasperFillManager.fillReport(jr, hm, SQLCONN);
                        JasperViewer.viewReport(jp, false);

                } else {
                    JOptionPane.showMessageDialog(null, "Error");  
                }

            } catch (JRException | SQLException e) {
                JOptionPane.showMessageDialog(null, e);
                System.out.println(e);
            }
            
            //END 4
        }
        
    }//GEN-LAST:event_reportPrintActionPerformed

    private void reportTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportTypeActionPerformed
        switch (reportType.getSelectedIndex()) {
            case 0 -> reportStat.setText("Null");
            case 1 -> reportStat.setText("Active Borrowed Book");
            case 2 -> reportStat.setText("Returned Book");
            case 3 -> reportStat.setText("Due Exceed Book Report");
            case 4 -> reportStat.setText("Summary Report");
            default -> {
            }
        }
    }//GEN-LAST:event_reportTypeActionPerformed

    private void returnTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_returnTableMouseClicked

    private void rbyNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbyNameFocusGained
        if(rbyName.getText().equals("Search by Name")){
            rbyName.setText(null);
        }
    }//GEN-LAST:event_rbyNameFocusGained

    private void rbyNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbyNameFocusLost
        if(rbyName.getText().isBlank()){
            rbyName.setText("Search by Name");
        }
    }//GEN-LAST:event_rbyNameFocusLost

    private void fbyName1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyName1ActionPerformed
        if(rbyName.getText().equals("Search by Name") || rbyName.getText().isBlank())
        {
         forReturn();   
        }
        else{
                RBYNAME();
                }
    }//GEN-LAST:event_fbyName1ActionPerformed

    private void fbyDate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyDate1ActionPerformed
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dn = sdf.format(rbyDate.getDate());
        
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where dateReturned like ? and borrowStatus = ?");
                pst.setString(1,dn+"%");
                pst.setString(2,"Returned");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) returnTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateReturned"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }//GEN-LAST:event_fbyDate1ActionPerformed

    private void rbyBookFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbyBookFocusGained
        if(rbyBook.getText().equals("Search by Book")){
            rbyBook.setText(null);
        }
    }//GEN-LAST:event_rbyBookFocusGained

    private void rbyBookFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbyBookFocusLost
        if(rbyBook.getText().isBlank()){
            rbyBook.setText("Search by Book");
        }
    }//GEN-LAST:event_rbyBookFocusLost

    private void rbyBookKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rbyBookKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rbyBookKeyReleased

    private void fbyBook1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fbyBook1ActionPerformed
        if(rbyBook.getText().equals("Search by Book") || rbyBook.getText().isBlank())
        {
         forReturn();   
        }
        else{
                RBYBOOK();
                }
    }//GEN-LAST:event_fbyBook1ActionPerformed

    private void refReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refReturnActionPerformed
        forReturn();
        rbyName.setText("Search by Name");
        rbyBook.setText("Search by Book");
        
    }//GEN-LAST:event_refReturnActionPerformed

    private void refBorrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refBorrowActionPerformed
        forBorrow();
        sbyName.setText("Search by Name");
        sbyBook.setText("Search by Book");
        
    }//GEN-LAST:event_refBorrowActionPerformed

    private void refBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refBookActionPerformed
        forBook();
        bookSearch.setText("Search");
    }//GEN-LAST:event_refBookActionPerformed

    private void setBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setBtnActionPerformed
       try{
           pst = SQLCONN.prepareStatement("select * from librarian where libId = ?");
           pst.setString(1,librarian);
           
           rs = pst.executeQuery();
           
           if(rs.next()){
               librarian ad = new librarian();
               ad.libId.setText(rs.getString("libId"));
               ad.libFn.setText(rs.getString("libFName"));
               ad.libMn.setText(rs.getString("libMName"));
               ad.libLn.setText(rs.getString("libLName"));
               ad.libSf.setText(rs.getString("libSuffix"));
               ad.libBd.setText(rs.getString("libBDate"));
               ad.libNo.setText(rs.getString("libContact"));
               ad.libEm.setText(rs.getString("libEmail"));
               ad.libAd.setText(rs.getString("libAddress"));
               ad.libUn.setText(rs.getString("username"));
               ad.libPw.setText(rs.getString("password"));
               ad.setVisible(true);
           }else{
               JOptionPane.showMessageDialog(null,"Librarian not found.");
           }
       }catch(SQLException e){
           JOptionPane.showMessageDialog(null,"Unable to Access Librarian Account.","Access",JOptionPane.INFORMATION_MESSAGE);
       }
    }//GEN-LAST:event_setBtnActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        new student().setVisible(true);
    }//GEN-LAST:event_addBtnActionPerformed

    private void rbyNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbyNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbyNameActionPerformed

    private void ddrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddrActionPerformed

        Date dt= new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                
                System.out.println(sdf.format(dt));
                
        
        try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where dateDue like ? and borrowStatus = ?");
                pst.setString(1,sdf.format(dt)+"%");
                pst.setString(2,"Active");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateDue"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }//GEN-LAST:event_ddrActionPerformed

    private void erActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_erActionPerformed
        if(JOptionPane.showConfirmDialog(null,"Continue to send Email?","Email Reminder",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
            DefaultTableModel dtm = (DefaultTableModel)borrowTable.getModel();
        String studId=null,studEm=null,studName=null;
        
        int count = dtm.getRowCount();
        System.out.println(count);
        
        
        int eStart = 0;
        
        try{
            while(eStart!=count){
                //code for retrieving student information thru studentId
                try{
                    pst = SQLCONN.prepareStatement("select * from student where studentId = ?");
                    pst.setString(1,dtm.getValueAt(eStart,1).toString());
                    rs=pst.executeQuery();
                    if(rs.next()){
                        studId = rs.getString("studentId");
                        studEm = rs.getString("em");
                        studName = rs.getString("fn");
                    }else{
                        JOptionPane.showMessageDialog(null,"Failed to retrieve student information.");
                    }
                    
                }catch(HeadlessException | SQLException e){
                    System.out.println(e);
                }
                //end na dito
                
                
                //start dito ng pag email
                String from,to,host,sub,content;
                from = "Librarysystem2023.2024@gmail.com";
                to = studEm;
                host = "localhost";
                sub= "YNG Library Due Date Reminder";

               content = "<html>"
                    + "<head>"
                    + "<style>"
                    + "    body {"
                    + "        font-family: Arial, sans-serif;"
                    + "        margin: 20px;"
                    + "    }"
                    + "    h2 {"
                    + "        color: #008CBA;"
                    + "    }"
                    + "    p {"
                    + "        font-size: 16px;"
                    + "    }"
                    + "    strong {"
                    + "        font-weight: bold;"
                    + "    }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<p>Hi " + studName + "! Today is the return/due date of your borrowed book at YNG. To prevent a disciplinary action from school"
                       + ", return the said book at YNG Library.</p>"
                    + "<h2>Thank you.</h2>"
                    + "<p><strong>Important Reminder:</strong></p>"
                    + "<p>Failure to return the book prior to the due date will cause the student to face disciplinary action.</p>"
                    + "</body>"
                    + "</html>";

                            Properties p = new Properties();
                            p.put("mail.smtp.auth","true");
                            p.put("mail.smtp.starttls.enable","true");
                            p.put("mail.smtp.host","smtp.gmail.com");
                            p.put("mail.smtp.port","587");
                            Session s = Session.getDefaultInstance(p,new javax.mail.Authenticator(){
                            protected PasswordAuthentication getPasswordAuthentication(){
                                return new PasswordAuthentication("Librarysystem2023.2024@gmail.com","dxbtzcjufseqpyes");
                            }    
                            }); 
                            try{
                                MimeMessage m = new MimeMessage(s);
                                m.setFrom(from);
                                m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                                m.setSubject(sub);
                                m.setContent(content, "text/html; charset=utf-8");
                                Transport.send(m);

                            } catch(MessagingException e){
                                JOptionPane.showMessageDialog(null,"No Internet Found."
                                        + "\n Mail didn't sent.","Mail Status",JOptionPane.INFORMATION_MESSAGE);
                            }
                //end pag email and then sa baba naman is increment para maemail yung susunod.
                
                System.out.println(eStart+" Email sent to "+dtm.getValueAt(eStart,2).toString()+", Book Borrowed: "+dtm.getValueAt(eStart,3).toString());
                eStart++;
            }
        }
        catch(HeadlessException e){
            System.out.println(e);
        }
        }
        
        
    }//GEN-LAST:event_erActionPerformed

    private void forCatSort(){
        try{
                //inner join `courses` ON `regform`.`get_course` = `courses`.`course_id` WHERE `regform`.`email`='$username' && `regform`.`reg_id`='$password'";
                pst =SQLCONN.prepareStatement("select * from book where bookCategory = ? order by bookCategory ASC");
                pst.setString(1,sortCat.getSelectedItem().toString());
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel rt = (DefaultTableModel) bookTable.getModel();
                rt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("bookId"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("bookSeries"));
                        cd.add(rs.getString("bookAuthor"));
                        cd.add(rs.getString("bookCategory"));
                        cd.add(rs.getInt("bookShelf"));
                        cd.add(rs.getInt("bookQty"));
                        cd.add(rs.getInt("activeBorrow"));
                    }
                    rt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }
    private void sortCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortCatActionPerformed
        
            
    }//GEN-LAST:event_sortCatActionPerformed

    private void bookBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookBtn1ActionPerformed
        String sc = sortCat.getSelectedItem().toString();
        try{   
            if(sc.equals("Book Category")){
                forBook();
            }else{
                forCatSort();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }//GEN-LAST:event_bookBtn1ActionPerformed

    private void ddr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddr1ActionPerformed
        //Date dt= new Date();
        //SimpleDateFormat dn = new SimpleDateFormat("yyyy-MM-dd");
        //String db = dn.format(dt);
        
    try{
                pst =SQLCONN.prepareStatement("select * from borrow inner join book on borrow.getBook = book.bookId where borrowStatus = ?");
                pst.setString(1,"Expired");
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bt = (DefaultTableModel) borrowTable.getModel();
                bt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        cd.add(rs.getString("borrowId"));
                        cd.add(rs.getString("getStudent"));
                        cd.add(rs.getString("borrowerName"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("dateBorrowed"));
                        cd.add(rs.getString("dateDue"));
                    }
                    bt.addRow(cd);
                }    
    }
            catch(SQLException e){
             JOptionPane.showMessageDialog(null,"Error.\n"+e,"Status",JOptionPane.WARNING_MESSAGE);   
            }
    }//GEN-LAST:event_ddr1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
if(JOptionPane.showConfirmDialog(null,"Are you sure to exit?","Exit",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
           System.exit(0);
        }        
    }//GEN-LAST:event_formWindowClosing
    
    private void printReports(){
        try{
            pst =SQLCONN.prepareStatement("select * from book where borrowStatus = ? order by dateBorrowed ASC ");
            pst.setString(1,bookSearch.getText()+"%");
            
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel rt = (DefaultTableModel) bookTable.getModel();
                rt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("bookId"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("bookSeries"));
                        cd.add(rs.getString("bookAuthor"));
                        cd.add(rs.getString("bookCategory"));
                        cd.add(rs.getInt("bookShelf"));
                        cd.add(rs.getInt("bookQty"));
                        cd.add(rs.getInt("activeBorrow"));
                    }
                    rt.addRow(cd);
                }    
        }catch(SQLException e){
           System.out.println(e); 
        }
    }
    private void updateBorrow(){
        try{
                pst = SQLCONN.prepareStatement("update borrow SET `dateReturned` = ? , borrowStatus = ? where borrowId = ?");
                pst.setString(1,retDate.getText());
                pst.setString(2, "Returned");
                pst.setString(3,getBorrow.getText());
                
                if(JOptionPane.showConfirmDialog(null, "Confirm Return Book","Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();rStat=1;returnEmail();
                    JOptionPane.showMessageDialog(null,"Book Returned.","Success",JOptionPane.INFORMATION_MESSAGE);
                 }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
            }
    }
    private void updateQty(){
        try{
                pst = SQLCONN.prepareStatement("update book SET `bookQty` = ? , activeBorrow = ? where bookId = ?");
                pst.setInt(1,returnQty.getValue());
                pst.setInt(2,activeB.getValue());
                pst.setString(3,bookId1.getText());
                
                pst.executeUpdate();
                System.out.println("updated");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
            }
    }
    private void nullReturn(){
        getBorrow.setText("Search Borrow ID");
        returnName.setText("Student Name");
        returnEmail.setText("Student Email");
        returnbookName.setText("Book Name");
        returnbookAuthor.setText("Book Author");
        returnQty.setValue(1);
        activeB.setValue(1);
        rStat = 0;
    }
    
    private void checkPanel(){
        if(bookFunction.isVisible()){
            bookFunction.setVisible(false);
            bookPane.setBounds(10, 110, 950, 300);
        }else{
            bookFunction.setVisible(true);
            bookPane.setBounds(10, 110, 630, 300);
        }
    }
    
    private void forAddBook(){
        try{
                 pst =SQLCONN.prepareStatement("insert into book (`bookId`,`bookName`,`bookAuthor`,`bookCategory`,`bookCode`,`bookSeries`,`bookShelf`,`bookQty`,`bookKeeper`) values (?,?,?,?,?,?,?,?,?)");
                 pst.setString(1,BOOKID);
                 pst.setString(2,bookName.getText());
                 pst.setString(3,bookAuth.getText());
                 pst.setString(4,bookCat.getSelectedItem().toString());
                 pst.setString(5,bookCode.getText());
                 pst.setString(6,bookSrs.getText());
                 pst.setInt(7,(int) bookShelf.getValue());
                 pst.setInt(8,(int) bookQty.getValue());
                 pst.setString(9,bookLib.getText());
                 
                 if(JOptionPane.showConfirmDialog(null, "Confirm Add Book","Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();forBook();setBookNull();genBookId();
                    JOptionPane.showMessageDialog(null,"Book Added.","Success",JOptionPane.INFORMATION_MESSAGE);
                 }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e);forBook();genBookId();
            }
    }
    private void forEditBook(){
        try{
                 pst =SQLCONN.prepareStatement("update book set `bookName`=?,`bookAuthor`=?,`bookCategory`=?,`bookCode`=?,`bookSeries`=?,`bookShelf`=?,`bookQty`=?,`bookKeeper`=? where `bookId`=?");
                 pst.setString(1,bookName.getText());
                 pst.setString(2,bookAuth.getText());
                 pst.setString(3,bookCat.getSelectedItem().toString());
                 pst.setString(4,bookCode.getText());
                 pst.setString(5,bookSrs.getText());
                 pst.setInt(6,(int) bookShelf.getValue());
                 pst.setInt(7,(int) bookQty.getValue());
                 pst.setString(8,bookLib.getText());
                 pst.setString(9,BOOKID);
                 
                 if(JOptionPane.showConfirmDialog(null, "Confirm Edit Book","Confirmation",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
                    pst.executeUpdate();forBook();setBookNull();genBookId();
                    JOptionPane.showMessageDialog(null,"Book Edited.","Success",JOptionPane.INFORMATION_MESSAGE);
                 }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e);forBook();genBookId();
            }
    }
    private void setBookNull(){
        bookName.setText(null);
        bookAuth.setText(null);
        bookCat.setSelectedIndex(0);
        bookName.requestFocus();
        bookCode.setText(null);
        bookSrs.setText(null);
        bookQty.setValue(1);
        bookShelf.setValue(1);
    }
    private void searchBook(){
        try{
            pst =SQLCONN.prepareStatement("select * from book where bookName like ? order by bookName ASC ");
            pst.setString(1,"%"+bookSearch.getText()+"%");
            
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel rt = (DefaultTableModel) bookTable.getModel();
                rt.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("bookId"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("bookSeries"));
                        cd.add(rs.getString("bookAuthor"));
                        cd.add(rs.getString("bookCategory"));
                        cd.add(rs.getInt("bookShelf"));
                        cd.add(rs.getInt("bookQty"));
                        cd.add(rs.getInt("activeBorrow"));
                    }
                    rt.addRow(cd);
                }    
        }catch(SQLException e){
           System.out.println(e); 
        }
    }
    private void searchBookTab(){
        try{
            pst =SQLCONN.prepareStatement("select * from book where bookName like ? order by bookName ASC ");
            pst.setString(1,"%"+searchBorrow.getText()+"%");
            
                rs = pst.executeQuery();
                ResultSetMetaData st = rs.getMetaData();
                q = st.getColumnCount();
                DefaultTableModel bl = (DefaultTableModel) bookList.getModel();
                bl.setRowCount(0);

                while(rs.next()){
                    Vector cd = new Vector();
                    for(i=1;i<=q;i++){
                        
                        cd.add(rs.getString("bookId"));
                        cd.add(rs.getString("bookName"));
                        cd.add(rs.getString("bookSeries"));
                        cd.add(rs.getString("bookAuthor"));
                        cd.add(rs.getString("bookCategory"));
                        cd.add(rs.getInt("bookShelf"));
                        cd.add(rs.getInt("bookQty"));
                        cd.add(rs.getInt("activeBorrow"));
                    }
                    bl.addRow(cd);
                }    
        }catch(SQLException e){
           System.out.println(e); 
        }
    }
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
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.components.JSpinField activeB;
    private javax.swing.JButton addBook;
    private javax.swing.JButton addBtn;
    private com.toedter.components.JSpinField bbActive;
    private javax.swing.JTextField bbAuth;
    private javax.swing.JTextField bbName;
    private com.toedter.components.JSpinField bbQty;
    private javax.swing.JPanel body;
    private javax.swing.JButton bookAdd;
    private javax.swing.JTextField bookAuth;
    private javax.swing.JButton bookBorrow;
    private javax.swing.JButton bookBtn;
    private javax.swing.JButton bookBtn1;
    private javax.swing.JComboBox<String> bookCat;
    private javax.swing.JTextField bookCode;
    private javax.swing.JButton bookEdit;
    private javax.swing.JPanel bookFunction;
    private javax.swing.JLabel bookId;
    private javax.swing.JLabel bookId1;
    public javax.swing.JTextField bookLib;
    private javax.swing.JTable bookList;
    private javax.swing.JTextField bookName;
    private javax.swing.JScrollPane bookPane;
    private com.toedter.components.JSpinField bookQty;
    private javax.swing.JButton bookReturn;
    private javax.swing.JTextField bookSearch;
    private com.toedter.components.JSpinField bookShelf;
    private javax.swing.JTextField bookSrs;
    private javax.swing.JTable bookTable;
    private javax.swing.JTextField borDate;
    private javax.swing.JButton borrowBtn;
    private javax.swing.JTextField borrowDate;
    private javax.swing.JScrollPane borrowPane;
    private javax.swing.JScrollPane borrowPane1;
    private javax.swing.JTable borrowTable;
    private javax.swing.JButton borrowedBtn;
    private javax.swing.JLabel borrowerId;
    private javax.swing.JLabel brwDate1;
    private javax.swing.JLabel checkId;
    private javax.swing.JButton ddr;
    private javax.swing.JButton ddr1;
    private javax.swing.JButton delBook;
    private javax.swing.JButton dueBtn;
    private javax.swing.JButton editBook;
    private javax.swing.JButton er;
    private javax.swing.JButton fbyBook;
    private javax.swing.JButton fbyBook1;
    private javax.swing.JButton fbyDate;
    private javax.swing.JButton fbyDate1;
    private javax.swing.JButton fbyName;
    private javax.swing.JButton fbyName1;
    private javax.swing.JButton findBorrow;
    private javax.swing.JButton findStudent;
    private javax.swing.JLabel forBAuth;
    private javax.swing.JLabel forBCat;
    private javax.swing.JLabel forBCode;
    private javax.swing.JLabel forBName;
    private javax.swing.JLabel forBQty;
    private javax.swing.JLabel forBShe;
    private javax.swing.JLabel forBSrs;
    private javax.swing.JTextField getBorrow;
    private javax.swing.JButton getBorrowBTN;
    private javax.swing.JTextField getId;
    private javax.swing.JButton invBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JPanel navBar;
    private javax.swing.JButton outBtn;
    private javax.swing.JPanel pnl1;
    private javax.swing.JPanel pnl2;
    private javax.swing.JPanel pnl3;
    private javax.swing.JPanel pnl4;
    private javax.swing.JPanel pnl5;
    private javax.swing.JPanel pnl6;
    private javax.swing.JTextField rbyBook;
    private com.toedter.calendar.JDateChooser rbyDate;
    private javax.swing.JTextField rbyName;
    private javax.swing.JButton ref;
    private javax.swing.JButton refBook;
    private javax.swing.JButton refBorrow;
    private javax.swing.JButton refReturn;
    private javax.swing.JButton reportBtn;
    private com.toedter.calendar.JDateChooser reportFrom;
    private javax.swing.JButton reportPrint;
    private javax.swing.JLabel reportStat;
    private com.toedter.calendar.JDateChooser reportTo;
    private javax.swing.JComboBox<String> reportType;
    private javax.swing.JTextField retDate;
    private javax.swing.JButton returnBtn;
    private javax.swing.JTextField returnDate;
    private javax.swing.JTextField returnEmail;
    private javax.swing.JTextField returnName;
    private com.toedter.components.JSpinField returnQty;
    private javax.swing.JTable returnTable;
    private javax.swing.JTextField returnbookAuthor;
    private javax.swing.JTextField returnbookName;
    private javax.swing.JTextField sbEmail;
    private javax.swing.JTextField sbName;
    private javax.swing.JTextField sbPhone;
    private javax.swing.JTextField sbyBook;
    private com.toedter.calendar.JDateChooser sbyDate;
    private javax.swing.JTextField sbyName;
    private javax.swing.JTextField searchBorrow;
    private javax.swing.JButton selectBook;
    private javax.swing.JButton setBtn;
    private javax.swing.JLabel showDate;
    private javax.swing.JComboBox<String> sortCat;
    private javax.swing.JTextField studentEmail;
    private javax.swing.JTextField studentName;
    private javax.swing.JTextField studentPhone;
    private javax.swing.JPanel tl1;
    private javax.swing.JPanel tl2;
    private javax.swing.JPanel tl3;
    private javax.swing.JPanel tl4;
    private javax.swing.JPanel tl5;
    private javax.swing.JPanel tl6;
    private javax.swing.JLabel tlLbl1;
    private javax.swing.JLabel tlLbl2;
    private javax.swing.JLabel tlLbl3;
    private javax.swing.JLabel tlLbl4;
    private javax.swing.JLabel tlLbl5;
    private javax.swing.JLabel tlLbl6;
    // End of variables declaration//GEN-END:variables

    class ModeKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                isDarkMode=!isDarkMode;
                updateMode();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }    
    
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

/*
if(JOptionPane.showConfirmDialog(null,"Are you sure to exit?","Exit",JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION){
           System.exit(0);
        }

*/