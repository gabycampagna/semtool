/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import java.net.URL;
import java.util.Collection;
import javax.swing.JCheckBox;

/**
 *
 * @author ryan
 */
public final class CopyWhatPanel extends javax.swing.JPanel {

	/**
	 * Creates new form CopyWhatPanel
	 */
	public CopyWhatPanel() {
		initComponents();
		showVocabularies( false );
	}

	public CopyWhatPanel( String s ) {
		this();
		setLabel( s );
	}

	public CopyWhatPanel( String s, boolean selData, boolean selIns ) {
		this();
		setLabel( s );
		selectData( selData );
		selectInsight( selIns );
	}

	public void showVocabularies( boolean b ) {
		vocabPanel.setVisible( b );
	}
	
	public void enableVocabularies( boolean b ){
		vocabPanel.setEnabled( b );
	}

	public Collection<URL> getSelectedVocabularies() {
		return vocabPanel.getSelectedVocabularies();
	}
	
	public JCheckBox getDataBox(){
		return data;
	}

	public void setLabel( String l ) {
		label.setText( l );
	}

	public boolean isDataSelected() {
		return data.isSelected();
	}

	public boolean isInsightSelected() {
		return insights.isSelected();
	}

	public void selectInsight( boolean b ) {
		insights.setSelected( b );
	}

	public void selectData( boolean b ) {
		data.setSelected( b );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    data = new javax.swing.JCheckBox();
    insights = new javax.swing.JCheckBox();
    label = new javax.swing.JLabel();
    vocabPanel = new gov.va.semoss.ui.components.VocabularyPanel();

    data.setSelected(true);
    data.setText("Data");

    insights.setSelected(true);
    insights.setText("Insights");

    vocabPanel.setLayout(new javax.swing.BoxLayout(vocabPanel, javax.swing.BoxLayout.PAGE_AXIS));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
      .addComponent(vocabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(insights)
          .addComponent(data))
        .addGap(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(data)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(insights)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        //.addComponent(vocabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addComponent(vocabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
       // .addComponent(vocabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox data;
  private javax.swing.JCheckBox insights;
  private javax.swing.JLabel label;
  private gov.va.semoss.ui.components.VocabularyPanel vocabPanel;
  // End of variables declaration//GEN-END:variables
}
