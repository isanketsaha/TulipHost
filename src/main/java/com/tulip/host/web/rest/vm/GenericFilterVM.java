package com.tulip.host.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericFilterVM {

        private Map<String, Object> filters = new HashMap<>(); // e.g., {"type": "REPORT", "uploads.id": 1}
        private String sortBy = "createdDate";
        private String sortDirection = "DESC";

}
