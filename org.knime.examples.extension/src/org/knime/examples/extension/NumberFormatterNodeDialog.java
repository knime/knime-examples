package org.knime.examples.extension;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is an example implementation of the node dialog of the
 * "NumberFormaterNode"node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New dialog pane for configuring NumberFormaterNode. The dialog created here
	 * will show up when double clicking on a node in KNIME Analytics Platform.
	 */
	protected NumberFormatterNodeDialog() {
		super();
		/*
		 * The DefaultNodeSettingsPane provides methods to add simple standard
		 * components to the dialog pane. They are connected to the node model via
		 * settings model objects that can easily load and save their settings to the
		 * node settings. Here, the loading/saving in the dialog is already taken care
		 * of by the DefaultNodeSettingsPane. It is important to use the same key for
		 * the settings model here as used in the node model implementation (it does not
		 * need to be the same object). One best practice is to use package private
		 * static methods to create the settings model as we did in the node model
		 * implementation (see createNumberFormatSettingsModel()).
		 * 
		 * Here we create a simple String DialogComponent that will display a label
		 * String and a text box in which the use can enter a value. The
		 * DialogComponentString has additional options to disallow empty inputs, hence
		 * we do not need to worry about that in the model implementation anymore.
		 * 
		 * There are default dialog components for all types of settings models.
		 */
		SettingsModelString stringSettings = NumberFormatterNodeModel.createNumberFormatSettingsModel();
		addDialogComponent(new DialogComponentString(stringSettings, "Number Format", true, 10));
	}
}
