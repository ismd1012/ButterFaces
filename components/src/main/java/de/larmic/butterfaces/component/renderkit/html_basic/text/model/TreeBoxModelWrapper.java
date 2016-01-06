/*
 * Copyright Lars Michaelis and Stephan Zerhusen 2016.
 * Distributed under the MIT License.
 * (See accompanying file README.md file or copy at http://opensource.org/licenses/MIT)
 */
package de.larmic.butterfaces.component.renderkit.html_basic.text.model;

import de.larmic.butterfaces.component.html.text.HtmlTreeBox;
import de.larmic.butterfaces.model.tree.DefaultNodeImpl;
import de.larmic.butterfaces.model.tree.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Michaelis
 */
public class TreeBoxModelWrapper {

    private final List<Node> nodes;
    private final TreeBoxModelType treeBoxModelType;

    public TreeBoxModelWrapper(HtmlTreeBox treeBox) {
        nodes = new ArrayList<>();

        final Object treeBoxValues = treeBox.getValues();

        if (treeBoxValues instanceof Node) {
            nodes.add((Node) treeBoxValues);
            treeBoxModelType = TreeBoxModelType.NODES;
        } else if (treeBoxValues instanceof Iterable) {
            treeBoxModelType = this.handleIterableValues((Iterable) treeBoxValues);
        } else {
            treeBoxModelType = TreeBoxModelType.UNKOWN;
        }
    }

    private TreeBoxModelType handleIterableValues(Iterable treeBoxValues) {
        final Iterable iterable = treeBoxValues;

        boolean foundString = false;

        for (Object value : iterable) {
            if (value instanceof Node) {
                nodes.add((Node) value);
            } else if (value instanceof String) {
                nodes.add(new DefaultNodeImpl<>((String) value, (String) value));
                foundString = true;
            } else {
                // TODO implement me
                throw new NotImplementedException();
            }
        }

        return foundString ? TreeBoxModelType.STRINGS : TreeBoxModelType.NODES;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public TreeBoxModelType getTreeBoxModelType() {
        return treeBoxModelType;
    }
}