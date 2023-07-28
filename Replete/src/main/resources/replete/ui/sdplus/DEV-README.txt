
sdplus Developer README
=======================

Author: Derek Trumbo

Table of Contents:
1.  Intro (NOTE)
2.  External Dependencies (DESIGN NOTE)
3.  Object Usage For Generality (DESIGN NOTE)
4.  Usage of null Value (DESIGN NOTE)
5.  Scale Properties (TODO)
6.  Shapes (TODO)
7.  XMLizable (TODO)
8.  Wave & Project Color Settings (TODO)
9.  Scale Panel Model Persistence (DESIGN NOTE)
10. Single-Select Enumerated Panels (OPTIONAL)
11. Scale Panel Model vs. Scale Panel UI (DESIGN NOTE)
12. Scale-Specific Element Count & Subselection Context (DESIGN NOTE)
13. Note Labels Wrap (QUIRK)
14. UI Settings (DESIGN NOTE)
15. Model Updates (DESIGN NOTE)

=======================

1.  Intro (NOTE):
        The ScaleSetPanel Framework (a.k.a. sdplus) is intended to
    be a generic UI component that could easily be used in any Java
    desktop application.  The following README will reiterate that
    ideal.  Of course, it's possible that things need to be changed
    in the framework as Wave's specific needs become more obvious
    during integration.  The code/design is NOT perfect, and there's
    already things I would change if I were to do it all over again,
    but at least it should be clean and documented enough for someone
    to easily change and/or enhance it.

2.  External Dependencies (DESIGN NOTE):
        sdplus is fairly well self-contained but depends on at least
    these external classes that either already exist in Wave or PSCC:

        * ChangeNotifier
        * EmbeddedIntegerComparator
        * EscapeDialog (for color dialog, etc.)
        * GradientPanel
        * GUIUtil
            - GUIUtil.addBorderedComponent needs to have its arguments
              reordered to conform to sdplus.  Change all places in Wave
              that use this method after changing the order of arguments.
        * IconButton (for color dialog)
        * OsUtil (for precise right-click behavior)
        * Wave's color package has been moved to the sdplus package since
          it is so integral to Slice & Dice.  Delete this package from
          Wave and use the one in sdplus (the color package in Wave is
          obsolete).  Another note on this later.
        * The following classes are only included in the project because
          sdplus as provided by me was using some PSCC classes that are
          more comprehensive than the corresponding Wave classes which
          aren't always a direct copy of the PSCC classes but slightly
          different.
            - ClassUtil, EscapeFrame, JarUtil, NewlineType, StringUtil
          Include these in Wave as needed to resolve references (Wave's
          may not need them or already have them).

3.  Object Usage For Generality (DESIGN NOTE):
        sdplus assumes the data model is a collection of Object's.  The
    enumerated scales can show any object by calling the toString method
    of that object and group identical objects by using the equals method.
    This should not impact Wave that much, since Strings are Objects.  However,
    the COLOR PACKAGE, which is now packaged INSIDE the sdplus package, now
    uses a mapping of Object to Color instead of String to Object.  Since other
    parts of Wave (project and Wave color settings) use the color package, they
    will need to conform to this new definition of ColorMap (impact should be low).
    The continuous scales will only include a value if the object is a Number
    object.  This was again to make this as generic a UI component as possible.
    However, if your data model uses double[1] to store single double values, a
    translation will need to be made somewhere, either in the
    ScaleSetPanelModel.getValue method or in the ContScalePanelModel perhaps.

4.  Usage of null Value (DESIGN NOTE):
        This is important and a big deviation from the way Slice & Dice was
    doing something before.  In order become as generic a UI component as
    possible, the concept of <no value> for both enumerated scale panels and
    continuous scale panels is defined by the MODEL RETURNING NULL for a given
    scale and data element.  In other words, if the model (i.e. the class that
    implements ScaleSetPanelModel) returns null for getValue(key, row), THAT
    is <no value>.  The reason this is different is because Wave has traditionally
    used the zero-length string ("") to mean <no value>.  Even though philosophically
    null and "" for Java strings both represent NO DATA, only the null values are
    recognized as <no value> by sdplus.
        In Wave, data elements cannot return null values for enumerated values.
    They instead return "" in place of null values.  I have a feeling that Wave
    will not be able to all of a sudden begin returning null values at the
    DataElement layer without creating a complete disaster.  So either there
    needs to be some translation at the getValue(key, row) layer, or the framework
    needs to treat null and "" the same.  This would involve looking at all the
    code in the EnumScale*Panel* classes and seeing where "" values might need
    to be considered in addition to null values.

5.  Scale Properties (TODO):
        I didn't add all the different aspects of a scale to the framework.
    In other words subject, dimension, units, etc. are not all in the framework.
    I thought this should be easy enough to add to the framework based on specific
    needs.  To add more of this kind of generic scale information, one would add
    fields to ScalePanelModel, update all the constructors in ScalePanelModel
    and subclasses to initialize the fields and add accessors and mutators and
    then display the information as you see fit on the scale panels.

6.  Shapes (TODO):
        Although colors were fully implemented - shapes were not implemented.
    I left this as an exercise to the developer.  It should be very simple to
    look wherever color is implemented and add analogous code for shapes.  Some
    code related to shapes was left in, just commented out.

7.  XMLizable (TODO):
        XMLizable was not implemented.  The goal of sdplus was to completely
    be a UI COMPONENT.  Plus it was meant to be as generic as possible - almost
    as generic as ANY SWING COMPONENT.  If possible, XMLizable should be kept
    at the level of the class that implements the ScaleSetPanelModel interface.
    Then when this model is xmlized - all of the scale panel models are xmlized.
    When the Wave project and thus the ScaleSetPanelModel is de-xmlized, the
    ScaleSetPanel is then rebuilt from the de-xmlized model.

8.  Wave & Project Color Settings (TODO):
        ContScalePanelModel and EnumScaleBasePanelModel have sections of code
    commented out that delegate color identification to either the project
    or Wave settings if there are no override colors.  Naturally this code will
    have to be replaced when inserted into Wave.  Also: The Pass/Fail and 
    Color Code specialty code has to be uncommented in EnumScaleBasePanelModel.

9.  Scale Panel Model Persistence (DESIGN NOTE):
        Related to above TODO.  The class that implements ScaleSetPanelModel
    will implement a method called getScalePanelModel(key).  This method COULD
    return a new scale panel model object each time it is called, but the real
    practice will be to return the "current" model for the given key, keeping
    all the models in memory.  In this manner, this model can be XMLized easily
    because it will be keeping track of all the models in memory for a given
    transform:

       public class MyModel implements ScaleSetPanelModel, XMLizable {
           ListOrMap<ScalePanelModel> panelModels;
           public ScalePanelModel getScalePanelModel(String key) {
               if(panelModels.get(key) == null) {
                   panelModels.put(key, buildPanelModel(key));
                   // Or all the panel models could be created before
                   // hand in the constructor of MyModel instead of
                   // having a buildPanelModel method.
               }
               return panelModels.get(key);
           }
           protected ScalePanelModel buildPanelModel(String key) {
           }
           ...
       }

    An example of this is seen in the demo code (DemoModel).

10. Single-Select Enumerated Panels (OPTIONAL):
        If you end up deciding you would never use the radio button
    enum scale panels, you can simplify the framework by this process:

        Delete EnumScaleSinglePanel
        Delete EnumScaleSinglePanelModel
            (and delete any references to these classes)
        Rename EnumScaleBasePanelModel to EnumScalePanelModel
            and make not abstract.
        Delete EnumScaleMultiPanelModel and replace all references
            with EnumScalePanelModel
        Rename EnumScaleBasePanel to EnumScalePanel make not abstract
        Merge the methods and fields from EnumScaleMultiPanel into
            EnumScalePanel and replace all references to the former
            with the latter.

11. Scale Panel Model vs. Scale Panel UI (DESIGN NOTE):
        One aspect which I did not address fully, but which should not be a
    detractor to integration is that I couldn't completely decide what fields
    should be part of the scale panel models and what should be part of the scale
    panels.  Part of me wanted to keep everything in the model (even UI-related
    things) to get persistence so that the panels can be rebuilt at anytime without
    losing information and another part of me wanted to put only those things in
    the model that would conceivably be desired to be saved to the project file
    (i.e. non-transient qualities that that crucially defined a scale).  In the
    end I threw my hands up and just left it more or less the way it was in Wave
    since the impact of getting it wrong was fairly negligible.  Here is a
    brainstorming list to record the issue and does not necessarily reflect
    current code:

        * Model-crucial qualities: key, name, units, note, colors, shapes
        * GUI/transient qualities: highlighted, open, enum show values, enum
          coalesce events
        * Not Really Sure, Both?: subselected, listed in table, visualization type,
          menu configuration

    In other words, if you have a strong desire or need to move something around,
    go ahead and change it (moving around appropriate accessors and mutators as
    well).  Otherwise, things might work just fine.

12. Scale-Specific Element Count & Subselection Context (DESIGN NOTE):
        Remember that each scale in the ScaleSetPanel does not have to
    be backed by the same number of data elements.  The reason was to create a
    much more flexible UI component and avoid a component that could only be
    backed by a rigid M rows x N columns grid.  Conceptually this is a big change
    but technically all it required was adding key argument to the ScaleSetPanelModel
    interface for getDataElementCount(key).  If you want the ScaleSetPanel backed
    by a rigid grid, this method will return the same value regardless of which
    key is passed in.
        This impacts the subselection algorithm.  Subselection only makes sense
    on a set of scales that are logically related and backed by the same backing
    model.  Since the ScaleSetPanelModel is agnostic to which data source each
    scale panel model is backed by, the subselection algorithm must know what
    scales it should pay attention to by some other manner.  The solution was
    to create a SubselectionContext interface.  A Subselector must exist around
    a SubselectionContext.  The subselection algorithm only uses those scales
    as defined by the model that exist in the context as defined by the
    SubselectionContext.  This was a very simple design that seemed to work
    nicely.  If you don't like this subselection algorithm, feel free to add
    your own simply by listening to the value change events from the scale
    set panel.

13. Note Labels Wrap (QUIRK):
        Same thing that exists in Wave at this moment: the "note labels"
    actually do not wrap - you have to put in your own <BR> right now to
    produce line breaks.  This is not seen very much because we often don't have
    notes in our Wave data scales.  But just something to be aware of as a small
    annoyance that could be probably fixed with some more Swing expertise if you
    decide the rule for when the text in the label should wrap (at a given
    width or the width of the scale set panel, etc).

14. UI Settings (DESIGN NOTE):
        You will see what seem to be duplicated UI setting instance variables
    in both the ScaleSetPanel class and ScalePanel base class.  There are reasons
    for this.  1.  The scale panels are allowed to exist outside of the scale set
    panel.  2.  The scale panels each can have their own UI settings.  In order
    to support these design requirements, each scale panel has its own UI settings.
    The UI settings in the scale set panel are the "default" settings that are
    applied to the new panels that are created each time the scale set panel is
    rebuilt from the model.  See applyScaleSetUISettings methods.  This means that
    if you always want scale panel "xyz" to have a BLUE highlight color, then
    right after you rebuild the model, grab that panel and make it blue:

        pnlSet.rebuildAll();
        ScalePanel pnlXYZScale = pnlSet.getScalePanel("xyz");
        pnlXYZScale.setHighlightColor(Color.blue);

15. Model Updates (DESIGN NOTE):
        The interaction between any UI component and the model that backs it is
    extremely important.  First of all, at an individual scale panel level, the
    scale panel models are almost always only changed by the scale panels.  Because
    of this the scale panel models themselves were not made more complicated by
    having the capability of notifying listeners when they change (even though 
    this would probably be the more elegant design -- though there are practical 
    arguments against it -- and you'd have to remove the accessors for the Collection 
    instance variables to know when every instance variable changes, which I wasn't 
    planning on doing).  Because of this, if for some reason the scale panel model 
    changes programmatically out from underneath the scale panel, then the method 
    updateUIFromModel must be called:
        
        ScalePanel panel = pnlSet.getScalePanelMap().get("ln");
        ScalePanelModel model = panel.getScalePanelModel();
        model.setName("New Name");
        model.setNote("This is the new note.");
        model.setOpen(true);
        ((EnumScaleMultiPanelModel) model).getSelectedValues().remove("Andersen");
        panel.updateUIFromModel();
        
    If the user is interacting through the panel, then the model will be of course
    updated automatically.
        Now for the model that backs the scale set panel as a whole.  This model
    (ScaleSetPanelModel) enumerates the scales that will be represented in the 
    panel by providing a list if keys and returns scale panel models for each
    key.  The model also provides the number of data elements for each scale and
    the VALUE of each of the scale's data elements.  Which scale panel model
    is returned for a given key determines how the scale will be visualized in
    the scale set panel (based on the class map in ScaleSetPanel).
        There are two main "update" methods in the ScaleSetPanel class - rebuildAll
    and rebuildUIOnly.  The method rebuildAll constructs new scale panels based
    on the scale panel models that are returned by the scale set panel model and
    then calls rebuildUIOnly to layout those panels.  The method rebuildUIOnly 
    lays out existing scale panels according to current sort and group membership.
    When the sort or groups change, only rebuildUIOnly need be called.  Also,
    the class that implements ScaleSetPanelModel must notify listeners when
    the model changes.  Right now, the scale set panel is listening and calls
    rebuildAll (in a similar way that a TableModel is required to notify listeners,
    e.g. JTable's, when it changes).
        The limitation with this design is that UI settings are stored in the 
    scale panels and the default UI settings stored in the scale set panel are 
    applied to the scale panels each time they are reconstructed in rebuildAll.  
    So if you have scale-specific UI settings applied to a given scale panel (say
    you made scale panel X have a green selection color) they will be cleared
    when the scale set panel is rebuilt.  The main option right now is that you
    reapply any scale panel-specific UI settings each time after rebuildAll is
    called.
        If you have the need, you could have two different methods in the 
    ScaleSetPanelModel interface for adding listeners - one for changes to the
    scale "structure" and one for the scale "data" similar to how the JTable
    can update based on structure or data changes from its underlying model.
    "Structure" changes would involve when the keys change, or number or type 
    of scales change and "data" changes would involve when content in the scales 
    change, such as names, units, notes, data values, or selected/filter ranges.  
    Then in the ScaleSetPanel you could have a 3rd "update" method that doesn't 
    rebuild anything but just asks each of the existing scale panels to update 
    themselves.  Here is some of the code that could be needed for this change:

        ScaleSetPanelModel:
            public void addModelStructureListener(ChangeListener listener);
            public void addModelDataListener(ChangeListener listener);

        ? implements ScaleSetPanelModel:
            public void fireModelStructureChanged() {
                modelStructureNotifier.fireStateChanged();
            }
            public void fireModelDataChanged() {
                modelDataNotifier.fireStateChanged();
            }
        
        ScaleSetPanel.setModel:
            model.addModelStructureListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    rebuildAll();
                }
            });
            model.addModelDataListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    updateUIFromModel();
                }
            });
            
        ScaleSetModel.updateUIFromModel (new, 3rd "update" method):
            public void updateUIFromModel() {
                for(ScalePanel scalePanel : scalePanels) {
                    scalePanel.updateUIFromModel();
                }
            }
            // This would allow for updating of the scale panels
            // without losing the UI settings saved on the scale
            // panels.  But it doesn't 1) pick up any new or removed
            // scales, 2) reflect changes in key values, or 3) change
            // the type of scale panels.
        
    This is just an idea.  The reason this wasn't implemented initially
    was that it will depend on your specific usage of the component as to
    whether you will need this additional layer of flexibility.
        Remember that the menu configuration is a UI setting.  So depending
    on when you need to rebuild the scale set panel, you might lose settings
    like which menu items are visible -- in that case the settings will either
    have to be reapplied right after rebuildAll is called, or some sort of
    new update method like the one outlined above might be required.
    
* So many of the uncertainties in the above README will be known once you  
  try integrating the component into Wave.
  