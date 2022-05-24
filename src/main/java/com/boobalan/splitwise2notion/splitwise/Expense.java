package com.boobalan.splitwise2notion.splitwise;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Expense {
    private String id;
    private String description;
    private boolean repeats;
    private String repeat_interval;
    private String next_repeat;
    private String date;
    private String created_at;
    private String updated_at;
    private List<User> users;


}
