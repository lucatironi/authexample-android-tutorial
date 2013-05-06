package net.primegap.authexample;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.savagelook.android.UrlJsonAsyncTask;

public class HomeActivity extends SherlockActivity {

	private static final String TASKS_URL = "http://10.0.2.2:3000/api/v1/tasks.json";
	private static final String TOGGLE_TASKS_URL = "http://10.0.2.2:3000/api/v1/tasks/";
	private static final String LOGOUT_URL = "http://10.0.2.2:3000/api/v1/sessions.json";
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mPreferences.contains("AuthToken")) {
			loadTasksFromAPI(TASKS_URL);
		} else {
			Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_new_task:
			Intent intent = new Intent(HomeActivity.this, NewTaskActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.menu_refresh:
			loadTasksFromAPI(TASKS_URL);
			return true;
		case R.id.menu_logout:
			logoutFromAPI(LOGOUT_URL);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logoutFromAPI(String url) {
		LogoutTask logoutTask = new LogoutTask(HomeActivity.this);
		logoutTask.setMessageLoading("Loggin out...");
		logoutTask.execute(url);

	}

	private void loadTasksFromAPI(String url) {
		GetTasksTask getTasksTask = new GetTasksTask(HomeActivity.this);
		getTasksTask.setMessageLoading("Loading tasks...");
		getTasksTask.setAuthToken(mPreferences.getString("AuthToken", ""));
		getTasksTask.execute(url);
	}

	private void toggleTasksWithAPI(String url) {
		ToggleTaskTask completeTasksTask = new ToggleTaskTask(HomeActivity.this);
		completeTasksTask.setMessageLoading("Updating task...");
		completeTasksTask.execute(url);
	}

	private class TaskAdapter extends ArrayAdapter<Task> implements
			OnClickListener {

		private ArrayList<Task> items;
		private int layoutResourceId;

		public TaskAdapter(Context context, int layoutResourceId,
				ArrayList<Task> items) {
			super(context, layoutResourceId, items);
			this.layoutResourceId = layoutResourceId;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (CheckedTextView) layoutInflater.inflate(
						layoutResourceId, null);
			}
			Task task = items.get(position);
			if (task != null) {
				CheckedTextView taskCheckedTextView = (CheckedTextView) view
						.findViewById(android.R.id.text1);
				if (taskCheckedTextView != null) {
					taskCheckedTextView.setText(task.getTitle());
					taskCheckedTextView.setChecked(task.getCompleted());
					taskCheckedTextView.setOnClickListener(this);
				}
				view.setTag(task.getId());
			}
			return view;
		}

		@Override
		public void onClick(View view) {
			CheckedTextView taskCheckedTextView = (CheckedTextView) view
					.findViewById(android.R.id.text1);
			if (taskCheckedTextView.isChecked()) {
				taskCheckedTextView.setChecked(false);
				toggleTasksWithAPI(TOGGLE_TASKS_URL + view.getTag()
						+ "/open.json");
			} else {
				taskCheckedTextView.setChecked(true);
				toggleTasksWithAPI(TOGGLE_TASKS_URL + view.getTag()
						+ "/complete.json");
			}

		}
	}

	private class GetTasksTask extends UrlJsonAsyncTask {
		public GetTasksTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				JSONArray jsonTasks = json.getJSONObject("data").getJSONArray(
						"tasks");
				JSONObject jsonTask = new JSONObject();
				int length = jsonTasks.length();
				final ArrayList<Task> tasksArray = new ArrayList<Task>(length);

				for (int i = 0; i < length; i++) {
					jsonTask = jsonTasks.getJSONObject(i);
					tasksArray.add(new Task(jsonTask.getLong("id"), jsonTask
							.getString("title"), jsonTask
							.getBoolean("completed")));
				}

				ListView tasksListView = (ListView) findViewById(R.id.tasks_list_view);
				if (tasksListView != null) {
					tasksListView.setAdapter(new TaskAdapter(HomeActivity.this,
							android.R.layout.simple_list_item_checked,
							tasksArray));
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	private class ToggleTaskTask extends UrlJsonAsyncTask {
		public ToggleTaskTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPut put = new HttpPut(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					put.setHeader("Accept", "application/json");
					put.setHeader("Content-Type", "application/json");
					put.setHeader("Authorization", "Token token="
							+ mPreferences.getString("AuthToken", ""));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(put, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	private class LogoutTask extends UrlJsonAsyncTask {
		public LogoutTask(Context context) {
			super(context);
		}

		@Override
		protected JSONObject doInBackground(String... urls) {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpDelete delete = new HttpDelete(urls[0]);
			String response = null;
			JSONObject json = new JSONObject();

			try {
				try {
					json.put("success", false);
					json.put("info", "Something went wrong. Retry!");
					delete.setHeader("Accept", "application/json");
					delete.setHeader("Content-Type", "application/json");
					delete.setHeader("Authorization", "Token token="
							+ mPreferences.getString("AuthToken", ""));

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = client.execute(delete, responseHandler);
					json = new JSONObject(response);

				} catch (HttpResponseException e) {
					e.printStackTrace();
					Log.e("ClientProtocol", "" + e);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("IO", "" + e);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSON", "" + e);
			}

			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getBoolean("success")) {
					SharedPreferences.Editor editor = mPreferences.edit();
					editor.remove("AuthToken");
					editor.commit();

					Intent intent = new Intent(HomeActivity.this,
							WelcomeActivity.class);
					startActivityForResult(intent, 0);
				}
				Toast.makeText(context, json.getString("info"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
}
