package com.haruhiism.bbs.service.DataEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptDataEncoder implements DataEncoder{

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String string) {
        return passwordEncoder.encode(string);
    }

    @Override
    public boolean compare(String rawString, String encodedString) {
        if(rawString == null || encodedString == null) return false;
        return passwordEncoder.matches(rawString, encodedString);
    }
}
