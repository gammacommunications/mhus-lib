package de.mhus.lib.sql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.MRuntimeException;

/**
 * The class holds a bundle of different database pools.
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class DbPoolBundle extends MObject {


	private IConfig config;
	private MActivator activator;
	private Map<String, DbPool> bundle = new HashMap<String, DbPool>();

	/**
	 * Create a new bundle from default configuration. Load it from MSingleton with the
	 * key of this class.
	 */
	public DbPoolBundle() {
		this(null,null);
	}

	/**
	 * Create a new Bundle from configuration.
	 *
	 * @param config Config element or null. null will use the central MSingleton configuration.
	 * @param activator Activator or null. null will use the central MSingleton activator.
	 */
	public DbPoolBundle(IConfig config, MActivator activator) {

		if (config == null) config = MSingleton.get().getCfgManager().getCfg(DbPoolBundle.class, null);
		if (activator == null) activator = MSingleton.lookup(MActivator.class);

		this.config = config;
		this.activator = activator;
	}

	/**
	 * <p>getPool.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.sql.DbPool} object.
	 * @throws java.lang.Exception if any.
	 */
	public DbPool getPool(String name) throws Exception {

		if (bundle == null) throw new MException("Bundle already closed");

		synchronized (bundle) {
			DbPool pool = bundle.get(name);
			if (pool == null) {
				IConfig poolCon = config.getNode(name);
				if (poolCon != null) {
					pool = new DefaultDbPool(poolCon, activator);
					bundle.put(name, pool);
				} else {
					throw new MException("pool config not found",name);
				}
			}
			return pool;
		}
	}

	/**
	 * <p>getNames.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] getNames() {
		LinkedList<String> out = new LinkedList<String>();
		for (IConfig c : config.getNodes()) {
			try {
				out.add(c.getName());
			} catch (MException e) {
				throw new MRuntimeException(e);
			}
		}
		return out.toArray(new String[out.size()]);
	}

	/**
	 * <p>Getter for the field <code>config</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.core.directory.ResourceNode} object.
	 */
	public IConfig getConfig(String name) {
		return config.getNode(name);
	}

	/**
	 * <p>Getter for the field <code>config</code>.</p>
	 *
	 * @return a {@link de.mhus.lib.core.directory.ResourceNode} object.
	 */
	public IConfig getConfig() {
		return config;
	}

	/**
	 * <p>reset.</p>
	 */
	public void reset() {
		bundle = new HashMap<String, DbPool>();
	}

	/**
	 * <p>close.</p>
	 */
	public void close() {

		if (bundle == null) return;

		synchronized (bundle) {
			for (DbPool pool : bundle.values())
				pool.close();
			bundle = null;
		}
	}

}
