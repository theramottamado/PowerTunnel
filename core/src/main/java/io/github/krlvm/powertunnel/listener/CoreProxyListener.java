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

package io.github.krlvm.powertunnel.listener;

import io.github.krlvm.powertunnel.Server;
import io.github.krlvm.powertunnel.sdk.ServerListener;
import io.github.krlvm.powertunnel.sdk.http.ProxyRequest;
import io.github.krlvm.powertunnel.sdk.http.ProxyResponse;
import io.github.krlvm.powertunnel.sdk.plugin.PowerTunnelPlugin;
import io.github.krlvm.powertunnel.sdk.proxy.ProxyListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CoreProxyListener implements ProxyListener {

    private final Map<ProxyListener, ProxyListenerInfo> proxyListeners;

    public CoreProxyListener(Map<ProxyListener, ProxyListenerInfo> proxyListeners) {
        this.proxyListeners = proxyListeners;
    }

    @Override
    public void onClientToProxyRequest(@NotNull ProxyRequest request) {
        callProxyListeners(listener -> listener.onClientToProxyRequest(request));
    }

    @Override
    public void onProxyToServerRequest(@NotNull ProxyRequest request) {
        callProxyListeners(listener -> listener.onProxyToServerRequest(request));
    }

    @Override
    public void onServerToProxyResponse(@NotNull ProxyResponse response) {
        callProxyListeners(listener -> listener.onServerToProxyResponse(response));
    }

    @Override
    public void onProxyToClientResponse(@NotNull ProxyResponse response) {
        callProxyListeners(listener -> listener.onProxyToClientResponse(response));
    }

    private void callProxyListeners(ProxyListenerCallback callback) {
        for (Map.Entry<ProxyListener, ProxyListenerInfo> entry : proxyListeners.entrySet()) {
            try {
                callback.call(entry.getKey());
            } catch (Exception ex) {
                // TODO: Use Logger
                System.out.printf(
                        "An error occurred in ProxyListener of plugin '%s' [class=%s, priority=%s]: %s%n",
                        entry.getValue().getPlugin().getInfo().getId(),
                        entry.getKey().getClass().getSimpleName(), entry.getValue().getPriority(),
                        ex.getMessage()
                );
                ex.printStackTrace();
            }
        }
    }
}