/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.extension.mail.impl;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.extension.mail.impl.container.MailTestRetrieverEnricher;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * MailRemoteExtension
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public class MailRemoteExtension implements RemoteLoadableExtension {

	@Override
	public void register(ExtensionBuilder builder) {

		if (Validate.classExists("org.jboss.arquillian.extension.mail.api.MailRemoteClient")) {
			builder.service(TestEnricher.class, MailTestRetrieverEnricher.class);
		}

	}
}