package de.mschumann.logicaldoc.api;

public class Document {
	private String name;
	private long docId;

	public Document(String fileName, long id) {
		this.name = fileName;
		this.docId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}
}
