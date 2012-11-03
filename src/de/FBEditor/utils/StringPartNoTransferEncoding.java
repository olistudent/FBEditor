package de.FBEditor.utils;

import org.apache.commons.httpclient.methods.multipart.StringPart;

public class StringPartNoTransferEncoding extends StringPart {

	public StringPartNoTransferEncoding(String name, String value) {
		super(name, value);
	}

	public String getTransferEncoding() {
		return null;
	}

	public String getContentType() {
		return null;
	}
}
