package org.jboss.arquillian.extension.mail.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public interface MailTestUtil {
	
	public void sendTextEmail(String to, String from, String subject, String msg, Session session);

	public void sendAttachmentEmail(String to, String from, String subject, String msg, final byte[] attachment, final String contentType, final String filename, final String description, final Session session) throws MessagingException, IOException;

	public List<Message> getReceivedMessages();
	
	public List<Message> getReceivedMessages(String user, String passwd);
	
	public MimeMessage newMimeMessage(InputStream inputStream);
	
	public MimeMessage newMimeMessage(String mailString) throws MessagingException;
	
	public String getBody(Part msg);
	
	public String getHeaders(Part msg);
	
	public String getWholeMessage(Part msg);

	public String toString(Part msg);

	public String getAddressList(Address[] addresses);

	public byte[] getBodyAsBytes(Part msg);

	public byte[] getHeaderAsBytes(Part part);

	public boolean hasNonTextAttachments(Part m);
	
	public int getLineCount(String str);

}
