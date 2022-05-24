package com.example.ldaptest;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LdapSettings {

    private String domain;
    private String primaryUrl;
    private String secondaryUrl;
    private String usersBaseDn;
    private String groupsBaseDn;
    private String userDn;
    private String userPassword;

    private boolean enabled;
    private List<String> groups = new ArrayList<>();

}
