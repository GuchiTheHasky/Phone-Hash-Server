package org.the.husky;

import java.text.DecimalFormat;
import java.util.Iterator;

public class PhoneNumberGenerator implements Iterator<String> {
    private final String prefix;  private final int max;  private int current;
    public PhoneNumberGenerator(String prefix,int max){this.prefix=prefix;this.max=max;}
    public boolean hasNext(){return current<max;}
    public String next(){
        return prefix+String.format("%07d",current++);
    }
}
