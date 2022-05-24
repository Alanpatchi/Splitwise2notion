package com.boobalan.splitwise2notion.notion;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotionPage {
    private ParentDatabase parent;
    private JsonNode properties;
}
