package org.jboss.arquillian.extension.mail.impl.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.jboss.arquillian.extension.mail.api.MailRetriever;

import com.icegreen.greenmail.util.ServerSetup;

public class MailFetcher implements MailRetriever {
	
	private static final Logger log = Logger.getLogger(MailFetcher.class.getName());
	
	private final Store store;
	
	private final ServerSetup imapSetup; 
	
	private final String[] users;
	
	public MailFetcher(final Store store, final ServerSetup imapSetup, final String[] users) {
		this.store = store;
		this.imapSetup = imapSetup;
		this.users = users;
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
	
	//-----------------------------------------------------------------------||
	//-- Private Methods ----------------------------------------------------||
	//-----------------------------------------------------------------------||	

	private List<Message> getMessages(final String user, final String passwd) throws MessagingException {
		try {
			store.connect(imapSetup.getBindAddress(), imapSetup.getPort(), user, passwd);
			final Folder rootFolder = store.getFolder("INBOX");
			rootFolder.open(Folder.READ_ONLY);
			final Message[] msgs = rootFolder.getMessages();
			return Arrays.asList(msgs);
		} finally {
			if (store.isConnected()) {
				store.close();
			}
		}
	}
}
