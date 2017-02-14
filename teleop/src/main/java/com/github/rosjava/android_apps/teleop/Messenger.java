package com.github.rosjava.android_apps.teleop;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import geometry_msgs.Twist;
import java.util.Timer;
import java.util.TimerTask;
import nav_msgs.Odometry;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class Messenger extends RelativeLayout implements MessageListener<Odometry>, NodeMain {
    private Publisher<std_msgs.String> publisher;
    private Timer publisherTimer;
    private String topicName;
    boolean sending = false;

    public Messenger(Context context) {
        super(context);
        this.topicName = "appMessages";
    }

    public void onNewMessage(Odometry message) {
        double w = message.getPose().getPose().getOrientation().getW();
        double x = message.getPose().getPose().getOrientation().getX();
        double y = message.getPose().getPose().getOrientation().getZ();
        double z = message.getPose().getPose().getOrientation().getY();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public GraphName getDefaultNodeName() {
        return GraphName.of("androidMessenger");
    }

    public void onStart(ConnectedNode connectedNode) {
        this.publisher = connectedNode.newPublisher(this.topicName, "std_msgs/String");
        Subscriber subscriber = connectedNode.newSubscriber(this.topicName, "std_msgs/String");
        subscriber.addMessageListener(new MessageListener() {
            @Override
            public void onNewMessage(Object o) {
                Log.d("DebuggingTag", "Getting a message");
            }
        });

        this.publisherTimer = new Timer();
        this.publisherTimer.schedule(new TimerTask() {
            public void run() {
                if(sending) {
                    Log.d("DebuggingTag", publisher.getTopicName().toString());
                    std_msgs.String str = publisher.newMessage();
                    str.setData("Test Message");
                    publisher.publish(str);
                    sending = false;
                    Log.d("DebuggingTag", "NumSubscribers: "+publisher.getNumberOfSubscribers());
                }
            }
        }, 0L, 80L);
    }

    public void setSending(boolean val) {
        sending = val;
    }

    public void onShutdown(Node node) {

    }

    public void onShutdownComplete(Node node) {
        this.publisherTimer.cancel();
        this.publisherTimer.purge();
    }

    public void onError(Node node, Throwable throwable) {

    }
}
