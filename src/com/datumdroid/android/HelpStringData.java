package com.datumdroid.android;

public class HelpStringData {

	public String[] video_titles;
	public String[] video_thumbnails;
	public String[] video_urls;

	public String[] picture_urls;
	public String[] picture_thumbnails;

	public String[] guardian_titles;
	public String[] guardian_urls;

	public String[] twitter_texts;
	public String[] twitter_thumbnails;

	public String[] feedzilla_titles;
	public String[] feedzilla_urls;

	public HelpStringData(String[] vt, String[] vth, String[] vu, String[] pu,
			String[] pth, String[] gt, String[] gu, String[] tt, String[] tth,
			String[] ft, String[] fu) {

		video_titles = vt;
		video_thumbnails = vth;
		video_urls = vu;

		picture_urls = pu;
		picture_thumbnails = pth;

		guardian_titles = gt;
		guardian_urls = gu;

		twitter_texts = tt;
		twitter_thumbnails = tth;

		feedzilla_titles = ft;
		feedzilla_urls = fu;

	}

	public HelpStringData(HelpStringData h) {
		video_titles = h.video_titles;
		video_thumbnails = h.video_thumbnails;
		video_urls = h.video_urls;

		picture_urls = h.picture_urls;
		picture_thumbnails = h.picture_thumbnails;

		guardian_titles = h.guardian_titles;
		guardian_urls = h.guardian_urls;

		twitter_texts = h.twitter_texts;
		twitter_thumbnails = h.twitter_thumbnails;

		feedzilla_titles = h.feedzilla_titles;
		feedzilla_urls = h.feedzilla_urls;

	}

}
