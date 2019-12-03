package com.springframework.service.serviceImpl;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Component;
import com.springframework.service.AService;
import com.springframework.service.BService;

@Component
public class BServiceImpl implements BService {
    //@Autowired
    //private AService aService;

    public String b() {
        return "BBBB";
    }
}
