package org.jboss.arquillian.extension.mail.impl.client;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.extension.mail.api.MailTest;

public class FilterMatchSubjectText extends AbstractFilterMessages {

	@Override
	public boolean isAccepted(MimeMessage mimeMessage, MailTest mailTest) throws MessagingException {
		final Boolean isEqual = equals(mailTest.matchSubject(), mimeMessage.getSubject());
		if (mailTest.verbose()) {
			getLogger().info(String.format("Compare subject: expected: %s in message: %s match result: %s", mailTest.contentType(), mimeMessage.getContentType(), isEqual.toString()));
		}		
		return isEqual;
	}

}
