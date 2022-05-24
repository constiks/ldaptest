package com.example.ldaptest;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppListener implements ApplicationListener<ApplicationReadyEvent> {
    
    public void onApplicationEvent(ApplicationReadyEvent event) {
        
    }
}
