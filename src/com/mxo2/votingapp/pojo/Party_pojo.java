package com.mxo2.votingapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Party_pojo implements Parcelable {

	private String id = "";
	private String name = "";
	private String abbreviation = "";
	private String symbol = "";

	public Party_pojo(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Party_pojo(String id, String name, String abbreviation, String symbol) {
		super();
		this.id = id;
		this.name = name;
		this.abbreviation = abbreviation;
		this.symbol = symbol;
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

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(abbreviation);
		dest.writeString(symbol);

	}

	private void readFromParcel(Parcel in) {

		id = in.readString();
		name = in.readString();
		abbreviation = in.readString();
		symbol = in.readString();

	}

	public static final Parcelable.Creator<Party_pojo> CREATOR = new Parcelable.Creator<Party_pojo>() {

		@Override
		public Party_pojo createFromParcel(Parcel in) {
			return new Party_pojo(in);
		}

		@Override
		public Party_pojo[] newArray(int size) {
			return new Party_pojo[size];
		}

	};

}
