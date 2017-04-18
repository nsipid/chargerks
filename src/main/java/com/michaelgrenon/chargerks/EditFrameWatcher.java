package com.michaelgrenon.chargerks;

import charger.EditFrame;
import chargerlib.WindowManager;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class EditFrameWatcher {
    private Thread watcherThread;
    private volatile boolean isStarted;
    private String menuName;
    private KnowledgeSpace knowledgeSpace;
    
    public EditFrameWatcher(String menuName, KnowledgeSpace knowledgeSpace) {
        this.menuName = menuName;
        this.knowledgeSpace = knowledgeSpace;
    }
    
    
    public synchronized void start() {
        if (isStarted)
            return;
        
        isStarted = true;
        watcherThread = createWatcherThread();
        
        watcherThread.start();
    }
    
    public synchronized void stop() {
        if (!isStarted)
            return;
        
        isStarted = false;
        
        try {
            watcherThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Logger.getLogger(EditFrameWatcher.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    private Thread createWatcherThread() {
        return new Thread(() -> {
            while(isStarted && !Thread.currentThread().isInterrupted()) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        Arrays.stream(Window.getWindows())
                                .filter((win) -> win instanceof EditFrame)
                                .map(win -> ((EditFrame) win))
                                .forEach((frame) -> {
                                    this.addMenuIfMissing(frame);
                                });        
                    });
    
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    Logger.getLogger(EditFrameWatcher.class.getName()).log(Level.WARNING, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(EditFrameWatcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private void addMenuIfMissing(EditFrame frame) {
        JMenuBar menuBar = frame.editFrameMenuBar;
        
        if (!menuBarHasMenu(menuBar)) {
            JMenu newMenu = new JMenu(menuName);
            
            JMenuItem subgraphMenuItem = new JMenuItem();
            subgraphMenuItem.setText("Check if subgraph exists (catA).");
            subgraphMenuItem.addActionListener(getSubgraphActionListener());
            newMenu.insert(subgraphMenuItem, 0);
            
            JMenuItem catalogMenuItem = new JMenuItem();
            catalogMenuItem.setText("Get catalog (catA).");
            catalogMenuItem.addActionListener(getCatalogActionListener());
            newMenu.insert(catalogMenuItem, 1);
            
            menuBar.add(newMenu);
            frame.revalidate();
        }
    }
    
    private EditFrame getEditFrame(JMenuItem menuItem) {
        JPopupMenu menu = (JPopupMenu) menuItem.getParent();
        JComponent invoker = (JComponent) menu.getInvoker();
        Container container = invoker.getTopLevelAncestor();
        return (EditFrame) container;
    }
    
    private ActionListener getSubgraphActionListener() {
        return e -> { 
            EditFrame editor = getEditFrame((JMenuItem) e.getSource());

            Question q = new SubgraphQuestion(editor.TheGraph, "catA");
            EditFrame resultWindow = new EditFrame();
            resultWindow.TheGraph = knowledgeSpace.Ask(q);
            resultWindow.repaint();
            resultWindow.bringToFront();
        };
    }
    
    private ActionListener getCatalogActionListener() {
        return e -> { 
            EditFrame editor = getEditFrame((JMenuItem) e.getSource());

            Question q = new CatalogQuestion("catA");
            EditFrame resultWindow = new EditFrame();
            resultWindow.TheGraph = knowledgeSpace.Ask(q);
            resultWindow.repaint();
            resultWindow.bringToFront();
        };
    }
    
    private boolean menuBarHasMenu(JMenuBar menuBar) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText().equals(menuName)) {
                return true;
            }
        }       
        return false;
    }
}
