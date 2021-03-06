/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.ui.actions;

import com.ostrichemulators.semtool.poi.main.ImportData;
import com.ostrichemulators.semtool.poi.main.LoadingSheetData;
import com.ostrichemulators.semtool.ui.components.AddTabPanel;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import com.ostrichemulators.semtool.ui.components.LoadingPlaySheetFrame;
import com.ostrichemulators.semtool.ui.components.ProgressTask;
import com.ostrichemulators.semtool.ui.components.playsheets.LoadingPlaySheetBase;
import com.ostrichemulators.semtool.ui.components.playsheets.NodeLoadingPlaySheet;
import com.ostrichemulators.semtool.ui.components.playsheets.RelationshipLoadingPlaySheet;
import com.ostrichemulators.semtool.util.DIHelper;

import javax.swing.JOptionPane;

/**
 *
 * @author ryan
 */
public class NewLoadingSheetAction extends DbAction {

	private final Frame frame;

	public NewLoadingSheetAction( String optg, Frame frame ) {
		super( optg, "Create Loading Sheet", "open-file3" );
		this.frame = frame;
		putValue( SHORT_DESCRIPTION, "Create a new Loading Sheet Workbook" );
		putValue( SMALL_ICON, DbAction.getIcon( "input_review_add1" ) );
	}

	@Override
	public void actionPerformed( ActionEvent ae ) {
		AddTabPanel panel = new AddTabPanel();
		int ans = JOptionPane.showOptionDialog( frame, panel, "Tab Details",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[]{ "Create", "Cancel" }, "Create" );
		if ( 0 == ans ) {
			LoadingSheetData lsd = panel.getSheet();
			LoadingPlaySheetBase base = ( lsd.isRel()
					? new RelationshipLoadingPlaySheet( lsd, true )
					: new NodeLoadingPlaySheet( lsd, true ) );
			
			LoadingPlaySheetFrame psf = new LoadingPlaySheetFrame();
			psf.addTab( base );
			psf.setTitle( "Loading Sheet Data" );
			DIHelper.getInstance().getDesktop().add( psf );
		}
	}

	@Override
	protected ProgressTask getTask( ActionEvent ae ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}
}
