package org.knime.examples.unitconverter;

import java.util.function.UnaryOperator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Label;
import org.knime.core.webui.node.dialog.defaultdialog.widget.ValueSwitchWidget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.ChoicesProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.choices.column.CompatibleColumnsProvider.DoubleColumnsProvider;

public class Conversion implements DefaultNodeSettings {

	@Widget(title = "Input column", description = "Numeric column to convert")
	@ChoicesProvider(DoubleColumnsProvider.class)
	String m_inputColumn;

	enum Type {
		@Label(value = "°C → °F (* 9/5 + 32)", description = "Celsius to Fahrenheit")
		C_TO_F(value -> value * 9 / 5 + 32, "°F"),

		@Label(value = "°F → °C (- 32) * 5/9", description = "Fahrenheit to Celsius")
		F_TO_C(value -> (value - 32) * 5 / 9, "°C"),

		@Label(value = "km → mi (/ 1.60934)", description = "Kilometres to miles")
		KM_TO_MI(value -> value / 1.60934, "mi"),

		@Label(value = "mi → km (* 1.60934)", description = "Miles to kilometres")
		MI_TO_KM(value -> value * 1.60934, "km"),

		@Label(value = "L → gal (/ 3.78541)", description = "Litres to US gallons")
		L_TO_GAL(value -> value / 3.78541, "gal"),

		@Label(value = "gal → L (* 3.78541)", description = "US gallons to litres")
		GAL_TO_L(value -> value * 3.78541, "L");

		private final UnaryOperator<Double> m_conversionFunction;
		private final String m_outputUnit;

		Type(UnaryOperator<Double> conversionFunction, String outputUnit) {
			this.m_conversionFunction = conversionFunction;
			this.m_outputUnit = outputUnit;
		}

		String getOutputUnit() {
			return m_outputUnit;
		}

		double convert(double value) {
			return m_conversionFunction.apply(value);
		}
	}

	@Widget(title = "Conversion", description = "Unit conversion to apply")
	Type m_type = Type.C_TO_F;

	OutputColumnSettings m_outputColumnSettings = new OutputColumnSettings();

	enum StringOrNumber {
		@Label(value = "String with unit", description = "Return result as string with unit suffix")
		STRING(StringCell.TYPE),

		@Label(value = "Numeric only", description = "Return result as numeric value")
		NUMERIC(DoubleCell.TYPE);

		private final DataType m_dataType;

		StringOrNumber(DataType dataType) {
			this.m_dataType = dataType;
		}

		DataType getDataType() {
			return m_dataType;
		}

		DataCell createCell(double outputValue, String outputUnit) {
			if (this == STRING) {
				return new StringCell(outputValue + " " + outputUnit);
			} else {
				return new DoubleCell(outputValue);
			}

		}
	}

	@Widget(title = "Return as string with unit suffix", description = "If unchecked, result is numeric")
	@ValueSwitchWidget
	StringOrNumber m_stringOrNumber = StringOrNumber.NUMERIC;
}