package org.jboss.arquillian.extension.mail.impl.client;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.extension.mail.api.MailTest;

public interface FilterMessages {

	public List<MimeMessage> check(final MailTest mailTest, final List<MimeMessage> msgList) throws MessagingException;
	
}
