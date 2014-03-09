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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.extension.mail.impl.common.ExtractSetupUtil;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * MailServerInstaller
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public class MailServerInstaller {

	private static final Logger log = Logger.getLogger(MailServerInstaller.class.getName());
	
	private ConcurrentMap<String, GreenMailUser> userMap = new ConcurrentHashMap<String, GreenMailUser>();
	
	private GreenMail greenMail = null;

	@Inject @SuiteScoped
	private InstanceProducer<GreenMail> greenMailProxy;	  
	
	@Inject
	private Instance<ArquillianDescriptor> descriptorInst;

	public void installClass(@Observes BeforeClass event) {
		final MailServerSetup setup = ExtractSetupUtil.extractServerSetupFromTestClass(event);
		if (setup != null) {
			greenMail = setupServer(setup); 
			greenMailProxy.set(greenMail);
		}
	}

	public void installClass(@Observes AfterClass event) {
		if (greenMail != null) {
			greenMail.stop();
		}
	}
		
	public void mailEventListener(@Observes MailTestEvent event) {
		if (MailTestEvent.DeleteAllMails == event) {
			deleteAllMails();
		}
	}
	
	// -----------------------------------------------------------------------||
	// -- Private Methods ----------------------------------------------------||
	// -----------------------------------------------------------------------||

	private GreenMail setupServer(final MailServerSetup setup) {
		GreenMail server = null;
		if (setup.protocols() != null) {
			final ServerSetup[] serverSetup = getSetup(setup.protocols(), setup.host());
			server = new GreenMail(serverSetup);
			server.start();
		
			if (setup.users() != null) {
				for (String userItem : setup.users()) {
					GreenMailUser user = null;
					final String[] items = userItem.split(":", -1);
					if (items.length == 2) {
						user = server.setUser(items[0], items[1]);						
					} else if(items.length == 3) {
						server.setUser(items[0], items[1], items[2]);
					} else {
						throw new RuntimeException("User setup wrong - TODO better here");
					}
					
					if (user != null) {
						userMap.putIfAbsent(user.getEmail(), user);
					}
				}
			}
		}
		return server;
	}
	
	private ServerSetup[] getSetup(final String[] protocols, final String host) {
		final ServerSetup[] protocolSetups = new ServerSetup[protocols.length];
		for (int i = 0; i < protocols.length; i++) {
			String item = protocols[i];
			final String[] subitem = item.split(":", -1);
			if (subitem.length == 2) {
				final String protocol = subitem[0];
				final String port = subitem[1];
				protocolSetups[i] = new ServerSetup(Integer.valueOf(port), host, protocol);
			}
		}
		return protocolSetups;
	}
	
	private void deleteAllMails() throws RuntimeException {
		final Iterator<GreenMailUser> it = userMap.values().iterator();		
		while (it.hasNext()) {
			final GreenMailUser user = it.next();
			deleteAllUserMails(user.getEmail());
		}
	}
	
	private void deleteAllUserMails(String email) throws RuntimeException {
		final GreenMailUser user = userMap.get(email);
    	if (user != null) {
			try {
				final MailFolder folder = greenMail.getManagers().getImapHostManager().getInbox(user);
				folder.deleteAllMessages();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
    	} else {
    		log.warning("User does not exists with this email address: " + email);
    	}
    }
	
}
