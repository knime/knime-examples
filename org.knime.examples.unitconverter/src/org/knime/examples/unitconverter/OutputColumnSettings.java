package org.knime.examples.unitconverter;

import static org.knime.examples.unitconverter.OutputColumnSettings.ReplaceOrAppend.REPLACE;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.util.UniqueNameGenerator;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.NodeSettingsPersistor;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Label;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Effect.EffectType;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Predicate;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.PredicateProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.Reference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.ValueReference;
import org.knime.core.webui.node.dialog.defaultdialog.widget.updates.PredicateProvider.PredicateInitializer;

class OutputColumnSettings implements DefaultNodeSettings {

	@Widget(title = "Output column mode", description = "Select whether to replace or append the selected columns.")
	@ValueReference(ReplaceOrAppend.ValueRef.class)
	ReplaceOrAppend m_replaceOrAppend = ReplaceOrAppend.REPLACE;

	@Widget(title = "Suffix", description = "The suffix to append to the column names if the mode is Append.")
	@Effect(predicate = ReplaceOrAppend.IsAppend.class, type = EffectType.SHOW)
	String m_suffix;

	/**
	 * Create a column rearranger that processes multiple columns. If the mode is
	 * {@link #APPEND}, the new column names are created by appending a suffix to
	 * the old column names, and each is guaranteed to be unique. If the mode is
	 * {@link #REPLACE}, the output column names are the same as the input column
	 * names.
	 * 
	 * @param rearranger          the column rearranger to use.
	 * @param uniqueNameGenerator a name generator to ensure unique column names.
	 * @param originalSpec        the spec of the input table
	 * @param cellFactoryFactory  a function that creates a
	 *                            {@link SingleCellFactory} for a given input column
	 *                            spec and a given output column name.
	 * @param inputColumnNames    the names of the columns to process.
	 * @return a column rearranger that processes the input columns.
	 */
	public ColumnRearranger addToRearranger(final ColumnRearranger rearranger,
			final UniqueNameGenerator uniqueNameGenerator, final String inputColumnName,
			final DataTableSpec originalSpec,
			final BiFunction<DataColumnSpec, String, SingleCellFactory> cellFactoryFactory) {
		final var inputColumnSpec = originalSpec.getColumnSpec(inputColumnName);
		final var cellFac = constructSingleCellFactory(inputColumnSpec, cellFactoryFactory, uniqueNameGenerator);
		if (this.m_replaceOrAppend == REPLACE) {
			rearranger.replace(cellFac, inputColumnName);
		} else {
			rearranger.append(cellFac);
		}

		return rearranger;

	}

	private SingleCellFactory constructSingleCellFactory(final DataColumnSpec inputColumn,
			final BiFunction<DataColumnSpec, String, SingleCellFactory> cellFactoryFactory,
			final UniqueNameGenerator uniqueNameGenerator) {
		final var inputColumnName = inputColumn.getName();
		return cellFactoryFactory.apply(inputColumn, this.m_replaceOrAppend == REPLACE ? inputColumnName
				: uniqueNameGenerator.newName(inputColumnName + m_suffix));

	}

	public enum ReplaceOrAppend {

		/** Replace the existing column */
		@Label(value = "Replace", description = "Replaces the selected columns by the new columns.")
		REPLACE, //
		/**
		 * Append a new column with a name based on the existing column name + suffix
		 */
		@Label(value = "Append with suffix", description = """
				Appends the selected columns to the input table with \
				a new name that is the previous name plus the provided suffix.
				""")
		APPEND; //

		/**
		 * A reference for the ReplaceOrAppend enum. Will only work for the last usage
		 * of {@link ReplaceOrAppend} in a node settings.
		 *
		 * @see ValueReference
		 */
		public interface ValueRef extends Reference<ReplaceOrAppend> {
		}

		/**
		 * Predicate to check if the selected value is {@link #APPEND}. Will only work
		 * for the last usage of {@link ReplaceOrAppend} in a node settings.
		 */
		static final class IsAppend implements PredicateProvider {

			@Override
			public Predicate init(final PredicateInitializer i) {
				return i.getEnum(ValueRef.class).isOneOf(ReplaceOrAppend.APPEND);
			}
		}

	}
}
