package org.jboss.arquillian.extension.mail.impl.client.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.extension.mail.api.MailTest;

public class FilterChain {

	public List<MimeMessage> filter(final MailTest mailTest, final MimeMessage[] msgs) throws MessagingException {
		return applyFilters(mailTest, msgs);
	}
	
	//-----------------------------------------------------------------------||
	//-- Private Methods ----------------------------------------------------||
	//-----------------------------------------------------------------------||
	
	private List<MimeMessage> applyFilters(final MailTest mailTest, final MimeMessage[] msgs) throws MessagingException {
		List<MimeMessage> msgList = Arrays.asList(msgs);
		for (FilterMessages filter : getFilters()) {
			msgList = filter.check(mailTest, msgList);
		}
		return msgList;
	}
	
	private List<FilterMessages> getFilters() {
		final List<FilterMessages> filterList = new ArrayList<FilterMessages>();
		filterList.add(new FilterMatchSubjectText());
		filterList.add(new FilterContentType());
		return filterList;
	}
}
