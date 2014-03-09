/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.extension.mail.impl.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.extension.mail.api.MailRemoteClient;
import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.test.spi.TestEnricher;

import com.icegreen.greenmail.AbstractServer;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.Retriever;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Enricher that provide EJB class and setter method injection.
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 */
public class MailTestUtilEnricher implements TestEnricher {
	
	private static final String ANNOTATION_NAME_REMOTE_CLIENT = "org.jboss.arquillian.extension.mail.api.MailRemoteClient";
	private static final String ANNOTATION_NAME_SERVER_SETUP = "org.jboss.arquillian.extension.mail.api.MailServerSetup";
	
	private static final Logger log = Logger.getLogger(TestEnricher.class.getName());

	@Inject
	private Instance<Context> contextInst;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.arquillian.spi.TestEnricher#enrich(org.jboss.arquillian.spi
	 * .Context, java.lang.Object)
	 */
	public void enrich(Object testCase) {
		if (SecurityActions.isClassPresent(ANNOTATION_NAME_REMOTE_CLIENT)) {
			try {
				if (createContext() != null) {
					injectClass(testCase);
				}
			} catch (Exception e) {
				log.throwing(MailTestUtilEnricher.class.getName(), "enrich", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.arquillian.spi.TestEnricher#resolve(org.jboss.arquillian.spi
	 * .Context, java.lang.reflect.Method)
	 */
	public Object[] resolve(Method method) {
		return new Object[method.getParameterTypes().length];
	}

	/**
	 * Obtains all field in the specified class which contain the specified
	 * annotation
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 * @throws IllegalArgumentException
	 *             If either argument is not specified
	 */
	// TODO Hack, this leaks out privileged operations outside the package.
	// Extract out properly.
	protected List<Field> getFieldsWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotation) throws IllegalArgumentException {
		// Precondition checks
		if (clazz == null) {
			throw new IllegalArgumentException("clazz must be specified");
		}
		if (annotation == null) {
			throw new IllegalArgumentException("annotation must be specified");
		}

		// Delegate to the privileged operations
		return SecurityActions.getFieldsWithAnnotation(clazz, annotation);
	}

	@SuppressWarnings("unchecked")
	protected void injectClass(Object testCase) {
		try {			
			final Class<? extends Annotation> remoteClientAnnotation = (Class<? extends Annotation>) SecurityActions.getThreadContextClassLoader().loadClass(ANNOTATION_NAME_REMOTE_CLIENT);
			final Class<? extends Annotation> serverSetupAnnotation = (Class<? extends Annotation>) SecurityActions.getThreadContextClassLoader().loadClass(ANNOTATION_NAME_SERVER_SETUP);
			final List<Field> annotatedFields = SecurityActions.getFieldsWithAnnotation(testCase.getClass(), remoteClientAnnotation);
			final List<Class<?>> annotatedClasses = SecurityActions.getClassWithAnnotation(testCase.getClass(), serverSetupAnnotation);			
			final MailServerSetup setup = (MailServerSetup) annotatedClasses.get(0).getAnnotation(serverSetupAnnotation);
			
			for (Field field : annotatedFields) {
				if (field.get(testCase) == null) {					
					final MailRemoteClient fieldAnnotation = (MailRemoteClient) field.getAnnotation(remoteClientAnnotation);
					final String[] protocols = setup.protocols();
					final String host = setup.host();
					
					try {
						final ServerSetup[] serverSetups = getSetup(protocols, host);
						final ServerSetup imapServerSetup = getImapServerSetup(serverSetups);
						if (imapServerSetup != null) {							
							final DefaultMailTestUtil mailFetcher = new DefaultMailTestUtil(imapServerSetup, setup.users(), setup.verbose());
							field.set(testCase, mailFetcher);
						} else {
							log.log(Level.WARNING, "No imap server defined. Please define an imap server for fetching mails");
						}
					} catch (Exception ex) {
						log.fine("Could not inject " + fieldAnnotation + ", other Enrichers might, move on. Exception: " + ex.getMessage());
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException("Could not inject members", ex);
		}
	}

	protected Context createContext() throws Exception {
		return contextInst.get();
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

	private ServerSetup getImapServerSetup(ServerSetup[] setups) {
		for (ServerSetup setup : setups) {
			if (setup.getProtocol().equals(ServerSetup.PROTOCOL_IMAP)) {
				return setup;
			}
		}
		return null;
	}
}