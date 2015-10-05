package paltakis.george.DYL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents an item in a ToDo list
 */
public class ToDoItem {

	/**
	 * Item Minutes of Aerobic Activity
	 */
	@com.google.gson.annotations.SerializedName("minaerobicactivity")
	private int mMinAerobicActivity;

	/**
	 * Item Number of wine glasses
	 */
	@com.google.gson.annotations.SerializedName("wineglasses")
	private int mWineGlasses;


	/**
	 * Item sleepHours
	 */
	@com.google.gson.annotations.SerializedName("sleephours")
	private int mSleepHours;

	/**
	 * Item dateToday
	 */
	@com.google.gson.annotations.SerializedName("datetoday")
	private Date mDateToday;

	/**
	 * Item Id
	 */
	@com.google.gson.annotations.SerializedName("id")
	private String mId;

	/**
	 * Indicates if the item is completed
	 */
	@com.google.gson.annotations.SerializedName("complete")
	private boolean mComplete;

	/**
	 * ToDoItem constructor
	 */
	public ToDoItem() {

	}

	@Override
	public String toString() {
		return String.valueOf(getMinAerobicActivity());
	}

	/**
	 * Initializes a new ToDoItem
	 * 
	 * @param text
	 *            The item text
	 * @param id
	 *            The item id
	 */
	public ToDoItem(int	intMinAerobicActivity, int intWineGlasses, int sleepHours, Date dateToday, String id) {
		this.setMinAerobicActivity(intMinAerobicActivity);
		this.setWineGlasses(intWineGlasses);
		this.setSleepHours(sleepHours);
		this.setDateToday(dateToday);
		this.setId(id);
	}

	/**
	 * Returns the item minutes of aerobic activity
	 */
	public int getMinAerobicActivity() {
		return mMinAerobicActivity;
	}

	/**
	 * Returns the item number of wine glasses
	 */
	public int getWineGlasses() {
		return mWineGlasses;
	}

	/**
	 * Returns the item sleepHours
	 */
	public Integer getSleepHours() {
		return mSleepHours;
	}

	/**
	 * Returns the item dateToday
	 */
	public Date getDateToday() { return mDateToday; }

	/**
	 * Returns the item dateToday in a String format
	 */
	public String getDateTodayString() {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String stringDateToday = formatter.format(mDateToday);
		return stringDateToday;
	}


	/**
	 * Sets the item minutes of aerobic activity
	 * 
	 * @param text
	 *            text to set
	 */
	public final void setMinAerobicActivity(int minAerobicActivity) {
		mMinAerobicActivity = minAerobicActivity;
	}

	/**
	 * Sets the item number of wine glasses
	 *
	 * @param text
	 *            text to set
	 */
	public final void setWineGlasses(int wineGlasses) {
		mWineGlasses = wineGlasses;
	}

	/**
	 * Sets the item sleepHours
	 *
	 * @param sleepHours
	 *            int to set
	 */
	public final void setSleepHours(int sleepHours) { mSleepHours = sleepHours;	}

	/**
	 * Sets the item dateToday
	 *
	 * @param dateToday
	 *            Date to set
	 */
	public final void setDateToday(Date dateToday) { mDateToday = dateToday;}


	/**
	 * Returns the item id
	 */
	public String getId() {
		return mId;
	}

	/**
	 * Sets the item id
	 * 
	 * @param id
	 *            id to set
	 */
	public final void setId(String id) {
		mId = id;
	}

	/**
	 * Indicates if the item is marked as completed
	 */
	public boolean isComplete() {
		return mComplete;
	}

	/**
	 * Marks the item as completed or incompleted
	 */
	public void setComplete(boolean complete) {
		mComplete = complete;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ToDoItem && ((ToDoItem) o).mMinAerobicActivity == mMinAerobicActivity
				&& ((ToDoItem) o).mWineGlasses == mWineGlasses
				&& ((ToDoItem) o).mSleepHours == mSleepHours
				&& ((ToDoItem) o).mDateToday == mDateToday;

	}


}
