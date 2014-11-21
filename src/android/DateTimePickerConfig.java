package com.checkroom.plugin.datepicker;

public class DateTimePickerConfig {

	private String mode;
	private long initDate = -1;
	private long minDate = -1;
	private long maxDate = -1;
	private int minuteInterval = 1;

	private String positiveButtonText = "Set";
	private String negativeButtonText = "Cancel";
	private String setDateTitle = "Set date";
	private String setTimeTitle = "Set time";

	public DateTimePickerConfig(String mode, long initDate, long minDate,
			long maxDate, int minuteInterval, String positiveButtonText,
			String negativeButtonText, String setDateTile, String setTimeTitle) {
		this.mode = mode;
		this.initDate = initDate;
		this.minDate = minDate;
		this.maxDate = maxDate;
		this.minuteInterval = minuteInterval;
		this.positiveButtonText = positiveButtonText;
		this.negativeButtonText = negativeButtonText;
		this.setDateTitle = setDateTile;
		this.setTimeTitle = setTimeTitle;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode){
		this.mode = mode;
	}
	
	public long getInitDate() {
		return initDate;
	}

	public long getMinDate() {
		return minDate;
	}

	public long getMaxDate() {
		return maxDate;
	}

	public int getMinuteInterval() {
		return minuteInterval;
	}

	public String getPositiveButtonText() {
		return positiveButtonText;
	}

	public String getNegativeButtonText() {
		return negativeButtonText;
	}

	public String getSetDateTitle() {
		return setDateTitle;
	}

	public String getSetTimeTitle() {
		return setTimeTitle;
	}

}
