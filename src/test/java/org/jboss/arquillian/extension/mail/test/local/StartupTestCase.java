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
package org.jboss.arquillian.extension.mail.test.local;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.byteman.test.model.AccountService;
import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.extension.mail.api.MailTest;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link BMRule} on Method level Local protocol
 * 
 * Requires containers that use Local protocol, Weld | OpenEJB | IronJacamar |
 * OpenWebBeans
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
@MailServerSetup(protocols = {"smtp:3025"}, users= {"john.doe@testmail.com:mypasswd", "testUser1@noreply:mypasswd"})
public class StartupTestCase {

	@Deployment
	public static JavaArchive deploy() {
		return ShrinkWrap.create(JavaArchive.class)
				.addClass(AccountService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Resource(mappedName = "java:jboss/mail/testMail")
	private Session mailSession;
	
	@Test
	@MailTest(messageCount = 1)
	public void shouldBeAbleToInjectMethodLevelThrowRule() throws Exception {
		final MimeMessage m = new MimeMessage(mailSession);
		final Address from = new InternetAddress("testUser1@noreply");
		final Address[] to = new InternetAddress[] { new InternetAddress("john.doe@testmail.com") };

		m.setFrom(from);
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject("JBoss AS 7 Mail");
		m.setSentDate(new java.util.Date());
		m.setContent("Mail sent from JBoss AS 7", "text/plain");
		Transport.send(m);
	}
}