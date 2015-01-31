package com.mxo2.votingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String LOG_TAG = "DatabaseHelper";
	private SQLiteDatabase MDb;

	private final int OPERATION_SUCCESS = 1;
	private final int OPERATION_FAIL = -1;
	// private final int UNIQUE_FIELD_UNDEFINED = 2;
	private final int DUPLICATE_ENTRY = 3;

	// private final int INVALID_USER = -2;

	public DatabaseHelper(Context context) {

		super(context, DatabaseSchema.DB_NAME, null,
				DatabaseSchema.DATABASE_VERSION);
		Log.v("db helper", "============start=========");

	}

	public void open() {
		try {
			MDb = getWritableDatabase();
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
	}

	public void close() {
		try {
			if (MDb != null) {
				MDb.close();
				MDb = null;
			}
			super.close();
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase sqlDB) {
		try {
			Log.v("on create", "============start=========");
			CreateEmptyDB(sqlDB);
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			Log.v("on upgrade", "============start=========");
			dropTables(db);
			onCreate(db);
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
	}

	private void dropTables(SQLiteDatabase sqlDB) {

		Log.v("drop tables", "============start=========");
		sqlDB.execSQL(DatabaseSchema.ELECTION_MASTER.DROP_TABLE);
		sqlDB.execSQL(DatabaseSchema.DISTRICT_MASTER.DROP_TABLE);
		sqlDB.execSQL(DatabaseSchema.CONSTITUENCY_MASTER.DROP_TABLE);
		sqlDB.execSQL(DatabaseSchema.PARTY_MASTER.DROP_TABLE);
	}

	private void CreateEmptyDB(SQLiteDatabase sqlDB) {
		try {
			Log.v("create empty db", "============start=========");
			sqlDB.execSQL(DatabaseSchema.ELECTION_MASTER.CREATE_TABLE);
			sqlDB.execSQL(DatabaseSchema.DISTRICT_MASTER.CREATE_TABLE);
			sqlDB.execSQL(DatabaseSchema.CONSTITUENCY_MASTER.CREATE_TABLE);
			sqlDB.execSQL(DatabaseSchema.PARTY_MASTER.CREATE_TABLE);
			Log.v(DatabaseHelper.LOG_TAG, "Table Creation finish");

		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
	}

	public void DeleteAllTables() {
		try {
			open();
			if (MDb != null) {
				MDb.delete(DatabaseSchema.ELECTION_MASTER.TABLE_NAME, null,
						null);
				MDb.delete(DatabaseSchema.DISTRICT_MASTER.TABLE_NAME, null,
						null);
				MDb.delete(DatabaseSchema.CONSTITUENCY_MASTER.TABLE_NAME, null,
						null);
				MDb.delete(DatabaseSchema.PARTY_MASTER.TABLE_NAME, null, null);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		} finally {
			if (MDb != null) {
				MDb.close();
			}
		}
	}

	public void deleteAllRecords(String tableName) {

		try {
			if (!tableName.isEmpty()) {
				MDb.delete(tableName, "1", null);
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}

	}

	/** ADD DETAIL IN DATABASE **/
	public void addDetailInDb(String tableName, ContentValues values,
			String uniquefield) {

		try {
			long itemId = MDb.insertOrThrow(tableName, null, values);
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}

	}

	private int recordExistsInDb(String tableName, String uniquefield,
			String value) {

		int recordCount = 0;
		try {
			Cursor cursor = null;
			String query = "select " + uniquefield + " from " + tableName
					+ " where " + uniquefield + " = ?";
			cursor = MDb.rawQuery(query, new String[] { value });
			recordCount = cursor.getCount();
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		}
		return recordCount;
	}

	/** EDIT DETAIL IN DATABASE **/
	public Boolean updateDetailInDb(String tableName, ContentValues values,
			String where, String whereparam, String uniquefield) {

		try {
			if (!TextUtils.isEmpty(uniquefield)) {
				String uniquefield_value = values.getAsString(uniquefield);
				if ((recordExistsInDb(tableName, uniquefield, uniquefield_value)) > 0) {
					return false;
				}
			}
			int isUpdated = MDb.update(tableName, values, where + " = ? ",
					new String[] { whereparam });
			if (isUpdated > 0) {
				return true;
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
			return false;
		}
		return true;
	}

	/** FETCH LIST FROM REQUIRED DATABASE TABLE **/
	public String fetchRequiredList(int Type, String Table,
			String[] projection, String selWhere, String[] selArguments) {

		String response = "{\"result\":\"failed\"}";
		Cursor cursor = null;
		try {
			cursor = MDb.query(Table, projection, selWhere, selArguments, null,
					null, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.move(0);
					response = "";
					response += "{\"result\":\"success\", \"msg\":[";
					boolean isFirst = true;

					while (cursor.moveToNext()) {

						if (!isFirst) {
							response += ",";
						}
						response += "{";

						switch (Type) {

						case AppConstants.ELECTION: {

						}
							break;

						case AppConstants.DISTRICT: {
							response += "\""
									+ DatabaseSchema.DISTRICT_MASTER.ID
									+ "\":\""
									+ cursor.getInt(cursor
											.getColumnIndex(DatabaseSchema.DISTRICT_MASTER.ID))
									+ "\",";
							response += "\""
									+ DatabaseSchema.DISTRICT_MASTER.NAME
									+ "\":\""
									+ cursor.getString(cursor
											.getColumnIndex(DatabaseSchema.DISTRICT_MASTER.NAME))
									+ "\"";
						}
							break;

						case AppConstants.CONSTITUENCY: {
							response += "\""
									+ DatabaseSchema.CONSTITUENCY_MASTER.ID
									+ "\":\""
									+ cursor.getInt(cursor
											.getColumnIndex(DatabaseSchema.CONSTITUENCY_MASTER.ID))
									+ "\",";
							response += "\""
									+ DatabaseSchema.CONSTITUENCY_MASTER.NAME
									+ "\":\""
									+ cursor.getString(cursor
											.getColumnIndex(DatabaseSchema.CONSTITUENCY_MASTER.NAME))
									+ "\"";
						}
							break;
						default:
							break;
						}
						response += "}";
						isFirst = false;
					}
					response += "]" + "}";
				}
			}
		} catch (Exception e) {
			Log.e("Exception ==> ", e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return response;
	}

}
