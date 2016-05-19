package com.flipkart.flipperf.oldlib;


public class FlipperfTag {
	String tagName;

	public String getTagName() {
		return tagName;
	}
	
	private FlipperfTag(String name) {
		tagName = name;
	}
	
	public static final FlipperfTag defaultTag = new FlipperfTag("custom");
	public static final FlipperfTag internalTag = new FlipperfTag("fp");
	public static FlipperfTag appInitTag = new FlipperfTag("appInit");
	public static FlipperfTag tableCellTag = new FlipperfTag("cell");
	public static FlipperfTag connTag = new FlipperfTag("conn");

//	static FlipperfTag createTagWithName(String name)
//	{
//		return newlib FlipperfTag(name);
//	}
	
	public FlipperfTag createChildTagWithName(String name)
	{
		return new FlipperfTag(this.tagName+"|"+name);
	}
}
