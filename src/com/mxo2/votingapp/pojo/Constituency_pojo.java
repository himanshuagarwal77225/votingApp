package com.mxo2.votingapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Constituency_pojo implements Parcelable {

	private String id = "";
	private String name = "";
	private String state_id = "";
	private String district_id = "";

	public Constituency_pojo(Parcel in) {
		readFromParcel(in);
	}

	public Constituency_pojo(String id, String name, String state_id,
			String district_id) {

		super();
		this.id = id;
		this.name = name;
		this.state_id = state_id;
		this.district_id = district_id;

	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState_id() {
		return state_id;
	}

	public void setState_id(String state_id) {
		this.state_id = state_id;
	}

	public String getDistrict_id() {
		return district_id;
	}

	public void setDistrict_id(String district_id) {
		this.district_id = district_id;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(state_id);
		dest.writeString(district_id);

	}

	private void readFromParcel(Parcel in) {

		id = in.readString();
		name = in.readString();
		state_id = in.readString();
		district_id = in.readString();

	}

	public static final Parcelable.Creator<Constituency_pojo> CREATOR = new Parcelable.Creator<Constituency_pojo>() {

		@Override
		public Constituency_pojo createFromParcel(Parcel in) {
			return new Constituency_pojo(in);
		}

		@Override
		public Constituency_pojo[] newArray(int size) {
			return new Constituency_pojo[size];
		}

	};

}
