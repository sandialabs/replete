package replete.ui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import replete.ui.ColorLib;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class UiHintedModelTableCellRenderer extends RTableRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int visualRow, int col) {
        int modelRow = table.convertRowIndexToModel(visualRow);

        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, visualRow, col);

        if(insetsRenderer != null) {
            lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(),
                BorderFactory.createEmptyBorder(
                    insetsRenderer.top,
                    insetsRenderer.left,
                    insetsRenderer.bottom,
                    insetsRenderer.right
                )
            ));
        }

        TableModel model = table.getModel();
        if(model instanceof UiHintedTableModel) {
            UiHintedTableModel uiModel = (UiHintedTableModel) model;

            // Background
            Color bg = getBackgroundColor(uiModel, modelRow, col);
            if(bg == null) {
                bg = Color.white;
            }
            if(!isSelected) {
                lbl.setBackground(bg);
            }

            // Foreground
            Color fg = getForegroundColor(uiModel, modelRow, col);
            if(fg != null) {
                lbl.setForeground(fg);
            } else {
                lbl.setForeground(Color.black);
            }

            // Icon
            Icon icon = getIcon(uiModel, modelRow, col);
            lbl.setIcon(icon);

            // Alignment
            int align = getAlignment(uiModel, modelRow, col);
            if(align != -1) {
                lbl.setHorizontalAlignment(align);
            } else {
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
            }

            // Could do border as well...

            // Insets
            Insets insets = getInsets(uiModel, modelRow, col);
            if(insets != null) {
                lbl.setBorder(BorderFactory.createCompoundBorder(lbl.getBorder(),
                    BorderFactory.createEmptyBorder(
                        insets.top,
                        insets.left,
                        insets.bottom,
                        insets.right
                    )
                ));
            }

            Font font = getFont(uiModel, modelRow, col);
            if(font != null) {
                lbl.setFont(font);
            } else {
                Boolean bold   = isBold(uiModel, modelRow, col);
                Boolean italic = isItalic(uiModel, modelRow, col);

                if(bold != null || italic != null) {
                    int style = Font.PLAIN;
                    if(bold != null && bold) {
                        style |= Font.BOLD;
                    }
                    if(italic != null && italic) {
                        style |= Font.ITALIC;
                    }
                    lbl.setFont(new Font(lbl.getFont().getName(), style, lbl.getFont().getSize()));
                } // else need to set to something? working for now...
            } // else need to set to something? working for now...

            if(isSelected) {
                Color selBg = getSelectionBackgroundColor(uiModel, modelRow, col);
                if(selBg != null) {
                    lbl.setBackground(selBg);
                }
                lbl.setForeground(Color.white);
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                Border selBrd = getSelectionBorder(uiModel, modelRow, col);
                if(selBrd != null) {
    //                lbl.setBackground(selBg);
                    lbl.setBorder(selBrd);
                }
            }

            if(!hasFocus) {
                Border b = getBorder(uiModel, modelRow, col);
                if(b != null) {
                    lbl.setBorder(b);
                }
            }

            int rowHeight = getRowHeight(uiModel, modelRow);
            if(rowHeight > 0) {
                table.setRowHeight(visualRow, rowHeight);
            }

            TableCellRenderer renderer = getRenderer(uiModel, modelRow, col);
            if(renderer != null) {
                return renderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, visualRow, col);
            }
        }
        return lbl;
    }

    // Helper methods - These are here so that we can minimize
    // the negative effect of exceptions bubbling up to the UI
    // thread.  Often it can be easy to make mistakes in complex
    // table models.  Exceptions in table model methods can
    // cause a recursive dialog loop if we've registered a uncaught
    // exception handler that shows a dialog to show the exception.
    // The dialog is shown over the table and then a repaint is then
    // requested again for the table and another dialog is opened.

    private Color getBackgroundColor(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getBackgroundColor(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "background color", e);
            return ColorLib.RED_LIGHT;
        }
    }

    private Color getForegroundColor(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getForegroundColor(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "foreground color", e);
            return ColorLib.RED_BRIGHT;
        }
    }

    private Icon getIcon(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getIcon(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "icon", e);
            return ImageLib.get(CommonConcepts.ERROR);
        }
    }

    private int getAlignment(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getAlignment(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "alignment", e);
            return -1;
        }
    }

    private Insets getInsets(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getInsets(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "insets", e);
            return null;
        }
    }

    private Font getFont(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getFont(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "font", e);
            return null;
        }
    }

    private Boolean isBold(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.isBold(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "bold", e);
            return null;
        }
    }

    private Boolean isItalic(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.isItalic(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "italic", e);
            return null;
        }
    }

    private Color getSelectionBackgroundColor(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getSelectionBackgroundColor(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "selection background color", e);
            return ColorLib.RED_LIGHT;
        }
    }

    private Border getSelectionBorder(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getSelectionBorder(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "selection border", e);
            return null;
        }
    }

    private Border getBorder(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getBorder(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "border", e);
            return null;
        }
    }

    private int getRowHeight(UiHintedTableModel uiModel, int modelRow) {
        try {
            return uiModel.getRowHeight(modelRow);
        } catch(Exception e) {
            showModelError(uiModel, "row height", e);
            return -1;
        }
    }

    private TableCellRenderer getRenderer(UiHintedTableModel uiModel, int modelRow, int col) {
        try {
            return uiModel.getRenderer(modelRow, col);
        } catch(Exception e) {
            showModelError(uiModel, "renderer", e);
            return null;
        }
    }

    // We don't rethrow these errors because of the potentially damaging
    // effects that having exceptions thrown *during the repainting* of a
    // component can have.  Instead we will dump the exception to the console.
    private void showModelError(UiHintedTableModel uiModel, String aspect, Exception e) {
        String msg =
            "UiHintedTableModel of type '" + uiModel.getClass().getName() +
            "' encountered a problem rendering '" +
            aspect + "' for this table.";

        System.err.println(msg);
        e.printStackTrace();
    }
}
