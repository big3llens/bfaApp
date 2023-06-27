package ru.bfad.bfaApp.bots.botsApi;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AccessRoleContext {
    Map<Long, List<AccessRoleDuration>> userAccessRole = new HashMap<>();

    public void setUserAccessRole (long id, AccessRoleDuration role){
        List<AccessRoleDuration> accessList = userAccessRole.get(id);
        if (accessList == null) accessList = new ArrayList<>();
        accessList.removeIf(ar -> ar.getRole().equals(role.getRole()));
        accessList.add(role);
        userAccessRole.put(id, accessList);
    }

    public List<AccessRoleDuration> getUserAccessRoleById(long id){
        return userAccessRole.get(id);
    }

    public Map<Long, List<AccessRoleDuration>> getUserAccessRole(){
        return userAccessRole;
    }
}
