/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.poi.main;

import com.ostrichemulators.semtool.poi.main.GraphMLWriter;
import com.ostrichemulators.semtool.poi.main.ImportData;
import com.ostrichemulators.semtool.poi.main.LoadingSheetData;
import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author ryan
 */
public class GraphMLWriterTest {

	private static final File EXPECTED = new File( "src/test/resources/graphtest.graphml" );
	private static final Logger log = Logger.getLogger( GraphMLWriterTest.class );
	private static final Date now;

	static {
		TimeZone.setDefault( TimeZone.getTimeZone( "GMT-04:00" ) );
		Calendar cal = Calendar.getInstance();
		cal.set( 2031, 9, 22, 6, 58, 59 );
		cal.set( Calendar.MILLISECOND, 15 );
		now = cal.getTime();
	}

	@Test
	public void testWrite() throws Exception {
		LoadingSheetData rels = LoadingSheetData.relsheet( "Human Being", "Car", "Purchased" );
		rels.addProperties( Arrays.asList( "Price", "Date" ) );

		LoadingSheetData nodes = LoadingSheetData.nodesheet( "Human Being" );
		nodes.addProperties( Arrays.asList( "First Name", "Last Name" ) );

		ImportData data = new ImportData();
		data.add( rels );
		data.add( nodes );

		ValueFactory vf = new ValueFactoryImpl();
		Map<String, Value> props = new HashMap<>();
		props.put( "Price", vf.createLiteral( "3000 USD" ) );
		props.put( "Date", vf.createLiteral( now ) );
		rels.add( "Yuri", "Yugo", props );
		rels.add( "Yuri", "Pinto" );

		Map<String, Value> hprop = new HashMap<>();
		hprop.put( "First Name", vf.createLiteral( "Yuri" ) );
		hprop.put( "Last Name", vf.createLiteral( "Gagarin" ) );
		nodes.add( "Yuri", hprop );

		GraphMLWriter writer = new GraphMLWriter();
		StringWriter strings = new StringWriter();
		writer.write( data, strings );
		data.release();

		if ( log.isTraceEnabled() ) {
			File tmpdir = FileUtils.getTempDirectory();
			File trace = new File( tmpdir, EXPECTED.getName() );
			FileUtils.write( trace, strings.toString() );
		}

//		log.fatal( strings.toString() );
//		log.fatal(  FileUtils.readFileToString( EXPECTED ) );
		assertEquals( FileUtils.readFileToString( EXPECTED ), strings.toString() );
	}
}
