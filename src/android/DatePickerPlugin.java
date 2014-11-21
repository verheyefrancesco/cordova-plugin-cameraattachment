/**
 * @author Bikas Vaibhav (http://bikasv.com) 2013
 * Rewrote the plug-in at https://github.com/phonegap/phonegap-plugins/tree/master/Android/DatePicker
 * It can now accept `min` and `max` dates for DatePicker.
 */

package com.checkroom.plugin.datepicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.checkroom.plugin.datepicker.DateTimePickerDialog.DateTimePickerCallback;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("NewApi")
public class DatePickerPlugin extends CordovaPlugin implements
		DateTimePickerCallback {
	private final String pluginName = "DatePickerPlugin";

	private static final String ARG_MODE = "mode";
	private static final String ARG_DATE = "date";
	private static final String ARG_MIN_DATE = "minDate";
	private static final String ARG_MAX_DATE = "maxDate";
	private static final String ARG_MINUTE_INTERVAL = "minuteInterval";
	private static final String ARG_POSITIVE_BUTTON_TEXT = "positiveButtonText";
	private static final String ARG_NEGATIVE_BUTTON_TEXT = "negativeButtonText";
	private static final String ARG_SET_DATE_TITLE = "setDateTitle";
	private static final String ARG_SET_TIME_TITLE = "setTimeTitle";

	private String mode = "date";
	private long initDate = -1;
	private long minDate = -1;
	private long maxDate = -1;
	private int minuteInterval = 1;
	private String positiveButtonText = "Set";
	private String negativeButtonText = "Cancel";
	private String setDateTitle = "Set date";
	private String setTimeTitle = "Set time";

	private DateTimePickerDialog dateTimePickerDialog;

	private CallbackContext callbackContext;

	// http://pastebin.com/K5stMFf9

	@Override
	public boolean execute(final String action, final JSONArray data,
			final CallbackContext callbackContext) {
		Log.d(pluginName, "DatePicker called with options: " + data);
		boolean result = false;

		this.show(data, callbackContext);

		this.callbackContext = callbackContext;

		result = true;

		return result;
	}

	public synchronized void show(final JSONArray data,
			final CallbackContext callbackContext) {
		readParametersFromData(data);
		showDateTimePicker();
	}

	private void readParametersFromData(JSONArray data) {
		try {
			JSONObject obj = data.getJSONObject(0);
			if (obj.has(ARG_MODE)) {
				mode = obj.getString(ARG_MODE);
			}
			if (obj.has(ARG_DATE)) {
				initDate = obj.getLong(ARG_DATE);
			}
			if (obj.has(ARG_MIN_DATE)) {
				minDate = obj.getLong(ARG_MIN_DATE);
			}
			if (obj.has(ARG_MAX_DATE)) {
				maxDate = obj.getLong(ARG_MAX_DATE);
			}
			if (obj.has(ARG_MINUTE_INTERVAL)) {
				minuteInterval = obj.getInt(ARG_MINUTE_INTERVAL);
			}
			if (obj.has(ARG_POSITIVE_BUTTON_TEXT)) {
				positiveButtonText = obj.getString(ARG_POSITIVE_BUTTON_TEXT);
			}
			if (obj.has(ARG_NEGATIVE_BUTTON_TEXT)) {
				negativeButtonText = obj.getString(ARG_NEGATIVE_BUTTON_TEXT);
			}
			if (obj.has(ARG_SET_DATE_TITLE)) {
				setDateTitle = obj.getString(ARG_SET_DATE_TITLE);
			}
			if (obj.has(ARG_SET_TIME_TITLE)) {
				setTimeTitle = obj.getString(ARG_SET_TIME_TITLE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private DateTimePickerConfig createPickerConfig() {
		DateTimePickerConfig config = new DateTimePickerConfig(mode, initDate,
				minDate, maxDate, minuteInterval, positiveButtonText,
				negativeButtonText, setDateTitle, setTimeTitle);
		return config;
	}

	private void showDateTimePicker() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				DateTimePickerConfig config = createPickerConfig();

				dateTimePickerDialog = new DateTimePickerDialog(
						cordova.getActivity(), config, DatePickerPlugin.this);
				dateTimePickerDialog.show();
			}
		};
		cordova.getActivity().runOnUiThread(runnable);
	}

	/* DateTimePickerCallback */
	@Override
	public void onCanceled() {
		dateTimePickerDialog.dismiss();
		callbackContext.success("cancel");
	}

	@Override
	public void onDateTimePicked(int day, int month, int year, int hour,
			int minute) {
		dateTimePickerDialog.dismiss();

		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
				Locale.getDefault());
		//sdf.setTimeZone(TimeZone.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String toReturn = sdf.format(calendar.getTime());

		callbackContext.success(toReturn);
	}
}
