package de.mhus.lib.cao.fs;

import java.io.File;

import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.CreateConfiguration;
import de.mhus.lib.cao.action.DeleteConfiguration;
import de.mhus.lib.cao.action.UploadRenditionConfiguration;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public class FsUploadRendition extends CaoAction {

	@Override
	public String getName() {
		return UPLOAD_RENDITION;
	}

	@Override
	public CaoConfiguration createConfiguration(CaoList list, IProperties configuration) throws CaoException {
		return new UploadRenditionConfiguration(null, list, null);
	}

	@Override
	public boolean canExecute(CaoConfiguration configuration) {
		try {
			return configuration.getList().size() != 1 
					&& 
					configuration.getList().get(0) instanceof FsNode 
					;
		} catch (Throwable t) {
			log().d(t);
			return false;
		}
	}

	@Override
	public OperationResult doExecute(CaoConfiguration configuration, Monitor monitor) throws CaoException {
		if (!canExecute(configuration)) return new NotSuccessful(getName(), "can't execute", -1);

		try {
			FsNode parent = (FsNode)configuration.getList().get(0);
			
			String rendition = configuration.getProperties().getString(UploadRenditionConfiguration.RENDITION);
			String path = configuration.getProperties().getString(UploadRenditionConfiguration.FILE);
			File file = new File(path);
			if (!file.exists() || !file.isFile()) throw new NotFoundException(path);
			
			File renditionFile = ((FsConnection)parent.getConnection()).getContentFileFor(parent.getFile(), rendition);
			if (renditionFile == null) throw new MException("can't create rendition to internal file", rendition);
			MFile.copyFile(file, renditionFile);
			
			return new Successful(getName());
		} catch (Throwable t) {
			log().d(t);
			return new NotSuccessful(getName(),t.toString(),-1);
		}
	}
}
