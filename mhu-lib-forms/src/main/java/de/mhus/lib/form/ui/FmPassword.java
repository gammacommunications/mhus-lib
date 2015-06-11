package de.mhus.lib.form.ui;

import de.mhus.lib.core.definition.IDefAttribute;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FmDefaultSources;
import de.mhus.lib.form.definition.FmElement;
import de.mhus.lib.form.definition.FmNls;

public class FmPassword extends FmElement {

	public FmPassword(String name, String title, String description) {
		this(name, new FmNls(title, description), new FmDefaultSources());
	}

	public FmPassword(String name, IDefAttribute ... definitions) {
		super(name, definitions);
		setString("type", "password");
	}


}
