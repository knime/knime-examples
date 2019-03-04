package org.knime.examples.extension;

import org.knime.core.node.NodeView;

/**
 * This is an example implementation of the node view of the
 * "NumberFormaterNode"node.
 * 
 * As this example node does not have a view, this is just an empty stub of the 
 * NodeView class which not providing a real view pane.
 *
 * @author KNIME GmbH, Konstanz, Germany
 */
public class NumberFormatterNodeView extends NodeView<NumberFormatterNodeModel> {

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel The model (class: {@link NumberFormatterNodeModel})
	 */
	protected NumberFormatterNodeView(final NumberFormatterNodeModel nodeModel) {
		super(nodeModel);
		// The components of the view should be instantiated here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {
		// Retrieve the new model from your node model and update the view.
		NumberFormatterNodeModel nodeModel = (NumberFormatterNodeModel) getNodeModel();
		assert nodeModel != null;
		/*
		 * Be aware of a possibly not executed nodeModel! The data you retrieve from
		 * your nodemodel could be null, emtpy, or invalid in any kind.
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() {
		// Things to do when closing the view.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen() {
		// Things to do when opening the view.
	}
}
