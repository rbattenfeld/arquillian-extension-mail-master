package org.jboss.arquillian.extension.mail.impl.client;

import java.util.logging.Level;

import javax.mail.Folder;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class MailServerInstallerTest {

	private GreenMail server;
	
	@Before
	public void init() {
		final ServerSetup[] setups = getSetup(new String[] { "smtp:3025", "imap:3026" }, "localhost");
		server = new GreenMail(setups);
		server.setUser("john.doe@testmail.com", "mypasswd");
		System.out.println("BindAddress: " + server.getSmtp().getServerSetup().getBindAddress());
		System.out.println("port       : " + server.getSmtp().getServerSetup().getPort());
		System.out.println("Protocol   : " + server.getSmtp().getServerSetup().getProtocol());
		System.out.println("BindAddress: " + server.getImap().getServerSetup().getBindAddress());
		System.out.println("port       : " + server.getImap().getServerSetup().getPort());
		System.out.println("Protocol   : " + server.getImap().getServerSetup().getProtocol());
		
//		server.getManagers().getUserManager().
		server.start();
	}
	
	@After
	public void stop() {
		if (server != null) {
			server.stop();
		}
	}
	
	@Test
	public void test() {
		final ServerSetup[] setups = getSetup(new String[] { "imap:3026" }, "localhost");
		final Session session = GreenMailUtil.instance().getSession(setups[0]);
		session.setDebug(true);
		for (Provider provider : session.getProviders()) {
			if (provider.getProtocol().equals("imap")) {
				try {
					final Store store = session.getStore("imap");
					try {
						store.connect("localhost", 3026, "john.doe@testmail.com", "mypasswd");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			        final Folder rootFolder = store.getFolder("INBOX");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private ServerSetup[] getSetup(final String[] protocols, final String host) {
		final ServerSetup[] protocolSetups = new ServerSetup[protocols.length];
		for (int i = 0; i < protocols.length; i++) {
			String item = protocols[i];
			final String[] subitem = item.split(":", -1);
			if (subitem.length == 2) {
				final String protocol = subitem[0];
				final String port = subitem[1];
				protocolSetups[i] = new ServerSetup(Integer.valueOf(port), host, protocol);
			}
		}
		return protocolSetups;
	}
}
