package de.mhus.lib.core.system;

import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.activator.MutableActivator;
import de.mhus.lib.core.lang.Base;

public class DefaultBase extends Base {
	
	MActivator activator;

	public DefaultBase(Base parent) {
		super(parent);
		this.activator = MSingleton.get().createActivator();
	}
	
	@Override
	public void destroy() {
		if (activator != null) activator.destroy();
	}

	@Override
	public boolean isDestroyed() {
		return activator == null || activator.isDestroyed();
	}

	@Override
	public boolean isBase(Class<?> ifc) {
		return activator != null && activator.isInstance(ifc) || parent != null && parent.isBase(ifc);
	}

	public MActivator getActivator() {
		return activator;
	}

	@Override
	public <T, D extends T> T lookup(Class<T> ifc, Class<D> def) {
		try {
			if (activator == null) {
				if (parent != null)
					return parent.lookup(ifc, def);
			} else {
			
				if (parent != null && !activator.isInstance(ifc) && ( local == null || !local.contains(ifc.getCanonicalName()) ) )
					return parent.lookup(ifc, def);
			}
			
			T ret = activator.getObject(ifc);
			if (ret != null)
				return ret;
			
		} catch (Exception e) {
			MSingleton.dirtyLog(ifc,e);
		}
		
		if (def == null) return null;
		
		try {
			return def.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			MSingleton.dirtyLog(ifc,e);
		}
		return null;
	}

	@Override
	public void addObject(Class<?> ifc, Object obj) {
		MActivator act = getActivator();
		if (act instanceof MutableActivator)
			((MutableActivator)act).addObject(ifc, null, obj);
	}

	@Override
	public void removeObject(Class<?> ifc) {
		MActivator act = getActivator();
		if (act instanceof MutableActivator)
			((MutableActivator)act).removeObject(ifc, null);
		
	}

}
