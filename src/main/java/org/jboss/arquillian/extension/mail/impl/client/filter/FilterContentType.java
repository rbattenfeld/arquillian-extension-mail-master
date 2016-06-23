package org.jboss.arquillian.extension.mail.impl.client.filter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.extension.mail.api.MailTest;

public class FilterContentType extends AbstractFilterMessages {
	
	@Override
	public boolean isAccepted(MimeMessage mimeMessage, MailTest mailTest) throws MessagingException {
                String contentType = mimeMessage.getContentType();
                if(contentType.startsWith("multipart/mixed;")){//after this it will contain part info that you never check
                    contentType = "multipart/mixed;";
                }
            
		final Boolean isEqual = equals(mailTest.expectedContentType(), mimeMessage.getContentType());
		if (mailTest.verbose()) {
			getLogger().info(String.format("Compare contet type. Expected: %s Received: %s match result: %s", 
					mailTest.expectedContentType(), 
					mimeMessage.getContentType(), 
					isEqual.toString()));
		}
		return isEqual;
	}

}
