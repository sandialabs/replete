package replete.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import replete.threads.SwingTimerManager;

public class LiveRefreshUiHintedTableModel extends DefaultUiHintedTableModel {


    ////////////
    // FIELDS //
    ////////////

    // Constant

    private static final int DEFAULT_LIVE_REFRESH_INTERVAL = 1000;

    // Other

    private List<int[]> liveCells = new ArrayList<>();
    private Timer liveCellRefreshTimer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LiveRefreshUiHintedTableModel() {
        this(DEFAULT_LIVE_REFRESH_INTERVAL);
    }
    public LiveRefreshUiHintedTableModel(int liveRefreshInterval) {
        liveCellRefreshTimer = SwingTimerManager.create(liveRefreshInterval, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(liveCells != null) {
                    synchronized(liveCells) {
                        for(int[] cell : liveCells) {
                            int row = cell[0];
                            int col = cell[1];
                            if(row < getRowCount() && col < getColumnCount()) {
                                fireTableCellUpdated(row, col);
                            }
                        }
                    }
                }
            }
        });
        liveCellRefreshTimer.start();
    }

    public void clearLiveCells() {
        synchronized(liveCells) {
            liveCells.clear();
        }
    }


    //////////
    // MISC //
    //////////

    public void registerLiveCell(int row, int col) {
        synchronized(liveCells) {
            liveCells.add(new int[] {row, col});
        }
    }
}
