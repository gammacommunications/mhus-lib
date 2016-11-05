package de.mhus.lib.cao;

import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.directory.MResourceProvider;
import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.core.strategy.DefaultMonitor;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsFactory;

/**
 * A action is doing something with one or more objects. The main
 * difference to a operation is that the objects do not have
 * meanings. A action can call a operation to execute.
 * 
 * @author mikehummel
 *
 */
public abstract class CaoAction extends MObject {

	public static final String UPDATE = "updateNode";
	public static final String DELETE = "deleteNode";
	public static final String MOVE   = "moveNode";
	public static final String RENAME = "renameNode";
	public static final String CREATE = "createNode";
	public static final String UPLOAD_RENDITION = "uploadRendition"; // CREATE or UPDATE; CREATE would suggest to generate from default rendition, e.g. create a thumbnail
	public static final String DELETE_RENDITION = "deleteRendition";

	private MNls resourceBundle;

	public CaoAction() {
		resourceBundle = new MNls();
	}

	public CaoAction(MResourceProvider<?> res, String resourceName) {
		resourceBundle = MSingleton.baseLookup(this,MNlsFactory.class).load(res,this.getClass(), resourceName, null);
	}

	public abstract String getName();

	/**
	 * Returns a configuration Form for the operation. The list of elements
	 * should be a representative list. The configuration use most time the
	 * first element of this list. In other cases the hole list is needed,
	 * it depends on the operation.
	 * 
	 * @param list
	 * @param configuration specific initial attributes
	 * @return x
	 * @throws CaoException 
	 */
	public abstract CaoConfiguration createConfiguration(CaoList list,IProperties configuration) throws CaoException;

	public abstract boolean canExecute(CaoConfiguration configuration);

	/**
	 * Executes a defined action. Is the action need to execute an operation it will
	 * return the operation object to be executed by the caller.
	 * 
	 * @param configuration
	 * @param monitor 
	 * @return x
	 * @throws CaoException
	 */
	public abstract OperationResult doExecute(CaoConfiguration configuration, Monitor monitor) throws CaoException;

	@Override
	public String toString() {
		return "Action " + getName() + " (" + getClass().getCanonicalName() + ")";
	}

	public MNls getResourceBundle() {
		return resourceBundle;
	}

	protected Monitor checkMonitor(Monitor monitor) {
		if (monitor == null)
			monitor = new DefaultMonitor(getClass());
		return monitor;
	}

}
