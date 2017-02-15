/*
 * Copyright (C) 2013 OSRF.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.rosjava.android_apps.teleop;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.view.VirtualJoystickView;
import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;

/**
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class MainActivity extends RosAppActivity {
	private VirtualJoystickView virtualJoystickView;

	private Messenger messenger;
	private Listener listener;

	private Button messengerButton;
	private Button backButton;

	public MainActivity() {
		super("android teleop", "android teleop");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setDashboardResource(R.id.top_bar);
		setMainWindowResource(R.layout.main);
		super.onCreate(savedInstanceState);

        virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);

		messenger = new Messenger(virtualJoystickView.getContext());
		listener = new Listener(messenger.getContext());

		messengerButton = (Button)findViewById(R.id.messenger_button);
		messengerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMessage();
			}
		});

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
	}

	public void sendMessage()
	{
        messenger.setSending(true);
	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {
		
		super.init(nodeMainExecutor);

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

			String joyTopic = remaps.get(getString(R.string.joystick_topic));

			NameResolver appNameSpace = getMasterNameSpace();
			joyTopic = appNameSpace.resolve(joyTopic).toString();
			virtualJoystickView.setTopicName(joyTopic);

			nodeMainExecutor.execute(messenger, nodeConfiguration.setNodeName("android/messenger"));
			nodeMainExecutor.execute(listener, nodeConfiguration.setNodeName("android/listener"));
			nodeMainExecutor.execute(virtualJoystickView, nodeConfiguration.setNodeName("android/virtual_joystick"));
		}
		catch (IOException e) {
				Log.d("tag", "Oh nooooooo");
				e.printStackTrace();
		}
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu){
		  menu.add(0,0,0,R.string.stop_app);
		  return super.onCreateOptionsMenu(menu);
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item){
		  super.onOptionsItemSelected(item);
		  switch (item.getItemId()){
			  case 0:
				  onDestroy();
				  break;
		  }
		  return true;
	  }
}
