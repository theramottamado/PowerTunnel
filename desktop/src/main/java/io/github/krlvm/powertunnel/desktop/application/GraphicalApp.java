/*
 * This file is part of PowerTunnel.
 *
 * PowerTunnel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PowerTunnel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PowerTunnel.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.krlvm.powertunnel.desktop.application;

import io.github.krlvm.powertunnel.desktop.BuildConstants;
import io.github.krlvm.powertunnel.desktop.Main;
import io.github.krlvm.powertunnel.desktop.frames.MainFrame;
import io.github.krlvm.powertunnel.desktop.managers.TrayManager;
import io.github.krlvm.powertunnel.desktop.ui.JPanelCallback;
import io.github.krlvm.powertunnel.sdk.configuration.Configuration;
import io.github.krlvm.powertunnel.sdk.exceptions.ProxyStartException;
import io.github.krlvm.powertunnel.sdk.proxy.ProxyStatus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class GraphicalApp extends DesktopApp {

    private static GraphicalApp instance;

    public static final Image ICON = Toolkit.getDefaultToolkit().getImage(
            Main.class.getResource("/icon" + (BuildConstants.IS_RELEASE ? "" : "_dev") + ".png")
    );

    private final TrayManager trayManager;

    private final MainFrame frame;

    public GraphicalApp(Configuration configuration, boolean start, boolean minimized, boolean tray) {
        super(configuration, start);
        instance = this;

        frame = new MainFrame(this);
        if(tray) {
            trayManager = new TrayManager(this);
            try {
                trayManager.load();
            } catch (AWTException ex) {
                System.err.println("Tray is not available: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            trayManager = null;
        }

        if(!minimized) {
            showFrame();
        } else {
            if(!isTrayAvailable()) System.err.println("Can't run minimized when tray icon is disabled");
        }
    }

    @Override
    public void start() {
        final ProxyStartException ex = startInternal();
        if(ex == null) return;
        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Failed to start server", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void beforeProxyStatusChanged(@NotNull ProxyStatus status) {
        frame.update();
    }

    @Override
    public void onProxyStatusChanged(@NotNull ProxyStatus status) {
        frame.update();
    }

    public boolean isTrayAvailable() {
        return trayManager != null && trayManager.isLoaded();
    }

    public void showNotification(String message) {
        if(isTrayAvailable()) trayManager.showNotification(message);
    }

    public void showFrame() {
        frame.showFrame();
    }

    public void showPluginsFrame() {

    }

    public void showOptionsFrame() {

    }

    public void extendButtonsPanel(JPanelCallback callback) {
        frame.getExtensibleButtonsPanel(callback);
    }

    public void dispose() {
        new Thread(() -> {
            if (isRunning()) stop();
            if (isTrayAvailable()) trayManager.unload();
            System.exit(0);
        }, "App Shutdown Thread").start();
    }

    public static GraphicalApp getInstance() {
        return instance;
    }
}