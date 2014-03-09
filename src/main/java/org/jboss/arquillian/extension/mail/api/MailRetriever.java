package org.jboss.arquillian.extension.mail.api;

import java.util.List;

import javax.mail.Message;

public interface MailRetriever {

	public List<Message> getReceivedMessages();

}
