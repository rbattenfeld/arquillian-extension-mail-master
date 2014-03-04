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
package org.jboss.arquillian.extension.mail.impl.common;

import org.jboss.arquillian.extension.mail.api.MailServerSetup;
import org.jboss.arquillian.extension.mail.api.MailTest;
import org.jboss.arquillian.test.spi.event.suite.ClassLifecycleEvent;
import org.jboss.arquillian.test.spi.event.suite.TestLifecycleEvent;

/**
 * ExtractScriptUtil
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 * @version $Revision: $
 */
public final class ExtractSetupUtil {
	
	public static MailServerSetup extractServerSetupFromTestClass(final ClassLifecycleEvent event) {
		final MailServerSetup setup = event.getTestClass().getAnnotation(MailServerSetup.class);
		return setup;
	}

	public static MailServerSetup extractServerSetupFromTestClass(TestLifecycleEvent event) {
		final MailServerSetup setup = event.getTestClass().getAnnotation(MailServerSetup.class);
		return setup;
	}

	public static MailTest extractMailTestFromTestMethod(final TestLifecycleEvent event) {
		MailTest test = event.getTestMethod().getAnnotation(MailTest.class);
		return test;
	}

	public static MailServerSetup extractServerSetupFromTestMethod(final TestLifecycleEvent event) {
		MailServerSetup test = event.getTestMethod().getAnnotation(MailServerSetup.class);
		return test;
	}
	
}
