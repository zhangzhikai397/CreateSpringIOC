package com.springframework.service.serviceImpl;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Component;
import com.springframework.service.AService;
import com.springframework.service.BService;

@Component
public class AServiceImpl implements AService {
    @Autowired
    private BService bService;

    public String a() {
        return bService.b() + "AAAA";
    }
}
