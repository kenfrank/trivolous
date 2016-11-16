package com.trivolous.game.domain.logic;


import static org.junit.Assert.assertEquals;

import org.apache.velocity.app.VelocityEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trivolous.game.domain.Game;
import com.trivolous.game.notify.NotificationFormatter;
import com.trivolous.game.notify.NotificationFormatter.EmailContent;

public class TestNotificationFormatter {
	Game game;
	NotificationFormatter nf;

	@Before
	public void setUp() throws Exception {
		game = new Game();
		game.setName("UnitTest Game");

		nf = new NotificationFormatter();
		nf.setBaseUrl("http://trivolous.com/unittest/");
		VelocityEngine ve = new VelocityEngine();
		ve.addProperty("resource.loader", "class");
		ve.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		nf.setVelocityEngine(ve);
	
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRemindAutostart() {
		
		EmailContent emailContent = nf.formatRemindAutostart(game);
		assertEquals(false, emailContent.htmlText.isEmpty());
		assertEquals(false, emailContent.plainText.isEmpty());
		assertEquals(false, emailContent.subject.isEmpty());
	}
	
	@Test
	public void testInvite() {
		
		boolean isReminder = false;
		boolean isLastReminder = false;
		EmailContent emailContent = nf.formatInvite(game, isReminder, isLastReminder);
		assertEquals(false, emailContent.htmlText.isEmpty());
		assertEquals(false, emailContent.plainText.isEmpty());
		assertEquals(false, emailContent.subject.isEmpty());

		isReminder = true;
		emailContent = nf.formatInvite(game, isReminder, isLastReminder);
		assertEquals(false, emailContent.htmlText.isEmpty());
		assertEquals(false, emailContent.plainText.isEmpty());
		assertEquals(false, emailContent.subject.isEmpty());

		isLastReminder = true;
		emailContent = nf.formatInvite(game, isReminder, isLastReminder);
		assertEquals(false, emailContent.htmlText.isEmpty());
		assertEquals(false, emailContent.plainText.isEmpty());
		assertEquals(false, emailContent.subject.isEmpty());
	}
	
	// TODO -- add test for all methods... this is mainly to make sure .vm files are there and ok.  
	// There have been a few latent issue here because these are NOT tested!
	

}
