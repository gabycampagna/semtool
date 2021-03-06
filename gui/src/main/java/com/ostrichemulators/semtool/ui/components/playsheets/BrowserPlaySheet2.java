/**
 * *****************************************************************************
 * Copyright 2013 SEMOSS.ORG
 *
 * This file is part of SEMOSS.
 *
 * SEMOSS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SEMOSS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SEMOSS. If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */
package com.ostrichemulators.semtool.ui.components.playsheets;

import com.ostrichemulators.semtool.util.Constants;
import com.ostrichemulators.semtool.util.DIHelper;
import com.ostrichemulators.semtool.util.ExportUtility;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;

import netscape.javascript.JSObject;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.Gson;

/**
 * The BrowserPlaySheet creates an instance of a browser to utilize the D3
 * Javascript library to create visualizations. This whole class and all its
 * subclasses need to be refactored for sanity's sake.
 */
public abstract class BrowserPlaySheet2 extends ImageExportingPlaySheet {

	private static final long serialVersionUID = 1142415334918968440L;
	private static final Logger log = Logger.getLogger( BrowserPlaySheet2.class );

	private final Map<String, Object> dataHash = new HashMap<>();
	private final String fileName;

	protected final JFXPanel jfxPanel = new JFXPanel();
	protected WebEngine engine;
	protected Scene scene;

	public BrowserPlaySheet2( String htmlPath ) {
		setLayout( new BorderLayout() );
		add( jfxPanel );

		fileName = "file:///"
				+ new File( DIHelper.getInstance().getLocalStore(), htmlPath );

		Platform.setImplicitExit( false );
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				WebView view = new WebView();
				scene = new Scene( view );
				jfxPanel.setScene( scene );

				engine = view.getEngine();
				engine.setOnAlert(
						new EventHandler<WebEvent<String>>() {
							@Override
							public void handle( WebEvent<String> event ) {
								log.debug( "handling event: " + event );
								if ( "document:loaded".equals( event.getData() ) ) {
									log.debug( "Document is loaded." );
									sendToWebView();
								}
								else if ( event.getData().startsWith( "download:csv" ) ) {
									log.debug( "Downloading CSV file from browser." );
									downloadCSV( event.getData().substring( 12 ) );
								}
							}

						}
				);
			}
		} );
	}

	/**
	 * Loads the HTML file
	 */
	public void createView() {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				engine.load( fileName );
			}
		} );
	}

	protected void createData() {
		// we don't have any data to create
	}

	/**
	 * Converts the internal data a Json object and passes it to the browser.
	 */
	public void sendToWebView() {
		// Initialize the key used to store the Data Series being visualized
		String dataSeriesKey = "dataSeries";
		// Get the data series
		Object dataSeries = dataHash.get( dataSeriesKey );
		dataSeries = processDataSeriesForDisplay( dataHash.get( dataSeriesKey ) );
		// After processing, put the data back
		dataHash.put( dataSeriesKey, dataSeries );
		// Continue with the processing
		String json = new Gson().toJson( dataHash );
		if ( null != json && !"".equals( json ) && !"{}".equals( json ) ) {
			json = json.replace( "\\n", " " );
			json = "'" + json + "'";
		}

		executeJavaScript( "start(" + json + ");" );
	}

	/**
	 * Gives subclasses a chance to modify their own data (?) before sending it to
	 * the webview engine. This function needs to be removed completely, I think
	 *
	 * @param undigestedData
	 * @return
	 */
	protected Object processDataSeriesForDisplay( Object undigestedData ) {
		return undigestedData;
	}

	@Override
	public void incrementFont( float incr ) {
		executeJavaScript( "resizeFont(" + incr + ");" );
	}

	public void executeJavaScript( String functionNameAndArgs ) {
		log.debug( "Calling javascript method: " + functionNameAndArgs );

		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				// get firebug involved...
				// engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
				engine.executeScript( functionNameAndArgs );
				postExecute( engine );
			}
		} );
	}

	protected void postExecute( WebEngine eng ) {
		// nothing by default
	}

	public void registerFunction( String namespace, Object theClass ) {
		log.debug( "Registering Java class whose methods can be called from javascript. Namespace: "
				+ namespace + ", Java class: " + theClass );

		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				engine.getLoadWorker().stateProperty().addListener(
						new ChangeListener<State>() {
							@Override
							public void changed( ObservableValue<? extends Worker.State> ov,
									State oldState, State newState ) {
								if ( newState == Worker.State.SUCCEEDED ) {
									JSObject jsobj = (JSObject) engine.executeScript( "window" );
									jsobj.setMember( namespace, theClass );
								}
							}
						}
				);
			}
		} );
	}

	/**
	 * Method processQueryData. Processes the data from the SPARQL query into an
	 * appropriate format for the specific play sheet.
	 *
	 * @return the data from the SPARQL query results, formatted accordingly.
	 */
	public Map<String, Object> processQueryData() {
		return new HashMap<>();
	}

	public void addDataHash( Map<String, Object> newdata ) {
		dataHash.putAll( newdata );
	}

	private void downloadCSV( String data ) {
		ExportUtility.doExportCSVWithDialogue( this, data );
	}

	@Override
	protected BufferedImage getExportImage() throws IOException {
		BufferedImage bufferedImage = new BufferedImage( getWidth(), getHeight(),
				BufferedImage.TYPE_INT_ARGB );
		paint( bufferedImage.getGraphics() );
		try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
			ImageIO.write( bufferedImage, Constants.PNG, baos );
			return ImageIO.read( new ByteArrayInputStream( baos.toByteArray() ) );
		}
	}

	protected BufferedImage getExportImageFromSVGBlock() throws IOException {
		log.debug( "Using SVG block to save image." );
		DOMReader rdr = new DOMReader();
		Document doc = rdr.read( engine.getDocument() );
		Document svgdoc = null;
		File svgfile = null;
		try {
			Map<String, String> namespaceUris = new HashMap<>();
			namespaceUris.put( "svg", "http://www.w3.org/2000/svg" );
			namespaceUris.put( "xhtml", "http://www.w3.org/1999/xhtml" );

			XPath xp = DocumentHelper.createXPath( "//svg:svg" );
			xp.setNamespaceURIs( namespaceUris );
			// don't forget about the styles
			XPath stylexp = DocumentHelper.createXPath( "//xhtml:style" );
			stylexp.setNamespaceURIs( namespaceUris );

			svgdoc = DocumentHelper.createDocument();
			Element svg = null;
			List<?> theSVGElements = xp.selectNodes( doc );
			if ( theSVGElements.size() == 1 ) {
				svg = Element.class.cast( theSVGElements.get( 0 ) ).createCopy();
			}
			else {
				int currentTop = 0;
				int biggestSize = 0;
				for ( int i = 0; i < theSVGElements.size(); i++ ) {
					Element thisElement = Element.class.cast( theSVGElements.get( i ) ).createCopy();
					int thisSize = thisElement.asXML().length();
					if ( thisSize > biggestSize ) {
						currentTop = i;
						biggestSize = thisSize;
					}
				}
				svg = Element.class.cast( theSVGElements.get( currentTop ) ).createCopy();
			}

			svgdoc.setRootElement( svg );

			Element oldstyle = Element.class.cast( stylexp.selectSingleNode( doc ) );
			if ( null != oldstyle ) {
				Element defs = svg.addElement( "defs" );
				Element style = defs.addElement( "style" );
				style.addAttribute( "type", "text/css" );
				String styledata = oldstyle.getTextTrim();
				style.addCDATA( styledata );
				// put the stylesheet definitions first
				List l = svg.elements();
				l.remove( defs );
				l.add( 0, defs );
			}

			// clean up the SVG a little...
			// d3 comes up with coords like
			// M360,27475.063247863247C450,27475.063247863247 450,27269.907692307694 540,27269.907692307694
			XPath cleanxp1 = DocumentHelper.createXPath( "//svg:path" );
			Pattern pat = Pattern.compile( ",([0-9]+)\\.([0-9]{1,2})[0-9]+" );
			cleanxp1.setNamespaceURIs( namespaceUris );
			List<?> cleanups = cleanxp1.selectNodes( svgdoc );
			for ( Object n : cleanups ) {
				Element e = Element.class.cast( n );
				String dstr = e.attributeValue( "d" );
				Matcher m = pat.matcher( dstr );
				dstr = m.replaceAll( ",$1.$2 " );
				e.addAttribute( "d", dstr.replaceAll( "([0-9])C([0-9])", "$1 C$2" ).trim() );
			}
			XPath cleanxp2 = DocumentHelper.createXPath( "//svg:g[@class='node']" );
			cleanxp2.setNamespaceURIs( namespaceUris );
			cleanups = cleanxp2.selectNodes( svgdoc );
			for ( Object n : cleanups ) {
				Element e = Element.class.cast( n );
				String dstr = e.attributeValue( "transform" );
				Matcher m = pat.matcher( dstr );
				dstr = m.replaceAll( ",$1.$2" );
				e.addAttribute( "transform", dstr.trim() );
			}

			svgfile = File.createTempFile( "graphviz-", ".svg" );
			try ( Writer svgw = new BufferedWriter( new FileWriter( svgfile ) ) ) {
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter xmlw = new XMLWriter( svgw, format );
				xmlw.write( svgdoc );
				xmlw.close();

				if ( log.isDebugEnabled() ) {
					FileUtils.copyFile( svgfile,
							new File( FileUtils.getTempDirectory(), "graphvisualization.svg" ) );
				}
			}

			try ( Reader svgr = new BufferedReader( new FileReader( svgfile ) ) ) {
				TranscoderInput inputSvg = new TranscoderInput( svgr );

				ByteArrayOutputStream baos = new ByteArrayOutputStream( (int) svgfile.length() );
				TranscoderOutput outputPng = new TranscoderOutput( baos );

				try {
					PNGTranscoder transcoder = new PNGTranscoder();
					transcoder.addTranscodingHint( PNGTranscoder.KEY_INDEXED, 256 );
					transcoder.addTranscodingHint( ImageTranscoder.KEY_BACKGROUND_COLOR,
							Color.WHITE );
					transcoder.transcode( inputSvg, outputPng );
				}
				catch ( Throwable t ) {
					log.error( t, t );
				}
				baos.flush();
				baos.close();

				return ImageIO.read( new ByteArrayInputStream( baos.toByteArray() ) );
			}
		}
		catch ( InvalidXPathException e ) {
			log.error( e );
			String msg = "Problem creating image";
			if ( null != svgdoc ) {
				try {
					File errsvg = new File( FileUtils.getTempDirectory(), "graphvisualization.svg" );
					FileUtils.write( errsvg, svgdoc.asXML() );
					msg = "Could not create the image. SVG data store here: "
							+ errsvg.getAbsolutePath();
				}
				catch ( IOException ex ) {
					// don't care
				}
			}
			throw new IOException( msg, e );
		}
		finally {
			if ( null != svgfile ) {
				FileUtils.deleteQuietly( svgfile );
			}
		}
	}
}
