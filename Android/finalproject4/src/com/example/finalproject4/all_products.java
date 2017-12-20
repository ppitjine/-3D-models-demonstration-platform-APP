package com.example.finalproject4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.EditProductActivity;
import com.example.androidhive.R;
import com.example.androidhive.AllProductsActivity.LoadAllProducts;
import com.example.finalproject4.JSONParser;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class all_products  extends ListActivity{
	
	private ProgressDialog pDialog;
	
		
	// Creating JSON Parser object
		JSONParser jParser = new JSONParser();

		ArrayList<HashMap<String, String>> productsList;

		// url to get all products list
		private static String url_all_products = "http://163.21.245.135/phpconnect/get_all_products.php";

		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MODELS = "models";
		private static final String TAG_PID = "pid";
		private static final String TAG_NAME = "name";
		private static final String TAG_PICADDRESS = "picaddress";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_DESCRIPTION = "description";
		private static final String TAG_EDITOR = "editor";


		// products JSONArray
		JSONArray products = null;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.list);
			
			// Hashmap for ListView
			productsList = new ArrayList<HashMap<String, String>>();

			// Loading products in Background Thread
			new LoadAllProducts().execute();

			// Get listview
			ListView lv = getListView();

			// on seleting single product
			// launching Edit Product Screen
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// getting values from selected ListItem
					String pid = ((TextView) view.findViewById(R.id.pid)).getText()
							.toString();
					
					/*
					 * 判斷是否有下載 
					 * 
					 * 若沒有就先下載
					 * 
					 * 有就直接開啟
					 * 
					 */
					
					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							EditProductActivity.class);
					// sending pid to next activity
					in.putExtra(TAG_PID, pid);
					
					// starting new activity and expecting some response back
					startActivityForResult(in, 100);
				}
			});
			
		}

		
		
		
		
		
		
		
		class LoadAllProducts extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(all_products.this);
				pDialog.setMessage("Loading products. Please wait...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}

			/**
			 * getting All products from url
			 * */
			protected String doInBackground(String... args) {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
				
				// Check your log cat for JSON reponse
				Log.d("All Models: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						products = json.getJSONArray(TAG_MODELS);

						// looping through All Products
						for (int i = 0; i < products.length(); i++) {
							JSONObject c = products.getJSONObject(i);

							// Storing each json item in variable
							String id = c.getString(TAG_PID);
							String name = c.getString(TAG_NAME);
							String picaddress = c.getString(TAG_PICADDRESS);
							String location = c.getString(TAG_LOCATION);
							String description = c.getString(TAG_DESCRIPTION);
							String editor = c.getString(TAG_EDITOR);

							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							map.put(TAG_PID, id);
							map.put(TAG_NAME, name);
							map.put(TAG_PICADDRESS, picaddress);
							map.put(TAG_LOCATION, location);
							map.put(TAG_DESCRIPTION, description);
							map.put(TAG_EDITOR, editor);

							// adding HashList to ArrayList
							productsList.add(map);
						}
					} /*else {
						// no products found
						// Launch Add New product Activity
						Intent i = new Intent(getApplicationContext(),
								NewProductActivity.class);
						// Closing all previous activities
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}*/
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * After completing background task Dismiss the progress dialog
			 * **/
			protected void onPostExecute(String file_url) {
				// dismiss the dialog after getting all products
				pDialog.dismiss();
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						ListAdapter adapter = new SimpleAdapter(
								AllProductsActivity.this, productsList,
								R.layout.list_item, new String[] { TAG_PID,
										TAG_NAME},
								new int[] { R.id.ppid, R.id.nname });
						// updating listview
						setListAdapter(adapter);
					}
				});

			}

		}
}
