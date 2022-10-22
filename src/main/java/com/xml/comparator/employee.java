package com.xml.comparator;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode(exclude = "name,department,phone")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class employee {
    private String id;
    private String name;
    private String department;
    private String phone;

}
