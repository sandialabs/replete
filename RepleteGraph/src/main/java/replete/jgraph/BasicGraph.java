/*
Copyright 2013 Sandia Corporation.
Under the terms of Contract DE-AC04-94AL85000 with Sandia Corporation,
the U.S. Government retains certain rights in this software.
Distributed under the BSD-3 license. See the file LICENSE for details.
*/

package replete.jgraph;

import java.awt.Color;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;

public class BasicGraph extends mxGraph {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public BasicGraph() {
        setCellsDisconnectable(false);  // Can select the end of an edge and drag it off of a vertex
        setDisconnectOnMove(false);     // These allow you to just randomly remove edges from the vertices they connect.
        setAllowDanglingEdges(false);   // Whether or not edges can not connect to something.
        setCellsCloneable(false);       // Hold control key while dragging to create copy. (+ still shows up but doesn't do anything on mouse up)
//            setBorder(30); //??
//            setCellsSelectable(false);  // Still shows mouse + icon, but can't select
//            setCellsMovable(false);  // Removes + icon and can't drag to move. (combine with cells selectable for purely static cells)
//        setCellsResizable(false);
//            setCellsDeletable(true);   // ??
        setGridEnabled(true);           // Snap to grid, default true
        setGridSize(10);
        setHtmlLabels(true);
        setCellsEditable(false);        // Can't double-click labels to change text.
        setVertexLabelsMovable(false);  // Can't move labels
        setEdgeLabelsMovable(false);    // Can't move labels
        setResetEdgesOnMove(true);
        setDropEnabled(false);

        // Debug
        listenTo(this);
        listenTo(getSelectionModel());
        listenTo(getView());
        listenTo(getModel());
    }

    public void removeAll() {
        mxCell parent = (mxCell) getDefaultParent();
        for(int i = parent.getChildCount() - 1; i >= 0; i--) {
            parent.remove(i);
        }
    }


    ////////////
    // HELPER //
    ////////////

    protected void listenTo(final mxEventSource src) {
        src.addListener(null, new mxEventSource.mxIEventListener() {
            public void invoke(Object gr, mxEventObject event) {
                listenOutput(src, gr, event);
            }
        });
    }
    protected void listenTo(final mxIGraphModel src) {
        src.addListener(null, new mxEventSource.mxIEventListener() {
            public void invoke(Object gr, mxEventObject event) {
                listenOutput(src, gr, event);
            }
        });
    }
    private void listenOutput(Object src, Object gr, mxEventObject event) {
        String msg = src.getClass().getSimpleName() + " ==> " + event.getName();
        System.out.println(msg);
        GraphDemoFrame.ed.getTextPane().appendln(msg, Color.blue);
        for(String prop : event.getProperties().keySet()) {
            Object value = event.getProperties().get(prop);
            msg = "    " + prop + " = " + value;
            System.out.println(msg);
            GraphDemoFrame.ed.getTextPane().appendln(msg);
        }
    }
}
