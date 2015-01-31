package com.mxo2.votingapp.db;

import android.provider.BaseColumns;

public class DatabaseSchema {

	public static final String DB_NAME = "ivote.db";
	public static final int DATABASE_VERSION = 5;
	public static final String SORT_ASC = " ASC";
	public static final String SORT_DESC = " DESC";
	public static final String[] ORDERS = { SORT_ASC, SORT_DESC };
	public static final char SYNC_NEW = 'N', SYNC_OLD = 'O', SYNC_UPDATE = 'U',
			SYNC_DELETE = 'D';
	public static final int OFF = 0;
	public static final int ON = 1;

	public static final class ELECTION_MASTER implements BaseColumns {

		public static final String TABLE_NAME = "election";

		// columns
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String TYPE = "type";
		public static final String STATE_ID = "state_id";
		public static final String DESCRIPTION = "desc";
		public static final String ELECTION_DATE = "ele_date";
		public static final String CREATE_DATE = "create_date";
		public static final String STATUS = "status";

		// create table query
		public static final String CREATE_TABLE = "create table " + TABLE_NAME
				+ "( " + ID + " INTEGER primary key autoincrement, " + NAME
				+ " TEXT, " + TYPE + " TEXT, " + STATE_ID + " TEXT, "
				+ DESCRIPTION + " TEXT, " + ELECTION_DATE + " DATETIME, "
				+ CREATE_DATE + " INTEGER, " + STATUS + " INTEGER " + ");";

		// delete table query
		public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME
				+ ";";
		public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}

	public static final class DISTRICT_MASTER implements BaseColumns {

		public static final String TABLE_NAME = "district";

		// columns
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String STATE_ID = "state_id";

		// create table query
		public static final String CREATE_TABLE = "create table " + TABLE_NAME
				+ "( " + ID + " INTEGER primary key autoincrement, " + NAME
				+ " TEXT, " + STATE_ID + " INTEGER " + ");";

		// delete table query
		public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME
				+ ";";
		public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}

	public static final class CONSTITUENCY_MASTER implements BaseColumns {

		public static final String TABLE_NAME = "constituency";

		// columns
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String STATE_ID = "state_id";
		public static final String DISTRICT_ID = "district_id";

		// create table query
		public static final String CREATE_TABLE = "create table " + TABLE_NAME
				+ "( " + ID + " INTEGER primary key autoincrement, " + NAME
				+ " TEXT, " + STATE_ID + " INTEGER, " + DISTRICT_ID
				+ " INTEGER " + ");";

		// delete table query
		public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME
				+ ";";
		public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}

	public static final class PARTY_MASTER implements BaseColumns {

		public static final String TABLE_NAME = "party";

		// columns
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String ABBREVIATION = "abbreviation";
		public static final String SYMBOL = "party_symbol";

		// create table query
		public static final String CREATE_TABLE = "create table " + TABLE_NAME
				+ "( " + ID + " INTEGER primary key autoincrement, " + NAME
				+ " TEXT, " + ABBREVIATION + " TEXT, " + SYMBOL + " TEXT "
				+ ");";

		// delete table query
		public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME
				+ ";";
		public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}
	public static final class CONSTITUENCY_RESULT_MASTER implements BaseColumns {

		public static final String TABLE_NAME = "constituency_result";

		// columns
		public static final String ID = "id";
		public static final String CONSTNAME = "cname";
		public static final String NAME = "pname";
		public static final String ABBREVIATION = "pabbr";
		public static final String SYMBOL = "picon";
		public static final String TOTAL_VOTE = "total";

		// create table query
		public static final String CREATE_TABLE = "create table " + TABLE_NAME
				+ "( " + ID + " INTEGER primary key autoincrement, " + NAME
				+ " TEXT, " + ABBREVIATION + " TEXT, " + SYMBOL + " TEXT "
				+ ");";

		// delete table query
		public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME
				+ ";";
		public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

	}

}