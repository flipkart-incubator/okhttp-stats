package com.flipkart.fk_android_flipperf.models;

import com.google.mygson.JsonElement;
import com.google.mygson.annotations.SerializedName;

public class PerfContext implements Cloneable
{
	@SerializedName("contextGlobal")
	public String contextGlobal;
	
	@SerializedName("contextGlobalId")
	public Long contextGlobalId;

	@SerializedName("contextLocal")
	public String contextLocal;

	@SerializedName("contextLocalId")
	public Long contextLocalId;

	@SerializedName("info")
	public JsonElement info;
	
	public void resetContext()
	{
		contextGlobal = null;
		contextGlobalId = null;
		contextLocal = null;
		contextLocalId = null;
		info = null;
	}
	
	public PerfContext copy()
	{
		try {
			return (PerfContext) this.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}