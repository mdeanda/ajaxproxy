package com.thedeanda.ajaxproxy.ui.options;

public class OptionValue {
	private String label;
	private int sliderValue;
	private int realValue;

	public OptionValue(String label, int sliderValue, int realValue) {
		this.label = label;
		this.sliderValue = sliderValue;
		this.realValue = realValue;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getSliderValue() {
		return sliderValue;
	}

	public void setSliderValue(int sliderValue) {
		this.sliderValue = sliderValue;
	}

	public int getRealValue() {
		return realValue;
	}

	public void setRealValue(int realValue) {
		this.realValue = realValue;
	}
}
