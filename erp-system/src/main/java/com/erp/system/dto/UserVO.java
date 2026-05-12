package com.erp.system.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String avatar;
    private Long deptId;
    private String deptName;
    private List<String> roles;
    private List<String> permissions;
}
