/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 */
package org.knime.examples.unitconverter;

import static org.knime.examples.unitconverter.OutputColumnSettings.ReplaceOrAppend.REPLACE;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.util.UniqueNameGenerator;
import org.knime.node.DefaultModel.RearrangeColumnsInput;
import org.knime.node.DefaultModel.RearrangeColumnsOutput;

/** Model for the "Unit Converter" node. */
final class UnitConverterNodeModel {

    static void rearrangeColumns(final RearrangeColumnsInput in, final RearrangeColumnsOutput out)
        throws InvalidSettingsException {
        final var spec = in.getDataTableSpec();
        final var settings = in.<UnitConverterNodeSettings> getSettings();
        final var rearranger = new ColumnRearranger(spec);
        final var uniqueNameGenerator = new UniqueNameGenerator(spec);

        for (final ConversionSettings conversion : settings.m_conversions) {
            final var inputColumnIndex = spec.findColumnIndex(conversion.m_inputColumn);
            if (inputColumnIndex < 0) {
                throw new InvalidSettingsException(
                    "Input column '" + conversion.m_inputColumn + "' not found in input table specification.");
            }
            final var inputColumnName = spec.getColumnSpec(inputColumnIndex).getName();
            final var outputColumnSettings = conversion.m_outputColumnSettings;

            if (outputColumnSettings.m_replaceOrAppend == REPLACE) {
                rearranger.replace(new UnitConverterCellFactory(inputColumnName, inputColumnIndex, conversion),
                    inputColumnName);
            } else {
                rearranger.append(new UnitConverterCellFactory(
                    uniqueNameGenerator.newName(inputColumnName + outputColumnSettings.m_suffix), inputColumnIndex,
                    conversion));
            }
        }

        out.setColumnRearranger(rearranger);
    }

    static final class UnitConverterCellFactory extends SingleCellFactory {

        private final int m_columnIndex;

        private final ConversionSettings m_conversion;

        UnitConverterCellFactory(final String columnName, final int columnIndex, final ConversionSettings conversion) {
            super(new DataColumnSpecCreator(columnName, conversion.m_stringOrNumber.getDataType()).createSpec());
            m_conversion = conversion;
            m_columnIndex = columnIndex;
        }

        @Override
        public DataCell getCell(final DataRow row) {
            final var inputCell = row.getCell(m_columnIndex);
            if (inputCell.isMissing()) {
                return inputCell;
            }
            final var inputValue = ((DoubleValue)row.getCell(m_columnIndex)).getDoubleValue();
            final var outputValue = m_conversion.m_type.convert(inputValue);
            return m_conversion.m_stringOrNumber.createCell(outputValue, m_conversion.m_type.getOutputUnit());
        }
    }
}
