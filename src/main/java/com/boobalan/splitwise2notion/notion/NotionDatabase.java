package com.boobalan.splitwise2notion.notion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionDatabase {
    private String id;
    private Map<String, ?> properties;

}
