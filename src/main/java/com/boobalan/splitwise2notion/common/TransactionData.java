package com.boobalan.splitwise2notion.common;

import lombok.*;


@Builder
@Getter
public class TransactionData {

    private String description = null;
    private String source = null;
    private String amount = null;
    private String date = null;

}
