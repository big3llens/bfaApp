package ru.bfad.bfaApp.bots.botsApi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessRoleDuration {
    private AccessRole role;
    private long addingTime;
}
