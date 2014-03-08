package org.jboss.arquillian.extension.mail.impl.client.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jboss.arquillian.extension.mail.api.MailTest;

public abstract class AbstractFilterMessages implements FilterMessages {
	
	private static final Logger log = Logger.getLogger(AbstractFilterMessages.class.getName());
	
	protected abstract boolean isAccepted(MimeMessage mimeMessage, final MailTest mailTest) throws MessagingException;
			
	@Override
	public List<MimeMessage> check(final MailTest mailTest, final List<MimeMessage> msgList) throws MessagingException {
		final List<MimeMessage> filteredList = new ArrayList<MimeMessage>();
		for (MimeMessage mimeMessage : msgList) {
			if (isAccepted(mimeMessage, mailTest)) {
				filteredList.add(mimeMessage);
			}
		}		
		return filteredList;
	}
	
	public boolean equals(final Object val1, final Object val2) {
		return new EqualsBuilder()
	        .append(val1, val2)
	        .isEquals();
	}
	
	public Logger getLogger() {
		return log;
	}

}
