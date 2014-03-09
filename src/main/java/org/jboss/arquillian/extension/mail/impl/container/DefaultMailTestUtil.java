package org.jboss.arquillian.extension.mail.impl.container;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.mail.util.SharedByteArrayInputStream;

import org.jboss.arquillian.extension.mail.api.MailTestUtil;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class DefaultMailTestUtil implements MailTestUtil {

	private static final Logger log = Logger.getLogger(DefaultMailTestUtil.class.getName());

	private final boolean verbose;

	private final ServerSetup imapSetup;

	private final String[] users;

	public DefaultMailTestUtil(final ServerSetup imapSetup, final String[] users, final boolean verbose) {
		this.imapSetup = imapSetup;
		this.users = users;
		this.verbose = verbose;
	}

	@Override
	public List<Message> getReceivedMessages() {
		final List<Message> msgs = new ArrayList<Message>();
		try {
			for (final String user : users) {
				final String[] userItems = user.split(":", -1);
				if (userItems.length == 2) {
					msgs.addAll(getMessages(userItems[0], userItems[1]));
				} else if (userItems.length == 3) {
					msgs.addAll(getMessages(userItems[0], userItems[2]));
				}
			}
		} catch (Exception ex) {
			log.log(Level.WARNING, ex.getMessage(), ex);
		}
		return msgs;
	}
	
	@Override
	public List<Message> getReceivedMessages(String user, String passwd) {
		final List<Message> msgs = new ArrayList<Message>();
		try {
			msgs.addAll(getMessages(user, passwd));
		} catch (Exception ex) {
			log.log(Level.WARNING, ex.getMessage(), ex);
		}
		return msgs;
	}

	@Override
	public void sendTextEmail(final String to, final String from, final String subject, final String msg, final Session session) {
		try {
			final Address[] tos = new InternetAddress[] { new InternetAddress(to) };
			final Address[] froms = new InternetAddress[] { new InternetAddress(from) };
			final MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(froms[0]);
			mimeMessage.setText(msg);
			Transport.send(mimeMessage, tos);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendAttachmentEmail(final String to, final String from, final String subject, final String msg, final byte[] attachment, final String contentType, final String filename, final String description, final Session session) throws MessagingException, IOException {
		final Address[] tos = new InternetAddress[] { new InternetAddress(to) };
		final Address[] froms = new InternetAddress[] { new InternetAddress(from) };
		final MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setSubject(subject);
		mimeMessage.setFrom(froms[0]);

		final MimeMultipart multiPart = new MimeMultipart();
		final MimeBodyPart textPart = new MimeBodyPart();
		multiPart.addBodyPart(textPart);
		textPart.setText(msg);

		final MimeBodyPart binaryPart = new MimeBodyPart();
		multiPart.addBodyPart(binaryPart);

		final DataSource ds = new DataSource() {
			public InputStream getInputStream() throws IOException {
				return new ByteArrayInputStream(attachment);
			}

			public OutputStream getOutputStream() throws IOException {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				byteStream.write(attachment);
				return byteStream;
			}

			public String getContentType() {
				return contentType;
			}

			public String getName() {
				return filename;
			}
		};

		binaryPart.setDataHandler(new DataHandler(ds));
		binaryPart.setFileName(filename);
		binaryPart.setDescription(description);

		mimeMessage.setContent(multiPart);
		Transport.send(mimeMessage, tos);
	}

	@Override
	public MimeMessage newMimeMessage(final InputStream inputStream) {
		return GreenMailUtil.newMimeMessage(inputStream);
	}

	@Override
	public MimeMessage newMimeMessage(final String mailString) throws MessagingException {
		return GreenMailUtil.newMimeMessage(mailString);
	}

	@Override
	public String getBody(final Part msg) {
		return GreenMailUtil.getBody(msg);
	}

	@Override
	public String getHeaders(final Part msg) {
		return GreenMailUtil.getHeaders(msg);
	}

	@Override
	public String getWholeMessage(final Part msg) {
		return GreenMailUtil.getWholeMessage(msg);
	}

	@Override
	public String toString(final Part msg) {
		return GreenMailUtil.toString(msg);
	}

	@Override
	public String getAddressList(Address[] addresses) {
		return GreenMailUtil.getAddressList(addresses);
	}

	@Override
	public byte[] getBodyAsBytes(Part msg) {
		return GreenMailUtil.getBodyAsBytes(msg);
	}

	@Override
	public byte[] getHeaderAsBytes(Part part) {
		return GreenMailUtil.getHeaderAsBytes(part);
	}

	@Override
	public boolean hasNonTextAttachments(Part m) {
		return GreenMailUtil.hasNonTextAttachments(m);
	}

	@Override
	public int getLineCount(String str) {
		return GreenMailUtil.getLineCount(str);
	}

	// -----------------------------------------------------------------------||
	// -- Private Methods ----------------------------------------------------||
	// -----------------------------------------------------------------------||

	private List<Message> getMessages(final String user, final String passwd) throws MessagingException {
		try {
			return getTestMessages(user, passwd);
		} catch (Exception e) {
			throw new MessagingException(e.getMessage(), e);
		}
	}

//	private List<Message> getMessages(final Session session, final String user, final String passwd) throws MessagingException {
//		final List<Message> mailMsgList = new ArrayList<Message>();
//		try {
//			// if (setup.verbose()) {
//			session.setDebug(true);
//			// }
//			final Store store = session.getStore(ServerSetup.PROTOCOL_IMAP);
//			log.info(String.format("Connecting to %s %d %s %s", imapSetup.getBindAddress(), imapSetup.getPort(), user, passwd));
//			store.connect(imapSetup.getBindAddress(), imapSetup.getPort(), user, passwd);
//			final Folder rootFolder = store.getFolder("INBOX");
//
//			rootFolder.open(Folder.READ_ONLY);
//			/* Get the messages which is unread in the Inbox */
//			Message[] msgs = rootFolder.search(new FlagTerm(new Flags(Flag.SEEN), false));
//
//			/* Use a suitable FetchProfile */
//			final FetchProfile fp = new FetchProfile();
//			fp.add(FetchProfile.Item.ENVELOPE);
//			fp.add(FetchProfile.Item.CONTENT_INFO);
//			fp.add(FetchProfile.Item.SIZE);
//			rootFolder.fetch(msgs, fp);
//
//			// rootFolder.open(Folder.READ_WRITE);
//			// final Message[] msgs = rootFolder.getMessages();
//			for (Message msg : msgs) {
//				try {
//					MimeMessage clonedMsg = copy(session, msg);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				mailMsgList.add(new MimeMessage((MimeMessage) msg));
//			}
//			return mailMsgList;
//		} finally {
//			// if (store.isConnected()) {
//			// store.close();
//			// }
//		}
//	}
	
	private List<Message> getMessages(Folder folder) throws MessagingException {
		List<Message> ret = new ArrayList<Message>();
		if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			if (!folder.isOpen()) {
				folder.open(Folder.READ_ONLY);
			}
			Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				ret.add(messages[i]);
				
			}
		}
		if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
			Folder[] f = folder.list();
			for (int i = 0; i < f.length; i++) {
				ret.addAll(getMessages(f[i]));
			}
		}
		return ret;
	}
	
	private List<Message> getTestMessages(final String account, final String password) throws Exception {
        final Properties props = new Properties();
//        if (protocol.endsWith("s")) {
//            props.put("mail.pop3.starttls.enable", Boolean.TRUE);
//            props.put("mail.imap.starttls.enable", Boolean.TRUE);
//        }
        props.setProperty("mail.imaps.socketFactory.class", DummySSLSocketFactory.class.getName());
        props.setProperty("mail.pop3s.socketFactory.class", DummySSLSocketFactory.class.getName());
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        props.setProperty("mail.pop3s.socketFactory.fallback", "false");

        final String timeout = "15000";
        props.setProperty("mail.imap.connectiontimeout", timeout);
        props.setProperty("mail.imaps.connectiontimeout", timeout);
        props.setProperty("mail.pop3.connectiontimeout", timeout);
        props.setProperty("mail.pop3s.connectiontimeout", timeout);
        props.setProperty("mail.imap.timeout", timeout);
        props.setProperty("mail.imaps.timeout", timeout);
        props.setProperty("mail.pop3.timeout", timeout);
        props.setProperty("mail.pop3s.timeout", timeout);

        Store store = null;
        try {
	        final Session session = Session.getInstance(props, null);
	        session.setDebug(verbose);
	        store = session.getStore(ServerSetup.PROTOCOL_IMAP);
	        store.connect(imapSetup.getBindAddress(), imapSetup.getPort(), account, password);
	        final Folder rootFolder = store.getFolder("INBOX");        
	        final List<Message> t = getMessages(rootFolder);
	        
	        final List<Message> clonedMsgs = new ArrayList<Message>();
	        for (Message msg : t) {
	        	MimeMessage clonedMsg = copy(session, msg);
	        	clonedMsgs.add(clonedMsg);	        
	        }
	        return clonedMsgs;
        } finally {
        	if (store != null && store.isConnected()) {
        		store.close();
        	}
        }
    }
	
	/**
	 * Clones the <code>Message</code>. This is a workaround as documented here: http://www.oracle.com/technetwork/java/javamail/faq/index.html#imapserverbug.
	 * The internal Greenmail server is not supporting newer versions of the mail protocol.
	 * @param session
	 * @param n
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private MimeMessage copy(final Session session, final Message n) throws MessagingException, IOException {
		final MimeMessage msg = (MimeMessage)n;
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    msg.writeTo(bos);
	    bos.close();
	    final SharedByteArrayInputStream bis = new SharedByteArrayInputStream(bos.toByteArray());
	    final MimeMessage cmsg = new MimeMessage(session, bis);
	    bis.close();
	    return cmsg;
	}
}
