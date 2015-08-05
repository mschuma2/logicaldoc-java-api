package de.mschumann.logicaldoc.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.FolderClient;

@Service
public class LogicalDocSession {

	private Logger log = Logger.getLogger(getClass());

	@Value("${logicaldoc.base}")
	private String base;
	@Value("${logicaldoc.user}")
	private String user;
	@Value("${logicaldoc.password}")
	private String password;
	private String sessionId;

	private AuthClient auth;
	private DocumentClient doc;
	private FolderClient folder;

	@SuppressWarnings("restriction")
	@PostConstruct
	private void init() {
		try {
			auth = new AuthClient(base + "/Auth");
			doc = new DocumentClient(base + "/Document");
			folder = new FolderClient(base + "/Folder");
		} catch (Exception e) {
			log.error(e);
		}
	}

	public List<String> getFileNames(long folderId) {
		try {
			List<String> names = new ArrayList<String>();
			for (Document doc : getFiles(folderId)) {
				names.add(doc.getName());
			}
			return names;
		} catch (Exception e) {
			log.error("Could not retrieve file names for folder " + folderId, e);
			return new ArrayList<String>();
		}
	}

	public List<Document> getFiles(long folderId) {
		try {
			List<Document> docs = new ArrayList<Document>();
			WSDocument documents[] = doc.listDocuments(getSessionId(),
					folderId, null);
			if (documents == null) {
				log.debug("Found no documents in folder " + folderId);
			} else {
				log.debug("Found " + documents.length + " files in folder "
						+ folderId);
			}
			for (WSDocument doc : documents) {
				docs.add(new Document(doc.getFileName(), doc.getId()));
			}
			return docs;
		} catch (Exception e) {
			log.error("Could not retrieve file names for folder " + folderId, e);
			return new ArrayList<Document>();
		}
	}

	private String getSessionId() throws Exception {
		if (sessionId != null && auth.valid(sessionId)) {
			log.debug("Session is valid");
			auth.renew(sessionId);
			log.debug("Session renewed");
		} else {
			try {
				sessionId = auth.login(user, password);
			} catch (Exception e) {
				log.error("Could not login to LogicalDoc", e);
			}
			log.debug("Created session: " + sessionId);
		}
		return sessionId;
	}

	public Long upload(byte[] file, String fileName, long folderId) {

		try {
			File f = File.createTempFile("logicaldoc", "tmp");
			WSDocument newDoc = new WSDocument();
			newDoc.setFileName(fileName);
			newDoc.setFolderId(folderId);
			newDoc = doc.create(getSessionId(), newDoc, f);
			log.info("Created file " + fileName + ", id=" + newDoc.getId());
			if (f.exists()) {
				f.delete();
			}
			return newDoc.getId();
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public long getFolderIdByPath(String path) throws Exception {
		return folder.findByPath(getSessionId(), path).getId();
	}
}
