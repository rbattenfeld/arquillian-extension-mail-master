package org.jboss.arquillian.extension.mail.api;

public class MailTestAssertionError extends AssertionError {

	private static final long serialVersionUID= 1L;

	public MailTestAssertionError() {
		super();
	}

	public MailTestAssertionError(final String message) {
		super(message);
	}
}