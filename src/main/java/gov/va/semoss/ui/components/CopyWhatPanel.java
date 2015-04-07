/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

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
	@SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    data = new javax.swing.JCheckBox();
    insights = new javax.swing.JCheckBox();
    label = new javax.swing.JLabel();

    data.setSelected(true);
    data.setText("Data");

    insights.setSelected(true);
    insights.setText("Insights");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(insights)
      .addComponent(data)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(data)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(insights))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox data;
  private javax.swing.JCheckBox insights;
  private javax.swing.JLabel label;
  // End of variables declaration//GEN-END:variables
}
