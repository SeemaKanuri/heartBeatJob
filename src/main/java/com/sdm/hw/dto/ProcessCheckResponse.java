package com.sdm.hw.dto;

public class ProcessCheckResponse 
{
	private boolean isInstanceCountOne;
	private boolean isInstanceCountMoreThanOne;
	private boolean isInstanceCountZero;
	public boolean isInstanceCountOne() {
		return isInstanceCountOne;
	}
	public void setInstanceCountOne(boolean isInstanceCountOne) {
		this.isInstanceCountOne = isInstanceCountOne;
	}
	public boolean isInstanceCountMoreThanOne() {
		return isInstanceCountMoreThanOne;
	}
	public void setInstanceCountMoreThanOne(boolean isInstanceCountMoreThanOne) {
		this.isInstanceCountMoreThanOne = isInstanceCountMoreThanOne;
	}
	public boolean isInstanceCountZero() {
		return isInstanceCountZero;
	}
	public void setInstanceCountZero(boolean isInstanceCountZero) {
		this.isInstanceCountZero = isInstanceCountZero;
	}

}
