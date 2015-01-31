package com.mxo2.votingapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Election_pojo implements Parcelable {

	private String id = "";
	private String name = "";
	private String type = "";
	private String state_id = "";
	private String description = "";
	private String election_date = "";
	private String create_date = "";
	private String status = "";

	public Election_pojo(Parcel in) {
		readFromParcel(in);
	}

	public Election_pojo(String id, String name, String type, String state_id,
			String description, String election_date, String create_date,
			String status) {

		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.state_id = state_id;
		this.description = description;
		this.election_date = election_date;
		this.create_date = create_date;
		this.status = status;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState_id() {
		return state_id;
	}

	public void setState_id(String state_id) {
		this.state_id = state_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getElection_date() {
		return election_date;
	}

	public void setElection_date(String election_date) {
		this.election_date = election_date;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeString(state_id);
		dest.writeString(description);
		dest.writeString(election_date);
		dest.writeString(create_date);
		dest.writeString(status);

	}

	private void readFromParcel(Parcel in) {

		id = in.readString();
		name = in.readString();
		type = in.readString();
		state_id = in.readString();
		description = in.readString();
		election_date = in.readString();
		create_date = in.readString();
		status = in.readString();

	}

	public static final Parcelable.Creator<Election_pojo> CREATOR = new Parcelable.Creator<Election_pojo>() {

		@Override
		public Election_pojo createFromParcel(Parcel in) {
			return new Election_pojo(in);
		}

		@Override
		public Election_pojo[] newArray(int size) {
			return new Election_pojo[size];
		}

	};

}
