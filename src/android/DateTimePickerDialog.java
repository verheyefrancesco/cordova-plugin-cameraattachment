package com.checkroom.plugin.datepicker;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class DateTimePickerDialog extends Dialog implements
		android.view.View.OnClickListener {
	// UI
	private LinearLayout llDatePickerContainer;
	private LinearLayout llTimePickerContainer;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button btnPositive;
	private Button btnNegative;

	private static final String MODE_DATE = "date";
	private static final String MODE_TIME = "time";
	private static final String MODE_DATETIME = "datetime";

	private DateTimePickerConfig mConfig;
	private DateTimePickerCallback mCallback;

	private int pickedDay = -1;
	private int pickedMonth = -1;
	private int pickedYear = -1;
	private int pickedHour = -1;
	private int pickedMinute = -1;

	private String[] minuteValuesForTimePicker;

	private Activity mContext;

	public DateTimePickerDialog(Activity a, DateTimePickerConfig config,
			DateTimePickerCallback callback) {
		super(a);
		mContext = a;
		mConfig = config;
		mCallback = callback;

		setupPickedValuesFromNow();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getIdFromProjectsRFile(RESOURCE_TYPE_LAYOUT,
				"dialog_date_time"));
		setWidgets();
		updateUIForCurrentType();
	}

	private void setWidgets() {
		llDatePickerContainer = (LinearLayout) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "dtpdDateContainer"));
		llTimePickerContainer = (LinearLayout) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "dtpdTimeContainer"));
		datePicker = (DatePicker) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "dtpdDatePicker"));
		timePicker = (TimePicker) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "dtpdTimePicker"));
		btnPositive = (Button) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "btnPositiveButton"));
		btnNegative = (Button) findViewById(getIdFromProjectsRFile(
				RESOURCE_TYPE_ID, "btnNegativeButton"));

		setActionButtons();
	}

	private void setActionButtons() {
		btnPositive.setOnClickListener(this);
		btnNegative.setOnClickListener(this);

		btnPositive.setText(mConfig.getPositiveButtonText());
		btnNegative.setText(mConfig.getNegativeButtonText());
	}

	/* ActionButtons onClick */
	@Override
	public void onClick(View v) {
		if (v.getId() == getIdFromProjectsRFile(RESOURCE_TYPE_ID,
				"btnPositiveButton")) {
			positiveButtonClicked();
		} else if (v.getId() == getIdFromProjectsRFile(RESOURCE_TYPE_ID,
				"btnNegativeButton")) {
			negativeButtonClicked();
		}
	}

	private void updateUIForCurrentType() {
		if (mConfig.getMode().equals(MODE_TIME)) {
			showTimeUI();
			setTitle(mConfig.getSetTimeTitle());
		} else {
			showDateUI();
			setTitle(mConfig.getSetDateTitle());
		}
	}

	/* Date picker */
	private void showDateUI() {
		llDatePickerContainer.setVisibility(View.VISIBLE);
		llTimePickerContainer.setVisibility(View.GONE);

		setInitDateOnDatePicker();
		setMinAndMaxDates();
	}

	private void setInitDateOnDatePicker() {
		Calendar c = Calendar.getInstance();
		if (mConfig.getInitDate() != -1) {
			c.setTimeInMillis(mConfig.getInitDate());
		}
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		datePicker.updateDate(year, month, day);
	}

	private void setMinAndMaxDates() {
		if (mConfig.getMinDate() != -1) {
			datePicker.setMinDate(mConfig.getMinDate());
		}
		if (mConfig.getMaxDate() != -1) {
			datePicker.setMaxDate(mConfig.getMaxDate());
		}
	}

	/* Time picker */
	private void showTimeUI() {
		llDatePickerContainer.setVisibility(View.GONE);
		llTimePickerContainer.setVisibility(View.VISIBLE);

		setInitTimeForTimePicker();
		generateMinuteIntervals();
		customizePickerForMinuteIntervals();
	}

	private void setInitTimeForTimePicker() {
		Calendar c = Calendar.getInstance();
		if (mConfig.getInitDate() != -1) {
			c.setTimeInMillis(mConfig.getInitDate());
		}
		timePicker.setCurrentHour(c.get(Calendar.HOUR));
		timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
	}

	private void customizePickerForMinuteIntervals() {
		if (minuteValuesForTimePicker != null) {
			NumberPicker minutePicker = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				minutePicker = (NumberPicker) ((ViewGroup) ((ViewGroup) timePicker
						.getChildAt(0)).getChildAt(0)).getChildAt(2);
			} else {
				minutePicker = (NumberPicker) ((ViewGroup) timePicker
						.getChildAt(0)).getChildAt(2);
			}

			minutePicker.setMinValue(0);
			minutePicker.setMaxValue(minuteValuesForTimePicker.length - 1);
			minutePicker.setDisplayedValues(minuteValuesForTimePicker);
		}
	}

	private void generateMinuteIntervals() {
		// Generate display values array
		int interval = mConfig.getMinuteInterval();
		int count = 60 / interval;
		int value = 0;
		minuteValuesForTimePicker = new String[count];
		for (int i = 0; i < count; i++) {
			if (value < 10) {
				minuteValuesForTimePicker[i] = "0" + String.valueOf(value);
			} else {
				minuteValuesForTimePicker[i] = String.valueOf(value);
			}
			value += interval;
		}
	}

	private void positiveButtonClicked() {
		setupPickedValuesFromPicker();

		if (mConfig.getMode().equals(MODE_DATETIME)) {
			mConfig.setMode(MODE_TIME);
			updateUIForCurrentType();
		} else {
			mCallback.onDateTimePicked(pickedDay, pickedMonth, pickedYear,
					pickedHour, pickedMinute);
		}
	}

	private void setupPickedValuesFromNow() {
		Calendar c = Calendar.getInstance();
		pickedDay = c.get(Calendar.DAY_OF_MONTH);
		pickedMonth = c.get(Calendar.MONTH);
		pickedYear = c.get(Calendar.YEAR);
		pickedHour = c.get(Calendar.HOUR_OF_DAY);
		pickedMinute = c.get(Calendar.MINUTE);
	}

	private void setupPickedValuesFromPicker() {
		if (mConfig.getMode().equals(MODE_TIME)) {
			// set the hour - minute
			pickedHour = timePicker.getCurrentHour();
			pickedMinute = timePicker.getCurrentMinute();
			if (minuteValuesForTimePicker != null) {
				pickedMinute = Integer
						.parseInt(minuteValuesForTimePicker[pickedMinute]);
			}
		} else {
			// set the day - month - year
			pickedDay = datePicker.getDayOfMonth();
			pickedMonth = datePicker.getMonth();
			pickedYear = datePicker.getYear();
		}
	}

	private void negativeButtonClicked() {
		mCallback.onCanceled();
	}

	/* Callback interface */
	public interface DateTimePickerCallback {
		public void onDateTimePicked(int day, int month, int year, int hour,
				int minute);

		public void onCanceled();
	}

	/*
	 * R.java util
	 */

	private static final String RESOURCE_TYPE_LAYOUT = "layout";
	private static final String RESOURCE_TYPE_ID = "id";
	private String packageName;

	private int getIdFromProjectsRFile(String resourceType, String resourceId) {
		if (packageName == null) {
			try {
				PackageManager pm = mContext.getPackageManager();
				PackageInfo packageInfo = pm.getPackageInfo(
						mContext.getPackageName(), 0);
				packageName = packageInfo.packageName;
			} catch (NameNotFoundException e) {
			}
		}
		Resources resources = mContext.getApplicationContext().getResources();
		return resources.getIdentifier(resourceId, resourceType, packageName);
	}
}