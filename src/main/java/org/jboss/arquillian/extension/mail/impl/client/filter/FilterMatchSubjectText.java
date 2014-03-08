package org.jboss.arquillian.extension.mail.impl.client.filter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.extension.mail.api.MailTest;

public class FilterMatchSubjectText extends AbstractFilterMessages {

	@Override
	public boolean isAccepted(MimeMessage mimeMessage, MailTest mailTest) throws MessagingException {
		final Boolean isEqual = equals(mailTest.expectedSubject(), mimeMessage.getSubject());
		if (mailTest.verbose()) {
			getLogger().info(String.format("Compare subject. Expected: %s Received: %s match result: %s", 
					mailTest.expectedContentType(), 
					mimeMessage.getContentType(), 
					isEqual.toString()));
		}		
		return isEqual;
	}

}
