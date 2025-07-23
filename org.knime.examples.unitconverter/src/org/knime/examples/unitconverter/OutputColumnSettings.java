package org.knime.examples.unitconverter;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.widget.choices.Label;

final class OutputColumnSettings implements NodeParameters {

	enum ReplaceOrAppend {
		@Label(value = "Replace", description = "Replaces the selected columns by the new columns.")
		REPLACE,

		@Label(value = "Append with suffix", description = """
				Appends the selected columns to the input table with \
				a new name that is the previous name plus the provided suffix.
				""")
		APPEND;

		interface ReplaceOrAppendRef extends ParameterReference<ReplaceOrAppend> {
		}

		static final class IsAppend implements EffectPredicateProvider {
			@Override
			public EffectPredicate init(final PredicateInitializer i) {
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
