/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.web.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author ryan
 */
@Controller
public class HomeController extends SemossControllerBase {
	private static final Logger log = Logger.getLogger( HomeController.class );

	@RequestMapping( value = "/", method = RequestMethod.GET )
	public String getWelcome() {
		log.debug("Controller routing to index page.");
		return "main";
	}

	@RequestMapping( value = "/testDriver", method = RequestMethod.GET )
	public String getTestDriver() {
		log.debug("Controller routing to test Driver.");
		return "testDriver";
	}
}
