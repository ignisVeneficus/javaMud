package org.ignis.javaMud.messages;

import java.util.Arrays;

public class Event {
	protected String[] text;
	
	public Event() {
		text = new String[0];
	}

	public String[] getText() {
		return text;
	}

	public void setText(String[] text) {
		this.text = text;
	}
	public void addText(String newLine) {
		this.text = Arrays.copyOf(this.text, this.text.length+1);
		this.text[this.text.length-1] = newLine;
	}
	public void setText(String oneline) {
		this.text = oneline.split("\\\n",-1);
	}
}
