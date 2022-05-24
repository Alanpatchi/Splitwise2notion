package com.boobalan.splitwise2notion.splitwise;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ExpenseInfo {
    private List<Expense> expenses;
}
