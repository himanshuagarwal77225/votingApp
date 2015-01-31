package com.mxo2.votingapp.utils;

import java.io.File;

import com.mxo2.votingapp.MainAppClass;

/**
 * This class holds all static constant variables being used throughout the
 * application. All such variables and constants, whenever required, should be
 * placed in this class and used herefrom.
 **/

public class AppConstants {

	public static final String APP_PACKAGE_NAME = "com.physivert.votingapp";
	public static final String APP_FOLDER = "iVote";
	public static final String APP_IMAGES_FOLDER = "iVote_Images";
	public static final String APP_FILES_FOLDER = "iVote_Files";
	public static String APP_Images_directory = MainAppClass.APP_directory
			+ File.separator + AppConstants.APP_IMAGES_FOLDER;
	public static String APP_Files_directory = MainAppClass.APP_directory
			+ File.separator + AppConstants.APP_FILES_FOLDER;
	public static final String FILE_TIMESTAMP = AppUtils.currentTimeSecs()
			+ "_";
	// public static final String app_url = "http://dev.mxo2.com/ivote/";
	public static final String app_url = "http://ivote.mxo2.com/";

	public static final int SMALL_DEVICE = 1;
	public static final int NORMAL_DEVICE = 2;
	public static final int LARGE_DEVICE = 3;
	public static final int XLARGE_DEVICE = 4;

	public static final int NETWORK_STATE_AVAILABLE = 11;
	public static final int NETWORK_STATE_UNAVAILABLE = 12;

	public static final int ELECTION = 1;
	public static final int STATE = 2;
	public static final int DISTRICT = 3;
	public static final int CONSTITUENCY = 4;

	public static class ActionKeys {
		public static final String emailid = "emailid";
		public static final String password = "password";
		public static final String firstname = "firstname";
		public static final String lastname = "lastname";
		public static final String mobile = "mobile";
		public static final String address = "address";
		public static final String voterid = "voterid";
		public static final String pancard = "pancard";
		public static final String dob = "dob";
		public static final String verification_code = "verification_code";

	}

}
