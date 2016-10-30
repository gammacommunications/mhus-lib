package de.mhus.lib.cao.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoConnection;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoMetadata;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoOperation;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotSupportedException;

public class WritablePropertiesNode extends CaoWritableElement {

	private static final long serialVersionUID = 1L;
	protected MProperties properties = new MProperties();

	public WritablePropertiesNode(CaoConnection con, PropertiesNode parent) throws CaoException {
		super(con, parent);
		reload();
	}

	@Override
	public ResourceNode getNode(String key) {
		return getOriginalElement().getNode(key);
	}

	@Override
	public ResourceNode[] getNodes() {
		return getOriginalElement().getNodes();
	}

	@Override
	public ResourceNode[] getNodes(String key) {
		return getOriginalElement().getNodes(key);
	}

	@Override
	public String[] getNodeKeys() {
		return getOriginalElement().getNodeKeys();
	}

	@Override
	public String getName() throws MException {
		return getOriginalElement().getName();
	}

	@Override
	public InputStream getInputStream(String rendition) {
		return null;
	}


	@Override
	public URL getUrl() {
		return getOriginalElement().getUrl();
	}

	@Override
	public boolean isValid() {
		return getOriginalElement().isValid();
	}

	@Override
	public boolean hasContent() {
		return getOriginalElement().hasContent();
	}

	@Override
	public void removeProperty(String key) {
		properties.remove(key);
	}

	@Override
	public void setProperty(String key, Object value) {
		properties.setProperty(key, value);
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public String[] getPropertyKeys() {
		return properties.keySet().toArray(new String[0]);
	}

	@Override
	public Object getProperty(String name) {
		return properties.getProperty(name);
	}

	@Override
	public boolean isProperty(String name) {
		return properties.containsKey(name);
	}

	@Override
	public CaoOperation getUpdateOperation() throws CaoException {
		try {
			return getOriginalElement().getConnection().getActions().getAction(CaoAction.UPDATE).createOperation(new CaoList(getOriginalElement()).append(this), null);
		} catch (Throwable t) {}
		return new SaveThis();
	}

	private class SaveThis extends CaoOperation {

		@Override
		public void initialize() throws CaoException {
		}

		@Override
		public void execute() throws CaoException {
			((PropertiesNode)getOriginalElement()).doUpdate(properties);
		}

		@Override
		public void dispose() throws CaoException {
		}
		
	}

	@Override
	public CaoWritableElement getWritableNode() throws MException {
		return null;
	}

	@Override
	public CaoMetadata getMetadata() {
		return getOriginalElement().getMetadata();
	}

	@Override
	public String getId() throws MException {
		return getOriginalElement().getId();
	}

	@Override
	public boolean isNode() {
		return getOriginalElement().isNode();
	}

	@Override
	public void reload() throws CaoException {
		properties.clear();
		for (java.util.Map.Entry<String, Object> entry : getOriginalElement().entrySet())
			properties.put(entry.getKey(), entry.getValue());
	}
	
	@Override
	public String getVersionLabel() throws MException {
		throw new NotSupportedException();
	}

	@Override
	public Set<String> getVersions() {
		throw new NotSupportedException();
	}

	@Override
	public CaoNode getVersion(String version) {
		throw new NotSupportedException();
	}

}
