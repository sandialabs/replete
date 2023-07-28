package replete.ui.windows.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ComponentListener;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import replete.ui.validation.Validatable;
import replete.ui.validation.ValidationContext;
import replete.ui.validation.ValidationUtil;
import replete.util.ReflectionUtil;

public interface RWindow extends Validatable {
    public boolean isVisible();
    public void setVisible(boolean b);
    public void requestFocus();
    public void toFront();
    public boolean close();
    public void closeWindowNoFire();
    public void addClosingListener(ChangeListener listener);
    public void addAttemptToCloseListener(RWindowClosingListener listener);
    public void addComponentListener(ComponentListener listener);
    public String getTitle();
    public int getX();
    public int getY();
    public int getWidth();
    public int getHeight();
    public void setLocation(int x, int y);
    public void setSize(int w, int h);
    public void waitOn();
    public void waitOff();
    public void addWaitOnListener(ChangeListener listener);
    public void addWaitOffListener(ChangeListener listener);
    public void ensureOnScreen();
    public void ensureOnScreen(boolean completely);
    public Container getParent();
    public void setLocationRelativeTo(Component c);
    public void updateUI();
    public void focus(Component c);
    public void enableDiagnostics();
    public boolean isShutdownSwingTimersOnClose();
    public void setShutdownSwingTimersOnClose(boolean shutdown);
    public void setCursor(Cursor cursor);
    public void setJMenuBar(JMenuBar menuBar);
    public Container getContentPane();
    public void toggleDebugColors();
    public void toggleDebugTicks();
    public void toggleDebugMouse();


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // By default, if the only thing added to the dialog is itself a
    // Validatable panel, then we can just delegate the validation
    // to that panel.  Since often the important panels are merely
    // placed one layer deeper within the CENTER region of a border
    // layout we'll also check there for a Validatable panel.  This
    // helps eliminate otherwise necessary but boilerplate
    //     @Override validateInput(ValidationContext context)
    // methods.  If there are controls that are contained directly
    // in the dialog subclass that need validation, then this default
    // implementation is not useful.
    @Override
    default void validateInput(ValidationContext context) {
        Container pnlMain = getContentPane();
        if(pnlMain instanceof Validatable) {
            context.check((Validatable) pnlMain);
        } else if(pnlMain instanceof JPanel) {
            if(pnlMain.getLayout() instanceof BorderLayout) {
                BorderLayout bl = (BorderLayout) pnlMain.getLayout();
                Component c = ReflectionUtil.get(bl, "center");
                if(c instanceof Validatable) {
                    context.check((Validatable) c);
                }
            }
         }
    }

    default boolean checkValidationPass() {
        return ValidationUtil.checkValidationPass((Window) this, this);
    }
}
