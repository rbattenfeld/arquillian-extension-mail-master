/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.extension.mail.impl.client;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.extension.mail.impl.MailRemoteExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * MailDeploymentAppender
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public class MailDeploymentAppender implements AuxiliaryArchiveAppender {
	
	@Inject
	private Instance<ArquillianDescriptor> descriptorInst;

	@Override
	public Archive<?> createAuxiliaryArchive() {

		final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "arquillian-mail.jar")
				.addClass(MailRemoteExtension.class)
				.addPackages(true, "org.jboss.arquillian.extension.mail.api")
				.addPackages(true, "org.jboss.arquillian.extension.mail.impl.container")
				.addPackages(true, "com.icegreen.greenmail")
				.addAsServiceProvider(RemoteLoadableExtension.class, MailRemoteExtension.class);
//				.setManifest(
//                    new StringAsset("Manifest-Version: 1.0\n"
//                            + "Created-By: Arquillian\n"
//                            + "Dependencies: javax.mail.api javax.activation.api\n"));

		return jar;
	}
}