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
 * Annotation to check the result of a test case.
 * <p>
 * This annotation simplifies writing mail tests. The fields allow to set the success criteria for a
 * certain test case. The logic is simple. The test case will send one or more mail messages.
 * Then, the arquillian extension mail framework will verify the result. 
 * <p>
 * The assert logic is as well simple:
 * <ul>
 * <li> all stored mails are filtered by expectedSentTo(), expectedSentFrom(), expectedSubject(), expectedContentType()
 * <li> then, after filtering, the remaining mail list are compared with defined expectedMessageCount().
 * <li> if that this not matching, then an <code>AssertionException</code> is thrown.
 * </ul>
 * 
 * @author <a href="mailto:ralf.battenfeld@bluewin.ch">Ralf Battenfeld</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MailTest {
	
	/**
	 * Defines the expected number of mails after all filters like <code>expectedSubject()</code> are applied.
	 * @return the expected amount of mails.
	 */
    public int expectedMessageCount();
    
    /**
     * Filters mails by the sentTo field. Will be ignored when this field is empty.
     * @return array of expected mail addresses.
     */
    public String[] expectedSentTo() default "";
    
    /**
     * Filters mails by the sentFrom field. Will be ignored when this field is empty.
     * @return the expected sentFrom mail address.
     */
    public String expectedSentFrom() default "";    

    /**
     * Filters mails by the subject field. Will be ignored when this field is empty.
     * @return the expected subject mail field.
     */
    public String expectedSubject();
    
    /**
     * Filters mails by the content type field. Will be ignored when this field is empty.
     * @return the expected content type mail field.
     */
    public String expectedContentType() default "";
    
    /**
     * Deletes all mails before executing the test.
     * @return if true, then all stored mails are deleted before executing the test.
     */
    public boolean clearAllMails() default false;
    
    /**
     * Allows to activate detailed processing log messages. Useful for checking why a test is failing.
     * @return if true, then processing log messages are logged.
     */
    public boolean verbose() default false;
    
//    /**
//     * Filters all mails for attachments. --> TODO later
//     * @return if true, then only mails with an attachment are considered.
//     */
//    public boolean checkAttachementExists() default false;
}
