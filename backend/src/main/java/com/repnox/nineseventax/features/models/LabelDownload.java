package com.repnox.nineseventax.features.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabelDownload{
	@JsonProperty(value = "href")
	private String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}