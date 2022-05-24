package com.boobalan.splitwise2notion.notion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionSearchResult {

    private List<NotionDatabase> results;
}
