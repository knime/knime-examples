package org.knime.examples.unitconverter;

import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;

@SuppressWarnings("restriction")
final class OutputColumnSettings implements DefaultNodeSettings {

    // TODO add a setting to differentiate between appending and replacing the converted column

	@Widget(title = "Suffix", description = "The suffix to append to the appended column.")
	String m_suffix = "";
}
