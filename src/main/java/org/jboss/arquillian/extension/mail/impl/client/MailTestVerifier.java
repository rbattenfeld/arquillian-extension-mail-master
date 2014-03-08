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

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.mail.api.MailTest;
import org.jboss.arquillian.extension.mail.api.MailTestAssertionError;
import org.jboss.arquillian.extension.mail.impl.client.filter.FilterChain;
import org.jboss.arquillian.extension.mail.impl.common.ExtractSetupUtil;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

import com.icegreen.greenmail.util.GreenMail;

/**
 * MailServerInstaller
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public class MailTestVerifier {
	
	private MailTest mailTest = null;
	
	@Inject @SuiteScoped
	private InstanceProducer<GreenMail> greenMailProxy;	  	

	@Inject
	private Event<MailTestEvent> mailTestEvent;
	  
	public void installMethod(@Observes Before event) {
		mailTest = ExtractSetupUtil.extractMailTestFromTestMethod(event);
		if (mailTest != null) {
			if (mailTest.clearAllMails()) {
				mailTestEvent.fire(MailTestEvent.DeleteAllMails);
			}
		}
	}
	
	public void uninstallMethod(@Observes After event) {
		if (mailTest != null) {
			try {
				final MimeMessage[] messages = greenMailProxy.get().getReceivedMessages();	
				final FilterChain chain = new FilterChain();						
				final List<MimeMessage> messagesFiltered = chain.filter(mailTest, messages);
				if (mailTest.expectedMessageCount() != messagesFiltered.size()) {
					throw new MailTestAssertionError("Expected");
				}				
			} catch (MessagingException ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
			mailTest = null;
		}
	}
	
	// -----------------------------------------------------------------------||
	// -- Private Methods ----------------------------------------------------||
	// -----------------------------------------------------------------------||

}
