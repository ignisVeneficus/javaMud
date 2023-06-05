package org.ignis.javaMud.Mud.Core;

public class ReturnType {
	protected boolean success;
	protected String failedString;
	
	private ReturnType(boolean success,String failedString) {
		this.success = success;
		this.failedString = failedString;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getFailedString() {
		return failedString;
	}
	static final public ReturnType success() {
		return new ReturnType(true, null);
	}
	static final public ReturnType failed() {
		return new ReturnType(false, null);
	}
	static final public ReturnType failed(String msg) {
		return new ReturnType(false, msg);
	}
	
}
