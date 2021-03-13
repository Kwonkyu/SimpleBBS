package com.haruhiism.bbs.service.DataEncoder;

public interface DataEncoder {

    public String encode(String string);

    public boolean compare(String rawString, String encodedString);
}
