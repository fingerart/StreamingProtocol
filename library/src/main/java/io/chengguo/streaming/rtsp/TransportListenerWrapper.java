package io.chengguo.streaming.rtsp;

/**
 * TransportListener Wrapper
 */
public class TransportListenerWrapper implements ITransportListener {

    private ITransportListener behaviour;

    @Override
    public void onConnected() {
        if (behaviour != null) {
            behaviour.onConnected();
        }
    }

    @Override
    public void onConnectFail(Exception exception) {
        if (behaviour != null) {
            behaviour.onConnectFail(exception);
        }
    }

    @Override
    public void onDisconnected() {
        if (behaviour != null) {
            behaviour.onDisconnected();
        }
    }

    public void setBehaviour(ITransportListener listener) {
        behaviour = listener;
    }
}
