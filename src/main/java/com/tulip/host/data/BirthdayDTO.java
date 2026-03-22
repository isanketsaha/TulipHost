package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BirthdayDTO {

    private String name;

    private String grade;

    /** true when the birthday falls on today */
    private boolean today;

    /** how many days until the birthday (0 = today) */
    private int daysUntil;

    private String photoUrl;
}
