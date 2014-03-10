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

package org.jboss.arquillian.extension.mail.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to setup the test mail server.
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface MailServerSetup {
	
	public String host() default "localhost";
	
	/**
	 * Defines protocols. The format is [potocol:port];[protocol:port]...
	 * <p>
	 * The supported protocols are:
	 * <ul>
	 * <li> smtp
	 * <li> smtps
 	 * <li> pop3
	 * <li> pop3s
 	 * <li> imap
	 * <li> imaps
	 * </ul>
	 * 
	 * example:
	 * <pre>
	 * <code> protocols = { "smtp:3025", "pop3:3110" }  </code>
	 * </pre>
	 * 
	 * @return
	 */
	public String[] protocols() default "smtp:3025";
	
	/**
	 * Initializes the mail server with user accounts. The format is:
	 * <p>
	 * <ul> 
	 * <li> users = {"email-address1:passwd1", "email-address2:passwd2"} or
	 * <li> users = {"email-address1:login1:passwd1", "email-address2:login2:passwd2"}
	 * </ul>
	 * 
	 * example:
	 * <pre>
	 * <code> users= { "john.doe@testmail.com:mypasswd", "testUser1@noreply:mypasswd" }  </code>
	 * </pre>
	 * 
	 * @return
	 */
	public String[] users() default "";
	
	/**
     * Allows to activate detailed mail session log messages. Useful for checking why a test is failing.
     * @return if true, then processing log messages are logged.
     */
    public boolean verbose() default false;
}
