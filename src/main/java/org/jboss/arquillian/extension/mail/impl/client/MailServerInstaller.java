/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.extension.mail.impl.client;

import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.extension.mail.api.MailTest;
import org.jboss.arquillian.extension.mail.impl.common.ExtractSetupUtil;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * MailServerInstaller
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public class MailServerInstaller {

	/** The class GreenMail instance */
	private GreenMail greenMailClassInstance = null;
	
	/** The method GreenMail instance */
	private GreenMail greenMailMethodInstance = null;
	
	private MailTest mailTest = null;

	@Inject
	private Instance<ArquillianDescriptor> descriptorInst;

	public void installClass(@Observes BeforeClass event) {
		final MailServerSetup setup = ExtractSetupUtil.extractSetup(event);
		if (setup != null) {
			greenMailClassInstance = setupServer(setup); 
//			if (setup.protocols() != null) {
//				final ServerSetup[] serverSetup = getSetup(setup.protocols());
//				greenMailClassInstance = new GreenMail(serverSetup);
//				greenMailClassInstance.start();
//			}
//			
//			if (setup.users() != null) {
//				for (String userItem : setup.users()) {
//					final String[] items = userItem.split(":", -1);
//					if (items.length == 2) {
//						greenMailClassInstance.setUser(items[0], items[1]);
//					} else if(items.length == 3) {
//						greenMailClassInstance.setUser(items[0], items[1], items[2]);
//					} else {
//						throw new RuntimeException("User setup wrong - TODO better here");
//					}
//				}
//			}
		}
	}

	public void installClass(@Observes AfterClass event) {
		if (greenMailClassInstance != null) {
			greenMailClassInstance.stop();
		}
	}
	
	public void installMethod(@Observes Before event) {
		final MailServerSetup setup = ExtractSetupUtil.extractSetup(event);
		if (setup != null) {
			greenMailMethodInstance = setupServer(setup);
		}
		mailTest = ExtractSetupUtil.extractTest(event);
	}
	
	public void uninstallMethod(@Observes After event) {
		if (mailTest != null) {
			if (isEmpty(mailTest.sentTo()) && isEmpty(mailTest.sentFrom())) {
				final int expectedCount = mailTest.messageCount();
				final MimeMessage[] messages = greenMailClassInstance.getReceivedMessages();
				if (messages != null) {
					if (expectedCount != messages.length) {
						throw new RuntimeException("");
					}
				} else {
					if (expectedCount != 0) {
						throw new RuntimeException("");
					}
				}
			}
			mailTest = null;
		}
		
		if (greenMailMethodInstance != null) {
			greenMailMethodInstance.stop();
		}
	}
	
	// -----------------------------------------------------------------------||
	// -- Private Methods ----------------------------------------------------||
	// -----------------------------------------------------------------------||

	private GreenMail setupServer(final MailServerSetup setup) {
		GreenMail server = null;
		if (setup.protocols() != null) {
			final ServerSetup[] serverSetup = getSetup(setup.protocols());
			server = new GreenMail(serverSetup);
			server.start();
		
			if (setup.users() != null) {
				for (String userItem : setup.users()) {
					final String[] items = userItem.split(":", -1);
					if (items.length == 2) {
						server.setUser(items[0], items[1]);
					} else if(items.length == 3) {
						server.setUser(items[0], items[1], items[2]);
					} else {
						throw new RuntimeException("User setup wrong - TODO better here");
					}
				}
			}
		}
		return server;
	}
	
	private ServerSetup[] getSetup(final String[] protocols) {
		final ServerSetup[] protocolSetups = new ServerSetup[protocols.length];
		for (int i = 0; i < protocols.length; i++) {
			String item = protocols[i];
			final String[] subitem = item.split(":", -1);
			if (subitem.length == 2) {
				final String protocol = subitem[0];
				final String port = subitem[1];
				protocolSetups[i] = new ServerSetup(Integer.valueOf(port),
						null, protocol);
			}
		}
		return protocolSetups;
	}

	private boolean isEmpty(final String value) {
		if (value == null || value.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
