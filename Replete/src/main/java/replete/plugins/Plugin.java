package replete.plugins;

import javax.swing.ImageIcon;

/**
 * A plug-in's ID is its fully-qualified class name.
 *
 * @author dtrumbo
 */

public interface Plugin {

    /**
     * Returns the display name that is shown in the UI to represent
     * this plug-in.  This name can change as desired.
     *
     * @return the plug-in's display name
     */
    public String getName();

    /**
     * Returns the version of the plug-in.  The version # should be
     * in the form: major.minor.service.qualifier.  The major segment
     * should be incremented when the plug-in's API changes.  The
     * minor segment should be incremented when the plug-in has included
     * externally visible changes.  The service segment is should be
     * incremented for bug fixes and other minor changes.  The qualifier
     * is an arbitrary string that indicates the build of the plug-in.
     * Suggested format for this string is "bYYYYMMDD-HHMM".
     *
     * @return the plug-in's version
     */
    public String getVersion();

    /**
     * Returns the provider of the plug-in.  This can be the person,
     * company, or institution who created the plug-in.
     *
     * @return the plug-in's provider
     */
    public String getProvider();

    /**
     * Returns the icon of this plug-in which is displayed
     * in the platform's plug-in dialog box.  This is unrelated
     * to the icon(s) that the plug-in provides if it wishes to
     * customize the product.
     *
     * @return the plug-in's icon
     */
    public ImageIcon getIcon();

    /**
     * Returns the description of this plug-in which is displayed
     * in the platform's plug-in dialog box.
     *
     * @return the plug-in's description
     */
    public String getDescription();

    /**
     * xxx
     *
     * @return
     */
    public Class<? extends ExtensionPoint>[] getExtensionPoints();

    /**
     * xxx
     *
     * @return
     */
    public ExtensionPoint[] getExtensions();

    /**
     * Called after all the plug-ins have been loaded.  The plug-in
     * is given a chance to run some generic code.  The order in
     * which plug-ins are started is non-deterministic.
     */
    public void start();
}
