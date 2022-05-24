package com.boobalan.splitwise2notion.splitwise;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class User {
    private UserInfo user;
    private String paid_share;  // is shows up in the credit_card's transactions' page && also includes cash payments
    // Note: all cash payments from Amrita should be separately added in splitwise
    private String owed_share;  // is the amount that is effectively paid by other party
    private String net_balance; // paid_share + owed_share

}
