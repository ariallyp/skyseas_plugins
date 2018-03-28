package com.skyseas.openfireplugins.chatlogs;

import org.jivesoftware.util.Blowfish;

public class Test2 {
	static Blowfish blowfish = new Blowfish("8ab79ad8fb5e3d45597848db9a2cdea5bc8cec7a8fcf54fbfbcf9fe0d1d9471b");
	public static void main(String[] args) {
		System.out.println(blowfish.decryptString("xxxxxxxxxxxxxxxxxxxx"));
	}
}
