/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.ui.components;

import gov.va.semoss.rdf.engine.impl.AbstractSesameEngine;
import gov.va.semoss.rdf.engine.impl.BigDataEngine;
import gov.va.semoss.rdf.engine.impl.SesameEngine;
import gov.va.semoss.util.Constants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author ryan
 */
public class RemoteDbPanel extends javax.swing.JPanel {

	public static final URL INTERNAL;

	static {
		URL temp;
		try {
			temp = new URL( "http://internal" );
		}
		catch ( Exception e ) {
			temp = null;
		}
		INTERNAL = temp;
	}

	/**
	 * Creates new form RemoteDbPanel
	 */
	public RemoteDbPanel( String ext, String inter ) {
		initComponents();

		remoteurl.setText( ext );
		insightsurl.setText( inter );
	}

	public URL getUrl() throws MalformedURLException {
		return new URL( remoteurl.getText() );
	}

	public URL getInsights() throws MalformedURLException {
		return new URL( insightsurl.getText() );
	}

	public Properties getConnectionProperties() throws MalformedURLException {
		Properties props = new Properties();
		if ( bigdataRb.isSelected() ) {
			props.setProperty( Constants.ENGINE_IMPL, BigDataEngine.class.getCanonicalName() );
		}
		else {
			props.setProperty( Constants.ENGINE_IMPL, SesameEngine.class.getCanonicalName() );
		}

		props.setProperty( SesameEngine.REPOSITORY_KEY, getUrl().toExternalForm() );
		props.setProperty( SesameEngine.INSIGHTS_KEY, getInsights().toExternalForm() );
		props.setProperty( AbstractSesameEngine.REMOTE_KEY, "true" );

		return props;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings( "unchecked" )
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    jLabel1 = new javax.swing.JLabel();
    remoteurl = new javax.swing.JTextField();
    insightsurl = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    bigdataRb = new javax.swing.JRadioButton();
    sesameRb = new javax.swing.JRadioButton();

    jLabel1.setText("Remote URL");

    jLabel2.setText("Insights URL");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(java.awt.Color.gray, 1, true), "Database Provider"));

    buttonGroup1.add(bigdataRb);
    bigdataRb.setText("Big Data/Blazegraph");
    bigdataRb.setEnabled(false);

    buttonGroup1.add(sesameRb);
    sesameRb.setSelected(true);
    sesameRb.setText("Sesame");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(bigdataRb)
          .addComponent(sesameRb))
        .addGap(0, 81, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(bigdataRb)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(sesameRb))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(insightsurl, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
          .addComponent(remoteurl)))
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1)
          .addComponent(remoteurl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(insightsurl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JRadioButton bigdataRb;
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JTextField insightsurl;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JTextField remoteurl;
  private javax.swing.JRadioButton sesameRb;
  // End of variables declaration//GEN-END:variables
}