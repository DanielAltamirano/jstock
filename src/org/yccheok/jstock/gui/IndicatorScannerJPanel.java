/*
 * IndicatorScannerJPanel.java
 *
 * Created on June 15, 2007, 9:58 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2007 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.event.*;
import com.nexes.wizard.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.*;
import org.yccheok.jstock.engine.*;
import org.yccheok.jstock.analysis.*;
import java.util.concurrent.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  yccheok
 */
public class IndicatorScannerJPanel extends javax.swing.JPanel implements ChangeListener {
    
    /** Creates new form IndicatorScannerJPanel */
    public IndicatorScannerJPanel() {
        initComponents();
        
        initTableHeaderToolTips();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout(5, 5));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/player_play.png")));
        jButton1.setText("Scan...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16x16/stop.png")));
        jButton2.setText("Stop");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel1.add(jButton2);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Indicator Scan Result"));
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jTable1.setModel(new IndicatorTableModel());
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        this.jTable1.setDefaultRenderer(Number.class, new StockTableCellRenderer());
        this.jTable1.setDefaultRenderer(Double.class, new StockTableCellRenderer());
        this.jTable1.setDefaultRenderer(Object.class, new StockTableCellRenderer());

        this.jTable1.getTableHeader().addMouseListener(new TableColumnSelectionPopupListener());
        this.jTable1.addMouseListener(new TableRowPopupListener());
        jScrollPane1.setViewportView(jTable1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
        stop();
        jButton1.setEnabled(true);
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        final MainFrame m = (MainFrame)javax.swing.SwingUtilities.getAncestorOfClass(MainFrame.class, IndicatorScannerJPanel.this);
        
        if(m.getStockCodeAndSymbolDatabase() == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "We haven't connected to KLSE server.", "Not Connected", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if(operatorIndicators != null) {
            final int result = JOptionPane.showConfirmDialog(this, "You have previous built indicators, do you want to re-use them?", "Re-use Indicators", JOptionPane.YES_NO_OPTION);
            
            if(result == JOptionPane.YES_OPTION)
            {
                realTimeStockMonitor.stop();
                realTimeStockMonitor.clearStockCodes(); 
                removeAllIndicatorsFromTable();
                alertRecords.clear();
                
                final Set<String> codes = operatorIndicators.keySet();
                for(String code : codes) {
                    realTimeStockMonitor.addStockCode(code);
                }

                jButton1.setEnabled(false);
                jButton2.setEnabled(true);
                
                return;
            }
            /*
            else if(result == JOptionPane.CLOSED_OPTION) {
                return;
            }
            */
        }
        
        initWizardDialog();
        

        int ret = wizard.showModalDialog();
        
        if(ret != Wizard.FINISH_RETURN_CODE)
            return;
        
        realTimeStockMonitor.stop();
        realTimeStockMonitor.clearStockCodes();
        removeAllIndicatorsFromTable();
        alertRecords.clear();
        
        WizardModel wizardModel = wizard.getModel();
        
        WizardPanelDescriptor wizardPanelDescriptor = wizardModel.getPanelDescriptor(WizardDownloadHistoryProgressDescriptor.IDENTIFIER);
        WizardDownloadHistoryProgressJPanel wizardDownloadHistoryProgressJPanel = (WizardDownloadHistoryProgressJPanel)wizardPanelDescriptor.getPanelComponent();
        
        operatorIndicators = wizardDownloadHistoryProgressJPanel.getOperatorIndicators();        
        
        final Set<String> codes = operatorIndicators.keySet();
        for(String code : codes) {
            realTimeStockMonitor.addStockCode(code);
        }
        
        jButton1.setEnabled(false);
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private JPopupMenu getMyTableColumnSelectionPopupMenu(final int mouseXLocation) {
        JPopupMenu popup = new JPopupMenu();
        TableModel tableModel = jTable1.getModel();
        final int col = tableModel.getColumnCount();
        
        for(int i=2; i<col; i++) {
            String name = tableModel.getColumnName(i);            
            
            boolean isVisible = true;
            
            try {
                TableColumn tableColumn = jTable1.getColumn(name);
            }
            catch(java.lang.IllegalArgumentException exp) {
                isVisible = false;
            }
            
            javax.swing.JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name, isVisible);
                        
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    String name = evt.getActionCommand();
                    JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)evt.getSource();
                    if(menuItem.isSelected() == false) {
                        JTableUtilities.removeTableColumn(jTable1, name);
                    }
                    else {
                        TableColumnModel colModel = jTable1.getColumnModel();
                        int vColIndex = colModel.getColumnIndexAtX(mouseXLocation);
                        JTableUtilities.insertTableColumnFromModel(jTable1, name, vColIndex);
                    }
                }
            });
            
            popup.add(menuItem);            
        }
        
        return popup;
    }
    
    private class TableColumnSelectionPopupListener extends MouseAdapter {        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                getMyTableColumnSelectionPopupMenu(e.getX()).show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }    

    private static class ColumnHeaderToolTips extends MouseMotionAdapter {

        // Current column whose tooltip is being displayed.
        // This variable is used to minimize the calls to setToolTipText().
        TableColumn curCol;
    
        // Maps TableColumn objects to tooltips
        Map<TableColumn, String> tips = new HashMap<TableColumn, String>();
    
        // If tooltip is null, removes any tooltip text.
        public void setToolTip(TableColumn col, String tooltip) {
            if (tooltip == null) {
                tips.remove(col);
            } else {
                tips.put(col, tooltip);
            }
        }
    
        public void mouseMoved(MouseEvent evt) {
            TableColumn col = null;
            JTableHeader header = (JTableHeader)evt.getSource();
            JTable table = header.getTable();
            TableColumnModel colModel = table.getColumnModel();
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
    
            // Return if not clicked on any column header
            if (vColIndex >= 0) {
                col = colModel.getColumn(vColIndex);
            }
    
            if (col != curCol) {
                header.setToolTipText((String)tips.get(col));
                curCol = col;
            }
        }
    }
    
    private void initTableHeaderToolTips() {
        JTableHeader header = jTable1.getTableHeader();
    
        ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
            
        header.addMouseMotionListener(tips);        
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        mainFrame = (MainFrame)javax.swing.SwingUtilities.getAncestorOfClass(MainFrame.class, IndicatorScannerJPanel.this);
    }    
    
    public void stop()
    {
        realTimeStockMonitor.stop();
        
        systemTrayAlertPool.shutdownNow();
        try {            
            systemTrayAlertPool.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException exp) {
            log.error("", exp);
        }
        
        emailAlertPool.shutdownNow();
        try {
            emailAlertPool.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException exp) {
            log.error("", exp);
        }    
        
        emailAlertPool = Executors.newFixedThreadPool(1);
        systemTrayAlertPool = Executors.newFixedThreadPool(1);        
    }
    
    public void initWizardDialog() {
        wizard = new Wizard((MainFrame)javax.swing.SwingUtilities.getAncestorOfClass(MainFrame.class, this));

        wizard.getDialog().setTitle("Indicator Scanning Wizard");

        WizardPanelDescriptor wizardSelectIndicatorDescriptor = new WizardSelectIndicatorDescriptor();
        wizard.registerWizardPanel(WizardSelectIndicatorDescriptor.IDENTIFIER, wizardSelectIndicatorDescriptor);
        
        WizardPanelDescriptor wizardSelectStockDescriptor = new WizardSelectStockDescriptor();
        wizard.registerWizardPanel(WizardSelectStockDescriptor.IDENTIFIER, wizardSelectStockDescriptor);

        WizardPanelDescriptor wizardSelectHistoryDescriptor = new WizardSelectHistoryDescriptor();
        wizard.registerWizardPanel(WizardSelectHistoryDescriptor.IDENTIFIER, wizardSelectHistoryDescriptor);

        WizardPanelDescriptor wizardVerifyDatabaseDescriptor = new WizardVerifyDatabaseDescriptor();
        wizard.registerWizardPanel(WizardVerifyDatabaseDescriptor.IDENTIFIER, wizardVerifyDatabaseDescriptor);
        
        WizardPanelDescriptor wizardDownloadHistoryProgressDescriptor = new WizardDownloadHistoryProgressDescriptor();
        wizard.registerWizardPanel(WizardDownloadHistoryProgressDescriptor.IDENTIFIER, wizardDownloadHistoryProgressDescriptor);

        WizardPanelDescriptor wizardIndicatorConstructionDescriptor = new WizardIndicatorConstructionDescriptor();
        wizard.registerWizardPanel(WizardIndicatorConstructionDescriptor.IDENTIFIER, wizardIndicatorConstructionDescriptor);
        
        wizard.setCurrentPanel(WizardSelectIndicatorDescriptor.IDENTIFIER); 
 
        // Center to screen.
        wizard.getDialog().setLocationRelativeTo(null);
    }
    
    public void updateScanningSpeed(int speed) {
        this.realTimeStockMonitor.setDelay(speed);
    }
    
    public void initRealTimeStockMonitor(java.util.List<StockServerFactory> stockServerFactories) {
        realTimeStockMonitor = new RealTimeStockMonitor(4, 20, MainFrame.getJStockOptions().getScanningSpeed());
        
        for(StockServerFactory factory : stockServerFactories) {
            realTimeStockMonitor.addStockServerFactory(factory);
        }
        
        realTimeStockMonitor.attach(this.realTimeStockMonitorObserver);
    }
    
    // This is the workaround to overcome Erasure by generics. We are unable to make MainFrame to
    // two observers at the same time.
    private org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>> getRealTimeStockMonitorObserver() {
        return new org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>>() {
            public void update(RealTimeStockMonitor monitor, java.util.List<Stock> stocks)
            {
                IndicatorScannerJPanel.this.update(monitor, stocks);
            }
        };
    }
    
    public void update(RealTimeStockMonitor monitor, final java.util.List<Stock> stocks) {
        for(Stock stock : stocks) {
            final java.util.List<OperatorIndicator> indicators = operatorIndicators.get(stock.getCode());
            
            if(indicators == null) continue;
            
            final JStockOptions jStockOptions = mainFrame.getJStockOptions();

            if(jStockOptions.isSingleIndicatorAlert()) {
                for(OperatorIndicator indicator : indicators) {
                    indicator.setStock(stock);

                    if(indicator.isTriggered()) {
                        addIndicatorToTable(indicator);
                        alert(indicator);
                    }
                    else {
                        removeIndicatorFromTable(indicator);
                    }
                }
            }
            else
            {
                // Multiple indicators alert.
                boolean alert = true;
                for(OperatorIndicator indicator : indicators) {
                    indicator.setStock(stock);

                    if(indicator.isTriggered() == false) {
                        alert = false;
                        break;
                    }
                }
                
                if(alert) {
                    for(OperatorIndicator indicator : indicators) {
                        addIndicatorToTable(indicator);
                        alert(indicator);
                    }                    
                }
                else {
                    for(OperatorIndicator indicator : indicators) {
                        removeIndicatorFromTable(indicator);
                    }                     
                }
            }
        }                
    }  
    
    // Should we synchronized the jTable1, or post the job at GUI event dispatch
    // queue?
    private void addIndicatorToTable(final Indicator indicator) {
        final Runnable r = new Runnable() {
            public void run() {          
                IndicatorTableModel tableModel = (IndicatorTableModel)jTable1.getModel();
                tableModel.addIndicator(indicator);
           } 
        };
        
        SwingUtilities.invokeLater(r);
    }
    
    private void removeIndicatorFromTable(final Indicator indicator) {
        final Runnable r = new Runnable() {
            public void run() {          
                IndicatorTableModel tableModel = (IndicatorTableModel)jTable1.getModel();
                tableModel.removeIndicator(indicator);
           } 
        };
        
        SwingUtilities.invokeLater(r);        
    }
    
    private void removeAllIndicatorsFromTable() {
        final Runnable r = new Runnable() {
            public void run() {          
                IndicatorTableModel tableModel = (IndicatorTableModel)jTable1.getModel();
                tableModel.removeAll();
           } 
        };
        
        SwingUtilities.invokeLater(r);        
    }
    
    private class TableRowPopupListener extends MouseAdapter {
        
        public void mouseClicked(MouseEvent evt) {
        }
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if(jTable1.getSelectedColumn() != -1)
                    getMyJTablePopupMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private ImageIcon getImageIcon(String imageIcon) {
        return new javax.swing.ImageIcon(getClass().getResource(imageIcon));
    }   

    private JPopupMenu getMyJTablePopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        TableModel tableModel = jTable1.getModel();            
        
        final MainFrame m = (MainFrame)javax.swing.SwingUtilities.getAncestorOfClass(MainFrame.class, IndicatorScannerJPanel.this);
        
        javax.swing.JMenuItem menuItem = new JMenuItem("History...", this.getImageIcon("/images/16x16/strokedocker.png"));
        
	menuItem.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
                    int rows[] = jTable1.getSelectedRows();
                    final IndicatorTableModel tableModel = (IndicatorTableModel)jTable1.getModel();
                    
                    for(int row : rows) {                
                        final int modelIndex = jTable1.convertRowIndexToModel(row);
                        final Indicator indicator = tableModel.getIndicator(modelIndex);
                        if(indicator != null)
                            m.displayHistoryChart(indicator.getStock());
                    } 
                }
	});
                
	popup.add(menuItem);
            
	popup.add(menuItem);
        
        return popup;
    }
    
    private String getIndicatorKey(Indicator indicator) {
        // Stock shouldn't be null.
        assert(indicator.getStock() != null);
        
        return indicator.toString() + indicator.getStock().getCode();
    }
    
    private void alert(final Indicator indicator) {        
        final JStockOptions jStockOptions = mainFrame.getJStockOptions();
        
        synchronized(alertRecords) {
            if(jStockOptions.isSingleIndicatorAlert() == true) {
                final String key = getIndicatorKey(indicator);
                
                if(alertRecords.contains(key)) return;
                
                alertRecords.add(key);
            }
            else {
                /* When performing multiple indicators alert, we will only display once for a same stock. */
                final String code = indicator.getStock().getCode();
                
                if(alertRecords.contains(code)) return;
                
                alertRecords.add(code);
            }      
        }                 
        
        if(jStockOptions.isPopupMessage()) {
            final Runnable r = new Runnable() {
                public void run() {
                    final Stock stock = indicator.getStock();
                    final String message = stock.getSymbol() + " (" + stock.getCode() + " " +
                            "last=" + stock.getLastPrice() + " high=" + stock.getHighPrice() + " " +
                            "low=" + stock.getLowPrice() + ") hits " + indicator.toString();
                    
                    mainFrame.displayPopupMessage(stock.getSymbol(), message);
                    
                    try {
                        Thread.sleep(jStockOptions.getAlertSpeed() * 1000);
                    }
                    catch(InterruptedException exp) {
                        log.error("", exp);
                    }
                }
            };
            
            try {
                systemTrayAlertPool.submit(r);
            }
            catch(java.util.concurrent.RejectedExecutionException exp) {
                log.error("", exp);
            }
        }
        
        if(jStockOptions.isSendEmail()) {
            final Runnable r = new Runnable() {
                public void run() {
                    final Stock stock = indicator.getStock();
                    final String title = stock.getSymbol() + " (" + stock.getCode() + " " +
                            "last=" + stock.getLastPrice() + " high=" + stock.getHighPrice() + " " +
                            "low=" + stock.getLowPrice() + ") hits " + indicator.toString();
                    
                    final String message = title + "\nbrought to you by JStock";
                    
                    try {                        
                        GoogleMail.Send(jStockOptions.getEmail(), jStockOptions.getEmailPassword(), jStockOptions.getEmail() + "@gmail.com", message, message);
                    } catch (AddressException exp) {
                        log.error("", exp);
                    } catch (MessagingException exp) {
                        log.error("", exp);
                    }
                }
            };
            
            try {
                emailAlertPool.submit(r); 
            }
            catch(java.util.concurrent.RejectedExecutionException exp) {
                log.error("", exp);
            }            
        }
    }
    
    public void repaintTable() {
        jTable1.repaint();
    }
    
    public void clearTableSelection() {
        jTable1.getSelectionModel().clearSelection();
    }
    
    private Wizard wizard;
    private RealTimeStockMonitor realTimeStockMonitor;
    private org.yccheok.jstock.engine.Observer<RealTimeStockMonitor, java.util.List<Stock>> realTimeStockMonitorObserver = this.getRealTimeStockMonitorObserver();
    private java.util.Map<String, java.util.List<OperatorIndicator>> operatorIndicators;

    private java.util.List<String> alertRecords = new java.util.ArrayList<String>();
    private ExecutorService emailAlertPool = Executors.newFixedThreadPool(1);
    private ExecutorService systemTrayAlertPool = Executors.newFixedThreadPool(1);
    private MainFrame mainFrame;
    
    private static final Log log = LogFactory.getLog(IndicatorScannerJPanel.class);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    
}
