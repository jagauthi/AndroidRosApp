package com.github.rosjava.android_apps.teleop;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

public class Listener extends RelativeLayout implements NodeMain {
    private String topicName;
    Subscriber subscriber;

    public Listener(Context context) {
        super(context);
        this.topicName = "appMessages";
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public GraphName getDefaultNodeName() {
        return GraphName.of("androidMessenger");
    }

    public void onStart(ConnectedNode connectedNode) {
        subscriber = connectedNode.newSubscriber(this.topicName, "std_msgs/String");
        subscriber.addMessageListener(new MessageListener() {
            @Override
            public void onNewMessage(Object o) {
                Log.d("DebuggingTag", "Getting a message");
            }
        });
    }

    public void onShutdown(Node node) {

    }

    public void onShutdownComplete(Node node) {

    }

    public void onError(Node node, Throwable throwable) {

    }
}