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
package org.jboss.arquillian.extension.mail.test.client;

import java.util.List;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Session;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.mail.api.MailRemoteClient;
import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.extension.mail.api.MailTest;
import org.jboss.arquillian.extension.mail.api.MailTestUtil;
import org.jboss.arquillian.extension.mail.test.model.AccountService;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for more complex mail test. The tester has more control about the test cases but has to implement more.
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 */
@RunWith(Arquillian.class)
@MailServerSetup(protocols = {"smtp:3025", "imap:3026"}, users= {"john.doe@testmail.com:mypasswd"}, verbose = true)
public class MailTestWithUserVerificationTestCase {

	@Deployment // @TargetsContainer("container-2")
	public static JavaArchive deploy() {
		return ShrinkWrap.create(JavaArchive.class, "externalMail.jar")
				.addClass(AccountService.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Resource(mappedName = "java:jboss/mail/Default")
	private Session mailSession;	

	@MailRemoteClient
	private MailTestUtil mailTestUtil;
	
	@Test
	@MailTest(clearAllMails = true, verifyResult = false, verbose = true)
	public void fetchMessageTest() throws Exception {		
		mailTestUtil.sendTextEmail("john.doe@testmail.com", "testUser1@noreply", "Wildfly Mail 1", "Mail sent from Wildfly", mailSession);
		final List<Message> msgs = mailTestUtil.getReceivedMessages();
		int count = 0;
		for (Message msg : msgs) {
			if ("Wildfly Mail 1".equals(msg.getSubject())) {
				count++;
			}
		}
		if (count != 1) {
			throw new AssertionError("Expected 1 mail but was " + count);
		}
	}	

	@Test
	@MailTest(clearAllMails = true, verifyResult = false, verbose = true)
	public void fetchMessagesTest() throws Exception {		
		mailTestUtil.sendTextEmail("john.doe@testmail.com", "testUser1@noreply", "Wildfly Mail 1", "Mail sent from Wildfly", mailSession);
		mailTestUtil.sendTextEmail("john.doe@testmail.com", "testUser1@noreply", "Wildfly Mail 2", "Mail sent from Wildfly", mailSession);
		mailTestUtil.sendTextEmail("john.doe@testmail.com", "testUser1@noreply", "Wildfly Mail 3", "Mail sent from Wildfly", mailSession);
		final List<Message> msgs = mailTestUtil.getReceivedMessages();
		int count = 0;
		for (Message msg : msgs) {
			if (msg.getSubject().indexOf("Wildfly Mail") >= 0) {
				count++;
			}
		}
		if (count != 3) {
			throw new AssertionError("Expected 1 mail but was " + count);
		}
	}	
	
	@Test
	@MailTest(clearAllMails = true, verifyResult = false, verbose = true)
	public void clearAllMailsTest() throws Exception {		
		final List<Message> msgs = mailTestUtil.getReceivedMessages();
		if (msgs.size() != 0) {
			throw new AssertionError("Expected 0 mail but was " + msgs.size());
		}
	}
}