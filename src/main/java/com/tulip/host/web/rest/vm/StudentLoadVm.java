package com.tulip.host.web.rest.vm;

import com.tulip.host.enums.RelationEnum;
import io.github.rushuat.ocell.annotation.FieldFormat;
import io.github.rushuat.ocell.annotation.FieldName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentLoadVm {

    @FieldName("studentName")
    private String name;

    @FieldName("birthday")
    @FieldFormat("mm/dd/yyyy")
    private String dob;

    @FieldName("phoneNumber")
    String phoneNumber;

    @FieldName("address")
    String address;

    @FieldName("gender")
    String gender;

    @FieldName("blood_group")
    String bloodGroup;

    @FieldName("religion")
    String religion;

    @FieldName("previous_school")
    String previousSchool;

    @FieldName("std")
    String std;

    @FieldName("session")
    Long session;

    @FieldName("father_Name")
    String fatherName;

    @FieldName("father_contact")
    String fatherContact;

    @FieldName("father_aadhaar")
    String fatherAadhaar;

    @FieldName("father_relation")
    RelationEnum fatherRelation;

    @FieldName("father_qualification")
    String fatherQualification;

    @FieldName("father_occupation")
    String fatherOccupation;
}
