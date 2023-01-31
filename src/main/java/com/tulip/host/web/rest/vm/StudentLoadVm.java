package com.tulip.host.web.rest.vm;

import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentLoadVm {

    @FieldName("Student Name")
    private String name;

    @FieldName("Birthday")
    @FieldFormat("mm/dd/yyyy")
    private String dob;

    @FieldName("Phone Number")
    String phoneNumber;

    @FieldName("Address")
    String address;

    @FieldName("Gender")
    String gender;

    @FieldName("Blood Group")
    String bloodGroup;

    @FieldName("Religion")
    String religion;

    @FieldName("Previous School")
    String previousSchool;

    @FieldName("Class")
    String std;

    @FieldName("Session")
    Long session;

    @FieldName("Father Name")
    String fatherName;

    @FieldName("Father Contact")
    String fatherContact;

    @FieldName("Father Aadhaar")
    String fatherAadhaar;

    @FieldName("Father Qualification")
    String fatherQualification;

    @FieldName("Father Occupation")
    String fatherOccupation;
}
