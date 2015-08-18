package com.trail2peak.pdi.fastjsoninput;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.ReadContext;

/**
 * @author Samatar
 * @author edube
 * @since 2015-01-07
 */
public class FastJsonReader {
	private static Class<?> PKG = FastJsonInputMeta.class; // for i18n purposes,
															// needed by
															// Translator2!!

	// as per RFC 7159, the default JSON encoding shall be UTF-8
	// see https://tools.ietf.org/html/rfc7159#section-8.1
	private final static String JSON_CHARSET = "UTF-8";

	private ReadContext jsonReadContext;
	private Configuration jsonConfiguration;

	private boolean ignoreMissingPath;
	private boolean defaultPathLeafToNull;

	public FastJsonReader() throws KettleException {
		this.ignoreMissingPath = false;
		this.defaultPathLeafToNull = false;
		jsonConfiguration = Configuration.defaultConfiguration().addOptions(
				Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
	}

	public void setIgnoreMissingPath(boolean value) {
		this.ignoreMissingPath = value;
	}

	public void setDefaultPathLeafToNull(boolean value) {
		this.defaultPathLeafToNull = value;
		if (this.defaultPathLeafToNull) {
			jsonConfiguration = Configuration.defaultConfiguration().addOptions(
					Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL);
		}
	}

	private ParseContext getParseContext() {
		return JsonPath.using(jsonConfiguration);
	}

	private ReadContext getReadContext() {
		return jsonReadContext;
	}

	public void readFile(String filename) throws KettleException {
		InputStream is = null;
		try {
			is = KettleVFS.getInputStream(filename);
			jsonReadContext = getParseContext().parse(is, JSON_CHARSET);
			if (jsonReadContext == null) {
				throw new Exception(BaseMessages.getString(PKG,
						"FastJsonReader.Error.ReadFile.Null"));
			}
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"FastJsonReader.Error.ParsingFile", e));
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				// Ignore errors
			}
		}
	}

	public void readString(String value) throws KettleException {
		try {
			jsonReadContext = getParseContext().parse(value);
			if (jsonReadContext == null) {
				throw new Exception(BaseMessages.getString(PKG,
						"FastJsonReader.Error.ReadString.Null"));
			}
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"FastJsonReader.Error.ParsingString", e));
		}
	}

	public void readUrl(String value) throws KettleException {
		InputStream is = null;
		try {
			URL url = new URL(value);
			is = url.openConnection().getInputStream();
			jsonReadContext = getParseContext().parse(is, JSON_CHARSET);
			if (jsonReadContext == null) {
				throw new Exception(BaseMessages.getString(PKG,
						"FastJsonReader.Error.ReadUrl.Null"));
			}
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"FastJsonReader.Error.ParsingUrl", e));
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				// Ignore errors
			}
		}
	}

	public JsonResultList getPath(String path) throws KettleException {
		JsonResultList ja;
		try {
			List<Object> ls = getReadContext().read(path);
			if (ls != null && !ls.isEmpty()) {
				ja = new JsonResultList(ls);
			} else {
				if (!isIgnoreMissingPath()) {
					throw new KettleException(BaseMessages.getString(PKG,
							"FastJsonReader.Error.CanNotFindPath", path));
				}
				// The Json Path is missing
				// and user do not want to fail
				// so we need to populate it with NULL values
				ja = new JsonResultList();
				ja.setNull(true);
			}
		} catch (Exception e) {
			throw new KettleException(e);
		}

		return ja;
	}

	public boolean isIgnoreMissingPath() {
		return this.ignoreMissingPath;
	}

}
