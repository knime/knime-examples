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
 */
package org.knime.examples.dataview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.knime.core.data.container.filter.TableFilter;
import org.knime.core.node.BufferedDataTable;
import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.knime.node.DefaultView.ViewInput;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class DataViewNodeFactory extends DefaultNodeFactory {

    public static final class DataViewDataService {

        public BufferedDataTable m_table;

        public DataViewDataService(final ViewInput viewInput) {
            m_table = viewInput.getInternalTables()[0];
        }

        public List<List<String>> getFirstNRows(final int numRows) {
            return extractTableContent(m_table, numRows);
        }
    }

    @SuppressWarnings("restriction")
    private static final DefaultNode NODE = DefaultNode.create() //
        .name("Data View") //
        .icon("") //
        .shortDescription("A Simple Data View Node") //
        .fullDescription("A simple data view node") //
        .ports(p -> p//
            .addInputTable("Input table", "Table with column(s) to display") //
        ).model(m -> m//
            .withoutSettings()//
            .configure((coIn, coOu) -> {
            })//
            .execute((exIn, exOu) -> {
                exOu.setInternalData(exIn.getInData());
            })//
        ).addView(v -> v//
            .settingsClass(DataViewViewSettings.class)//
            .description("The view node description")//
            .page(p -> p//
                .fromFile()//
                .bundleClass(DataViewNodeFactory.class)//
                .basePath("js-src/dist")//
                .relativeFilePath("index.html")//
                .addResourceDirectory("assets")//
                .getReusablePage("dataview")//
            )//
            .initialData(rid -> rid//
                .data(vi -> {
                    final var table = vi.getInternalTables()[0];
                    final var settings = (DataViewViewSettings)vi.getSettings();
                    return extractTableContent(table, settings.m_numRows);
                })//
            )//
            .dataService(rds -> rds//
                .handler(DataViewDataService::new)//
            )//
        )//
        .nodeType(NodeType.Visualizer);

    public DataViewNodeFactory() {
        super(NODE);
    }

    private static List<List<String>> extractTableContent(final BufferedDataTable table, final long end) {
        final var tableContent = new ArrayList<List<String>>();
        try (final var cursor = table.cursor(new TableFilter.Builder().withToRowIndex(end - 1).build())) {
            while (cursor.canForward()) {
                final var row = cursor.forward();
                final var content = IntStream.range(0, cursor.getNumColumns()).mapToObj(i -> {
                    final var value = row.getValue(i);
                    return row.isMissing(i) ? null : POLICY.sanitize(value.materializeDataCell().toString());
                }).toList();
                tableContent.add(content);
            }
        }
        return tableContent;
    }

    static final PolicyFactory POLICY = new HtmlPolicyBuilder() //
        .allowCommonInlineFormattingElements() //
        .allowStandardUrlProtocols() //
        .allowCommonBlockElements() //
        .allowStyling() //
        .allowElements("a") //
        .allowAttributes("href", "target").onElements("a") //
        .toFactory();

}
