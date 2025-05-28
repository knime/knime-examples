package org.knime.examples.unitconverter;

import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Label;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect.EffectType;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Predicate;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.PredicateProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Reference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueReference;

@SuppressWarnings("restriction")
final class OutputColumnSettings implements DefaultNodeSettings {

	enum ReplaceOrAppend {
		@Label(value = "Replace", description = "Replaces the selected columns by the new columns.")
		REPLACE,

		@Label(value = "Append with suffix", description = """
				Appends the selected columns to the input table with \
				a new name that is the previous name plus the provided suffix.
				""")
		APPEND;

		interface ReplaceOrAppendRef extends Reference<ReplaceOrAppend> {
		}

		static final class IsAppend implements PredicateProvider {
			@Override
			public Predicate init(final PredicateInitializer i) {
				return i.getEnum(ReplaceOrAppendRef.class).isOneOf(ReplaceOrAppend.APPEND);
			}
		}
	}

	@Widget(title = "Output column mode", description = "Select whether to replace or append the selected columns.")
	@ValueReference(ReplaceOrAppend.ReplaceOrAppendRef.class)
	ReplaceOrAppend m_replaceOrAppend = ReplaceOrAppend.REPLACE;

	@Widget(title = "Suffix", description = "The suffix to append to the column names if the mode is Append.")
	@Effect(predicate = ReplaceOrAppend.IsAppend.class, type = EffectType.SHOW)
	String m_suffix = "";
}
