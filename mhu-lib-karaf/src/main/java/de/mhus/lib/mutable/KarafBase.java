package de.mhus.lib.mutable;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.lang.Base;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.system.DefaultBase;

public class KarafBase extends DefaultBase {

	private static HashMap<String, Container> apiCache = new HashMap<>();

	public KarafBase(Base parent) {
		super(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, D extends T> T lookup(Class<T> ifc, Class<D> def) {
		
		if (ifc == null) return null;
		
		if (def == null && ifc.isInterface()) { // only interfaces can be OSGi services

			Container cached = apiCache.get(ifc.getCanonicalName());
			if (cached != null) {
				if (cached.bundle.getState() != Bundle.ACTIVE || cached.modified != cached.bundle.getLastModified()) {
					apiCache.remove(cached.ifc.getCanonicalName());
					cached = null;
				}
			}
			
			if (cached == null) {
				Bundle bundle = FrameworkUtil.getBundle(KarafBase.class);
				if (bundle != null) {
					BundleContext context = bundle.getBundleContext();
					if (context != null) {
						ServiceReference<? extends T> ref = context.getServiceReference(ifc);
						if (ref != null) {
							T obj = context.getService(ref);
							if (obj != null) {
								MLogUtil.log().d("KarafBase","loaded from OSGi",ifc);
								cached = new Container();
								cached.bundle = ref.getBundle();
								cached.api = obj;
								cached.ifc = ifc;
								cached.modified = cached.bundle.getLastModified();
								apiCache.put(ifc.getCanonicalName(), cached);
								
							}
						}
					}
				}
			}
			if (cached != null)
				return (T)cached.api;
			
		}
		return super.lookup(ifc, def);
	}
	
	public void clearCache() {
		apiCache.clear();
	}

	private static class Container {

		public long modified;
		public Class<?> ifc;
		public Object api;
		public Bundle bundle;
		
	}

	public void dumpCache(PrintStream out) {
		for (Entry<String, Container> item : apiCache.entrySet()) {
			out.println(item.getKey() + ": " + item.getValue().ifc);
			out.println("  Bundle: " + item.getValue().bundle.getSymbolicName() + " " + item.getValue().bundle.getState());
			out.println("  Object: " + item.getValue().api);
		}
	}

}
