package com.appaholics.umd;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	ProgressDialog pd;
	
	ActionBar actionBar;
	
	int pos;
	String userNumber;
	String TAG = "UM";
	String userName;
	String[] users;
	ArrayList<String> list;
	ArrayAdapter<String> adapter;
	String newName;

	SharedPreferences prefs;
	
	Context mContext;

	boolean modPrimary = false;
	
	ListView listView;

	@Override
	public void onResume()
	{
		modPrimary = prefs.getBoolean("editPrimary", false);
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		Log.i(TAG, "Started MainActivity");

		actionBar = getActionBar();
		
		prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		modPrimary = prefs.getBoolean("editPrimary", false);
		
		setContentView(R.layout.main);
		mContext = this;
		listView = (ListView) findViewById(R.id.list);
		registerForContextMenu(listView);

		Log.i(TAG, "About to setup ListView");

		listView();

		final EditText input = (EditText) findViewById(R.id.userName);
		Button create = (Button) findViewById(R.id.create);
		create.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String name = input.getText().toString();
				TerminalUtils.createUser(name);
				listView();
			}

		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.remove:
			removeUser();
			break;
		case R.id.rename:
			renameUser();
			break;
		case R.id.switchto:
			switchToSelectedUser();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void switchToSelectedUser() {
		TerminalUtils.switchUser(userNumber);
	}

	public void renameUser() {

		if ((userNumber.equals("0")) && (modPrimary == false)) {
			Toast toast = Toast.makeText(getBaseContext(), "You are not allowed to rename the Primary user.",
					Toast.LENGTH_LONG);
			toast.show();
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Rename User");
			alert.setMessage("Enter the new name for " + userName);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					newName = input.getText().toString();
					TerminalUtils.renameUser(userNumber, newName);
					AlertDialog.Builder alert2 = new AlertDialog.Builder(mContext);

					alert2.setTitle("Reboot Required");
					alert2.setMessage("A reboot is required for the renaming to take effect. Would you like to reboot now?");

					alert2.setPositiveButton("Reboot", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							TerminalUtils.reboot();
						}

					});

					alert2.setNegativeButton("Later", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							Toast toast = Toast.makeText(mContext, "Reboot will be done manually", Toast.LENGTH_SHORT);
							toast.show();
						}
					});

					alert2.show();
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();
		}
	}

	public void removeUser() {
		if ((userNumber.equals("0"))) {
			final AlertDialog warning = new AlertDialog.Builder(MainActivity.this).create();
			warning.setTitle("Not Allowed");
			warning.setMessage("You are not allowed to delete the primary user. " +
					"Very very bad things happen when you do this, like your device being bricked. See the help section for details.");
			warning.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					warning.dismiss();
				}
			});
			warning.show();
		} else {
			AlertDialog confirmation = new AlertDialog.Builder(MainActivity.this).create();
			confirmation.setTitle("Confirm Delete");
			confirmation.setMessage("Are you sure you want to delete " + userName + "?\n THIS CANNOT BE UNDONE.");
			confirmation.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					TerminalUtils.deleteUser(userNumber);
					list.remove(pos);
					adapter.notifyDataSetChanged();
				}
			});
			confirmation.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Toast toast = Toast.makeText(getBaseContext(), "Deletion Canceled", Toast.LENGTH_SHORT);
					toast.show();
				}
			});
			confirmation.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.helpBar:
	        	Intent intent = new Intent(mContext, Help.class);
	        	startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void listView() {
		Log.i(TAG, "Started ListView");
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				userName = ((TextView) view).getText().toString();
				pos = position;
				int space = userName.indexOf(" ");

				userNumber = String.valueOf(userName.substring(0, space));
				userName = userName.substring(space + 1);

				openContextMenu(listView);
			}
		});
		Log.i(TAG, "Listener set");

		Back back = new Back();
		Void params = null;
		back.execute(params);
		
		Log.i(TAG, "Done with ListView");
	}

	private class Back extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute()
		{
			pd = ProgressDialog.show(mContext, "Populating", "Retrieving users... \nPlease Wait\n\nThis could take up to 2 minutes");
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			users = TerminalUtils.getUserList();
			Log.i(TAG, "First list retrieved");
			users = TerminalUtils.getUserList();
			Log.i(TAG, "Second list retrieved");

			Log.i(TAG, "Length: " + users.length);
			for (int ctr = 0; ctr < users.length; ctr++) {
				Log.i(TAG, "Formatting... Cycle X started");
				Log.i(TAG, users[ctr]);
				int bracket = users[ctr].indexOf("{");
				int firstColon = users[ctr].indexOf(":");
				String number = String.valueOf(users[ctr].substring(bracket + 1, firstColon));
				int lastColon = users[ctr].lastIndexOf(":");
				String name = users[ctr].substring(firstColon + 1, lastColon);
				users[ctr] = number + " " + name;
				Log.i(TAG, "Formatting... Cycle X complete");
			}
			Log.i(TAG, "Formatting done");

			list = new ArrayList<String>(Arrays.asList(users));
			Log.i(TAG, "List created");
			adapter = new ArrayAdapter<String>(mContext, R.layout.row, list);
			Log.i(TAG, "Adapter created");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			listView.setAdapter(adapter);
			Log.i(TAG, "Adapter set");
			pd.dismiss();
			return;
		}

	}

}